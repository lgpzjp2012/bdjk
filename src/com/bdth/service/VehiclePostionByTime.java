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

import com.bdth.bean.Position;

public class VehiclePostionByTime {
    private static final String METHOD_NAME = "GetMoreVehicleLatestPositionByTime";

    private static String SOAP_ACTION = "http://tempuri.org/IService/GetMoreVehicleLatestPositionByTime";

    /**
     * 根据条件查询webService
     * 
     * @param vehicleId
     *            ,StartTime,EndTime
     * @return
     */
    public static String getResult(Map<String, String> key) {

	SoapObject rpc = new SoapObject(Configer.NAMESPACE, METHOD_NAME);
	rpc.addProperty("vehicleId", Integer.parseInt(key.get("vehicleId")));
	rpc.addProperty("StartTime", key.get("StartTime"));
	rpc.addProperty("EndTime", key.get("EndTime"));

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
     *             {"distance":11593.57,"msg":"OK", "rows":[{"Direction":303, //
     *             行车方向 "GroupId":0, // 组编号
     *             "ID":"52999d63d1c5e9ed22ffdafb2013-11-30161011", //
     *             "Lat":40.679025, // 纬度 "Lon":122.237713, // 经度 "PosType":4,
     *             // "ReceiptTime":"2013-11-30 16:10:11", //发送时间
     *             "SIM":"1302927260", "SatelliteTime":"2013-11-30 16:10:13",
     *             "Speed":20.9,//速度 "State":0, "VehicleInfo":null},
     *             {"Direction":277,"GroupId":0,"ID":
     *             "52999d68d1c5e9ed22ffdafc2013-11-30161016"
     *             ,"Lat":40.67909,"Lon"
     *             :122.237298,"PosType":4,"ReceiptTime":"2013-11-30 16:10:16"
     *             ,"SIM"
     *             :"1302927260","SatelliteTime":"2013-11-30 16:10:18","Speed"
     *             :31.4,"State":0,"VehicleInfo":null},
     *             {"Direction":276,"GroupId"
     *             :0,"ID":"52999d6ad1c5e9ed22ffdafe2013-11-30161018"
     *             ,"Lat":40.679116,"Lon":122.237077,"PosType":4,"ReceiptTime":
     *             "2013-11-30 16:10:18"
     *             ,"SIM":"1302927260","SatelliteTime":"2013-11-30 16:10:20"
     *             ,"Speed":35,"State":0,"VehicleInfo":null}],
     *             "status":true,"total":287}
     */
    public static List<Position> parseResult(String detail) {
	List<Position> list = null;
	if(detail == null){
	    return list;
	}
	try {
	    
	    JSONObject json = new JSONObject(detail);
	    JSONArray jsonArray = json.getJSONArray("rows");
	    if ("OK".equals(json.getString("msg"))) {
		list = new ArrayList<Position>();
		for (int i = 0; i < jsonArray.length(); i++) {
		    Position demo = new Position();
		    JSONObject jsonObj = jsonArray.getJSONObject(i);
		    demo.setDirection(jsonObj.getDouble("Direction"));
		    demo.setSpeed(jsonObj.getDouble("Speed"));
		    demo.setPosLat(jsonObj.getDouble("Lat"));
		    demo.setPosLon(jsonObj.getDouble("Lon"));
		    list.add(demo);
		}
	    }
	    return list;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }
}
