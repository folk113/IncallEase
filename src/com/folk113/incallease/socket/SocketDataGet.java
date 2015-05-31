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
			HttpGet httpRequest = new HttpGet(url);// ����http get����
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);// ����http����
			if (httpResponse.getStatusLine().getStatusCode() == 200)
			{
				return EntityUtils.toString(httpResponse.getEntity(),"utf-8");// ��ȡ��Ӧ���ַ���
			}
		}
		catch(Throwable e)
		{
			Log.e(TAG, e.getMessage());
		}
		return "";
	}
}
