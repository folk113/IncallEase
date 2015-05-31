package com.folk113.incallease.db;

import java.util.ArrayList;

import com.folk113.incallease.socket.Data360;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

public class LocationDB extends SQLiteOpenHelper{
	public static class Columns
	{
		public static String NUM = "num";
		public static String PROVINCE = "province";
		public static String CITY = "city";
		public static String AREA_CODE="area_code";
		public static String POST_CODE="post_code";
	}
	
	public static class DbData
	{
		public String num;
		public String province;
		public String city;
		public String area_code;
		public String post_code;
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			if(!TextUtils.isEmpty(num))
				sb.append("num:").append(num).append("\n");
			if(!TextUtils.isEmpty(province))
				sb.append("province:").append(province).append("\n");
			if(!TextUtils.isEmpty(city))
				sb.append("city:").append(city).append("\n");
			if(!TextUtils.isEmpty(area_code))
				sb.append("area_code:").append(area_code).append("\n");
			if(!TextUtils.isEmpty(post_code))
				sb.append("post_code:").append(post_code).append("\n");
			return sb.toString();
		}
	}
	
	private static final String DB_NAME = "/sdcard/incall_ease/db.db";
	private static final String TABLE_LOCATION="location";
	private static final int VERSION = 1;
	private SQLiteDatabase mWritableDb,mReadableDb;
	private static LocationDB mInstance;
	private LocationDB(Context context) {
		super(context, DB_NAME, null, VERSION);
		
		mWritableDb = getWritableDatabase();
		mReadableDb = getReadableDatabase();
	}
	public synchronized static LocationDB getInstance(Context context)
	{
		if(mInstance == null)
			mInstance = new LocationDB(context);
		return mInstance;
	}
	
	public static LocationDB getInstance()
	{
		return mInstance;
	}
	
	public boolean insert(ArrayList<Data360> datas)
	{
		if(datas == null || datas.size() == 0)
			return false;
		Data360 data = datas.remove(0);
		
		StringBuilder sb = new StringBuilder();
		sb.append("insert into ").append(TABLE_LOCATION);
		sb.append("(").append(Columns.NUM).append(",");
		sb.append(Columns.PROVINCE).append(",");
		sb.append(Columns.CITY).append(") ");
		sb.append(" select '").append(data.num).append("','").append(data.province).append("','").append(data.city).append("'");
		
		for(int i =0; i < datas.size(); i++)
		{
			sb.append(" union all select '").append(datas.get(i).num).append("','").append(datas.get(i).province).append("','").append(datas.get(i).city).append("'");
		}
		synchronized(mWritableDb){
			mWritableDb.beginTransaction();
			try{
				mWritableDb.execSQL(sb.toString());
				mWritableDb.setTransactionSuccessful();
			}catch(Throwable e)
			{
				e.printStackTrace();
				return false;
			}finally
			{
				mWritableDb.endTransaction();
			}
		}
		return true;
	}
	
	public boolean insert(String num,String prov,String city)
	{
		mWritableDb.beginTransaction();
		try{
			ContentValues values = new ContentValues();
			values.put(Columns.NUM, num);
			values.put(Columns.PROVINCE, prov);
			values.put(Columns.CITY, city);
			long ret = mWritableDb.insert(TABLE_LOCATION, null, values);
			mWritableDb.setTransactionSuccessful();
			return ret >=0 ;
		}catch(Throwable e)
		{
			e.printStackTrace();
		}
		finally{
			mWritableDb.endTransaction();
		}
		return false;
	}
	
	/**
	 * ����7λ��
	 * @param num
	 * @return
	 */
	public DbData getData(String num)
	{
		Cursor cursor = null;
		try{
			 cursor = mReadableDb.query(TABLE_LOCATION, new String[]{Columns.NUM,Columns.PROVINCE,Columns.CITY,Columns.AREA_CODE,Columns.POST_CODE},
					Columns.NUM+"=?", new String[]{num}, null, null, null);
			if(cursor.moveToLast())
			{
				DbData db = new DbData();
				db.num = cursor.getString(cursor.getColumnIndex(Columns.NUM));
				db.province = cursor.getString(cursor.getColumnIndex(Columns.PROVINCE));
				db.city = cursor.getString(cursor.getColumnIndex(Columns.CITY));
				db.area_code = cursor.getString(cursor.getColumnIndex(Columns.AREA_CODE));
				db.post_code = cursor.getString(cursor.getColumnIndex(Columns.POST_CODE));
				return db;
			}
		}catch(Throwable e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(cursor != null)
				cursor.close();
		}
		
		return null;
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder("create table if not exists ").append(TABLE_LOCATION).append("(");
		sb.append(Columns.NUM).append(" varchar2(20) not null,");
		sb.append(Columns.PROVINCE).append(" varchar2(50),");
		sb.append(Columns.CITY).append(" varchar2(50),");
		sb.append(Columns.AREA_CODE).append(" varchar2(10),");
		sb.append(Columns.POST_CODE).append(" varchar2(10),");
		sb.append("CONSTRAINT [] PRIMARY KEY (").append(Columns.NUM).append("))");
		db.execSQL(sb.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
