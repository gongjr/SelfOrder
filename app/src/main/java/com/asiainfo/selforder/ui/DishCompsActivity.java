package com.asiainfo.selforder.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.asiainfo.selforder.R;
import com.asiainfo.selforder.biz.dishComps.DishCompsTypeAdapter;
import com.asiainfo.selforder.http.HttpHelper;
import com.asiainfo.selforder.model.MerchantDesk;
import com.asiainfo.selforder.model.MerchantRegister;
import com.asiainfo.selforder.model.dishComps.DishesComp;
import com.asiainfo.selforder.model.dishComps.DishesCompItem;
import com.asiainfo.selforder.model.dishes.DishesProperty;
import com.asiainfo.selforder.model.dishes.DishesPropertyItem;
import com.asiainfo.selforder.model.dishes.MerchantDishes;
import com.asiainfo.selforder.model.order.OrderGoodsItem;
import com.asiainfo.selforder.ui.base.mBaseActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;


/**
 * .
 *
 * @author 姚海林(skynight@dingtalk.com)
 * @version V1.0, 16/2/26 上午9:55
 */
public class DishCompsActivity extends mBaseActivity implements View.OnClickListener {

    private static String TAG = null;
    public static final Integer DISHE_COMPS = 10001;
    private List<DishesComp> dishesCompList;
    private String dishId = "100014732";
    private String childMerchantId;
    private MerchantRegister mRegister;
    private MerchantDesk mDesk;
    private static MerchantDishes mMerchantDishes;

