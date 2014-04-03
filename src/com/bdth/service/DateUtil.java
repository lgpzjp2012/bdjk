package com.bdth.service;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateUtil {
    @SuppressLint("SimpleDateFormat")
    public static Date parseStringToDate(String str) {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	try {
	    Date d = sdf.parse(str);
	    return d;
	} catch (ParseException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static XMLGregorianCalendar converToXMLGregorianCalendar(Date date) {
	GregorianCalendar cal = new GregorianCalendar();
	cal.setTime(date);
	XMLGregorianCalendar gc = null;
	try {
	    gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return gc;
    }

    public static Date converToDate(XMLGregorianCalendar gc) {

	GregorianCalendar ca = gc.toGregorianCalendar();

	return ca.getTime();

    }

    public static String GetTDateTime(String dateTime) {
	String valueString = "";
	if (null != dateTime && !"null".equals(dateTime)) {
	    String[] array = dateTime.split(" ");
	    if (array.length >= 2) {
		valueString = array[0] + "T" + array[1];
	    }
	}
	return valueString;
    }
    
    public static String GetTDateTime(String dateTime,boolean debug,String flag) {
	if(debug){
	    if(flag.equals("Start")){
		return "2013-11-30T16:00:00";
	    }else{
		return "2013-11-30T17:00:00";
	    }
	}else{
	    return "";
	}
    }
}
