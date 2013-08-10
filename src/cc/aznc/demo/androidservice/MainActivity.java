package cc.aznc.demo.androidservice;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// IMPORTANT ! limit max length so the crack can going quickly
		Tools.MaxLen = getResources().getInteger(R.integer.max_pass_length);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/*
	 * code for using "start service"
	 */
	
	public void btnStartService(View view) {
		String pass = ((EditText)findViewById(R.id.passInput)).getText().toString();
		Intent it = new Intent(MainActivity.this, CrackByStartService.class);
		it.putExtra("passhash", Tools.md5(pass));
		startService(it);
	}
	
	/*
	 * code for using "bind service" begin
	 */
	
	private CrackByBindService bindService;
	private ServiceConnection bindServiceConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			bindService = ((CrackByBindService.LocalBinder)arg1).getService();
			onBindServiceReady();
		}
		
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public void btnBindService(View view) {
		Log.e("btnBindService", "start bind");
		Intent it = new Intent(MainActivity.this, CrackByBindService.class);
		boolean success = bindService(it, bindServiceConn, BIND_AUTO_CREATE);
		if (!success) {
			Log.e("btnBindService", "bind failed");
		}
	}
	
	private void onBindServiceReady() {
		Log.e("onBindServiceReady", "ready bind");
		String passMD5 = Tools.md5( ((EditText)findViewById(R.id.passInput)).getText().toString() );
		String result = bindService.crackPassword(passMD5);
		((EditText)findViewById(R.id.resultInput)).setText(result);
	}
	
	/*
	 * code for using "remote service" begin
	 */
	
	private Messenger remoteMessenger;
	private ServiceConnection remoteServiceConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			remoteMessenger = new Messenger(arg1);
			onRemoteServiceReady();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			
		}
    };
    
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CrackByRemoteService.MSG_CRACK_FINISHED:
                	((EditText)findViewById(R.id.resultInput)).setText(msg.getData().getString("result"));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
    final Messenger incomingMessenger = new Messenger(new IncomingHandler());
    
	public void btnRemoteService(View view) {
		Intent it = new Intent(MainActivity.this, CrackByRemoteService.class);
    	bindService(it, remoteServiceConn, BIND_AUTO_CREATE);
	}
	
	private void onRemoteServiceReady() {
		String pass = ((EditText)findViewById(R.id.passInput)).getText().toString();
		Bundle data = new Bundle();
		data.putString("passhash", Tools.md5(pass));
		
		Message msg = Message.obtain(null, CrackByRemoteService.MSG_START_CRACK);
		msg.replyTo = incomingMessenger;
		msg.setData(data);
        try { remoteMessenger.send(msg); }
        catch (RemoteException e) {}
	}

}
