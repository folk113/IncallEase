package com.folk113.incallease;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.folk113.incallease.ThreadAdpter.StructData;
import com.folk113.incallease.db.LocationDB;
import com.folk113.incallease.socket.Data360;

public class ObtainDataThread extends Thread {
	
	private Handler mHandler;
	private boolean mIsRunning = false;
	private LocationDB mDb;
	private int mPrefixNum = 0;
	private StructData mData;
	public ObtainDataThread(Context context,StructData data)
	{
		mDb = LocationDB.getInstance(context);
		mData = data;
		mHandler = mData.mHandler;
		mIsRunning = true;
		mPrefixNum = mData.mPrefixNum;
		setName("ObtainDataThread_"+mPrefixNum);
	}
	
	public void release()
	{
		mIsRunning = false;
	}
	
	private void get(int start)
	{
		int end = start + 9999;
		ArrayList<Data360> datas = new ArrayList<Data360>();
		for(int num = start; num <= end && mIsRunning; num++)
		{
			Data360 data = Data360.get(num);
			if(data != null && !TextUtils.isEmpty(data.province))
			{
				datas.add(data);
				Message msg = mHandler.obtainMessage(ThreadAdpter.GET);
				mData.mData = data;
				msg.obj = mData;
				mHandler.removeMessages(ThreadAdpter.GET);
				mHandler.sendMessage(msg);
			}
			
			if(datas.size() >= 100 || num == end)
			{
				boolean ret = mDb.insert(datas);
				datas.clear();
				if(ret)
				{
					Message msg = mHandler.obtainMessage(ret?ThreadAdpter.INSERT_SUCCESS:ThreadAdpter.INSERT_FAIL);
					mData.mData = data;
					msg.obj = mData;
					mHandler.sendMessage(msg);
				}
				else
				{
					Message msg = mHandler.obtainMessage(ThreadAdpter.INSERT_FAIL);
					mData.mData = data;
					msg.obj = mData;
					mHandler.sendMessage(msg);
				}
			}
		}
		if(mIsRunning)
		{
			Message msg = mHandler.obtainMessage(ThreadAdpter.THREAD_FINISH);
			msg.obj = mData;
			mHandler.sendMessage(msg);
		}
	}
	
	@Override
	public void run()
	{
		get(mPrefixNum);
	}
	
}
