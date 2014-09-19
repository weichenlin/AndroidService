package cc.aznc.demo.remotetimeservicehost;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class RemoteTimeService extends Service {
	private String TAG = RemoteTimeService.class.getSimpleName();
	private ConcurrentLinkedQueue<TimeObserver> observers = new ConcurrentLinkedQueue<TimeObserver>();
	private boolean running = true;
	private Thread mainThread;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
	private String serviceId = "";
	private String timeString = "";
	
	private TimeService.Stub binder = new TimeService.Stub(){
		@Override
		public void registObserver(TimeObserver o) throws RemoteException {
			observers.add(o);
			// update guest ASAP
			o.onTimeUpdate(serviceId, timeString);
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		serviceId = Integer.toString((int)(Math.random() * 1000 + 1));
		Log.i(TAG, "start TimeService with id " + serviceId);
		updateTime();
		
		mainThread = new Thread(new Runnable(){
			@Override
			public void run() {
				long lastUpdateTime = getTimestamp();
				while (running) {
					try {
						long nowTime = getTimestamp();
						if (lastUpdateTime != nowTime) {
							lastUpdateTime = nowTime;
							updateTime();
					        notifyObserver();
						}
						Thread.sleep(100);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		});
		mainThread.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			running = false;
			mainThread.join();
		} catch (InterruptedException e) {
			// ignore exception
		}
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		// make service survive long long time
        return START_STICKY;
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.w(TAG, "on Unbind");
		return super.onUnbind(intent);
	}

	private synchronized void notifyObserver() {
		if (observers.size() == 0) {
			//Log.d(TAG, "no observer");
			return;
		}
		
		//Log.d(TAG, String.format("notify %d observer", observers.size()));
		LinkedList<TimeObserver> deadObservers = new LinkedList<TimeObserver>();
		for (TimeObserver o : observers) {
			try {
				o.onTimeUpdate(serviceId, timeString);
			} catch (RemoteException e) {
				Log.w(TAG, "got a dead observer");
				deadObservers.add(o);
			}
		}
		observers.removeAll(deadObservers);
	}
	
	private long getTimestamp() {
		return System.currentTimeMillis() / 1000;
	}
	
	private void updateTime() {
		timeString = dateFormat.format(new Date());
	}
}
