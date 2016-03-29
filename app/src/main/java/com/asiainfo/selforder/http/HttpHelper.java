package com.asiainfo.selforder.http;

/**
 *
 * 2015年6月17日
 */
public class HttpHelper {
	
	/**
	 * 生产验证环境
	 */
	public static final String Address_debug = "http://115.29.35.199:29890/tacos";
	
	/**
	 * 生产环境
	 */
	public static final String Address_release = "http://115.29.35.199:27890/tacos";
	
	/**
     * 测试环境
     */
    public static final String Address_tst = "http://139.129.35.66:30080/tacos";
    /**
     * 本地局域网内测试,对应IP
     */
    public static final String local_test="http://192.168.1.109:8080/tacos";
    /**
     * 使用地址
     */
    public static final String HOST = Address_release;


    /**
     * 微信相关测试环境
     */
    public static final String Busiunion_tst = "http://www.kxlive.com/busiunion_tst";
    /**
     * 微信相关生产环境
     */
    public static final String Busiunion = "http://www.kxlive.com/busiunion";
    /**
     * 微信相关使用地址
     */
    public static final String HOST_WBusiunion = Busiunion_tst;
    
}
