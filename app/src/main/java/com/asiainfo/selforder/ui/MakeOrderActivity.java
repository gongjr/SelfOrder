package com.asiainfo.selforder.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.asiainfo.selforder.R;
import com.asiainfo.selforder.biz.order.OrderEntity;
import com.asiainfo.selforder.biz.order.OrderListAdapter;
import com.asiainfo.selforder.biz.order.ShoppingOrder;
import com.asiainfo.selforder.config.Constants;
import com.asiainfo.selforder.http.HttpHelper;
import com.asiainfo.selforder.model.Listener.OnItemClickListener;
import com.asiainfo.selforder.model.MerchantRegister;
import com.asiainfo.selforder.model.net.SubmitOrderId;
import com.asiainfo.selforder.model.order.OrderGoodsItem;
import com.asiainfo.selforder.ui.base.mBaseActivity;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kxlive.gjrlibrary.http.ResultMapRequest;
import kxlive.gjrlibrary.http.VolleyErrorHelper;
import kxlive.gjrlibrary.http.VolleyErrors;
import kxlive.gjrlibrary.widget.LeafDialog.DialogDelayListener;
import kxlive.gjrlibrary.widget.sectionedlist.SimpleSectionedListAdapter;
import kxlive.gjrlibrary.widget.sectionedlist.SimpleSectionedListAdapter.Section;
import roboguice.inject.InjectView;

/**
 * 生产订单页面
 * Created by gjr on 2015/11/25.
 */
