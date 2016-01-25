package com.asiainfo.selforder.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.asiainfo.selforder.R;
import com.asiainfo.selforder.biz.order.OrderEntity;
import com.asiainfo.selforder.http.HttpHelper;
import com.asiainfo.selforder.model.MerchantRegister;
import com.asiainfo.selforder.model.net.appPrintDeskOrderInfoResultData;
import com.asiainfo.selforder.ui.base.mBaseActivity;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import kxlive.gjrlibrary.entity.eventbus.EventMain;
import kxlive.gjrlibrary.http.ResultMapRequest;
import kxlive.gjrlibrary.http.VolleyErrorHelper;
import kxlive.gjrlibrary.utils.KLog;
import kxlive.gjrlibrary.widget.LeafDialog.DialogDelayListener;
import roboguice.inject.InjectView;

/**
 * Created by gjr on 2015/12/17.
 */
public class PayXianjinActivity extends mBaseActivity {
    @InjectView(R.id.pay_type)
    private TextView pay_type;
    @InjectView(R.id.pay_info)
    private TextView pay_info;
    @InjectView(R.id.pay_hold_title)
    private TextView pay_hold_title;
    @InjectView(R.id.pay_wait_title)
    private TextView pay_wait_title;
    @InjectView(R.id.back_to_dishes)
    private Button back_to_dishes;
    private OrderEntity orderEntity;
    private MerchantRegister merchantRegister;
    private MakeOrderDF mMakeOrderDF;
    private String orderId="0";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_paystate);
        EventBus.getDefault().register(this);
        orderId=getIntent().getStringExtra("orderId");
        pay_type.setText("请您拿着小票");
        pay_info.setText("到收银台买单");
        pay_info.setVisibility(View.VISIBLE);
        pay_hold_title.setVisibility(View.GONE);
        pay_wait_title.setVisibility(View.GONE);
        orderEntity=(OrderEntity)mApp.getData(mApp.KEY_CURORDER_ENTITY);
        merchantRegister=(MerchantRegister)mApp.getData(mApp.KEY_GLOABLE_LOGININFO);
        showPrintDF();
        showDelay(new DialogDelayListener() {
            @Override
            public void onexecute() {
                VolleyNotityPersistOrder();
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
     * 通知服务器打印保留订单的客单
     */
    public void VolleyNotityPersistOrder() {
        String param = "/appController/appPrintDeskOrderInfo.do?childMerchantId="+merchantRegister.getChildMerchantId()+"&orderId="+orderId;
        KLog.i("URL:"+HttpHelper.HOST + param);
        ResultMapRequest<appPrintDeskOrderInfoResultData> ResultMapRequest = new ResultMapRequest<appPrintDeskOrderInfoResultData>(
                Request.Method.GET, HttpHelper.HOST + param, appPrintDeskOrderInfoResultData.class,
                new Response.Listener<appPrintDeskOrderInfoResultData>() {
                    @Override
                    public void onResponse(
                            appPrintDeskOrderInfoResultData response) {
                        if(response.getState()==1) {
                            dismissMakeOrderDF();
                        }
                        else if(response.getState()==0) {
                            dismissMakeOrderDF();
                            showShortTip("打印客单失败,请联系收银员!");
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
            if(mMakeOrderDF!=null){
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

}
