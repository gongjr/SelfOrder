package com.asiainfo.selforder.model.net;

public class UpdateOrderInfoResultData {
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    private int state;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    String error;
}
