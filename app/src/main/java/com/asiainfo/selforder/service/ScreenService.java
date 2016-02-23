package com.asiainfo.selforder.service;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ScreenService extends Service {
	KeyguardManager mKeyguardManager = null;   
    private KeyguardLock mKeyguardLock = null;  
    BroadcastReceiver mMasterResetReciever;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	 @Override   
	    public void onCreate() { 
	    	 Log.e("ScreenService","onCreate()");
	    	startScreenService();
	        super.onCreate();   
	      }   
	    
     @Override   
     public void onStart(Intent intent, int startId) {  
    	Log.e("ScreenService","onStart");
    	 startScreenService();
      }
	     
     private void startScreenService(){
    	 mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);   
         mKeyguardLock = mKeyguardManager.newKeyguardLock("");//键盘锁

         mKeyguardLock.disableKeyguard();//解出系统键盘锁,禁掉系统的锁屏
         
         //监听Intent.ACTION_SCREEN_OFF广播消息,熄屏时触发屏保页面
         mMasterResetReciever = new BroadcastReceiver() {   
             @Override
				public void onReceive(Context context, Intent intent) {   
                 try { 
	                     Intent i = new Intent();   
	                     i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                    //根据Activity Affinity判断是否需要创建新的Task，然后再创建新的Activit实例放进去。
                     i.setClass(context, ScreenSaverActivity.class);
	                     context.startActivity(i);
                 } catch (Exception e) {                     	
                     Log.i("mMasterResetReciever:", e.toString());   
                 }   
             }   
         };           
       registerReceiver(mMasterResetReciever, new IntentFilter(Intent.ACTION_SCREEN_OFF));   
     }
     
     @Override
 	public void onDestroy() {
 		Log.e("ScreenService","onDestroy()");
 		super.onDestroy();
 		unregisterReceiver(mMasterResetReciever);  
        ScreenService.this.stopSelf();
     }
}
