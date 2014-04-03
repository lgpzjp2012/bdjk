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

import com.bdth.bean.User;

public class UserLogin {

    private static final String METHOD_NAME = "UserLogin";

    private static String SOAP_ACTION = "http://tempuri.org/IService/UserLogin";

    /**
     * 根据条件查询webService
     * 
     * @param name
     *            ,password
     * @return
     */
    public static String getResult(Map<String, String> key) {

	SoapObject rpc = new SoapObject(Configer.NAMESPACE, METHOD_NAME);
	rpc.addProperty("name", key.get("name"));
	rpc.addProperty("password", key.get("password"));
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
     *             {"data":{"CorporationId":2,"CorporationName":"北斗天汇（泰州）",
     *             "Realname"
     *             :"bdth_tz","Role":0,"UserId":2},"msg":"OK","status":true}
     */
    public static User parseResult(String detail)
	    throws UnsupportedEncodingException, JSONException {
	User u = null;
	if (detail == null) {
	    return u;
	}
	JSONObject json = new JSONObject(detail);
	JSONObject data = json.getJSONObject("data");
	if ("OK".equals(json.getString("msg"))) {
	    u = new User();
	    u.setId(data.getInt("UserId"));
	    u.setCorporationId(data.getInt("CorporationId"));
	    u.setCorporationName(data.getString("CorporationName"));
	    u.setRealName(data.getString("Realname"));
	    u.setRole(data.getInt("Role"));
	}
	return u;
    }

}
