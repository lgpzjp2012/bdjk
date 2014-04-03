package com.bdth.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bdth.bean.Vehicle;

public class GetVehicleList {
    private static final String METHOD_NAME = "GetVehicleList";

    private static final String SOAP_ACTION = "http://tempuri.org/IService/GetVehicleList";

    /**
     * 根据条件查询webService
     * 
     * @param 参数
     *            userID
     * @return
     */
    public static String getResult(Map<String, String> key) {

	SoapObject rpc = new SoapObject(Configer.NAMESPACE, METHOD_NAME);
	rpc.addProperty("userID", Integer.parseInt(key.get("userID")));
	// rpc.addProperty("keyWords", "");
	// 被排序的列
	rpc.addProperty("position", Integer.parseInt(key.get("position")));
	// 排序方式 1顺序 -1倒序
	rpc.addProperty("direction", Integer.parseInt(key.get("direction")));
	// 每页条数
	rpc.addProperty("pageSize", Integer.parseInt(key.get("pageSize")));
	// 显示第几页
	rpc.addProperty("pageNo", Integer.parseInt(key.get("pageNo")));
	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
		SoapEnvelope.VER11);
	envelope.bodyOut = rpc;
	envelope.dotNet = true;
	HttpTransportSE ht = new HttpTransportSE(Configer.URL);

	try {
	    // 调用Web Service
	    ht.call(SOAP_ACTION, envelope);
	    if (envelope.getResponse() != null) {
		SoapPrimitive sp = (SoapPrimitive) envelope.getResponse();
		String detail = sp.toString();
		return detail;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * 根据条件查询webService
     * 
     * @param 参数
     *            userID , pageSize
     * @return
     */
    public static String getAllResult(Map<String, String> key) {

	SoapObject rpc = new SoapObject(Configer.NAMESPACE, METHOD_NAME);
	rpc.addProperty("userID", Integer.parseInt(key.get("userID")));
	// rpc.addProperty("keyWords", "");
	// 被排序的列
	rpc.addProperty("position", Integer.parseInt(key.get("position")));
	// 排序方式 1顺序 -1倒序
	rpc.addProperty("direction", Integer.parseInt(key.get("direction")));
	// 每页条数
	rpc.addProperty("pageSize", Integer.parseInt(key.get("pageSize")));
	// 显示第几页
	rpc.addProperty("pageNo", Integer.parseInt(key.get("pageNo")));
	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
		SoapEnvelope.VER11);
	envelope.bodyOut = rpc;
	envelope.dotNet = true;
	HttpTransportSE ht = new HttpTransportSE(Configer.URL);

	try {
	    // 调用Web Service
	    ht.call(SOAP_ACTION, envelope);
	    if (envelope.getResponse() != null) {
		SoapPrimitive sp = (SoapPrimitive) envelope.getResponse();
		String detail = sp.toString();
		return detail;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * 解析返回结果
     * 
     * @param detail
     * @return
     * @throws UnsupportedEncodingException
     * @throws JSONException
     *             {"Comment":"", "GroupId":28, "GroupName":"a1", "Icon":"BD",
     *             "Id":308, "JoinTime":"2014-03-12 14:59:57", "Model":"",
     *             "PlateNumber":"139459", "SIM":"139459", "SpeedLimit":90,
     *             "State":1, "Type":"测试"}
     */
    public static List<Vehicle> parseResult(String detail) {
	List<Vehicle> list = null;
	if (detail == null) {
	    return list;
	}
	try {

	    JSONObject json = new JSONObject(detail);
	    JSONArray jsonArray = json.getJSONArray("rows");
	    if ("OK".equals(json.getString("msg"))) {
		list = new ArrayList<Vehicle>();
		for (int i = 0; i < jsonArray.length(); i++) {
		    Vehicle demo = new Vehicle();
		    JSONObject jsonObj = jsonArray.getJSONObject(i);
		    demo.setId(jsonObj.getInt("Id"));
		    demo.setComment(jsonObj.getString("Comment"));
		    demo.setGroupId(jsonObj.getInt("GroupId"));
		    demo.setGroupName(jsonObj.getString("GroupName"));
		    demo.setIcon(jsonObj.getString("Icon"));
		    demo.setJoinTime(DateUtil.parseStringToDate(jsonObj
			    .getString("JoinTime")));
		    demo.setModel(jsonObj.getString("Model"));
		    demo.setPlateNumber(jsonObj.getString("PlateNumber"));
		    demo.setSim(jsonObj.getString("SIM"));
		    demo.setSpeedLimit(jsonObj.getInt("SpeedLimit"));
		    demo.setState(jsonObj.getInt("State"));
		    demo.setType(jsonObj.getString("Type"));
		    list.add(demo);
		}
	    }
	    return list;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * 获取总共多少条记录
     * 
     * @param detail
     * @return
     */
    public static int getListSize(String detail) {
	JSONObject json;
	if (detail == null) {
	    return 0;
	}
	try {
	    json = new JSONObject(detail);
	    int count = json.getInt("total");
	    return count;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return 0;
    }
}
