package cc.aznc.demo.androidservice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class CrackByStartService extends Service {
	Handler handler = new Handler();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		postCrackOperation(intent);
		return super.onStartCommand(intent, flags, startId);
	}
	
	protected void postCrackOperation(Intent intent) {
		final String md5String = intent.getStringExtra("passhash");
		
		handler.post(new Runnable() {
			@Override
			public void run() {
				String result = Tools.brutalCrack(md5String);
				Log.e("CrackByStartService", "crack result : " + result);
			}
		});
	}

}
