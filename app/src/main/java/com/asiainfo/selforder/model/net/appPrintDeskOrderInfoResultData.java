package com.asiainfo.selforder.model.net;

public class appPrintDeskOrderInfoResultData {
    public int getState() {
        return status;
    }

    public void setState(int state) {
        this.status = state;
    }

    private int status;

    public String getError() {
        return info;
    }

    public void setError(String error) {
        this.info = error;
    }

    String info;
}
