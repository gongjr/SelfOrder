package com.asiainfo.selforder.model.net;


/**
 * ClassName: ResultMap 网络响应实体类模板
 * date:      2015年3月28日 下午15:36
 *
 * @author gjr
 * @param <T> 不同响应返回不同内容
 */
public class ResultMap<T> {
    public final String errcode_ok="0";
    public final String errcode_param_missing="31";
    public final String errcode_update_failed_all="6";

    private String errcode;
    private String msg;
    private T data;

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrcode() {
        return this.errcode;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setError(Errors error) {
        this.errcode = error.getCode();
        this.msg = error.getMsg();
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

