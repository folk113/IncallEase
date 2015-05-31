package com.folk113.incallease.socket;

import org.json.JSONException;
import org.json.JSONObject;
import android.text.TextUtils;

public class Data360 {
	public String num;
	public String province;
	public String city;
	public String sp;
	
	public static Data360 get(int num)
	{
		String url = "http://cx.shouji.360.cn/phonearea.php?number="+num;
		String json = SocketDataGet.get(url);
		return ParseJson(num+"",json);
	}
	private static Data360 ParseJson(String num,String jsonString)
	{
		if(TextUtils.isEmpty(jsonString))
			return null;
		try {
			Data360 data = new Data360();
			data.num = num;
			JSONObject jb = new JSONObject(jsonString);
			JSONObject jb1 = (JSONObject)jb.get("data");
			data.province = (String) jb1.get("province");
			data.city = (String) jb1.get("city");
			data.sp = (String) jb1.get("sp");
			return data;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if(!TextUtils.isEmpty(num))
			sb.append("num=").append(num).append("\n");
		if(!TextUtils.isEmpty(province))
			sb.append("province=").append(province).append("\n");
		if(!TextUtils.isEmpty(city))
			sb.append("city=").append(city).append("\n");
		if(!TextUtils.isEmpty(sp))
			sb.append("sp=").append(sp).append("\n");
		return sb.toString();
	}
}

