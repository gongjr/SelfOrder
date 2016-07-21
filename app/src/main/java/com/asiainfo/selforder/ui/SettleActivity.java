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
import com.asiainfo.selforder.config.Constants;
import com.asiainfo.selforder.http.HttpHelper;
import com.asiainfo.selforder.model.MerchantRegister;
import com.asiainfo.selforder.model.net.PayBodyInfo;
import com.asiainfo.selforder.model.net.PayInfoNativeResult;
import com.asiainfo.selforder.model.net.PayOrderResultData;
import com.asiainfo.selforder.model.net.ResultBody;
import com.asiainfo.selforder.ui.base.EnsureDialogFragmentBase;
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
import kxlive.gjrlibrary.utils.ArithUtils;
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
    private String merchantId,childMerchantId,money,orderId,toId,attch;
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
        childMerchantId=merchantRegister.getChildMerchantId();
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
                        payCode.setImageBitmap(null);
                        if(curWeiXINBody==null){
                            showLoadingDialog(new DialogDelayListener() {
                                @Override
                                public void onexecute() {
                                    volleyGetv3PayInfoNative("1");                                }
                            }, 50);
                        }
                        else{
                            try {
                                dismissLoadingDialog();
                                Bitmap bitmap=ToolPicture.makeQRImage(curWeiXINBody.getCode_url(), 500, 500);
                                payCode.setImageBitmap(bitmap);
                            }catch (Exception e){
                                showShortTip("二维码生成有误,请退出重新提交!");
                                e.printStackTrace();
                            }
                        }
                        break;
                    case R.id.type_zhifubao:
                        CurrentShow=R.id.type_zhifubao;
                        weixinAndZhifubaoGroup.setVisibility(View.VISIBLE);
                        xianjinGroup.setVisibility(View.GONE);
                        needPayTitle.setText(res.getString(R.string.need_pay_zhifubao_title));
                        payCode.setImageBitmap(null);

                        if(curZhiFuBaoBody==null){
                            showLoadingDialog(new DialogDelayListener() {
                                @Override
                                public void onexecute() {
                                    volleyGetv3PayInfoNative("2");                                }
                            }, 50);
                        }
                        else{
                            try {
                                dismissLoadingDialog();
                                Bitmap bitmap=ToolPicture.makeQRImage(curZhiFuBaoBody.getQrCode(), 500, 500);
                                payCode.setImageBitmap(bitmap);
                            }catch (Exception e){
                                showShortTip("二维码生成有误,请退出重新提交!");
                                e.printStackTrace();
                            }
                        }
                        break;
                    case R.id.type_xianjin:
                        CurrentShow=R.id.type_xianjin;
                        xianjinGroup.setVisibility(View.VISIBLE);
                        xianjinTips.setText(Html.fromHtml(String.format(res.getString(R.string.pay_xianjin_title), "收银台")));
                        payCode.setImageBitmap(null);
                        weixinAndZhifubaoGroup.setVisibility(View.GONE);
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
                                    if(curWeiXINBody!=null&&curZhiFuBaoBody==null)volleyQueryPayOrder();
                                }else
                                if(CurrentShow==R.id.type_zhifubao){
                                    curZhiFuBaoBody=response.getBody();
                                    Bitmap bitmap=ToolPicture.makeQRImage(curZhiFuBaoBody.getQrCode(), 500, 500);
                                    payCode.setImageBitmap(bitmap);
                                    if(curZhiFuBaoBody!=null&&curWeiXINBody==null)volleyQueryPayOrder();
                                    }else{
                                    KLog.i("当前显示不匹配");
                                }
                                }catch (Exception e){
                                    showShortTip("二维码生成有误!");
                                     e.printStackTrace();
                                }
                            }else{
                                showShortTip(response.getMsg());
                                //                窗口强制提示
                                setmEnsureDialogListener(new EnsureDialogFragmentBase.CallBackListener() {
                                    @Override
                                    public void onLeftBtnFinish() {
                                        dismissEnsureDialog();
                                        showLoadingDialog(new DialogDelayListener() {
                                            @Override
                                            public void onexecute() {
                                                volleyGetv3PayInfoNative(payType);                             }
                                        }, 50);
                                    }

                                    @Override
                                    public void onRightBtnFinish() {
                                        dismissEnsureDialog();
                                    }
                                },"生成支付码时:请求错误?","必须重新生成,才能继续支付!","确定","取消");
                                //窗口强制提示,确定,取消都舍弃本单,返回重新生成订单提交
                                showEnsureDialog("getv3PayInfoNative_error_0");
                            }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoadingDialog();
                Log.e("TAG",
                        "VolleyError:" + error.getMessage(), error);
                showShortTip(VolleyErrorHelper.getMessage(error, mActivity));
                //                窗口强制提示
                setmEnsureDialogListener(new EnsureDialogFragmentBase.CallBackListener() {
                    @Override
                    public void onLeftBtnFinish() {
                        dismissEnsureDialog();
                        showLoadingDialog(new DialogDelayListener() {
                            @Override
                            public void onexecute() {
                                volleyGetv3PayInfoNative(payType);                             }
                        }, 50);
                    }

                    @Override
                    public void onRightBtnFinish() {
                        dismissEnsureDialog();
                    }
                },"提交请求时:网络异常?","必须重新提交,才能继续支付!","确定","取消");
                //窗口强制提示,确定,取消都舍弃本单,返回重新生成订单提交
                showEnsureDialog("getv3PayInfoNative_error_Net-Exception");

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramList = new HashMap<String, String>();
                paramList.put("attch", attch);
                paramList.put("merchantId", childMerchantId);
                Double moneys=Double.valueOf(money)*100;
                paramList.put("money", ArithUtils.d2str(moneys));
                paramList.put("orderId", orderId);
                paramList.put("payType", payType);
                paramList.put("discountableAmount", "0");
                paramList.put("undiscountableAmount", ArithUtils.d2str(moneys));
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
                                    getOperation().addParameter("payType",Constants.Order_PayType_weixin+"");
                                getOperation().addParameter("orderId",orderId);
                                    getOperation().forward(PayStateActivity.class);
                            }else if(payBodyInfo.getTradeStatus()!=null&&payBodyInfo.getTradeStatus().equals("TRADE_SUCCESS")){
                                isPayReponse = false;
                                getOperation().addParameter("payType",Constants.Order_PayType_zhifubao+"");
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
     * 在选择支付类型的时候,实时更新服务器预付单信息
     * 调用次数,当发生异常服务器需要默认新提交,最大次数3
     */
    public void VolleyPreOrder(final int payType,final int time){
        String param = "/appController/preOrder.do";
        Map<String, String> paramList = new HashMap<String, String>();
        paramList.put("orderId", orderId);
        int payPrice=Integer.valueOf(orderEntity.getOrderSubmitOriginalPrice())*100;
        paramList.put("payPrice", payPrice+"");//支付金额
        paramList.put("payType", payType+"");//支付类型
        paramList.put("tradeStaffId", merchantRegister.getStaffId());//操作员工
        paramList.put("merchantId", merchantRegister.getMerchantId());//商户ID
        paramList.put("childMerchantId", merchantRegister.getChildMerchantId());//子商户ID
        Log.i("tag","url:"+HttpHelper.HOST + param);
        ResultMapRequest<PayOrderResultData> resultMapRequest =new ResultMapRequest<PayOrderResultData>(
                Request.Method.POST, HttpHelper.HOST + param,paramList,
                PayOrderResultData.class,new Response.Listener<PayOrderResultData>() {
            @Override
            public void onResponse(PayOrderResultData response) {
                if (response.getErrcode().equals(response.errcode_ok)){
                    switch (payType){
                        case Constants.Order_PayType_weixin:
                            if(curWeiXINBody==null){
                                volleyGetv3PayInfoNative("1");
                            }
                            else{
                                try {
                                    dismissLoadingDialog();
                                    Bitmap bitmap=ToolPicture.makeQRImage(curWeiXINBody.getCode_url(), 500, 500);
                                    payCode.setImageBitmap(bitmap);
                                }catch (Exception e){
                                    showShortTip("二维码生成有误,请退出重新提交!");
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case Constants.Order_PayType_zhifubao:
                            if(curZhiFuBaoBody==null){
                                volleyGetv3PayInfoNative("2");
                            }
                            else{
                                try {
                                    dismissLoadingDialog();
                                    Bitmap bitmap=ToolPicture.makeQRImage(curZhiFuBaoBody.getQrCode(), 500, 500);
                                    payCode.setImageBitmap(bitmap);
                                }catch (Exception e){
                                    showShortTip("二维码生成有误,请退出重新提交!");
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case Constants.Order_PayType_xianjin:
                            dismissLoadingDialog();
                            printOrder.setVisibility(View.VISIBLE);
                            break;
                    }
                }else if (response.getErrcode().equals(response.errcode_update_failed_all)){
                    //接口错误,数据更新失败,需要默认重新提交,最打尝试次数3
                    if(time<3){
                        VolleyPreOrder(payType,time+1);
                    }else{
                        dismissLoadingDialog();
                        showShortTip("预付单信息生成失败,请重新下单支付!");
                        setmEnsureDialogListener(new EnsureDialogFragmentBase.CallBackListener() {
                            @Override
                            public void onLeftBtnFinish() {
                                dismissEnsureDialog();
                                showLoadingDialog(new DialogDelayListener() {
                                    @Override
                                    public void onexecute() {
                                        VolleyPreOrder(payType,0);                                }
                                }, 50);
                            }

                            @Override
                            public void onRightBtnFinish() {
                                dismissEnsureDialog();
                                isPayReponse=false;
                                cancelAllRequest();
                                getOperation().finish();
                            }
                        },"生成预付单时:更新信息失败?","必须重新生成,才能继续支付!","重新尝试","取消");
                        showEnsureDialog("preOrder_errcode_update_failed_all");
                    }
                }else if(response.getErrcode().equals(response.errcode_param_missing)){
                    dismissLoadingDialog();
                    showShortTip("参数值错误,请重新下单支付!");
                    setmEnsureDialogListener(new EnsureDialogFragmentBase.CallBackListener() {
                        @Override
                        public void onLeftBtnFinish() {
                            dismissEnsureDialog();
                            isPayReponse=false;
                            cancelAllRequest();
                            getOperation().finish();
                        }

                        @Override
                        public void onRightBtnFinish() {
                            dismissEnsureDialog();
                            isPayReponse=false;
                            cancelAllRequest();
                            getOperation().finish();
                        }
                    },"生成预付单时:参数值错误?","必须重新生成,才能继续支付!","确定","取消");
                    showEnsureDialog("preOrder_errcode_param_missing");
                }else{
                    dismissLoadingDialog();
                    showShortTip("订单未知异常,请重新下单支付!");
                    setmEnsureDialogListener(new EnsureDialogFragmentBase.CallBackListener() {
                        @Override
                        public void onLeftBtnFinish() {
                            dismissEnsureDialog();
                            isPayReponse=false;
                            cancelAllRequest();
                            getOperation().finish();
                        }

                        @Override
                        public void onRightBtnFinish() {
                            dismissEnsureDialog();
                            isPayReponse=false;
                            cancelAllRequest();
                            getOperation().finish();
                        }
                    },"订单未知异常?","必须重新生成,才能继续支付!","确定","取消");
                    showEnsureDialog("preOrder_errcode_unknow");
                }

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissLoadingDialog();
                showShortTip(VolleyErrorHelper.getMessage(error, mActivity));
                setmEnsureDialogListener(new EnsureDialogFragmentBase.CallBackListener() {
                    @Override
                    public void onLeftBtnFinish() {
                        dismissEnsureDialog();
                        showLoadingDialog(new DialogDelayListener() {
                            @Override
                            public void onexecute() {
                                VolleyPreOrder(payType,0);                                }
                        }, 50);
                    }

                    @Override
                    public void onRightBtnFinish() {
                        dismissEnsureDialog();
                        isPayReponse=false;
                        cancelAllRequest();
                        getOperation().finish();
                    }
                },"提交预付单时:网络异常?","必须重新提交,才能继续支付!","确定","取消");
                showEnsureDialog("preOrder_onErrorResponse");
            }
        });
        executeRequest(resultMapRequest);
    }

}
