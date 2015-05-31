package com.folk113.incallease;

import java.lang.reflect.Method;
import java.util.ArrayList;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.telephony.ITelephony;
import com.folk113.incallease.db.LocationDB;
import com.folk113.incallease.db.LocationDB.DbData;

public class PhoneStatReceiver extends BroadcastReceiver{  
	  
    String TAG = "PhoneStatReceiver";  
    TelephonyManager telMgr;  
    @Override  
    public void onReceive(Context context, Intent intent) {  
        telMgr = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);  
        switch (telMgr.getCallState()) {  
            case TelephonyManager.CALL_STATE_RINGING:  
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);   
                Log.v(TAG,"number:"+number);  
                if (!getPhoneNum(context).contains(number)) {  
                	if(number.length() > 7)
                	{
                		number = number.substring(0, 7);
                	}
                	Log.d(TAG, "num:"+number);
                   DbData data = LocationDB.getInstance(context).getData(number);
                   Log.d(TAG, "DbData:"+data.toString());
//                   if(data != null && !TextUtils.isEmpty(data.city) && data.city.trim().equals("深圳"))
                   if(data != null && !TextUtils.isEmpty(data.province) && data.province.trim().equals("福建"))
                	   endCall(); 
                }
                break;  
            case TelephonyManager.CALL_STATE_OFFHOOK:                                 
                break;  
            case TelephonyManager.CALL_STATE_IDLE:                                 
                break;  
        }  
          
    }  
    private void endCall()  
    {  
        Class<TelephonyManager> c = TelephonyManager.class;           
        try  
        {  
            Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);  
            getITelephonyMethod.setAccessible(true);  
            ITelephony iTelephony = null;  
            Log.e(TAG, "End call.");  
            iTelephony = (ITelephony) getITelephonyMethod.invoke(telMgr, (Object[]) null);  
            iTelephony.endCall();  
        }  
        catch (Exception e)  
        {  
            Log.e(TAG, "Fail to answer ring call.", e);  
        }          
    }  
    private ArrayList<String>  getPhoneNum(Context context) {  
        ArrayList<String> numList = new ArrayList<String>();  
        ContentResolver cr = context.getContentResolver();       
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);     
        while (cursor.moveToNext())
        {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));     
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,  
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);     
            while (phone.moveToNext())     
            {     
                String strPhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));     
                numList.add(strPhoneNumber);    
                Log.v("tag","strPhoneNumber:"+strPhoneNumber);  
            }
            phone.close();     
        }
        cursor.close();  
        return numList;  
    }  
}  