    @InjectView(R.id.dish_comp_name)
    private TextView dishCompNameText;
    @InjectView(R.id.dish_comp_close_btn)
    private ImageButton dishCompCloseBtn;
    @InjectView(R.id.dish_comp_price)
    private TextView priceText;
    @InjectView(R.id.dish_comp_member_price)
    private TextView memberPriceText;
    @InjectView(R.id.dish_comp_pay_btn)
    private Button payBtn;
    @InjectView(R.id.dish_comp_listview)
    private ListView contentListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_comps);
        TAG = getClass().getSimpleName();
        mRegister = (MerchantRegister) mApp.getData(mApp.KEY_GLOABLE_LOGININFO);
        mDesk = (MerchantDesk) mApp.getData(mApp.KEY_GLOABLE_MERCHANTDESk);
        initData();
        initListner();
        httpGetDishesData("100014732", "20000080");
    }

    /*
    * 初始化数据
    * */
    private void initData() {
        dishId = getIntent().getStringExtra("dishesId");
        childMerchantId = getIntent().getStringExtra("childMerchantId");
        dishCompNameText.setText(getIntent().getStringExtra("dishesName"));
        priceText.setText(getIntent().getStringExtra("dishesPrice"));
        memberPriceText.setText(getIntent().getStringExtra("dishesMemberPrice"));
        textSetFlag(priceText);
    }

    /*
    * 初始化监听事件
    * */
    private void initListner() {
        dishCompCloseBtn.setOnClickListener(this);
        payBtn.setOnClickListener(this);
    }

    /*
    *
    * 根据dishesId和childMerchantId获取套餐内容
    * */
    private void httpGetDishesData(String dishesId, String childMerchantID) {
        String url = "/appController/queryComboInfoForApp.do?dishesId=" + dishesId + "&childMerchantId=" + childMerchantID;
        Log.d(TAG, HttpHelper.HOST + url);
        JsonObjectRequest httpGetDishesData = new JsonObjectRequest(HttpHelper.HOST + url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject data) {
                        Log.d(TAG, "The data is:" + data);
                        try {
                            if (data.length() != 0) {
                                String dataStr = data.getString("compDishesTypeList");
                                if (dataStr.length() != 0) {
                                    dishesCompList = new ArrayList<DishesComp>();
                                    Gson gson = new Gson();
                                    Type type = new TypeToken<ArrayList<DishesComp>>() {
                                    }.getType();
                                    dishesCompList = gson.fromJson(dataStr, type);
                                    setLayout(dishesCompList);
                                } else {
                                    Log.d(TAG, "套餐数据有误,请后台确认!");
                                    showShortTip("套餐数据有误,请后台确认!");
                                }
                            } else {
                                Log.d(TAG, "套餐数据有误,请后台确认!");
                                showShortTip("套餐数据有误,请后台确认!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "套餐数据有误,请后台确认!");
                            showShortTip("套餐数据有误,请后台确认!");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        executeRequest(httpGetDishesData);
    }

    /*
    *
    * 跳转页面并将数据传递到新的页面中
    * */
    public static void actionStart(Activity context, MerchantDishes merchantDishes) {
        Intent intent = new Intent(context, DishCompsActivity.class);
        intent.putExtra("dishesId", merchantDishes.getDishesId()); //套餐ID
        intent.putExtra("childMerchantId", merchantDishes.getMerchantId()); //餐厅ID
        intent.putExtra("dishesName", merchantDishes.getDishesName()); //套餐名称
        intent.putExtra("dishesPrice", merchantDishes.getDishesPrice()); //套餐价格
        intent.putExtra("dishesMemberPrice", merchantDishes.getMemberPrice());//套餐会员价it
        context.startActivityForResult(intent, DISHE_COMPS);
        mMerchantDishes = merchantDishes;
    }

    /*
    *
    * 设置具体内容的布局
    * */
    private void setLayout(List<DishesComp> dishesCompList) {
        Log.d(TAG, "The dishes is: " + dishesCompList);
        DishCompsTypeAdapter adapter = new DishCompsTypeAdapter(this, dishesCompList);
        contentListview.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dish_comp_close_btn:
                finish();
                break;
            case R.id.dish_comp_pay_btn:
                getSelectedData();
                break;
        }
    }

    /*
    * 点击确定按钮后,将选择的菜传回上个页面
    * */
    private void getSelectedData() {
        List<OrderGoodsItem> orderGoodsItemList = new ArrayList<OrderGoodsItem>();
        for (DishesComp dishesComp : dishesCompList) {
            List<DishesCompItem> dishesCompItemList = dishesComp.getDishesInfoList();
            for (DishesCompItem dishesCompItem : dishesCompItemList) {
                OrderGoodsItem orderGoodsItem = new OrderGoodsItem();
                if (dishesCompItem.isChecked()) {
                    List<DishesProperty> dishesPropertyList = dishesCompItem.getDishesItemTypelist();
                    if (dishesPropertyList.size() != 0) {
                        List<String> remark = new ArrayList<String>();
                        for (DishesProperty dishesProperty : dishesPropertyList) {
                            List<DishesPropertyItem> dishesPropertyItemList = dishesProperty.getItemlist();
                            for (DishesPropertyItem dishesPropertyItem : dishesPropertyItemList) {
                                if (dishesPropertyItem.getIsChecked() != 0) {
                                    remark.add(dishesPropertyItem.getItemName());
                                }
                            }
                            orderGoodsItem.setRemark(remark);
                        }
                    }
                    orderGoodsItem.setCompId(dishId);
                    orderGoodsItem.setTradeStaffId(mRegister.getStaffId());
                    orderGoodsItem.setDeskId(mDesk.getDeskId());
                    orderGoodsItem.setDishesPrice(dishesCompItem.getDishesPrice());
                    orderGoodsItem.setDishesTypeCode(dishesCompItem.getDishesTypeCode());
                    orderGoodsItem.setExportId(dishesCompItem.getExportId());
                    orderGoodsItem.setInstanceId("" + System.currentTimeMillis());
                    orderGoodsItem.setInterferePrice("0");
                    orderGoodsItem.setOrderId("");
                    orderGoodsItem.setSalesId(dishesCompItem.getDishesId());
                    orderGoodsItem.setSalesName(dishesCompItem.getDishesName());
                    orderGoodsItem.setSalesNum(1);
                    orderGoodsItem.setSalesPrice("0");
                    orderGoodsItem.setSalesState("1");  //0稍后下单  1立即下单
                    orderGoodsItem.setIsCompDish("" + true); //套餐菜固定为true
                    orderGoodsItem.setAction("1");
                    orderGoodsItem.setIsZdzk(dishesCompItem.getIsZdzk()); //整单折扣
                    orderGoodsItem.setMemberPrice(dishesCompItem.getMemberPrice()); //会员价
                    orderGoodsItemList.add(orderGoodsItem);
                }
            }
        }

        OrderGoodsItem orderGoodsItem = new OrderGoodsItem();
        orderGoodsItem.setCompId("0");
        orderGoodsItem.setTradeStaffId(mRegister.getStaffId());
        orderGoodsItem.setDeskId(mDesk.getDeskId());
        orderGoodsItem.setDishesPrice(mMerchantDishes.getDishesPrice());
        orderGoodsItem.setDishesTypeCode(mMerchantDishes.getDishesTypeCode());
        orderGoodsItem.setExportId(mMerchantDishes.getExportId());
        orderGoodsItem.setInstanceId("" + System.currentTimeMillis());
        orderGoodsItem.setInterferePrice("0");
        orderGoodsItem.setOrderId("");
        orderGoodsItem.setSalesId(mMerchantDishes.getDishesId());
        orderGoodsItem.setSalesName(mMerchantDishes.getDishesName());
        orderGoodsItem.setSalesNum(1);
        orderGoodsItem.setSalesPrice(mMerchantDishes.getDishesPrice());
        orderGoodsItem.setSalesState("1");  //0稍后下单  1立即下单
        orderGoodsItem.setIsCompDish("" + false); //套餐菜固定为true
        orderGoodsItem.setAction("1");
        orderGoodsItem.setIsZdzk(mMerchantDishes.getIsZdzk()); //整单折扣
        orderGoodsItem.setMemberPrice(mMerchantDishes.getMemberPrice()); //会员价
        orderGoodsItem.setComp(true);
        Intent intent = new Intent();
        intent.putExtra("OrderGoodsItem", orderGoodsItem);
        intent.putExtra("MerchantDishes", mMerchantDishes);
        intent.putExtra("OrderGoodsList", (Serializable) orderGoodsItemList);
        setResult(DISHE_COMPS, intent);
        finish();
    }

    /*
    * 给字体中间加横线
    * */
    private void textSetFlag(TextView textView) {
        textView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }
}
