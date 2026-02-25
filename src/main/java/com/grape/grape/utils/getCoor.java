package com.grape.grape.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Tools.DataAndLog;
import com.grape.grape.utils.Tools.method;

public class getCoor implements Runnable{
 
 private static String comd=" shell input swipe ";
	/**
	 * @param args
	 */	
 String[] cmd={};
 String devices;

static String location = "无效"; //更具奇偶数判断是 终点还是 起点
 public getCoor(String [] cmd,String devices)
 {
	 this.cmd=cmd;
	 this.devices=devices;
 }
	public void run()
	{
		synchronized (this) {
					
			 int RBW_W = 0;
			 int RBW_H = 0;
			 int Max_widht = 0;
			 int Max_height = 0;
			 String[] Arry_Coor={};
			 String[] Arry_Coor1={};  
			// int RBW_W = 0,RBW_H = 0,Max_widht = 0,Max_height = 0;
			for (int i = 0; i < cmd.length; i++) {
//				DataAndLog.log("cmd.length:"+cmd.length+"  de:"+devices+"  "+i);
			     switch (i) {
					case 1:
						String RBW=execShell(cmd[i], i,RBW_W,RBW_H,Max_widht,Max_height).split(" ")[2];
						RBW_W=Integer.parseInt(RBW.split("x")[0]);
						RBW_H=Integer.parseInt(RBW.split("x")[1]);
						DataAndLog.log("RBW_W:"+RBW_W+"  RBW_H:"+RBW_H);
						break;
					case 2:
						String w=execShell(cmd[i], i,RBW_W,RBW_H,Max_widht,Max_height);
						Max_widht=Integer.parseInt(w.split(",")[0]);
						Max_height=Integer.parseInt(w.split(",")[1]);
						DataAndLog.log("Max_widht:"+Max_widht+"  Max_height:"+Max_height);
						break;
					case 3:
						int id=0;
						while (id<=2) {	
		//					DataAndLog.log("开始获取坐标");
							 location="请开始 "+devices+"  的 ---起点---坐标";
							 String coor1=execShell(cmd[i], i,RBW_W,RBW_H,Max_widht,Max_height);			
							 int W1=Integer.parseInt(coor1.split(",")[0]);
							 int H1=Integer.parseInt(coor1.split(",")[1]);
							 location="请开始 "+devices+"  的 ---终点---坐标";
							 String coor2=execShell(cmd[i], i,RBW_W,RBW_H,Max_widht,Max_height);			
							 int W2=Integer.parseInt(coor2.split(",")[0]);
							 int H2=Integer.parseInt(coor2.split(",")[1]);			
							 DataAndLog.log("coor:"+W1+"  "+H1+"  "+W2+"  "+H2);
							 					 
							 String mess=W1+"/"+H1;
							 Arry_Coor= method.insert(Arry_Coor, mess);
												 						 
							 String mess2=W2+"/"+H2;
							 Arry_Coor1=method.insert(Arry_Coor1, mess2);
							 id++;
						}
						break;				
				}
			}
			while(true)
			{	
				for (int j = 0; j < Arry_Coor.length; j++) {
					DataAndLog.log("Arry_Coor："+devices+" "+Arry_Coor[j]);
				}
				for (int j = 0; j < Arry_Coor1.length; j++) {
					DataAndLog.log("Arry_Coor1："+devices+" "+Arry_Coor1[j]);
				}
				String[] time=method.retrunConfig();
				Random ran=new Random();
				int time_new=ran.nextInt(time.length);
				int x1=Integer.parseInt(Arry_Coor[ran.nextInt(Arry_Coor.length)].split("/")[0]);
				int y1=Integer.parseInt(Arry_Coor[ran.nextInt(Arry_Coor.length)].split("/")[1]);
				int x2=Integer.parseInt(Arry_Coor1[ran.nextInt(Arry_Coor1.length)].split("/")[0]);
				int y2=Integer.parseInt(Arry_Coor1[ran.nextInt(Arry_Coor1.length)].split("/")[1]);
				try {
					String new_comd="adb -s "+devices+comd+x1+" "+y1+" "+x2+" "+y2;
					DataAndLog.log("执行while:"+new_comd+"  time:"+time[time_new]);				
					Runtime.getRuntime().exec(new_comd);
						Thread.sleep(1000*Integer.parseInt(time[time_new]));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
	}	
	public static String execShell(String cmd,int id,int RBW_W,int RBW_H,int Max_widht,int Max_height)
	{
		Process process = null;
		BufferedReader succResult = null;
		BufferedReader errResult = null;
		StringBuffer succMsg=new StringBuffer();
		StringBuffer result=new StringBuffer();
		StringBuffer errMsg=new StringBuffer();
		DataOutputStream out = null;
		String mes; 
		
		try {
			DataAndLog.log(cmd);
			if (id>=3) {
				DataAndLog.log(location);
			}
			process = Runtime.getRuntime().exec(cmd);
			out=new DataOutputStream(process.getOutputStream());
			out.flush();
			succResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
			errResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));				
			while((mes=succResult.readLine())!=null)
			{	
				   succMsg.append(mes+" ");
				   if (id==3 || id==2) {
				   Pattern p=Pattern.compile("0036");
				   Matcher m=p.matcher(method.dealSpace(succMsg.toString()));
				   if(m.find()) {					   	
					   process.destroy();  	
				    }
				   }
//				   DataAndLog.log(" "+mes);
			}
			while((mes=errResult.readLine())!=null)
			{
				errMsg.append(mes+" ");
			}			
//			DataAndLog.log("getWebsocket>>> succMsg:"+succMsg.toString()+"  errMsg:"+errMsg.toString());
			if (id==0) {
				result.append(succMsg.toString());
			}
			if (id==1) {
				result.append(succMsg.toString());
			}
			if (id==2) {
//				DataAndLog.log("id==2:"+succMsg.toString());
				String x = null,y = null;
				try {
					if (succMsg.toString()!=null && succMsg.toString()!=" ") {				
						for (int j = 0; j < method.dealSpace(succMsg.toString()).split(" ").length; j++) {
							if (method.dealSpace(succMsg.toString()).split(" ")[j].equals("0035")) {
								x=method.dealSpace(succMsg.toString()).split(" ")[j+7];
							}
							else if(method.dealSpace(succMsg.toString()).split(" ")[j].equals("0036")) {
							   y=method.dealSpace(succMsg.toString()).split(" ")[j+7];	
							}
						}
						DataAndLog.log("X_W:"+x+"  Y_H:"+y);
						result.append(x);
						result.append(y);
					}
				} catch (NullPointerException e) {
					// TODO: handle exception
					DataAndLog.log("请连接你的设备，或授权usb连接"+e);
				}
			}
			if (id==3) {
			String x = null,y = null;
			try {
//				DataAndLog.log("id==3:"+succMsg.toString());
				if (succMsg.toString()!=null && succMsg.toString()!=" ") {				
					for (int j = 0; j < method.dealSpace(succMsg.toString()).split(" ").length; j++) {
						if (method.dealSpace(succMsg.toString()).split(" ")[j].equals("0035")) {
							x=method.dealSpace(succMsg.toString()).split(" ")[j+1];
						}
						else if(method.dealSpace(succMsg.toString()).split(" ")[j].equals("0036")) {
						   y=method.dealSpace(succMsg.toString()).split(" ")[j+1];	
						}
					}
					DataAndLog.log("X:"+x+"  Y:"+y +"  坐标X："+Integer.parseInt(x, 16)*RBW_W/Max_widht+"  坐标y："+Integer.parseInt(y, 16)*RBW_H/Max_height);
					result.append(Integer.parseInt(x, 16)*RBW_W/Max_widht);
					result.append(","+Integer.parseInt(y, 16)*RBW_H/Max_height);
				}
			} catch (NullPointerException e) {
				// TODO: handle exception
				DataAndLog.log("请连接你的设备，或授权usb连接"+e);
			}
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
		 return result.toString();
	}
}
