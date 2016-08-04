package com.asiainfo.selforder.http;

import android.content.Context;
import android.content.SharedPreferences;

import com.asiainfo.selforder.model.AddressState;

/**
 *
 * 2015年6月17日
 */
public class HttpHelper {
	
    /**
     * 默认服务器配置,程序初始化确认的地址,恢复的初始状态
     */
    public static final AddressState init=AddressState.debug;
    /**
     * 当前服务器环境值,如果配置,保有最新选择
     */
    public static AddressState Address=init;

    /**
     * 使用地址内容
     */
    public static  String HOST = Address.getKey();

    /**
     * 微信相关默认服务器配置,程序初始化确认的地址,恢复的初始状态
     */
    public static AddressState wxInit=AddressState.wxRelease;
    /**
     * 微信相关使用地址
     */
    public static String HOST_WBusiunion = wxInit.getKey();

    private static HttpHelper httpHelper;

    public final String sharedPreferenceAddressKey="Address";

    /**
     * AppKey 服务器约定app更新key字段
     */
    public static final String AppKey = "com.asiainfo.selforder";


    public static HttpHelper newInstance() {
        if (httpHelper == null) {
            httpHelper = new HttpHelper();
        }
        return httpHelper;
    }

    public boolean isInit() {
        if (HOST.equals(init.getKey())) {
            return true;
        }
        return false;
    }

    public AddressState getAddress() {
        return this.Address;
    }

    public void setAddress(AddressState addressState) {
        this.Address = addressState;
        this.HOST = addressState.getKey();
    }

    public void setWXAddress(AddressState addressState) {
        this.wxInit = addressState;
        this.HOST_WBusiunion = addressState.getKey();
    }

    /**
     * 保存当前服务器环境到配置文件
     */
    public void saveAddress(Context mContext,AddressState pAddress){
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(AppKey, mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(sharedPreferenceAddressKey, pAddress.getValue());
        editor.apply();
    }

    public boolean toInitAddress(Context mContext) {
        Boolean isInit=false;
        if (Address.getValue().equals(init.getValue()))isInit=true;
        setAddress(init);
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(AppKey, mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear().apply();
        return isInit;
    }
    
}
