package com.asiainfo.selforder.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.asiainfo.selforder.R;
import com.asiainfo.selforder.biz.order.OrderEntity;
import com.asiainfo.selforder.http.HttpHelper;
import com.asiainfo.selforder.model.MerchantRegister;
import com.asiainfo.selforder.model.net.PayBodyInfo;
import com.asiainfo.selforder.model.net.PayInfoNativeResult;
import com.asiainfo.selforder.model.net.ResultBody;
import com.asiainfo.selforder.model.net.UpdateOrderInfoResultData;
import com.asiainfo.selforder.ui.base.mBaseActivity;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import kxlive.gjrlibrary.entity.eventbus.EventMain;
import kxlive.gjrlibrary.http.ResultMapRequest;
import kxlive.gjrlibrary.http.VolleyErrorHelper;
import kxlive.gjrlibrary.utils.KLog;
import kxlive.gjrlibrary.utils.ToolPicture;
import kxlive.gjrlibrary.widget.LeafDialog.DialogDelayListener;
import roboguice.inject.InjectView;

/**
 * 支付结算页面
 * Created by gjr on 2015/12/9.
 */
public class SettleActivity extends mBaseActivity {
    @InjectView(R.id.back)
    private Button back;
    @InjectView(R.id.cancle)
    private Button cancle;
    @InjectView(R.id.print_orderinfo)
    private Button printOrder;
    @InjectView(R.id.need_pay_code)
    private ImageView payCode;
    @InjectView(R.id.need_pay_price)
    private TextView payPrice;
    @InjectView(R.id.need_type_title)
    private TextView needPayTitle;
    @InjectView(R.id.xianjin_tips)
    private TextView xianjinTips;
    @InjectView(R.id.need_to_xianjin)
    private TextView toXianjin;
    @InjectView(R.id.type_weixin)
    private RadioButton type_weixin;
    @InjectView(R.id.type_zhifubao)
    private RadioButton type_zhifubao;
    @InjectView(R.id.type_xianjin)
    private RadioButton type_xianjin;
    @InjectView(R.id.weixinAndzhifubao_group)
    private RelativeLayout weixinAndZhifubaoGroup;
    @InjectView(R.id.xianjin_group)
    private RelativeLayout xianjinGroup;
    @Inject
    private Resources res;
    private String merchantId,money,orderId,toId,attch;
    private MerchantRegister merchantRegister;
    private ResultBody curWeiXINBody,curZhiFuBaoBody;
    private int CurrentShow;
    private String PayType="";
    private boolean isPayReponse =true;//是否响应处理,为false就不在继续
    private OrderEntity orderEntity;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_settle);
        EventBus.getDefault().register(this);
        Bundle bundle=getIntent().getBundleExtra("orderInfo");
        orderId=bundle.getString("orderId");
        attch=bundle.getString("attch");
        money=bundle.getString("money");
        merchantRegister=(MerchantRegister)mApp.getData(mApp.KEY_GLOABLE_LOGININFO);
        merchantId=merchantRegister.getMerchantId();
        toId=merchantRegister.getToId();
        if(toId==null)toId="0";
        orderEntity=(OrderEntity)mApp.getData(mApp.KEY_CURORDER_ENTITY);
        initData();
    }

    public void initData(){
        payPrice.setText("￥"+money);
        toXianjin.setText("若支付宝、微信支付未成功,请选择\"现金支付\"");
        initListener();
        type_weixin.setChecked(true);
    }

    public void initListener(){
        back.setOnClickListener(mOnClickListener);
        cancle.setOnClickListener(mOnClickListener);
        type_weixin.setOnCheckedChangeListener(mOnCheckedChangeListenernew);
        type_zhifubao.setOnCheckedChangeListener(mOnCheckedChangeListenernew);
        type_xianjin.setOnCheckedChangeListener(mOnCheckedChangeListenernew);
        printOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPayReponse = false;
             getOperation().addParameter("orderId",orderId);
             getOperation().forward(PayXianjinActivity.class);
            }
        });
    }

    CompoundButton.OnCheckedChangeListener mOnCheckedChangeListenernew= new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b){
                switch (compoundButton.getId()){
                    case R.id.type_weixin:
                        CurrentShow=R.id.type_weixin;
                        weixinAndZhifubaoGroup.setVisibility(View.VISIBLE);
                        xianjinGroup.setVisibility(View.GONE);
                        needPayTitle.setText(res.getString(R.string.need_pay_weixin_title));

                        if(curWeiXINBody==null){
                            payCode.setImageBitmap(null);
                            showLoadingDialog(new DialogDelayListener() {
                                @Override
                                public void onexecute() {
                                    volleyGetv3PayInfoNative("1");                                }
                            }, 50);
                        }
                        else{
                            try {
                                Bitmap bitmap=ToolPicture.makeQRImage(curWeiXINBody.getCode_url(), 500, 500);
                                payCode.setImageBitmap(bitmap);
                            }catch (Exception e){
                                showShortTip("二维码生成有误!");
                                e.printStackTrace();
                            }
                        }
                        break;
                    case R.id.type_zhifubao:
                        CurrentShow=R.id.type_zhifubao;
                        weixinAndZhifubaoGroup.setVisibility(View.VISIBLE);
                        xianjinGroup.setVisibility(View.GONE);
                        needPayTitle.setText(res.getString(R.string.need_pay_zhifubao_title));
                        if(curZhiFuBaoBody==null){
                            payCode.setImageBitmap(null);
                            showLoadingDialog(new DialogDelayListener() {
                                @Override
                                public void onexecute() {
                                    volleyGetv3PayInfoNative("2");                                }
                            }, 50);
                        }
                        else{
                            try {
                                Bitmap bitmap=ToolPicture.makeQRImage(curZhiFuBaoBody.getQrCode(), 500, 500);
                                payCode.setImageBitmap(bitmap);
                            }catch (Exception e){
                                showShortTip("二维码生成有误!");
                                e.printStackTrace();
                            }
                        }
                        break;
                    case R.id.type_xianjin:
                        CurrentShow=R.id.type_xianjin;
                        xianjinGroup.setVisibility(View.VISIBLE);
                        xianjinTips.setText(Html.fromHtml(String.format(res.getString(R.string.pay_xianjin_title),"收银台")));
                        weixinAndZhifubaoGroup.setVisibility(View.GONE);
                        payCode.setImageBitmap(null);
                        break;
                }
            }
        }
    };

    View.OnClickListener mOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.back:
                    isPayReponse=false;
                    cancelAllRequest();
                    getOperation().finish();
                    break;
                case R.id.cancle:
                    EventMain eventOrder =new EventMain();
                    eventOrder.setType(EventMain.TYPE_FIRST);
                    eventOrder.setDescribe("返回清空");
                    eventOrder.setName(DishesMenuActivity.class.getName());
                    EventBus.getDefault().post(eventOrder);
                    isPayReponse=false;
                    cancelAllRequest();
                    getOperation().forward(DishesMenuActivity.class);
                    getOperation().finish();
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            isPayReponse=false;
            cancelAllRequest();
            getOperation().finish();
            return  true;
        }
        return  super.onKeyDown(keyCode, event);
    }

    /**
     * 根据paytype生成提交生成支付码
     * @param payType
     */
    public void volleyGetv3PayInfoNative(final String payType){
        String param="/getv3PayInfoNative?";
        KLog.i("url:"+HttpHelper.HOST_WBusiunion + param);
        Type type=new TypeToken<PayInfoNativeResult<ResultBody>>() {}.getType();
        ResultMapRequest<PayInfoNativeResult<ResultBody>> resultMapRequest = new ResultMapRequest<PayInfoNativeResult<ResultBody>>(
                Request.Method.POST, HttpHelper.HOST_WBusiunion + param, type,
                new Response.Listener<PayInfoNativeResult<ResultBody>>() {
                    @Override
                    public void onResponse(
                            PayInfoNativeResult<ResultBody> response) {
                            dismissLoadingDialog();
                            if (response.getState().equals("1")){
                                try {
                                if(CurrentShow==R.id.type_weixin){
                                        curWeiXINBody=response.getBody();
                                    Bitmap bitmap=ToolPicture.makeQRImage(curWeiXINBody.getCode_url(), 500, 500);
                                    payCode.setImageBitmap(bitmap);
                                        volleyQueryPayOrder();
                                }else
                                if(CurrentShow==R.id.type_zhifubao){
                                    curZhiFuBaoBody=response.getBody();
                                    Bitmap bitmap=ToolPicture.makeQRImage(curZhiFuBaoBody.getQrCode(), 500, 500);
                                    payCode.setImageBitmap(bitmap);
                                    }else{
                                    KLog.i("当前显示不匹配,只保存body,不更新二维码");
                                }
                                }catch (Exception e){
                                    showShortTip("二维码生成有误!");
                                     e.printStackTrace();
                                }
                            }else{
                                  showShortTip(response.getMsg());
                            }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoadingDialog();
                Log.e("TAG",
                        "VolleyError:" + error.getMessage(), error);
                showShortTip(VolleyErrorHelper.getMessage(error, mActivity));

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramList = new HashMap<String, String>();
                paramList.put("attch", attch);
                paramList.put("merchantId", merchantId);
                int s=Integer.valueOf(money)*100;
                paramList.put("money", s+"");
                paramList.put("orderId", orderId);
                paramList.put("payType", payType);
                paramList.put("discountableAmount", "0");
                paramList.put("undiscountableAmount", s+"");
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
        executeRequest(resultMapRequest);
    }

    /**
     * 查询对应类型的支付结果
     */
    public void volleyQueryPayOrder(){

        if(CurrentShow==R.id.type_weixin){
            PayType="1";
        }else if(CurrentShow==R.id.type_zhifubao){
            PayType="2";
        }
        String param="/queryWAPayOrder?merchantId="+merchantId+"&orderId="+orderId+"&toId="+toId+"&payType="+PayType;
        KLog.i("url:"+HttpHelper.HOST_WBusiunion+param);
        Type type=new TypeToken<PayInfoNativeResult<PayBodyInfo>>() {}.getType();
        ResultMapRequest<PayInfoNativeResult<PayBodyInfo>> resultMapRequest = new ResultMapRequest<PayInfoNativeResult<PayBodyInfo>>(
                Request.Method.GET, HttpHelper.HOST_WBusiunion+param, type,
                new Response.Listener<PayInfoNativeResult<PayBodyInfo>>() {
                    @Override
                    public void onResponse(
                            PayInfoNativeResult<PayBodyInfo> response) {
                        if(isPayReponse){
                        if(response!=null&&response.getState()!=null) {
                        if (response.getState().equals("1")){
                                PayBodyInfo payBodyInfo=response.getBody();
                            if(payBodyInfo.getTrade_state()!=null&&payBodyInfo.getTrade_state().equals("SUCCESS")){
                                    isPayReponse = false;
                                    getOperation().addParameter("payType","4");
                                getOperation().addParameter("orderId",orderId);
                                    getOperation().forward(PayStateActivity.class);
                            }else if(payBodyInfo.getTradeStatus()!=null&&payBodyInfo.getTradeStatus().equals("TRADE_SUCCESS")){
                                isPayReponse = false;
                                getOperation().addParameter("payType","5");
                                getOperation().addParameter("orderId",orderId);
                                getOperation().forward(PayStateActivity.class);
                            }
                            else{
                                KLog.i("订单未支付成功");
                                showDelay(new DialogDelayListener() {
                                    @Override
                                    public void onexecute() {
                                        volleyQueryPayOrder();
                                    }
                                },3000);
                            }
                        }else{
                            KLog.i("订单未支付成功");
                            showDelay(new DialogDelayListener() {
                                @Override
                                public void onexecute() {
                                    volleyQueryPayOrder();
                                }
                            },3000);
                        }
                        }else{
                            KLog.i("订单未支付成功");
                            showDelay(new DialogDelayListener() {
                                @Override
                                public void onexecute() {
                                    volleyQueryPayOrder();
                                }
                            },3000);
                        }}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG","VolleyError:" + error.getMessage(), error);
                if(isPayReponse){
                showDelay(new DialogDelayListener() {
                    @Override
                    public void onexecute() {
                        volleyQueryPayOrder();
                    }
                },3000);
                }
                showShortTip(VolleyErrorHelper.getMessage(error, mActivity));
            }
        }){
            @Override                                                                                                                                 public Map<String, String> getHeaders() {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type",
                    "application/x-www-form-urlencoded; charset=utf-8");
            return headers;
        }
        };
        executeRequest(resultMapRequest);
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 打印客单
     */
    public void VolleyPrintOrderInfo() {
        String param = "/appController/appPrintDeskOrderInfo.do?childMerchantId="+merchantRegister.getChildMerchantId()+"&orderId="+orderEntity.getOrderId();
        System.out.println("VolleyPrintOrderInfo:" + HttpHelper.HOST + param);
        ResultMapRequest<UpdateOrderInfoResultData> ResultMapRequest = new ResultMapRequest<UpdateOrderInfoResultData>(
                Request.Method.GET, HttpHelper.HOST + param, UpdateOrderInfoResultData.class,
                new Response.Listener<UpdateOrderInfoResultData>() {
                    @Override
                    public void onResponse(
                            UpdateOrderInfoResultData response) {
                        dismissLoadingDialog();
                        if(response.getState()==1) {

                        }
                        else if(response.getState()==0) {
                            showShortTip("由于设备原因,打印客单失败,请联系收银员!");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"VolleyError:" + error.getMessage(), error);
                showShortTip(VolleyErrorHelper.getMessage(error, mActivity));
            }
        })
        {
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
}
