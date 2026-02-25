package com.grape.grape.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.sql.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Tools.DataAndLog;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.grape.grape.utils.Tools.method;

import static com.grape.grape.utils.Tools.method.dealSpace;


public class Adb_wifi_Connect {

	/**
	 * 日志记录器
	 */
	private static final Logger logger = LogManager.getLogger(Adb_wifi_Connect.class);

	/**
	 * @param args
	 */
	 static String[] cmd={
			 " shell ip -f inet addr show wlan0",
			 " tcpip 5555",
			 " connect"
	};
	private static String[] devices={};
	static String ipAddres;
	static String comd;
	private static JSONObject jsonObject = new JSONObject();

	//数据库连接信息
	private static String URL;
	private static String USER;
	private static String PASSWORD;
	private static Connection connection = null;
	private static PreparedStatement preparedStatement = null;
	static long currentTimestamp = System.currentTimeMillis();

	// 静态初始化块，从配置文件中读取数据库连接信息
	static {
		loadDatabaseConfig();
	}

	/**
	 * 从application.yml配置文件中加载数据库连接信息
	 */
	private static void loadDatabaseConfig() {
		Properties properties = new Properties();
		try (InputStream input = Adb_wifi_Connect.class.getClassLoader().getResourceAsStream("application.yml")) {
			if (input != null) {
				// 读取配置文件内容
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				String line;
				boolean inDatasource = false;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					
					// 检查是否进入datasource配置节
					if (line.startsWith("datasource:")) {
						inDatasource = true;
					} else if (inDatasource && line.startsWith("url:")) {
						// 提取完整的URL，包括数据库名和参数
						URL = line.substring(line.indexOf(':') + 1).trim();
					} else if (inDatasource && line.startsWith("username:")) {
						USER = line.substring(line.indexOf(':') + 1).trim();
					} else if (inDatasource && line.startsWith("password:")) {
						PASSWORD = line.substring(line.indexOf(':') + 1).trim();
					} else if (inDatasource && !line.isEmpty() && !line.startsWith("#") && !line.endsWith(":")) {
						// 检查是否退出datasource配置节
						if (!line.startsWith("type:") && !line.startsWith("driverClassName:") && 
						    !line.startsWith("url:") && !line.startsWith("username:") && !line.startsWith("password:")) {
							inDatasource = false;
						}
					}
				}
				logger.info("从配置文件加载数据库连接信息成功：");
				logger.info("URL: " + URL);
				logger.info("USER: " + USER);
				logger.info("PASSWORD: " + PASSWORD);
			} else {
				logger.warn("无法找到application.yml配置文件，使用默认值");
				// 使用默认值
				URL = "jdbc:mysql://192.168.22.140:3306/grape?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8";
				USER = "root";
				PASSWORD = "root";
			}
		} catch (Exception e) {
			logger.error("加载数据库配置失败，使用默认值：" + e.getMessage(), e);
			// 使用默认值
			URL = "jdbc:mysql://192.168.22.140:3306/grape?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8";
			USER = "root";
			PASSWORD = "root";
		}
	}

	public static void adb_connect()
	{
		devices= method.dealMoreDevices(shell("adb devices",0));
		logger.info("设备:"+ Arrays.toString(devices));
		logger.info("设备长度:"+devices.length);
//		getDevicesMessage(devices[0]);
		//无线连接
		for (int j = 0; j < devices.length; j++) {

			String deviceId = devices[j];
			if (!deviceId.contains(":") || !deviceId.contains(".")) {
				// USB设备：执行WiFi连接设置
				logger.info("执行USB设备:"+deviceId);
				for (int i = 0; i < cmd.length; i++) {
					if (i==2) {
						comd="adb "+cmd[i]+" "+ipAddres;
						logger.info(comd);
						shell(comd, i);
						//写入设备信息
						jsonObject.set("devices_serial_number", deviceId);
						jsonObject.set("wifi_address", ipAddres);
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							logger.error("线程休眠异常", e);
						}
						// 使用IP:5555作为设备ID（默认端口）
						String wifiDeviceId = ipAddres + ":5555";
						getDevicesMessage(wifiDeviceId);
						wirteSql(deviceId);
					}
					else {
						comd="adb -s "+deviceId+cmd[i];
						shell(comd, i);
					}
				}
			} else {
				// IP连接设备：直接处理
				logger.info("执行IP设备:"+deviceId);
				// 提取IP地址（不含端口）
				String ipAddress = deviceId.split(":")[0];
				// 使用完整的设备ID获取设备信息
				getDevicesMessage(deviceId);
				// 对于IP设备，我们需要通过WiFi地址查找现有的设备记录
				// 不应该使用IP地址作为设备序列号
				updateDeviceByWifiAddress(ipAddress, deviceId);
			}
		}
	}

	/**
	 * 通过WiFi地址更新设备信息
	 * 用于处理IP连接的设备
	 */
	private static void updateDeviceByWifiAddress(String wifiAddress, String deviceId) {
		// SQL 查询语句：通过WiFi地址查找设备
		String find_by_wifi = "select devices_serial_number from grape.device_info where wifi_address = ? limit 1";
		// 更新数据
		String update_sql = "update grape.device_info set updated_time=?, is_online=1 where wifi_address = ?";
		
		try {
			// 获取数据库连接
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			logger.info("数据库连接成功！");
			
			// 查找设备
			preparedStatement = connection.prepareStatement(find_by_wifi);
			preparedStatement.setString(1, wifiAddress);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				// 设备存在，更新状态
				String serialNumber = resultSet.getString("devices_serial_number");
				logger.info("通过WiFi地址找到设备: " + serialNumber);
				
				preparedStatement = connection.prepareStatement(update_sql);
				preparedStatement.setString(1, String.valueOf(System.currentTimeMillis()));
				preparedStatement.setString(2, wifiAddress);
				int rows = preparedStatement.executeUpdate();
				
				if (rows > 0) {
					logger.info("设备状态更新成功: " + serialNumber);
				} else {
					logger.info("设备状态更新失败: " + serialNumber);
				}
			} else {
				// 设备不存在，创建新记录
				logger.info("未找到WiFi地址对应的设备，创建新记录: " + wifiAddress);
				logger.info("IP设备ID: " + deviceId);
				
				// 为IP设备生成一个合理的序列号
				// 使用"IP_"前缀加上IP地址的简化版本
				String generatedSerial = "IP_" + wifiAddress.replace(".", "_");
				logger.info("为IP设备生成序列号: " + generatedSerial);
				
				// 写入数据库
				jsonObject.set("devices_serial_number", generatedSerial);
				jsonObject.set("wifi_address", wifiAddress);
				wirteSql(generatedSerial);
			}
			
		} catch (SQLException e) {
			logger.error("数据库操作失败！", e);
		} finally {
			// 关闭资源
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
					logger.info("数据库连接已关闭");
				}
			} catch (SQLException e) {
				logger.error("关闭数据库资源失败", e);
			}
		}
	}
	private static String shell(String comd,int id)
	 {
				Process process = null;
				BufferedReader succResult = null;
				BufferedReader errResult = null;
				StringBuffer succMsg=new StringBuffer();
				StringBuffer errMsg=new StringBuffer();
				DataOutputStream out = null;
				String mes;
				try {
					//System.out.println(cmd);
					logger.info("执行命令:"+comd);
					process = Runtime.getRuntime().exec(comd);
					process.waitFor();
					out=new DataOutputStream(process.getOutputStream());
					out.flush();
					succResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
					errResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
					while((mes=succResult.readLine())!=null)
					{
						   succMsg.append(mes+" ");
					}
					while((mes=errResult.readLine())!=null)
					{
						errMsg.append(mes+" ");
					}
//					System.out.println("getWebsocket>>> succMsg:"+succMsg.toString()+"  errMsg:"+errMsg.toString());
					if (id==0) {
				ipAddres= dealSpace(succMsg.toString().substring(succMsg.indexOf("inet")+5, succMsg.indexOf("/")));
				logger.info("ip地址:"+ipAddres);
			}

			}
			catch (Exception e) {
					// TODO: handle exception
				}
//                System.out.println("succMsg:"+succMsg.toString());
		return succMsg.toString();

	 }
	 private static void  getDevicesMessage(String devicesName){
		String [] devicesMessage={
//			"devices_serial_number_wifi= shell getprop ro.serialno",
			"resolution=  shell wm size",
			"devices_name= shell settings get global device_name",
			"devices_manufacturer= shell getprop ro.product.manufacturer",
			"devices_brand= shell getprop ro.product.brand",
			"android_version= shell getprop ro.build.version.release",
			"cpu_info= shell cat /proc/cpuinfo",
			"memory_info= shell cat /proc/meminfo",
		};
	 for (String data:devicesMessage) {
//		 logger.info("执行： "+"adb -s "+devicesName +data.split("=")[1]);
		    logger.info("执行： "+"adb -s "+devicesName +data.split("=")[1]);
		    logger.info(JSONUtil.toJsonStr(data));
		String messgae = shell("adb -s "+devicesName + data.split("=")[1],999999);
		logger.info("message:"+messgae);
		    logger.info("Message: " + messgae );
		    if (data.contains("cpu_info"))
		{
			String [] cpuinfo =messgae.split("processor\t:");
			jsonObject.set(data.split("=")[0], cpuinfo.length - 1);
		}
		else if (data.contains("memory_info"))
			{
				String Regx_MemTotal = "MemTotal:\\s+(\\d+)\\s+kB" ;
				Pattern pattern = Pattern.compile(Regx_MemTotal);
				Matcher matcher = pattern.matcher(messgae);

				String Regx_MemAvailable = "MemAvailable:\\s+(\\d+)\\s+kB" ;
				Pattern pattern1 = Pattern.compile(Regx_MemAvailable);
				Matcher matcher1 = pattern1.matcher(messgae);

				// 查找匹配项
				String MemTotal_number = "0";
				String MemAvailable_number ="0";

				if (matcher.find()) {
					// 返回第一个捕获组的内容（即11473784 kB）
					MemTotal_number = matcher.group(1);
				}
				if (matcher1.find()) {
					// 返回第一个捕获组的内容（即11473784 kB）
					MemAvailable_number = matcher1.group(1);
				}


				jsonObject.put(data.split("=")[0],"总存储："+Integer.parseInt(MemTotal_number)/1024+"MB"+" 可用存储："+Integer.parseInt(MemAvailable_number)/1024+"MB");
			}
		else if(data.contains("resolution")){
			jsonObject.set(data.split("=")[0],messgae.split(": ")[1]);
		}
		else
		{
//			DataAndLog.log("信息 :"+data);
			jsonObject.set(data.split("=")[0],messgae);
		}

	 }
	 logger.info("json： "+jsonObject.toString());
	}
	 private static void wirteSql(String devices)
	 {

		 String field="devices_serial_number,wifi_address,devices_name,devices_manufacturer,devices_brand,android_version,cpu_info,memory_info,resolution,is_online,created_time,updated_time";

		 // SQL 插入语句
		 String insert_sql = "INSERT INTO grape.device_info ("+field+") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		 //查询设备是否存在
		 String devices_is_exits = "select devices_serial_number from grape.device_info where devices_serial_number =? limit 1";

		 //更新数据
		 String Update_sql ="update grape.device_info set wifi_address=? ,updated_time=?, is_online=? where devices_serial_number = ?";

		 //下线所有设备
		 String Update_is_online ="update grape.device_info set is_online=?";
		 logger.info("devices_is_exits： "+devices_is_exits);

		 try {

			 // 2. 获取数据库连接
			 logger.info("正在连接数据库...");
			 connection =  DriverManager.getConnection(URL, USER, PASSWORD);
			 logger.info("数据库连接成功！");

			 preparedStatement = connection.prepareStatement(devices_is_exits);
			 preparedStatement.setString(1,devices);

			 ResultSet resultSet = preparedStatement.executeQuery();
			 if (resultSet.next())
			 {
				 //更新数据
			  logger.info("存在数据");
				 preparedStatement = connection.prepareStatement(Update_sql);
				 preparedStatement.setString(1,jsonObject.get("wifi_address").toString());
				 preparedStatement.setString(2,String.valueOf(currentTimestamp));
				 preparedStatement.setString(3,String.valueOf(1));
				 preparedStatement.setString(4,devices);
				 int rows = preparedStatement.executeUpdate();
				 // 4. 处理结果
				 if (rows > 0) {
					 logger.info("数据更新成功,设备信息："+devices);
				 } else {
					 logger.info("数据更新失败,设备信息："+devices);
				 }

			 }
			 else
			 {
				 logger.info("不存在数据");
				 preparedStatement = connection.prepareStatement(insert_sql);
				 for (int i = 0; i < field.split(",").length; i++) {

					 if (i>=field.split(",").length-2)
					 {
						 preparedStatement.setString(i+1,String.valueOf(currentTimestamp));
					 }
					 else if(field.split(",")[i].contains("is_online")){
						 preparedStatement.setString(i+1,String.valueOf(1));
					 }
					 else
					 {
						 preparedStatement.setString(i+1,jsonObject.getStr(field.split(",")[i]));
					 }
				 }

				 int affectedRows = preparedStatement.executeUpdate();

				 // 4. 处理结果
				 if (affectedRows > 0) {
					 logger.info("数据插入成功！新设备 '" + devices + "' 已添加到数据库。");
				 } else {
					 logger.info("数据插入失败。");
				 }
			 }

		 } catch (SQLException e) {
			 logger.error("数据库操作失败！", e);
		 } finally {
			 // 7. 关闭资源 (非常重要！)
			 // 关闭顺序：先关闭 Statement，再关闭 Connection
			 try {
				 if (preparedStatement != null) {
					 preparedStatement.close();
				 }
				 if (connection != null) {
					 connection.close();
					 logger.info("数据库连接已关闭。");
				 }
			 } catch (SQLException e) {
				 logger.error("关闭数据库资源失败", e);
			 }
		 }

	 }
	 public static void set_online_devices()
	 {
		 devices=method.dealMoreDevices(shell("adb devices",0));
		 logger.info("设备:"+ Arrays.toString(devices));
		 
		 // 2. 获取数据库连接
		 try {
			 connection =  DriverManager.getConnection(URL, USER, PASSWORD);
			 logger.info("数据库连接成功！");
			 
			 // 步骤1：先将所有设备标记为离线
			 String Update_all_offline ="update grape.device_info set is_online=0";
			 preparedStatement = connection.prepareStatement(Update_all_offline);
			 int rows_offline = preparedStatement.executeUpdate();
			 logger.info("所有设备已标记为离线，影响行数："+rows_offline);
			 
			 // 步骤2：将当前在线的设备标记为在线
			 String Update_is_online ="update grape.device_info set is_online=1 where wifi_address = ? or devices_serial_number = ?";
			 for (String devices_id:devices) {
				 if (devices_id.contains(":") || devices_id.contains(".")) {
					 // 对于WiFi连接的设备，使用IP地址匹配
					 String ipAddress = devices_id.split(":")[0];
					 preparedStatement = connection.prepareStatement(Update_is_online);
					 preparedStatement.setString(1, ipAddress);
					 preparedStatement.setString(2, ipAddress);
					 int rows_online = preparedStatement.executeUpdate();
					 // 4. 处理结果
					 if (rows_online > 0) {
						 logger.info("设备设置上线成功,设备信息："+devices_id);
					 } else {
						 logger.info("设备设置上线失败,设备信息："+devices_id);
					 }
				 } else {
					 // 对于USB连接的设备，使用序列号匹配
					 preparedStatement = connection.prepareStatement(Update_is_online);
					 preparedStatement.setString(1, devices_id);
					 preparedStatement.setString(2, devices_id);
					 int rows_online = preparedStatement.executeUpdate();
					 if (rows_online > 0) {
						 logger.info("设备设置上线成功,设备信息："+devices_id);
					 } else {
						 logger.info("设备设置上线失败,设备信息："+devices_id);
					 }
				 }
			 }

		 } catch (SQLException e) {
			 logger.error("数据库操作失败！", e);
		 }finally {
			 // 7. 关闭资源 (非常重要！)
			 // 关闭顺序：先关闭 Statement，再关闭 Connection
			 try {
				 if (preparedStatement != null) {
					 preparedStatement.close();
				 }
				 if (connection != null) {
					 connection.close();
					 logger.info("数据库连接已关闭");
				 }
			 } catch (SQLException e) {
				 logger.error("关闭数据库资源失败", e);
			 }
		 }

	 }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Adb_wifi_Connect.adb_connect();
		Adb_wifi_Connect.set_online_devices();
	}
}

