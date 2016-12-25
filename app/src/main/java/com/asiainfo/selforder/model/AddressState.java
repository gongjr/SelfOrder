package com.asiainfo.selforder.model;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 16/7/25 上午10:34
 */
public enum AddressState {

    debug("Address_debug", "验证环境", "http://115.29.35.199:29890/tacos"),
    release("Address_release", "生产环境", "http://115.29.35.199:27890/tacos"),
    tst("Address_tst","测试环境","http://139.129.35.66:30080/tacos"),
    localtest("Address_localtest","本地环境","http://192.168.1.101:8080/tacos"),
    baina("Address_baina","百纳环境","http://115.29.35.199:22890/tacos"),
    wxTst("Busiunion_tst", "微信相关测试环境", "http://www.kxlive.com/busiunion_tst"),
    wxRelease("Busiunion", "微信相关生产环境(微信项目部署不支持,地址+端口直接访问)", "http://www.kxlive.com/busiunion");


    private String value;
    private String title;
    private String key;

    AddressState(String value, String title, String key) {
        this.setValue(value);
        this.setTitle(title);
        this.setKey(key);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
