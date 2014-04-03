package com.bdth.bdjk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bdth.bdjk.popwin.ActionItem;
import com.bdth.bdjk.popwin.TitlePopup;
import com.bdth.bean.Vehicle;
import com.bdth.service.GetVehicleList;

@SuppressLint("InlinedApi")
public class IndexActivity extends Activity implements OnClickListener,
	OnTouchListener {
    private static final int FLING_MIN_DISTANCE = 120;// 移动最小距离
    private static final int FLING_MIN_VELOCITY = 200;// 移动最大速度

    private static final int POSITION = 5; // 按照第5列排序
    private static final int DIRECTION = -1;// 倒序排列
    private static final int PAGESIZE = 9; // 每页9条数据
    private static int pageNo = 1; // 第几页

    private TitlePopup titlePopup;

    private ImageView imageMore;
    private ImageView imageNext;
    private ImageView imagePrv;
    private ImageView imageHome;
    private ImageView imageEnd;
    private GridView gridMain;
    private List<Vehicle> listVeh = null;
    private static int userId = 0;
    private static int count = 0;
    private String compName = "";
    LayoutAnimationController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	// getWindow().getDecorView().setSystemUiVisibility(
	// View.SYSTEM_UI_FLAG_FULLSCREEN);
	setContentView(R.layout.activity_index);
	mygesture = new GestureDetector(this, onGestureListener);
	imageNext = (ImageView) findViewById(R.id.imageNext);
	imagePrv = (ImageView) findViewById(R.id.imagePrv);
	imageHome = (ImageView) findViewById(R.id.imageHome);
	imageEnd = (ImageView) findViewById(R.id.imageEnd);
	imageMore = (ImageView) findViewById(R.id.imageMore);
	imageNext.setOnClickListener(this);
	imagePrv.setOnClickListener(this);
	imageHome.setOnClickListener(this);
	imageEnd.setOnClickListener(this);
	imageMore.setOnClickListener(this);
	init();
	new Thread(run).start();

    }

    private void setAnmi() {
	AnimationSet set = new AnimationSet(true);
	Animation animation = new AlphaAnimation(0.0f, 1.0f);
	animation.setDuration(250);
	set.addAnimation(animation);
	animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
		Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
		-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
	animation.setDuration(200);
	set.addAnimation(animation);
	controller = new LayoutAnimationController(set, 0.5f);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
	public void handleMessage(android.os.Message msg) {
	    if (msg.what == 0x123) {
		if (listVeh == null) {
		    Toast.makeText(IndexActivity.this, "网络异常！",
			    Toast.LENGTH_SHORT).show();
		} else {
		    gridMain = (GridView) findViewById(R.id.gridMain);
		    gridMain.setAdapter(new MyAdapter(IndexActivity.this,
			    listVeh));
		    setAnmi();
		    gridMain.setLayoutAnimation(controller);
		    gridMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent,
				View view, int position, long id) {
			    if (listVeh == null) {
				Toast.makeText(IndexActivity.this, "网络异常！",
					Toast.LENGTH_SHORT).show();
			    } else {
				if (listVeh.size() == 0) {
				    Toast.makeText(IndexActivity.this, "网络异常！",
					    Toast.LENGTH_SHORT).show();
				} else {

				    Intent intent = new Intent(
					    IndexActivity.this,
					    InfoActivity.class);

				    /* 通过Bundle对象存储需要传递的数据 */
				    Bundle bundle = new Bundle();
				    /* 字符、字符串、布尔、字节数组、浮点数等等，都可以传 */
				    bundle.putInt("vehicleId",
					    listVeh.get(position).getId());
				    bundle.putString("compName", compName);
				    /* 把bundle对象assign给Intent */
				    intent.putExtras(bundle);
				    startActivity(intent);
				}
			    }
			}
		    });
		    gridMain.setOnTouchListener(IndexActivity.this);
		}
	    }
	};
    };

    private Runnable run = new Runnable() {

	@Override
	public void run() {
	    String username = "";
	    DBHelper db = new DBHelper(IndexActivity.this);
	    db.init();
	    Cursor cursor = db.query(DBHelper.TABLE_NAME_CURUSER);
	    while (cursor.moveToNext()) {
		username = cursor.getString(cursor.getColumnIndex("name"));
	    }
	    Cursor c = db.rawQuery("select * from "
		    + DBHelper.TABLE_NAME_USRERLOGIN + " where name=?",
		    new String[] { username });
	    while (c.moveToNext()) {
		userId = c.getInt(c.getColumnIndex("userId"));
		compName = c.getString(c.getColumnIndex("compName"));
		Log.d("userId", "userId======" + userId);
	    }
	    db.close();
	    listVeh = getBeanList(userId, POSITION, DIRECTION, PAGESIZE, pageNo);
	    Log.d("pageNo", "pageNo======" + pageNo);
	    handler.sendEmptyMessage(0x123);
	}
    };

    /**
     * 获取一页数据
     * 
     * @param userId
     * @param position
     * @param direction
     * @param pageSize
     * @param pageNo
     * @return
     */
    private List<Vehicle> getBeanList(int userId, int position, int direction,
	    int pageSize, int pageNo) {
	List<Vehicle> list = new ArrayList<Vehicle>();
	Map<String, String> key = new HashMap<String, String>();
	key.put("userID", "" + userId);
	key.put("position", "" + position);
	key.put("direction", "" + direction);
	key.put("pageSize", "" + pageSize);
	key.put("pageNo", "" + pageNo);
	String detail = GetVehicleList.getResult(key);
	list = GetVehicleList.parseResult(detail);
	// 获取总条数
	if (count == 0)
	    count = GetVehicleList.getListSize(detail);
	return list;
    }

    /**
     * 初始化管理菜单
     */
    private void init() {
	titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT,
		LayoutParams.WRAP_CONTENT);
	ActionItem setting = new ActionItem(this, "设置", R.drawable.sz01);
	titlePopup.addAction(setting);
	titlePopup.addAction(new ActionItem(this, "关于", R.drawable.about));
	titlePopup.addAction(new ActionItem(this, "退出", R.drawable.exit1));
	titlePopup
		.setItemOnClickListener(new TitlePopup.OnItemOnClickListener() {

		    @Override
		    public void onItemClick(ActionItem item, int position) {
			if (position == 0) {
			    Intent intent = new Intent();
			    intent.setClass(IndexActivity.this,
				    SettingActivity.class);
			    startActivity(intent);
			}
			if (position == 1) {
			    Intent intent = new Intent();
			    intent.setClass(IndexActivity.this,
				    AboutActivity.class);
			    startActivity(intent);
			}
			if (position == 2) {
			    finish();
			}
		    }
		});
    }

    @Override
    public void onClick(View v) {

	switch (v.getId()) {
	case R.id.imageMore:
	    if (titlePopup.isShowing()) {
		titlePopup.dismiss();
		titlePopup = null;
	    } else {
		titlePopup.show(v);
	    }
	    break;
	case R.id.imageNext:
	    int page = (count % PAGESIZE == 0) ? count / PAGESIZE : count
		    / PAGESIZE + 1;
	    if (pageNo < page) {
		pageNo++;

	    } else {
		pageNo = page;
	    }
	    new Thread(run).start();
	    break;
	case R.id.imagePrv:
	    if (pageNo > 1) {
		pageNo--;

	    } else {
		pageNo = 1;
	    }
	    new Thread(run).start();
	    break;
	case R.id.imageHome:
	    pageNo = 1;
	    new Thread(run).start();
	    break;
	case R.id.imageEnd:
	    // 计算总页数
	    int page1 = (count % PAGESIZE == 0) ? count / PAGESIZE : count
		    / PAGESIZE + 1;
	    pageNo = page1;
	    new Thread(run).start();
	    break;

	default:
	    break;
	}
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
	return mygesture.onTouchEvent(event);
    }

    /* 用户手势滑动判断 */
    GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
		float velocityY) {
	    // DisplayMetrics dm = new DisplayMetrics();
	    // // 取得窗口属性
	    // getWindowManager().getDefaultDisplay().getMetrics(dm);
	    // e1：第1个ACTION_DOWN MotionEvent
	    // e2：最后一个ACTION_MOVE MotionEvent
	    // velocityX：X轴上的移动速度（像素/秒）
	    // velocityY：Y轴上的移动速度（像素/秒）

	    // X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒
	    // 向右翻图片
	    Log.d("onFling", ".................");
	    if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
		    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
		Log.d("onFling", ".................1");
		int page = (count % PAGESIZE == 0) ? count / PAGESIZE : count
			/ PAGESIZE + 1;
		if (pageNo < page) {
		    pageNo++;

		} else {
		    pageNo = page;
		}
		new Thread(run).start();
	    }

	    // 向左翻图片
	    if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
		    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
		Log.d("onFling", ".................2");
		if (pageNo > 1) {
		    pageNo--;

		} else {
		    pageNo = 1;
		}
		new Thread(run).start();
	    }

	    return false;
	}
    };

    GestureDetector mygesture = null;
}
