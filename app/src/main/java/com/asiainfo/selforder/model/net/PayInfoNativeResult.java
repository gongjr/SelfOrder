package com.asiainfo.selforder.model.net;

/**
 * Created by gjr on 2015/12/15.
 */
public class PayInfoNativeResult<T> {
    String state;
    String msg;
    T body;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
