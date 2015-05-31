package com.folk113.incallease;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.folk113.incallease.socket.Data360;

public class ThreadAdpter extends android.widget.BaseAdapter implements Callback{
	class StructData
	{
		public int mPrefixNum;
		public ObtainDataThread mThread;
		public TextView mTv;
		public Handler mHandler; 
		public Data360 mData;
	}

	private ArrayList<StructData> mThreads = new ArrayList<StructData>();
	private ArrayList<Integer> mPrefixNums = new ArrayList<Integer>();
	private Context mContext;
	private SharedPreferences mPreference;
	public ThreadAdpter(Context context)
	{
		mContext = context;
		mPreference = mContext.getSharedPreferences("config", 0);
		try{
			Field fields[] = NumPrefixDefine.class.getFields();
			for(int i = 0; i < fields.length; i++)
			{
				int data = fields[i].getInt(null);
				mPrefixNums.add(data);
			}
			Collections.sort(mPrefixNums, new Comparator<Integer>(){
				@Override
				public int compare(Integer lhs, Integer rhs) {
					if(lhs > rhs)
						return 1;
					else if(lhs < rhs)
						return -1;
					return 0;
				}});
		}catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	
	public void increaseThread()
	{
		if(mPrefixNums.size()== 0)
			return;
		
		StructData data = new StructData();
		
		while(mPrefixNums.size() > 0){
			data.mPrefixNum = mPrefixNums.remove(0);
			int hasGeted = mPreference.getInt(data.mPrefixNum+"", 0);
			if(data.mPrefixNum == hasGeted)//已经获取过的号码段不再获取
			{
				if(mPrefixNums.size()== 0)
					return;
				continue;
			}
			else
			{
				break;
			}
		}
		
		data.mTv = new TextView(mContext);
		data.mHandler = new Handler(Looper.getMainLooper(),this);
		data.mThread = new ObtainDataThread(mContext, data);
		data.mThread.start();
		mThreads.add(data);
		notifyDataSetChanged();
	}
	
	public void decreaseThread()
	{
		if(mThreads.size() == 0)
			return;
		mThreads.remove(mThreads.size()-1).mThread.release();
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mThreads.size();
	}

	@Override
	public Object getItem(int position) {
		return mThreads.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return mThreads.get(position).mTv;
	}

	
	public static final int GET = 1000;
	public static final int INSERT_FAIL = 1001;
	public static final int INSERT_SUCCESS = 1002;
	public static final int THREAD_FINISH = 1003;
	
	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what)
		{
		case GET:
			StructData data = (StructData)msg.obj;
			data.mTv.setText("get "+data.mData.toString());
			break;
		case INSERT_FAIL:
			data = (StructData)msg.obj;
			data.mTv.setText("insert failed :"+data.mData.toString());
			break;
		case INSERT_SUCCESS:
			data = (StructData)msg.obj;
			data.mTv.setText("insert successful:"+data.mData.toString());
			break;
		case THREAD_FINISH:
			data = (StructData)msg.obj;
			data.mTv.setText("thread finished:"+data.mData.toString());
			mThreads.remove(data);
			Editor editor = mPreference.edit();
			editor.putInt(data.mPrefixNum+"", data.mPrefixNum);
			editor.commit();
			increaseThread();
		default:break;
		}
		notifyDataSetChanged();
		return true;
	}
}
