package com.bdth.service;

import java.util.Map;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bdth.bean.Vehicle;

public class GetVehicle {
    private static final String METHOD_NAME = "GetVehicleInfo";

    private static final String SOAP_ACTION = "http://tempuri.org/IService/GetVehicleInfo";

    /**
     * 根据条件查询webService
     * 
     * @param 参数
     *            userID
     * @return
     */
    public static String getResult(Map<String, String> key) {

	SoapObject rpc = new SoapObject(Configer.NAMESPACE, METHOD_NAME);
	rpc.addProperty("vehicleID", Integer.parseInt(key.get("vehicleId")));
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
     * {"data":{"Comment":"null", "GroupID":3, "GroupName":"未分组", "Icon":"1",
     * "Id":268, "JoinTime":"\/Date(-1235030400000+0800)\/", "Model":"null",
     * "PlateNumber":"1302927260", "SpleedLimit":50, "State":1, "Type":"测试设备"},
     * "msg":"OK", "status":true}
     */
    public static Vehicle parseResult(String detail) {
	Vehicle demo = null;
	if (detail == null)
	    return demo;
	try {
	    JSONObject jsonObj = new JSONObject(detail);
	    JSONObject data = jsonObj.getJSONObject("data");
	    if ("OK".equals(jsonObj.getString("msg"))) {
		demo = new Vehicle();
		demo.setId(data.getInt("Id"));
		demo.setComment(data.getString("Comment"));
		demo.setGroupId(data.getInt("GroupID"));
		demo.setGroupName(data.getString("GroupName"));
		demo.setIcon(data.getString("Icon"));
		demo.setModel(data.getString("Model"));
		demo.setPlateNumber(data.getString("PlateNumber"));
		demo.setSpeedLimit(data.getInt("SpleedLimit"));
		demo.setState(data.getInt("State"));
		demo.setType(data.getString("Type"));
	    }
	    return demo;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }
}
