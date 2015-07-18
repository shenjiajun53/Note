package com.tct.note.util;

import android.util.Log;

public class TctLog {
	static String sIsDebug=System.getProperty("ro.config.jrd.log", "1");
    public static void e(String Tag,String mes)
    {
        if(sIsDebug.equalsIgnoreCase("1")) {
        	Log.e(Tag,mes);
        }
    }
    public static void e(String Tag,String mes, Exception e)
    {
        if(sIsDebug.equalsIgnoreCase("1")) {
        	Log.e(Tag,mes);
        	e.printStackTrace();
        }
    }
    public static void i(String Tag,String mes)
    {
        if(sIsDebug.equalsIgnoreCase("1")) {
        	Log.i(Tag,mes);
        } 
    }
    public static void d(String Tag,String mes)
    {
        if(sIsDebug.equalsIgnoreCase("1")) {
        	Log.d(Tag,mes);
        }   
    }
    public static void d(String Tag,String mes, Exception e)
    {
        if(sIsDebug.equalsIgnoreCase("1")) {
        	Log.d(Tag,mes);
        	e.printStackTrace();
        }  
    }
    public static void v(String Tag,String mes)
    {
        if(sIsDebug.equalsIgnoreCase("1")) {
        	Log.v(Tag,mes);
        }   
    }
}
