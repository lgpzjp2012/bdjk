package com.bdth.bdjk;

import com.bdth.bean.User;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InlinedApi")
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    @SuppressWarnings("unused")
    private ProgressBar pb = null;
    @SuppressWarnings("unused")
    private TextView tvPersent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getWindow().getDecorView().setSystemUiVisibility(
		View.SYSTEM_UI_FLAG_FULLSCREEN);

	setContentView(R.layout.activity_main);
	pb = (ProgressBar) this.findViewById(R.id.pb);
	tvPersent = (TextView) this.findViewById(R.id.tvPersent);

	Thread thread = new Thread(myRun);

	thread.start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
	@Override
	public void handleMessage(Message message) {
	    if (message.arg1 == 0) {
		Log.d(TAG, "您的网络异常，请检查网络！");
		Toast.makeText(MainActivity.this, "您的网络异常，请检查网络！",
			Toast.LENGTH_SHORT).show();
	    } else if (message.arg1 == 1) {
		User u = (User) message.obj;
		Toast.makeText(MainActivity.this, "欢迎您，" + u.getName() + "!",
			Toast.LENGTH_SHORT).show();
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, IndexActivity.class);
		startActivity(intent);
		finish();
	    } else if (message.arg1 == 2) {

		Toast.makeText(MainActivity.this, "请登录！", Toast.LENGTH_SHORT)
			.show();
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	    } else {
		//
	    }
	}
    };

    Runnable myRun = new Runnable() {
	int netStatus = 1;
	User user = null;

	@Override
	public void run() {
	    Message msg = handler.obtainMessage();
	    try {
		Thread.sleep(1000);
		netStatus = NetworkUtil
			.getConnectivityStatus(MainActivity.this);
		if (netStatus == 0) {
		    msg.arg1 = 0;
		    handler.sendMessage(msg);
		} else {
		    DBHelper db = new DBHelper(MainActivity.this);
		    db.init();
		    Cursor cursor = db.query(DBHelper.TABLE_NAME_CURUSER);
		    if (cursor.moveToNext()) {
			user = new User();
			user.setId(cursor.getInt(0));
			user.setName(cursor.getString(1));
			msg.arg1 = 1;
			msg.obj = user;
			handler.sendMessage(msg);
		    } else {
			msg.arg1 = 2;
			handler.sendMessage(msg);
		    }
		    db.close();
		}
		Thread.sleep(1000);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    };
}
