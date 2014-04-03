package com.bdth.bdjk;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.bdth.bdjk.datetimePicker.DateTimePickerDialog;
import com.bdth.bean.Position;
import com.bdth.bean.Vehicle;
import com.bdth.bean.VehiclePosition;
import com.bdth.service.GetVehicle;
import com.bdth.service.GetVehiclePosition;
import com.bdth.service.VehiclePostionByTime;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;

public class InfoActivity extends Activity implements OnClickListener {

    private static final String TAG = "InfoActivity";
    //
    private static final int MAPZOOM = 1;
    //显示基本数据
    private static final int BASE_DATE = 0x123;
    // 网络不正常 或者数据不正常
    private static final int EXCEPTION = 0x125;
    //显示定位到的地点
    private static final int POSITION = 0x1234;    
    // 初始化历史轨迹地图
    private static final int INITLSMAP = 0x321;
    // 显示历史轨迹
    private static final int LSMAP = 0x4321;

    private int vehicleId = 0;
    private String compName = "";

    private Button btCarinfo;
    private Button btCarposi;
    private Button btCarlssj;
    private RelativeLayout lyCarinfo;
    private RelativeLayout lyCarposi;
    private RelativeLayout lyCarlssj;

    private TextView tvcarNo;
    private TextView tvmodel;
    private TextView tvtype;
    private TextView tvssdw;
    private TextView tvgroupName;
    private TextView tvspeedLimit;
    private TextView tvstatus;
    private TextView tvcomment;

    Vehicle bean;
    private BMapManager mapManager;
    private MapView mapView;
    private MapController mapController;

    VehiclePosition position = null;
    private MyOverlay mOverlay = null;

    // 历史轨迹
    private EditText startTime;
    private EditText endTime;
    private Button btShech;
    String startTimestr = "";
    String endTimestr = "";
    private MapView mapViewls;
    private MapController mapControllerls;
    private List<Position> listPosi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	// init
	mapManager = new BMapManager(getApplication());
	mapManager.init("7NosDhzBGCqt5cDE83MleQAA", null);

	setContentView(R.layout.activity_info);

	/* 获取Intent中的Bundle对象 */
	Bundle bundle = this.getIntent().getExtras();

	/* 获取Bundle中的数据，注意类型和key */
	vehicleId = bundle.getInt("vehicleId");
	compName = bundle.getString("compName");

	btCarinfo = (Button) findViewById(R.id.btCarinfo);
	btCarposi = (Button) findViewById(R.id.btCarposi);
	btCarlssj = (Button) findViewById(R.id.btCarlssj);
	btCarinfo.setOnClickListener(this);
	btCarposi.setOnClickListener(this);
	btCarlssj.setOnClickListener(this);

	lyCarinfo = (RelativeLayout) findViewById(R.id.lyCarinfo);
	lyCarposi = (RelativeLayout) findViewById(R.id.lyCarposi);
	lyCarlssj = (RelativeLayout) findViewById(R.id.lyCarlssj);

	/*
	 * VISIBLE--->可见 INVISIBLE--->不可见，但这个View在ViewGroupt中仍保留它的位置，不重新layout
	 * GONE---->不可见，但这个View在ViewGroupt中不保留位置，重新layout,那后面的view就会取代他的位置。
	 */
	lyCarinfo.setVisibility(View.VISIBLE);
	lyCarposi.setVisibility(View.GONE);
	lyCarlssj.setVisibility(View.GONE);

	new Thread(run).start();

	// 历史轨迹

