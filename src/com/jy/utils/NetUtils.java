package com.jy.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import com.jy.model.Item;

import android.util.Log;

public class NetUtils {

	private static final String TAG = "NetUtils";
	
	/**
	 * 下载图片
	 * @param 图片地址
	 * @return 图片数据流
	 */
	public static InputStream download(String key) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(key)
					.openConnection();
			 Log.d(TAG, "正在下载图片= " + key);
			return conn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if(conn != null) {
				conn.disconnect();
			}
		}
	}
	/**
	 * 使用post的方式发送图片
	 * @param 服务器servlet路径
	 * @param 图片路径
	 * @return
	 */
	public static String Post(String urlStr,String path,List<Item> itemList) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlStr);
			
			conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(10000); // 连接的超时时间
			conn.setReadTimeout(10000); // 读数据的超时时间
			conn.setDoOutput(true);	// 必须设置此方法, 允许输出
//			conn.setRequestProperty("Content-Length", 234);		// 设置请求头消息, 可以设置多个	
			
			// 将图片以流的形式发送
			FileInputStream in = null;			
		   try {
			   in=new FileInputStream(path);
			   byte[]  buffer = new byte[in.available()];
			   in.read(buffer);
				// 获得一个输出流, 用于向服务器写数据, 默认情况下, 系统不允许向服务器输出内容					
				OutputStream outStream = conn.getOutputStream();	
				outStream.write(buffer);
				outStream.flush();
				in.close();
				outStream.close();
		   	} catch (Exception e) {
		   		e.printStackTrace();
		   	} finally {
		   		
		   }			
			int responseCode = conn.getResponseCode();
			if(responseCode == 200) {
				InputStream is = conn.getInputStream();
				String result = getStringFromInputStream(is);
				//更新搜索结果列表
				int count=ChangeItemslistUtil.changeItemList(result,itemList);
				return "匹配到： "+count+" 个结果";
			} else {
				Log.i(TAG, "访问失败: " + responseCode);
				return "服务器出错： "+responseCode;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "无法连接服务器:  "+e.toString();
		} finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
	}

	/**
	 * 使用get的方式登录
	 * @param userName
	 * @param password
	 * @return 登录的状态
	 */
	public static String loginOfGet(String userName, String password) {
		HttpURLConnection conn = null;
		try {
			String data = "username=" + URLEncoder.encode(userName) + "&password=" + URLEncoder.encode(password);
			URL url = new URL("http://10.0.2.2:8080/ServerItheima28/servlet/LoginServlet?" + data);
			conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("GET");		// get或者post必须得全大写
			conn.setConnectTimeout(10000); // 连接的超时时间
			conn.setReadTimeout(5000); // 读数据的超时时间
			
			int responseCode = conn.getResponseCode();
			if(responseCode == 200) {
				InputStream is = conn.getInputStream();
				String state = getStringFromInputStream(is);
				return state;
			} else {
				Log.i(TAG, "访问失败: " + responseCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect();		// 关闭连接
			}
		}
		return null;
	}
	
	/**
	 * 根据流返回一个字符串信息
	 * @param is
	 * @return
	 * @throws IOException 
	 */
	private static String getStringFromInputStream(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		
		while((len = is.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		is.close();
		
		String data = baos.toString();	// 把流中的数据转换成字符串, 采用的编码是: utf-8
		
//		String html = new String(baos.toByteArray(), "GBK");
		
		baos.close();
		return data;
	}
}
