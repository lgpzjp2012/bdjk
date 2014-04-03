package com.bdth.bdjk;

public class StringUtil {
    public static String NULL = "数据为空，暂无数据！";
    public static String LAT = "119.937378";
    public static String LON = "32.478879";
    public static String utilNull(String str){
	if(str == null){
	    return NULL;
	}
	if(str.equals("")){
	    return NULL;
	}
	if(str.equals("null")){
	    return NULL;
	}
	return str;
    }
    
    public static String utilLat(String lat){
	if(lat == null){
	    return LAT;
	}
	if(lat.equals("")){
	    return LAT;
	}
	if(lat.equals("null")){
	    return LAT;
	}
	return lat;
    }
    
    public static String utilLon(String lon){
	if(lon == null){
	    return LON;
	}
	if(lon.equals("")){
	    return LON;
	}
	if(lon.equals("null")){
	    return LON;
	}
	return lon;
    }
}
