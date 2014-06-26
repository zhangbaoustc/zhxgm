package com.zhxg.zhxgm.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class TimeUtils {
	
	public static final String TIME_SERVER = "time-a.nist.gov";
	
	public TimeUtils() {
		super();
	}

	public static final String getCurrentNetworkTime() {
	    NTPUDPClient timeClient = new NTPUDPClient();
	    InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getByName(TIME_SERVER);
			TimeInfo timeInfo = timeClient.getTime(inetAddress);
			long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();   //server time

		    Date time = new Date(returnTime);
		    
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sdf.format(new Date(returnTime));
		    return date;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	    
	    //long returnTime = timeInfo.getReturnTime();   //local device time
	    
	    //Log.d(TAG, "Time from " + TIME_SERVER + ": " + time);

	    
	}
}
