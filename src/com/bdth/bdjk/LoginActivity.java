package com.bdth.bdjk;

import java.util.HashMap;
import java.util.Map;

import com.bdth.bean.User;
import com.bdth.service.UserLogin;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

    private Button login;
    private Button exit;
    private CheckBox ck;
    private EditText name;
    private EditText password;
    private String mName;
    private String mPassword;
    private boolean mCk;
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_login);
	init();
	login.setOnClickListener(this);
	exit.setOnClickListener(this);
    }

    private void init() {
	login = (Button) findViewById(R.id.ok);
	exit = (Button) findViewById(R.id.exit);
    }

    @Override
    public void onClick(View v) {
	switch (v.getId()) {
	case R.id.ok:
	    ck = (CheckBox) findViewById(R.id.cbPasswd);
	    name = (EditText) findViewById(R.id.editName);
	    password = (EditText) findViewById(R.id.editPasswd);
	    mCk = ck.isChecked();
	    mName = name.getText().toString();
	    mPassword = password.getText().toString();
	    new Thread(r).start();
	    break;
	case R.id.exit:
	    finish();
	    break;

	default:
	    break;
	}

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
	public void handleMessage(android.os.Message msg) {
	    if (msg.what == 0x123) {
		Toast.makeText(LoginActivity.this, "用户ID：" + user.getId(),
			Toast.LENGTH_LONG).show();
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, IndexActivity.class);
		startActivity(intent);
		finish();
	    }
	    if (msg.what == 0x125) {
		Toast.makeText(LoginActivity.this, "用户名或密码不正确！",
			Toast.LENGTH_LONG).show();
	    }
	};
    };

    private Runnable r = new Runnable() {

	@Override
	public void run() {
	    Map<String, String> key = new HashMap<String, String>();
	    key.put("name", mName);
	    key.put("password", mPassword);
	    String detail = UserLogin.getResult(key);
	    try {
		user = UserLogin.parseResult(detail);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    if (user == null || user.getId() == 0) {
		handler.sendEmptyMessage(0x125);
		return;
	    }
	    if (mCk) {
		if (null != user.getRealName()) {
		    if (!user.getRealName().equals("null")) {
			DBHelper db = new DBHelper(LoginActivity.this);
			db.init();
			String sql = "insert into "
				+ DBHelper.TABLE_NAME_CURUSER
				+ "(name) values('" + user.getRealName() + "')";
			Log.d("sql", sql);
			db.execSQL(sql);
			db.close();
		    }
		}
	    }

	    DBHelper db = new DBHelper(LoginActivity.this);
	    db.init();
	    db.delUser(user.getId(), DBHelper.TABLE_NAME_USRERLOGIN);
	    Log.d("sql-del", "table:" + DBHelper.TABLE_NAME_USRERLOGIN
		    + "value:" + user.getId());
	    db.delUser(0, DBHelper.TABLE_NAME_USRERLOGIN);
	    Log.d("sql-del", "table:" + DBHelper.TABLE_NAME_USRERLOGIN
		    + "value:0");
	    ContentValues values = new ContentValues();
	    values.put("userId", user.getId());
	    values.put("name", user.getRealName());
	    values.put("compId", user.getCorporationId());
	    values.put("compName", user.getCorporationName());
	    db.insert(values, DBHelper.TABLE_NAME_USRERLOGIN);
	    Log.d("sql-insert", "table:" + DBHelper.TABLE_NAME_USRERLOGIN
		    + "value:~");
	    db.close();
	    handler.sendEmptyMessage(0x123);
	}
    };

}