	startTime = (EditText) findViewById(R.id.editStartTime);
	endTime = (EditText) findViewById(R.id.editEndTime);
	btShech = (Button) findViewById(R.id.btShech);
	startTime.setOnClickListener(this);
	endTime.setOnClickListener(this);
	btShech.setOnClickListener(this);
    }

    private Runnable run = new Runnable() {

	@Override
	public void run() {
	    Map<String, String> key = new HashMap<String, String>();
	    key.put("vehicleId", "" + vehicleId);
	    Log.d(TAG, "vehicleId======" + vehicleId + "    compName====="
		    + compName);
	    bean = GetVehicle.parseResult(GetVehicle.getResult(key));
	    if (bean == null) {
		handler.sendEmptyMessage(0x125);
	    } else {
		handler.sendEmptyMessage(0x123);
	    }
	}
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
	@Override
	public void handleMessage(android.os.Message msg) {
	    switch (msg.what) {
	    // 显示基本数据
	    case BASE_DATE:
		initTextView();
		if(bean == null){
		    Toast.makeText(InfoActivity.this, "网络异常，请查看网络！",
				Toast.LENGTH_SHORT).show();
		    break;
		}
		tvcarNo.setText(StringUtil.utilNull("" + bean.getPlateNumber()));
		tvmodel.setText(StringUtil.utilNull("" + bean.getModel()));
		tvtype.setText(StringUtil.utilNull("" + bean.getType()));
		tvssdw.setText(StringUtil.utilNull(compName));
		tvgroupName.setText(StringUtil.utilNull(""
			+ bean.getGroupName()));
		tvspeedLimit.setText("" + bean.getSpeedLimit() + "km/h");
		tvstatus.setText(bean.getState() == 1 ? "正常" : "停用");
		tvcomment.setText(StringUtil.utilNull("" + bean.getComment()));
		break;
	    // 网络不正常
	    case EXCEPTION:
		Toast.makeText(InfoActivity.this, "网络异常，请查看网络！",
			Toast.LENGTH_SHORT).show();
		break;
	    // 显示定位到的地点
	    case POSITION:
		if(position == null){
		    Toast.makeText(InfoActivity.this, "网络异常，请查看网络！",
				Toast.LENGTH_SHORT).show();
		    break;
		}
		mapView = (MapView) findViewById(R.id.bmapView);
		mapView.setVisibility(View.VISIBLE);
		// 设置地图模式为交通地图
		mapView.setTraffic(true);
		// 设置启用内置的缩放控件
		mapView.setBuiltInZoomControls(true);
		// 用经纬度初始化中心点
		GeoPoint point = new GeoPoint(
			(int) (Double.parseDouble(StringUtil.utilLat(position
				.getLat())) * 1E6),
			(int) (Double.parseDouble(StringUtil.utilLon(position
				.getLon())) * 1E6));

		/**
		 * 创建自定义overlay
		 */
		mOverlay = new MyOverlay(getResources().getDrawable(
			R.drawable.icon_gcoding), mapView);
		/**
		 * 准备overlay 数据
		 */
		OverlayItem item1 = new OverlayItem(point, "当前位置", "");
		/**
		 * 设置overlay图标，如不设置，则使用创建ItemizedOverlay时的默认图标.
		 */
		item1.setMarker(getResources().getDrawable(
			R.drawable.icon_gcoding));

		/**
		 * 将item 添加到overlay中 注意： 同一个itme只能add一次
		 */
		mOverlay.addItem(item1);
		/**
		 * 将overlay 添加至MapView中
		 */
		mapView.getOverlays().add(mOverlay);
		// 取得地图控制器对象，用于控制MapView
		mapController = mapView.getController();
		// 设置地图的中心
		mapController.setCenter(point);
		// 设置地图默认的缩放级别
		mapController.setZoom(MAPZOOM);

		mapView.refresh();
		break;
	    case INITLSMAP:
		if(position==null){
		    Toast.makeText(InfoActivity.this, "网络异常，请查看网络！",
				Toast.LENGTH_SHORT).show();
		    break;
		}
		mapViewls = (MapView) findViewById(R.id.bmapViewLs);
		mapViewls.setVisibility(View.VISIBLE);
		// 设置地图模式为交通地图
		mapViewls.setTraffic(true);
		// 设置启用内置的缩放控件
		mapViewls.setBuiltInZoomControls(true);

		// 用经纬度初始化中心点
		GeoPoint initpoint = new GeoPoint(
			(int) (Double.parseDouble(StringUtil.utilLat(position
				.getLat())) * 1E6),
			(int) (Double.parseDouble(StringUtil.utilLon(position
				.getLon())) * 1E6));

		mapControllerls = mapViewls.getController();
		// 设置地图的中心
		mapControllerls.setCenter(initpoint);
		// 设置地图默认的缩放级别
		mapControllerls.setZoom(MAPZOOM);
		break;
	    // 显示历史轨迹
	    case LSMAP:
		if(listPosi == null){
		    Toast.makeText(InfoActivity.this, "网络异常，请查看网络！",
				Toast.LENGTH_SHORT).show();
		    break;
		}
		mapViewls = (MapView) findViewById(R.id.bmapViewLs);
		mapViewls.setVisibility(View.VISIBLE);
		// 设置地图模式为交通地图
		mapViewls.setTraffic(true);
		// 设置启用内置的缩放控件
		mapViewls.setBuiltInZoomControls(true);
		Position position = listPosi.get(1);
		// 用经纬度初始化中心点
		GeoPoint lspoint = new GeoPoint(
			(int) (position.getPosLat() * 1E6),
			(int) (position.getPosLon() * 1E6));
		mapControllerls = mapViewls.getController();
		// 设置地图的中心
		mapControllerls.setCenter(lspoint);
		// 设置地图默认的缩放级别
		mapControllerls.setZoom(MAPZOOM);

		for (int i = 0; i < listPosi.size(); i++) {
		    Position position1 = listPosi.get(i);
		    GeoPoint point1 = new GeoPoint(
			    (int) (position1.getPosLat() * 1E6),
			    (int) (position1.getPosLon() * 1E6));
		    /**
		     * 创建自定义overlay
		     */
		    MyOverlay mOverlayls = new MyOverlay(getResources()
			    .getDrawable(R.drawable.icon_gcoding), mapViewls);
		    /**
		     * 准备overlay 数据
		     */
		    OverlayItem item = new OverlayItem(point1, "当前位置", "");
		    /**
		     * 设置overlay图标，如不设置，则使用创建ItemizedOverlay时的默认图标.
		     */
		    item.setMarker(getResources().getDrawable(
			    R.drawable.icon_gcoding));

		    /**
		     * 将item 添加到overlay中 注意： 同一个itme只能add一次
		     */
		    mOverlayls.addItem(item);
		    /**
		     * 将overlay 添加至MapView中
		     */
		    mapViewls.getOverlays().add(mOverlayls);
		    
		}
		mapViewls.refresh();
		break;
	    default:
		break;
	    }
	}
    };

    private void initTextView() {
	tvcarNo = (TextView) findViewById(R.id.tvcarNo);
	tvmodel = (TextView) findViewById(R.id.tvmodel);
	tvtype = (TextView) findViewById(R.id.tvtype);
	tvssdw = (TextView) findViewById(R.id.tvssdw);
	tvgroupName = (TextView) findViewById(R.id.tvgroupName);
	tvspeedLimit = (TextView) findViewById(R.id.tvspeedLimit);
	tvstatus = (TextView) findViewById(R.id.tvstatus);
	tvcomment = (TextView) findViewById(R.id.tvcomment);
	tvcarNo.setText("");
	tvmodel.setText("");
	tvtype.setText("");
	tvssdw.setText("");
	tvgroupName.setText("");
	tvspeedLimit.setText("");
	tvstatus.setText("");
	tvcomment.setText("");
    }

    @Override
    public void onClick(View v) {

	switch (v.getId()) {
	case R.id.btCarinfo:
	    if(mapView != null)
		mapView.setVisibility(View.GONE);
	    if(mapViewls != null)
		mapViewls.setVisibility(View.GONE);
	    lyCarinfo.setVisibility(View.VISIBLE);
	    lyCarposi.setVisibility(View.GONE);
	    lyCarlssj.setVisibility(View.GONE);
	    break;

	case R.id.btCarposi:
	    if(mapViewls != null)
		mapViewls.setVisibility(View.GONE);
	    lyCarinfo.setVisibility(View.GONE);
	    lyCarposi.setVisibility(View.VISIBLE);
	    lyCarlssj.setVisibility(View.GONE);
	    new Thread(runposi).start();
	    break;

	case R.id.btCarlssj:
	    if(mapView != null)
		mapView.setVisibility(View.GONE);
	    lyCarinfo.setVisibility(View.GONE);
	    lyCarposi.setVisibility(View.GONE);
	    lyCarlssj.setVisibility(View.VISIBLE);
	    // 初始化地图
	    handler.sendEmptyMessage(0x321);
	    break;

	case R.id.editStartTime:
	    DateTimePickerDialog dateTimePicKDialog = new DateTimePickerDialog(
		    InfoActivity.this);
	    dateTimePicKDialog.dateTimePicKDialog(startTime, 0);
	    break;
	case R.id.editEndTime:
	    DateTimePickerDialog dateTimePicKDialog1 = new DateTimePickerDialog(
		    InfoActivity.this);
	    dateTimePicKDialog1.dateTimePicKDialog(endTime, 0);
	    break;
	case R.id.btShech:
//	    startTimestr = DateUtil
//		    .GetTDateTime(startTime.getText().toString());
//	    endTimestr = DateUtil.GetTDateTime(endTime.getText().toString());
//	    //debug data
	    vehicleId = 268;
	    
	    startTimestr = "2013-11-30T16:00:00";
		    
	    endTimestr = "2013-11-30T17:00:00";
	    // 执行历史查询
	    new Thread(runls).start();

	    break;

	default:
	    break;
	}
    }

    /**
     * 获取位置
     */
    private Runnable runposi = new Runnable() {

	@Override
	public void run() {
	    Map<String, String> key = new HashMap<String, String>();
	    key.put("vehicleId", "" + vehicleId);
	    Log.d(TAG, "vehicleId=========" + vehicleId);
	    try {
		position = GetVehiclePosition.parseResult(GetVehiclePosition
			.getResult(key));
	    } catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	    } catch (JSONException e) {
		e.printStackTrace();
	    }
	    if (position != null) {
		handler.sendEmptyMessage(0x1234);
	    } else {
		handler.sendEmptyMessage(0x125);
	    }
	}

    };

    /**
     * 获取历史位置
     */
    private Runnable runls = new Runnable() {

	@Override
	public void run() {
	    Map<String, String> key = new HashMap<String, String>();
	    key.put("vehicleId", "" + vehicleId);
	    Log.d(TAG, "vehicleId=========" + vehicleId);
	    key.put("StartTime", startTimestr);
	    key.put("EndTime", endTimestr);
	    String detail = VehiclePostionByTime.getResult(key);
	    listPosi = VehiclePostionByTime.parseResult(detail);
	    if (listPosi != null && listPosi.size() != 0) {
		handler.sendEmptyMessage(0x4321);
	    } else {
		handler.sendEmptyMessage(0x125);
	    }
	}
    };

    @Override
    protected void onPause() {
	/**
	 * MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
	 */
	if (mapView != null) {
	    mapView.onPause();
	}
	if (mapViewls != null) {
	    mapViewls.onPause();
	}
	super.onPause();
    }

    @Override
    protected void onResume() {
	/**
	 * MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
	 */
	if (mapView != null) {
	    mapView.onResume();
	}
	if (mapViewls != null) {
	    mapViewls.onResume();
	}
	super.onResume();
    }

    @Override
    protected void onDestroy() {
	/**
	 * MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
	 */
	if (mapView != null) {
	    mapView.destroy();
	}
	if (mapViewls != null) {
	    mapViewls.destroy();
	}
	super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
	super.onSaveInstanceState(outState);
	if (mapView != null) {
	    mapView.onSaveInstanceState(outState);
	}
	if (mapViewls != null) {
	    mapViewls.onSaveInstanceState(outState);
	}
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
	super.onRestoreInstanceState(savedInstanceState);
	if (mapView != null)
	    mapView.onRestoreInstanceState(savedInstanceState);
	if (mapViewls != null)
	    mapViewls.onRestoreInstanceState(savedInstanceState);
    }

    @SuppressWarnings("rawtypes")
    public class MyOverlay extends ItemizedOverlay {

	public MyOverlay(Drawable defaultMarker, MapView mapView) {
	    super(defaultMarker, mapView);
	}

	@Override
	public boolean onTap(int index) {
	    return true;
	}
    }
}
