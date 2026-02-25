package com.grape.grape.utils.Tools;

import Tools.DataAndLog;
import cn.hutool.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class method {	
	
	public static String dealSpace(String s)
	  {
		  StringBuffer buffer=new StringBuffer();
		  int count=0;
		  for (int i = 0; i < s.length(); i++) {
			char at=s.charAt(i);
			//DataAndLog.log("comm>>>:"+at);
			if (at ==' '){			
				if (count<1  && at!=0) {
					buffer.append(" ");
				}
			count++;
			}else {
				count=0;
				buffer.append(at);
				
			}
		}
		  return buffer.toString();
	  }
	public static String [] insert(String [] arr,String mess)
	{
		String[] arr_new=new String[arr.length+1];
		for (int i = 0; i < arr.length; i++) {
			arr_new[i]=arr[i];			
		}
//		DataAndLog.log("arr_new:"+arr_new.length);
		arr_new[arr_new.length-1]=mess;
		return arr_new;
	}
	public static  String [] dealMoreDevices(String mess)
	{
		String [] arry={};
		StringBuffer result=new StringBuffer();
		String new_mess = null;
		Pattern pat=Pattern.compile("attached");
		Matcher mat=pat.matcher(mess.toString());
		if (mat.find()) {
			new_mess=method.dealSpace((mess.substring(mess.indexOf("attached"), mess.length())).substring(9,mess.substring(mess.indexOf("attached"), mess.length()).length()));	
		}
		Pattern pat1=Pattern.compile("successfully *");
		Matcher mat1=pat1.matcher(mess.toString());
		if (mat1.find()) {
			new_mess=method.dealSpace((mess.substring(mess.indexOf("successfully *"), mess.length())).substring(15,mess.substring(mess.indexOf("successfully *"), mess.length()).length()));	
		}		
		String[] new_mess1=new_mess.split("device");		
		for (int i = 0; i < new_mess1.length-1; i++) {
			result.delete(0, result.length());
			for (int j = 0; j < new_mess1[i].length(); j++) {
				char current=new_mess1[i].charAt(j);
				if ((current>='A' && current<='Z') || (current>='a' && current<='z') || (current>='0' && current<='9') || current=='.' || current==':') {					
					result.append(current);				
				}				
			}
			arry=insert(arry, result.toString());		
		}
		
		return arry;
	}
	public static String[] retrunConfig()
	{
//		String FilePath=System.getProperty("user.dir");	
		String FilePath=System.getProperty("user.dir").substring(0, System.getProperty("user.dir").indexOf("run_java")-1);
//		DataAndLog.log("  FilePath:"+FilePath);
		File file=new File(FilePath+"/AutoTime.txt");
		String [] result={};
		if (file.exists()) {
           try {
         	  FileInputStream in=new FileInputStream(file);
         	  InputStreamReader ins=new InputStreamReader(in,"GBK");
				BufferedReader bufR=new BufferedReader(ins);				
				String line;
				while ((line=bufR.readLine()) !=null) {	
//					DataAndLog.log("retrunConfig输出："+line+"  "+line.indexOf(name));
//					DataAndLog.log("retrunConfig截取："+line.substring(5));
					String []line2=line.split(":"); //处理多余的==
					for (String value : line2[1].split(",")) {
						result = method.insert(result, value);
					}
					

				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			DataAndLog.log("配置文件不存在，请重试....");
		}
		DataAndLog.log("配置信息=="+Arrays.toString(result));
		return result;
	}
//	public static void main(String [] args)
//	{
//		retrunConfig();
//		for (String value : retrunConfig()) {
//			DataAndLog.log("配置信息=="+value);
//		}
//	}
}
