package com.bdth.service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bdth.bean.VehiclePosition;

public class GetVehiclePosition {
    private static final String METHOD_NAME = "GetVehicleLatestPosition";

    private static final String SOAP_ACTION = "http://tempuri.org/IService/GetVehicleLatestPosition";

    /**
     * 根据条件查询webService
     * 
     * @param vehicleId
     * @return
     */
    public static String getResult(Map<String, String> key) {

	SoapObject rpc = new SoapObject(Configer.NAMESPACE, METHOD_NAME);
	rpc.addProperty("vehicleId", Integer.parseInt(key.get("vehicleId")));
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
     *             {"data":{"Direction":0, "GroupId":0,
     *             "ID":"5320058995c77b88136cd48b2014-03-12145817",
     *             "Lat":32.454474, "Lon":119.89002, "PosType":1,
     *             "ReceiptTime":"2014-03-12 14:58:17", "SIM":"139459",
     *             "SatelliteTime":"2014-03-12 00:10:20", "Speed":0, "State":0,
     *             "VehicleInfo":{"Comment":"null", "GroupID":28,
     *             "GroupName":"a1", "Icon":"BD", "Id":308,
     *             "JoinTime":"2012-03-14 00:00:00", "Model":"null",
     *             "PlateNumber":"139459", "SpleedLimit":90, "State":1,
     *             "Type":"测试"} },"msg":"OK","status":true}
     */
    public static VehiclePosition parseResult(String detail)
	    throws UnsupportedEncodingException, JSONException {
	VehiclePosition u = null;
	if (detail == null)
	    return u;
	u = new VehiclePosition();
	JSONObject json = new JSONObject(detail);
	JSONObject data = json.getJSONObject("data");

	u.setId(data.getString("ID"));
	u.setLon(data.getString("Lon"));
	u.setLat(data.getString("Lat"));
	u.setSpeed(data.getLong("Speed"));
	return u;
    }
}
