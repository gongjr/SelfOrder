package com.asiainfo.selforder.model.net;

/**
 * Created by gjr on 2016/3/23.
 */
public class PayOrderResultData {
    public final String errcode_ok="0";
    public final String errcode_param_missing="31";
    public final String errcode_update_failed_all="6";
    private String errcode;
    private String msg;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
