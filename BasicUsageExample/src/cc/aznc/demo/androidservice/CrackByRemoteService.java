package cc.aznc.demo.androidservice;

import java.lang.ref.WeakReference;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class CrackByRemoteService extends Service {

	static final int MSG_START_CRACK = 1;
	static final int MSG_CRACK_FINISHED = 2;
	
	final Messenger mMessenger = new Messenger(new MessageReceiver(this));
	
	static class MessageReceiver extends Handler {
		private final WeakReference<CrackByRemoteService> mService; 

		MessageReceiver(CrackByRemoteService service) {
	        mService = new WeakReference<CrackByRemoteService>(service);
	    }
	    
		@Override
		public void handleMessage(Message msg) {
			Log.v("MessageReceiver", "handleMessage()");
			CrackByRemoteService service = mService.get();
			if (service != null) {
				service.handleMessage(msg);
			} else {
				super.handleMessage(msg);
			}
		}
	}
	
	public void handleMessage(Message msg) {
		Log.v("CrackByRemoteService", "handleMessage()");
		switch (msg.what) {
            case MSG_START_CRACK:
            	String passMD5 = msg.getData().getString("passhash");
            	String result = crackPassword(passMD5);
            	Message reply = Message.obtain(null, MSG_CRACK_FINISHED, 0, 0);
            	Bundle data = new Bundle();
        		data.putString("result", result);
        		reply.setData(data);
            	try {msg.replyTo.send(reply);}
            	catch (RemoteException e) {}
            	
                break;
		}
	}
	
	@Override
	public void onCreate() {
		// IMPORTANT ! limit max length so the crack can going quickly
		// this is remote service, so need do init it self
		Tools.MaxLen = getResources().getInteger(R.integer.max_pass_length);
		
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.e("CrackByRemoteService", "onBind");
		return mMessenger.getBinder();
	}
	
	public String crackPassword(String passMD5) {
		return Tools.brutalCrack(passMD5);
	}

}