public class MakeOrderActivity extends mBaseActivity{
    @InjectView(R.id.makeorder_disheslist)
    private ListView dishesList;
    @InjectView(R.id.makeorder_to_dishesmenu)
    private Button toDishesmenu;
    @InjectView(R.id.makeorder_settle)
    private Button settle;
//    @InjectView(R.id.makeorder_clear)
//    private Button clear;
    @InjectView(R.id.makeorder_num)
    private TextView orderNum;
    @InjectView(R.id.makeorder_price)
    private TextView orderPrice;
    @InjectView(R.id.makeorder_takeaway_all_check)
    private CheckBox takeawayAll;
    @Inject
    LayoutInflater inflater;
    private SimpleSectionedListAdapter mSimpleSectionedListAdapter;
    /**
     * 装载数据头部节点的list
     */
    private ArrayList<Section> sections = new ArrayList<Section>();
    private OrderListAdapter mAdapter;
    private OrderEntity orderEntity;
    private MakeOrderDF mMakeOrderDF;
    private MerchantRegister merchantRegister;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_makeorder);
        initData();
    }

    public void initData(){
        orderEntity=(OrderEntity)mApp.getData(mApp.KEY_CURORDER_ENTITY);
        merchantRegister=(MerchantRegister)mApp.getData(mApp.KEY_GLOABLE_LOGININFO);
        ShoppingOrder shopping=orderEntity.getShoppingOrderByList();
        refreshOrderList(shopping);
        updateNumAndPrice();
        initListener();
    }

    public void initListener(){
        toDishesmenu.setOnClickListener(mOnClickListener);
        settle.setOnClickListener(mOnClickListener);
//        clear.setOnClickListener(mOnClickListener);
        takeawayAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    orderEntity.setAllChecked();
                    mAdapter.setAllChecked();
                }
                else {
                    orderEntity.clearAllChecked();
                    mAdapter.clearAllChecked();
                }
            }
        });

    }

    public void refreshOrderList(ShoppingOrder shopping){
        sections.clear();
        for (int i = 0; i < shopping.getmHeaderPositions().size(); i++) {
            sections.add(new Section(shopping.getmHeaderPositions().get(i),
                    shopping.getmHeaderNames().get(i)));
        }
        if (mAdapter == null) {
            initLvAdapter();
        }
        mAdapter.setData(shopping.getOrderGoods());
//        mAdapter.setOrderCompsData(shopping.getOrderComps());
        mSimpleSectionedListAdapter
                .setSections(sections.toArray(new Section[0]));
        dishesList.setAdapter(mSimpleSectionedListAdapter);
    }

    /**
     * 初始化普通订单列表适配器
     */
    void initLvAdapter() {
        mAdapter = new OrderListAdapter(inflater);
        mAdapter.setContext(this);
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        mSimpleSectionedListAdapter = new SimpleSectionedListAdapter(
                this, mAdapter, R.layout.dish_selected_lv_item_header,
                R.id.view_header);
    }

    OnItemClickListener mOnItemClickListener=new OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position, Object o) {
            OrderGoodsItem orderGoodsItem=(OrderGoodsItem)o;
            switch (view.getId()){
                case R.id.makeorder_item_delete:
                    orderEntity.deleteOrderGoodsItem(orderGoodsItem);
                    ShoppingOrder shopping=orderEntity.getShoppingOrderByList();
                    refreshOrderList(shopping);
                    updateNumAndPrice();
                    break;
                case R.id.makeorder_item_takeaway_check:
                    boolean isTakeaway=(Boolean)view.getTag();
                    orderEntity.changeOrderGoodsItemTakwawayState(orderGoodsItem,isTakeaway);
                    break;
            }
        }
    };

    View.OnClickListener mOnClickListener =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        switch (view.getId()){
            case R.id.makeorder_to_dishesmenu:
                mApp.saveData(mApp.KEY_CURORDER_ENTITY,orderEntity);
                getOperation().finishByResultCode(Constants.ResultCode_MakeorderToDishesMenu_Back);
                break;
            case R.id.makeorder_settle:
                if(orderEntity.getOrderList().size()>0 || orderEntity.getOrderCompGoodsList().size() > 0){
                    showMakeOrderDF();
                    lockMakeOrderBtn();
                    showDelay(new DialogDelayListener() {
                        @Override
                        public void onexecute() {
                            VolleysubmitOrderInfo(orderEntity.prepareOrderSummaryInfo());
                        }
                    },100);
                }
                else showShortTip("您尚未点菜哦~~");
                break;
//            case R.id.makeorder_clear:
//                mApp.saveData(mApp.KEY_CURORDER_ENTITY,orderEntity);
//                getOperation().finishByResultCode(Constants.ResultCode_MakeorderToDishesMenu_Clear);
//                break;
        }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            mApp.saveData(mApp.KEY_CURORDER_ENTITY,orderEntity);
            getOperation().finishByResultCode(Constants.ResultCode_MakeorderToDishesMenu_Back);

            return  true;
        }
        return  super.onKeyDown(keyCode, event);
    }

    public void updateNumAndPrice(){
        int num=orderEntity.getOrderSubmitAllGoodsNum();
        orderNum.setText(num + "");
        orderPrice.setText(orderEntity.getOrderSubmitOriginalPrice());
    }

    /**
     * 向服务器新增订单，
     */
    public void VolleysubmitOrderInfo(final String order) {
        String param = "/appController/submitOrderInfo.do?";
        Log.i(TAG, "submitOrderInfo_url:" + HttpHelper.HOST + param);
        Map<String, String> paramList = new HashMap<String, String>();
        paramList.put("orderSubmitData", order);
        ResultMapRequest<SubmitOrderId> ResultMapRequest = new ResultMapRequest<SubmitOrderId>(
                Request.Method.POST, HttpHelper.HOST + param,paramList, SubmitOrderId.class,
                new Response.Listener<SubmitOrderId>() {
                    @Override
                    public void onResponse(
                            SubmitOrderId response) {
                        releaseMakeOrderBtn();
                        if (response.getState() == 1) {
                            dismissMakeOrderDF();
                            Bundle bundle=new Bundle();
                            bundle.putString("orderId",response.getOrderId());
                            orderEntity.updateOrderInfo(response.getOrderId());
                            bundle.putString("money",orderEntity.getOrderSubmitOriginalPrice());
                            bundle.putString("attch", merchantRegister.getMerchantName());
//                            if(orderEntity.getOrderList().size()>0)
//                            bundle.putString("attch", orderEntity.getOrderList().get(0).getSalesName() + "等菜");
//                            bundle.putString("attch", "YouNeedToPayForYourFastFood:"+orderEntity.getOrderSubmitOriginalPrice());
                            mApp.saveData(mApp.KEY_CURORDER_ENTITY,orderEntity);
                            getOperation().addParameter("orderInfo",bundle);
                            getOperation().forward(SettleActivity.class);
                        }
                        else if(response.getState()==0) {
                            onMakeOrderFailed(response.getError());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                releaseMakeOrderBtn();
                VolleyErrors errors= VolleyErrorHelper.getVolleyErrors(error,
                        mActivity);
                switch (errors.getErrorType()){
                    case VolleyErrorHelper.ErrorType_Socket_Timeout:
                        Log.e(TAG,
                                "VolleyError:" + errors.getErrorMsg(), error);
                        onMakeOrderFailed(errors.getErrorMsg());
                        break;
                    default:
                        Log.e(TAG,
                                "VolleyError:" + errors.getErrorMsg(), error);
                        onMakeOrderFailed(errors.getErrorMsg());
                        break;
                }
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
        executeRequest(ResultMapRequest,0);
    }

    /**
     * 锁定结算按钮
     */
    private void lockMakeOrderBtn(){
        settle.setEnabled(false);
        settle.setClickable(false);
    }

    /**
     * 释放结算按钮
     */
    private void releaseMakeOrderBtn(){
        settle.setEnabled(true);
        settle.setClickable(true);
    }

    /**
     * 下单提交
     */
    private void showMakeOrderDF(){
        try {
            mMakeOrderDF = new MakeOrderDF();
            mMakeOrderDF.setNoticeText("正在下单...");
            mMakeOrderDF.show(getSupportFragmentManager(), "dialog_fragment_http_common");
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * 提交失败处理
     */
    private void onMakeOrderFailed(String msg){
        Log.d(TAG, "msg:"+msg);
        try {
            if(mMakeOrderDF!=null&&mMakeOrderDF.isAdded()){
                mMakeOrderDF.updateNoticeText(msg);
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

    public void delete(int position) {
        orderEntity.deleteOrderCompGoods(position);
        ShoppingOrder shopping=orderEntity.getShoppingOrderByList();
        refreshOrderList(shopping);
        updateNumAndPrice();
    }
}
