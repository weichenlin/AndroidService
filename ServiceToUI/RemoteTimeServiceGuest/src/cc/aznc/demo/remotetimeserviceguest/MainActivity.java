package cc.aznc.demo.remotetimeserviceguest;

import cc.aznc.demo.remotetimeservicehost.TimeObserver;
import cc.aznc.demo.remotetimeservicehost.TimeService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	private String TAG = MainActivity.class.getSimpleName();
	private TimeService timeService;
	private Handler handler = new Handler();
	
	private TimeObserver.Stub timeObserver = new TimeObserver.Stub() {
		@Override
		public void onTimeUpdate(final String serviceId, final String time) {
			handler.post(new Runnable(){
				@Override
				public void run() {
					updateTime(serviceId, time);
				}
			});
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startTimeService();
		bindTimeService();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindTimeService();
		Log.i(TAG, "onDestroy");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private ServiceConnection bindServiceConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder binder) {
			timeService = TimeService.Stub.asInterface(binder);
			onServiceReady();
		}
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			timeService = null;
		}
	};
	
	private void onServiceReady() {
		Log.i(TAG, "onServiceReady");
		try {
			timeService.registObserver(timeObserver);
		} catch (RemoteException e) {
			unbindTimeService();
		}
	}
	
	private void startTimeService() {
		Intent it = new Intent();
		it.setClassName("cc.aznc.demo.remotetimeservicehost", "cc.aznc.demo.remotetimeservicehost.RemoteTimeService");
		startService(it);
	}
	
	private void bindTimeService() {
		Intent it = new Intent();
		it.setClassName("cc.aznc.demo.remotetimeservicehost", "cc.aznc.demo.remotetimeservicehost.RemoteTimeService");
		boolean success = bindService(it, bindServiceConn, BIND_AUTO_CREATE);
		if (!success) {
			Log.e("bindTimeService", "bind failed");
		}
	}
	
	private void unbindTimeService() {
		unbindService(bindServiceConn);
	}
	
	private void updateTime(String id, String time) {
		((TextView)findViewById(R.id.text_serviceId)).setText(id);
		((TextView)findViewById(R.id.text_time)).setText(time);
	}
}
