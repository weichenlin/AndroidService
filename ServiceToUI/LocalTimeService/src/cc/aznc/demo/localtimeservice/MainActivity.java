package cc.aznc.demo.localtimeservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	private String TAG = MainActivity.class.getSimpleName();
	private TimeService timeService;
	
	private TimeService.TimeUpdateObserver timeObserver = new TimeService.TimeUpdateObserver() {
		@Override
		public void onTimeServiceUpdate(final String serviceId, final String time) {
			runOnUiThread(new Runnable(){
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
		Log.i(TAG, "onDestroy");
		super.onDestroy();
		timeService.removeObserver(timeObserver);
		unbindTimeService();
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
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			timeService = ((TimeService.LocalBinder)arg1).getService();
			onServiceReady();
		}
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// do nothing
		}
	};
	
	private void onServiceReady() {
		Log.i(TAG, "onServiceReady");
		timeService.registObserver(timeObserver);
	}
	
	private void startTimeService() {
		Intent it = new Intent(MainActivity.this, TimeService.class);
		startService(it);
	}
	
	private void bindTimeService() {
		Intent it = new Intent(MainActivity.this, TimeService.class);
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
