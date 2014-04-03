package com.bdth.bdjk;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SettingActivity extends Activity {
    private Button zhuxiao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_setting);

	zhuxiao = (Button) findViewById(R.id.zhuxiao);
	zhuxiao.setOnClickListener(new View.OnClickListener() {

	    @Override
	    public void onClick(View v) {
		new Thread(run).start();
	    }
	});
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

	@Override
	public void handleMessage(android.os.Message msg) {
	    Intent intent = new Intent();
	    intent.setClass(SettingActivity.this, LoginActivity.class);
	    startActivity(intent);
	    finish();
	};
    };

    private Runnable run = new Runnable() {

	@Override
	public void run() {
	    DBHelper db = new DBHelper(SettingActivity.this);
	    db.init();
	    db.del(DBHelper.TABLE_NAME_CURUSER);
	    Log.d("sql-del", "table:" + DBHelper.TABLE_NAME_CURUSER + "value:~");
	    db.close();
	    handler.sendEmptyMessage(0x123);
	}
    };
}
