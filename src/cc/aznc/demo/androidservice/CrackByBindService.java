package cc.aznc.demo.androidservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class CrackByBindService extends Service {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.e("CrackByBindService", "onCreate");
		super.onCreate();
	}

	public class LocalBinder extends Binder {
		CrackByBindService getService() {
			return CrackByBindService.this;
		}
	}
	
	private LocalBinder mBinder = new LocalBinder();
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.e("CrackByBindService", "onBind");
		return mBinder;
	}
	
	public String crackPassword(String passMD5) {
		return Tools.brutalCrack(passMD5);
	}

}
