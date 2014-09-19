package cc.aznc.demo.localtimeservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class TimeService extends Service {
	public class LocalBinder extends Binder {
		TimeService getService() {
			return TimeService.this;
		}
	}
	
	public interface TimeUpdateObserver {
		public void onTimeServiceUpdate(String serviceId, String time);
	}
	
	private String TAG = TimeService.class.getSimpleName();
	private LinkedList<TimeUpdateObserver> observers = new LinkedList<TimeUpdateObserver>();
	private boolean running = true;
	private Thread mainThread;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
	private String serviceId = "";
	private String timeString = "";
	private LocalBinder mBinder = new LocalBinder();
	
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
		return mBinder;
	}
	
	public void registObserver(TimeUpdateObserver o) {
		observers.add(o);
		// update guest ASAP
		o.onTimeServiceUpdate(serviceId, timeString);
	}
	
	public void removeObserver(TimeUpdateObserver o) {
		observers.remove(o);
	}
	
	private void notifyObserver() {
		if (observers.size() == 0) {
			//Log.d(TAG, "no observer");
			return;
		}
		
		//Log.d(TAG, String.format("notify %d observer", observers.size()));
		for (TimeUpdateObserver g : observers) {
			g.onTimeServiceUpdate(serviceId, timeString);
		}
	}
	
	private long getTimestamp() {
		return System.currentTimeMillis() / 1000;
	}
	
	private void updateTime() {
		timeString = dateFormat.format(new Date());
	}
}
