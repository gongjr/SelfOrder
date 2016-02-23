package com.asiainfo.selforder.service;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.asiainfo.selforder.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import kxlive.gjrlibrary.utils.KLog;


/**
 * 锁屏保护页面,需要调用mWakeLock,是页面保持唤醒不锁屏
 */
public class ScreenSaverActivity extends Activity {
    private ImageView screenSaverImage;
	protected static final String TAG = "ScreenSaverActivity";
	private static PowerManager.WakeLock mWakeLock;
    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; //自己定义标志
    private WindowManager mWindowManager;
    private LayoutParams mWindowManagerParams;
    private View mEmptyView;
    private String ImageUrl="http://115.29.35.199:27890/material_img/images/upload/20000531/screensaver.png";
    /**
     * 菜单显示图片的加载参数
     * 默认是ARGB_8888， 使用RGB_565会比使用ARGB_8888少消耗2倍的内存，但是这样会没有透明度
     */
    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(false)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY)
            .build();

    //	private PlayControl mPlayControl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键屏蔽home代码
        setContentView(R.layout.activity_screen_saver);
        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | 
	  			PowerManager.SCREEN_DIM_WAKE_LOCK | 
	  			PowerManager.ON_AFTER_RELEASE, "SimpleTimer");
//        forbiddenHomeKey();
        screenSaverImage=(ImageView)findViewById(R.id.main_ad_background);
        ImageLoader.getInstance().displayImage(ImageUrl, screenSaverImage,
                    options, mImageLoadingListener);
	}
	
	@Override
	protected void onResume() {
        try {
		mWakeLock.acquire();//设置保持唤醒,获取WakeLock实例后通过acquire()获取相应的锁
        }catch (Exception e){
            e.printStackTrace();
        }
		super.onResume();
	}

	@Override
	protected void onPause() {
        try {
            mWakeLock.release();//解除保持唤醒
        }catch (Exception e){
            e.printStackTrace();
        }
		super.onPause();
	}

    /**
     * 响应按键事件结束屏保
     * @param keyCode
     * @param event
     * @return
     */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_MENU) {
            this.finish();
            this.overridePendingTransition(0, R.anim.base_slide_right_out);
	    } else if(keyCode == KeyEvent.KEYCODE_HOME) {
            Log.i("onKeyDown","KeyEvent.KEYCODE_HOME");
            return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
        this.finish();
        this.overridePendingTransition(0, R.anim.base_slide_right_out);
		return super.onTouchEvent(event);
	}

	//部分可以触发快捷调用系统内部搜索组件的,需要响应结束屏保
    @Override  
    public boolean onSearchRequested() {  
	     this.finish();
         this.overridePendingTransition(0, R.anim.base_slide_right_out);
	     return super.onSearchRequested();  
    }

    private void forbiddenHomeKey(){
        mWindowManager = this.getWindowManager();
        mWindowManagerParams = new LayoutParams();
        mWindowManagerParams.width = 100;
        mWindowManagerParams.height = 100;
        // internal system error windows, appear on top of everything they can
        mWindowManagerParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
        // indicate this view don’t respond the touch event
        mWindowManagerParams.flags = LayoutParams.FLAG_NOT_TOUCHABLE;
        // add an empty view on the top of the window
        mEmptyView = new View(this);
        mWindowManager.addView(mEmptyView, mWindowManagerParams);
    }

    ImageLoadingListener mImageLoadingListener = new ImageLoadingListener() {
        @Override
        public void onLoadingStarted(String arg0, View arg1) {

        }

        @Override
        public void onLoadingFailed(String arg0, View img, FailReason failReason) {
            if(img!=null){
                switch (failReason.getType()) {
                    case IO_ERROR:
                        KLog.i("图片加载IO异常");
                        //服务器下载异常,修改缓存中数据,避免重复请求
                        break;
                    case DECODING_ERROR:
                        KLog.i("图片加载编码异常");
                        break;
                    case NETWORK_DENIED:
                        KLog.i("图片加载网络异常");
                        break;
                    case OUT_OF_MEMORY:
                        KLog.i("图片加载内存溢出");
                        break;
                    case UNKNOWN:
                        KLog.i("图片加载未知异常");
                        //服务器地址无效,修改缓存中数据,避免重复请求
                        break;
                    default:
                        break;
                }
            }else{
                KLog.i("视图已销毁,忽视!");
            }
        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
        }

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
        }
    };

}
