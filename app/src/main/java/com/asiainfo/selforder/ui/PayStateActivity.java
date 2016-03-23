package com.asiainfo.selforder.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.asiainfo.selforder.R;
import com.asiainfo.selforder.biz.order.OrderEntity;
import com.asiainfo.selforder.http.HttpHelper;
import com.asiainfo.selforder.model.Listener.OnTimeOutListener;
import com.asiainfo.selforder.model.MerchantRegister;
import com.asiainfo.selforder.model.net.ResultMap;
import com.asiainfo.selforder.model.net.UpdateOrderInfoResultData;
import com.asiainfo.selforder.ui.base.mBaseActivity;
import com.asiainfo.selforder.widget.TimeTextView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import kxlive.gjrlibrary.entity.eventbus.EventMain;
import kxlive.gjrlibrary.http.ResultMapRequest;
import kxlive.gjrlibrary.http.VolleyErrorHelper;
import kxlive.gjrlibrary.widget.LeafDialog.DialogDelayListener;
import roboguice.inject.InjectView;

/**
 * Created by gjr on 2015/12/17.
 */
public class PayStateActivity extends mBaseActivity {
    @InjectView(R.id.back_to_dishes)
    private Button back_to_dishes;
    @InjectView(R.id.TimeOut_num)
    private TimeTextView mTimeTextView;
    @InjectView(R.id.paystate_timeout)
    private LinearLayout TimeOutGroup;
    private OrderEntity orderEntity;
    private MerchantRegister merchantRegister;
    private MakeOrderDF mMakeOrderDF;
    private String payType="";
    private String orderId="0";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_paystate);
        EventBus.getDefault().register(this);
        payType=getIntent().getStringExtra("payType");
        orderId=getIntent().getStringExtra("orderId");
        orderEntity=(OrderEntity)mApp.getData(mApp.KEY_CURORDER_ENTITY);
        merchantRegister=(MerchantRegister)mApp.getData(mApp.KEY_GLOABLE_LOGININFO);
        showPrintDF();
        showDelay(new DialogDelayListener() {
            @Override
            public void onexecute() {
                VolleyPayOrder();
//                VolleyNotityPersistOrder();
            }
        },100);
        back_to_dishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventMain eventOrder =new EventMain();
                eventOrder.setType(EventMain.TYPE_FIRST);
                eventOrder.setDescribe("返回清空");
                eventOrder.setName(DishesMenuActivity.class.getName());
                EventBus.getDefault().post(eventOrder);
                getOperation().forward(DishesMenuActivity.class);
                getOperation().finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            EventMain eventOrder =new EventMain();
            eventOrder.setType(EventMain.TYPE_FIRST);
            eventOrder.setDescribe("返回清空");
            eventOrder.setName(DishesMenuActivity.class.getName());
            EventBus.getDefault().post(eventOrder);
            getOperation().forward(DishesMenuActivity.class);
            getOperation().finish();
            return  false;
        }
        return  super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 通知服务器打印保留订单
     */
    public void VolleyNotityPersistOrder() {
        String param = "/appController/updateOrderInfo.do?";
        System.out.println("NotityPersistOrder:" + HttpHelper.HOST + param);
        ResultMapRequest<UpdateOrderInfoResultData> ResultMapRequest = new ResultMapRequest<UpdateOrderInfoResultData>(
                Request.Method.POST, HttpHelper.HOST + param, UpdateOrderInfoResultData.class,
                new Response.Listener<UpdateOrderInfoResultData>() {
                    @Override
                    public void onResponse(
                            UpdateOrderInfoResultData response) {
                        if(response.getState()==1) {
                            dismissMakeOrderDF();
                        }
                        else if(response.getState()==0) {
                            dismissMakeOrderDF();
                            showShortTip("由于设备原因,后厨打单失败,请联系收银员!");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"VolleyError:" + error.getMessage(), error);
                onMakeOrderFailed(VolleyErrorHelper.getMessage(error, mActivity)+"->请点击确定重新打印!",NotityPersistOrderListener);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramList = new HashMap<String, String>();
                String inparam = gson.toJson(orderEntity.getNotifyOrderInfo());
                paramList.put("orderSubmitData", inparam);
                Log.i("VolleyLogTag", "paramList:" + paramList.toString());
                return paramList;
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type",
                        "application/x-www-form-urlencoded; charset=utf-8");
                return headers;
            }
        };
        executeRequest(ResultMapRequest);
    }

    /**
     * 打印
     */
    private void showPrintDF(){
        try {
            mMakeOrderDF = new MakeOrderDF();
            mMakeOrderDF.setNoticeText("正在打印...");
            if(mMakeOrderDF!=null&&!mMakeOrderDF.isAdded())
                mMakeOrderDF.show(getSupportFragmentManager(), "printOrder");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 提交失败处理
     */
    private void onMakeOrderFailed(String msg,DialogDelayListener mListener){
        Log.d(TAG, "msg:"+msg);
        try {
            if(mMakeOrderDF!=null&&mMakeOrderDF.isAdded()){
                mMakeOrderDF.updateNoticeText(msg,mListener);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void dismissMakeOrderDF(){
        try {
            if(mMakeOrderDF!=null&&mMakeOrderDF.isAdded()){
                mMakeOrderDF.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    DialogDelayListener NotityPersistOrderListener=new DialogDelayListener() {
        @Override
        public void onexecute() {
            showPrintDF();
            showDelay(new DialogDelayListener() {
                @Override
                public void onexecute() {
                    VolleyNotityPersistOrder();
                }
            },100);
        }
    };

    DialogDelayListener payOrder=new DialogDelayListener() {
        @Override
        public void onexecute() {
            showPrintDF();
            showDelay(new DialogDelayListener() {
                @Override
                public void onexecute() {
                    VolleyPayOrder();
                }
            },100);
        }
    };



    /**
     * 通知服务器打印保留订单
     */
    public void VolleyPayOrder() {
        String param = "/appController/payOrder.do?";
        System.out.println("payOrder:" + HttpHelper.HOST + param);
        Type type= new TypeToken<ResultMap<UpdateOrderInfoResultData>>(){}.getType();
        ResultMapRequest<ResultMap<UpdateOrderInfoResultData>> ResultMapRequest = new ResultMapRequest<ResultMap<UpdateOrderInfoResultData>>(
                Request.Method.POST, HttpHelper.HOST + param, type,
                new Response.Listener<ResultMap<UpdateOrderInfoResultData>>() {
                    @Override
                    public void onResponse(
                            ResultMap<UpdateOrderInfoResultData> response) {
                        if(response.getMsg().equals("ok")) {
                            dismissMakeOrderDF();
                            startTimeOutText();
                        }
                        else  {
                            dismissMakeOrderDF();
                            showShortTip("打印订单失败,请联系收银员确认!");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"VolleyError:" + error.getMessage(), error);
                onMakeOrderFailed(VolleyErrorHelper.getMessage(error, mActivity)+"->请点击确定重新打印!",payOrder);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramList = new HashMap<String, String>();
                paramList.put("orderId", orderId);
                paramList.put("payType", payType);//付费类型 orderPay里面的payType,如果是现金结账传0，微信支付传4，支付宝支付传5
//                int payPrice=Integer.valueOf(orderEntity.getOrderSubmitOriginalPrice())*100;
//                paramList.put("payPrice", payPrice+"");//支付金额
//                paramList.put("state", "1");//状态 0未支付 1已支付
//                paramList.put("tradeStaffId", merchantRegister.getStaffId());//操作员工
                paramList.put("merchantId", merchantRegister.getMerchantId());//商户ID
                paramList.put("childMerchantId", merchantRegister.getChildMerchantId());//子商户ID
                Log.i("VolleyLogTag", "paramList:" + paramList.toString());
                return paramList;
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type",
                        "application/x-www-form-urlencoded; charset=utf-8");
                return headers;
            }
        };
        executeRequest(ResultMapRequest);
    }

    /**
     * 启动倒计时控件
     */
    public void startTimeOutText(){
        int[] time = { 0, 0, 0, 9};
        TimeOutGroup.setVisibility(View.VISIBLE);
        mTimeTextView.setTimes(time);
        mTimeTextView.setOnTimeOutListener(new OnTimeOutListener() {
            @Override
            public void onTimeOut() {
                EventMain eventOrder =new EventMain();
                eventOrder.setType(EventMain.TYPE_FIRST);
                eventOrder.setDescribe("返回清空");
                eventOrder.setName(DishesMenuActivity.class.getName());
                EventBus.getDefault().post(eventOrder);
                getOperation().forward(DishesMenuActivity.class);
                getOperation().finish();
            }
        });
        if (!mTimeTextView.isRun()) {
            mTimeTextView.run();
        }
    }

}
