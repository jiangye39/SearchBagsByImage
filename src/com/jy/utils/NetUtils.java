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
	 * ����ͼƬ
	 * @param ͼƬ��ַ
	 * @return ͼƬ������
	 */
	public static InputStream download(String key) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(key)
					.openConnection();
			 Log.d(TAG, "��������ͼƬ= " + key);
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
	 * ʹ��post�ķ�ʽ����ͼƬ
	 * @param ������servlet·��
	 * @param ͼƬ·��
	 * @return
	 */
	public static String Post(String urlStr,String path,List<Item> itemList) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlStr);
			
			conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(10000); // ���ӵĳ�ʱʱ��
			conn.setReadTimeout(10000); // �����ݵĳ�ʱʱ��
			conn.setDoOutput(true);	// �������ô˷���, �������
//			conn.setRequestProperty("Content-Length", 234);		// ��������ͷ��Ϣ, �������ö��	
			
			// ��ͼƬ��������ʽ����
			FileInputStream in = null;			
		   try {
			   in=new FileInputStream(path);
			   byte[]  buffer = new byte[in.available()];
			   in.read(buffer);
				// ���һ�������, �����������д����, Ĭ�������, ϵͳ��������������������					
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
				//������������б�
				int count=ChangeItemslistUtil.changeItemList(result,itemList);
				return "ƥ�䵽�� "+count+" �����";
			} else {
				Log.i(TAG, "����ʧ��: " + responseCode);
				return "���������� "+responseCode;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "�޷����ӷ�����:  "+e.toString();
		} finally {
			if(conn != null) {
				conn.disconnect();
			}
		}
	}

	/**
	 * ʹ��get�ķ�ʽ��¼
	 * @param userName
	 * @param password
	 * @return ��¼��״̬
	 */
	public static String loginOfGet(String userName, String password) {
		HttpURLConnection conn = null;
		try {
			String data = "username=" + URLEncoder.encode(userName) + "&password=" + URLEncoder.encode(password);
			URL url = new URL("http://10.0.2.2:8080/ServerItheima28/servlet/LoginServlet?" + data);
			conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("GET");		// get����post�����ȫ��д
			conn.setConnectTimeout(10000); // ���ӵĳ�ʱʱ��
			conn.setReadTimeout(5000); // �����ݵĳ�ʱʱ��
			
			int responseCode = conn.getResponseCode();
			if(responseCode == 200) {
				InputStream is = conn.getInputStream();
				String state = getStringFromInputStream(is);
				return state;
			} else {
				Log.i(TAG, "����ʧ��: " + responseCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(conn != null) {
				conn.disconnect();		// �ر�����
			}
		}
		return null;
	}
	
	/**
	 * ����������һ���ַ�����Ϣ
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
		
		String data = baos.toString();	// �����е�����ת�����ַ���, ���õı�����: utf-8
		
//		String html = new String(baos.toByteArray(), "GBK");
		
		baos.close();
		return data;
	}
}
