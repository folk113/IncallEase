package com.folk113.incallease.socket;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class SocketDataGet {
	private static final String TAG = "SocketDataGet";
	public static String get(String url)
	{
		try{
			HttpGet httpRequest = new HttpGet(url);// 建立http get联机
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);// 发出http请求
			if (httpResponse.getStatusLine().getStatusCode() == 200)
			{
				return EntityUtils.toString(httpResponse.getEntity(),"utf-8");// 获取相应的字符串
			}
		}
		catch(Throwable e)
		{
			Log.e(TAG, e.getMessage());
		}
		return "";
	}
}
