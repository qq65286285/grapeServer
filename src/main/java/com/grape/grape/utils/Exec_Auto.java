package com.grape.grape.utils;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Tools.DataAndLog;
import com.grape.grape.utils.Tools.method;

public class Exec_Auto {

	/**
	 * @param args
	 */
	  static String[] cmd={
		  "adb devices",
		  " shell wm size",
		  " shell getevent -p",
		  " shell getevent"  
		  };
	private static String[] devices;	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			devices= method.dealMoreDevices(getCoor.execShell(cmd[0], 0,0,0,0,0));
			DataAndLog.log("获取到设备"+Arrays.toString(devices));
		try {
			ExecutorService pool=Executors.newFixedThreadPool(devices.length);
			for (int i = 0; i < devices.length; i++) {
				String[] cmd1={};
				for (int j = 0; j < cmd.length; j++) {
					cmd1=method.insert(cmd1, "adb -s "+devices[i]+cmd[j]);
				}
				pool.execute(new getCoor(cmd1,devices[i]));
			}
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			DataAndLog.log("暂未获取到设备，请连接设备重试...");
		}		
		}

}
