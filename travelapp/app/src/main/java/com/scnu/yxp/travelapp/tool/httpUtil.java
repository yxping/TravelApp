package com.scnu.yxp.travelapp.tool;

import java.io.*;
 import java.net.*;
import java.util.*;
public class httpUtil
{
	public static String sendGet(String url, String params)
	{
		String result = "";
		BufferedReader in = null;
		try
		{
			String urlName = url + "?" + params;
			URL realUrl = new URL(urlName);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.connect();  
			Map<String, List<String>> map = conn.getHeaderFields();
			for (String key : map.keySet())
			{
				System.out.println(key + "--->" + map.get(key));
			}
			in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
			{
				result += "\n" + line;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static String sendPost(String url, String params)
	{
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try
		{
			URL realUrl = new URL(url);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(conn.getOutputStream());
			out.print(params); 
			out.flush();
			in = new BufferedReader(
				new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
			{
				result += "\n" + line;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static String httpPost(String urlAddress, String[] params) {
		URL url = null;
		HttpURLConnection conn = null;
		BufferedReader in = null;
		StringBuffer sb = new StringBuffer();

		try {
			url = new URL(urlAddress);
			conn = (HttpURLConnection) url.openConnection(); // 建立连接
			// 设置通用的请求属性
			/*
			 * conn.setRequestProperty("user-agent",
			 * "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			 */
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			conn.setUseCaches(false);
			conn.setDoInput(true);
			// conn.setConnectTimeout(5 * 1000);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			String paramsTemp = "";
			for (String param : params) {
				if (param != null && !"".equals(param)) {
					if (params.length > 1) {
						paramsTemp += "&" + param;
					} else if (params.length == 1) {
						paramsTemp = params[0];
					}
				}
			}

			byte[] b = paramsTemp.getBytes();
			System.out.println("btye length:" + b.length);
			// conn.setRequestProperty("Content-Length",
			// String.valueOf(b.length));
			conn.getOutputStream().write(b, 0, b.length);
			conn.getOutputStream().flush();
			conn.getOutputStream().close();
			int count = conn.getResponseCode();
			if (200 == count) {
				in = new BufferedReader(new InputStreamReader(
						conn.getInputStream())); // 发送请求
			} else {
				System.out.println("错误类型：" + count);
				return "server no start-up";
			}

			while (true) {
				String line = in.readLine();
				if (line == null) {
					break;
				} else {
					sb.append(line);
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("error ioexception:" + e.getMessage());
			e.printStackTrace();
			return "server no start-up";
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (conn != null) {
					conn.disconnect();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return sb.toString();
	}
}
