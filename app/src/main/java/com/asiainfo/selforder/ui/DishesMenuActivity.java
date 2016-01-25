package com.asiainfo.selforder.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asiainfo.selforder.R;
import com.asiainfo.selforder.biz.db.DishesEntity;
import com.asiainfo.selforder.biz.dishes.DishesAdapter;
import com.asiainfo.selforder.biz.dishes.DishesTypeAdapter;
import com.asiainfo.selforder.biz.order.OrderEntity;
import com.asiainfo.selforder.config.Constants;
import com.asiainfo.selforder.model.Listener.OnItemClickListener;
import com.asiainfo.selforder.model.Listener.OnPropertyItemClickListener;
import com.asiainfo.selforder.model.MerchantDesk;
import com.asiainfo.selforder.model.MerchantRegister;
import com.asiainfo.selforder.model.dishes.MerchantDishes;
import com.asiainfo.selforder.model.dishes.MerchantDishesType;
import com.asiainfo.selforder.model.order.PropertySelectEntity;
import com.asiainfo.selforder.ui.base.mBaseActivity;
import com.google.inject.Inject;

import java.util.List;

import de.greenrobot.event.EventBus;
import kxlive.gjrlibrary.entity.eventbus.EventMain;
import kxlive.gjrlibrary.utils.KLog;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * 菜单
 * Created by gjr on 2015/12/1.
 */
public class DishesMenuActivity extends mBaseActivity{
    @InjectView(R.id.dishes_menu_selected_num)
    private TextView shoppingNum;
    @InjectView(R.id.dishes_menu_selected_price)
    private TextView shoppingPrice;
    @InjectView(R.id.dishesmenu_bottom_clear)
    private Button clear;
    @InjectView(R.id.dishesmenu_bottom_clear_init)
    private Button clear_init;
    @InjectView(R.id.dishesmenu_next)
    private Button next;
    @InjectView(R.id.dishesmenu_next_init)
    private Button next_init;
    @InjectView(R.id.dishes_type_group)
    private RecyclerView dishesTypeView;
    @InjectView(R.id.dishes_group)
    private RecyclerView dishesView;
    @Inject
    Resources res;
    @Inject
    LayoutInflater inflater;
    @InjectResource(R.string.clear_init_tips) String clear_init_tips;
    @InjectResource(R.string.next_init_tips) String next_init_tips;
    private DishesTypeAdapter mDishesTypeAdapter;
    private DishesAdapter mDishesAdapter;
    private List<MerchantDishesType> dishesTypeList;
    private DishesEntity dishesEntity;
    private OrderEntity orderEntity;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_dishes_menu);
        initData();
        initListener();
    }

    private void initData(){
        //当前master
        EventBus.getDefault().register(this);
        MerchantRegister merchantRegister=(MerchantRegister)mApp.getData(mApp.KEY_GLOABLE_LOGININFO);
        MerchantDesk merchantDesk=(MerchantDesk)mApp.getData(mApp.KEY_GLOABLE_MERCHANTDESk);
        KLog.i("merchantDesk:"+merchantDesk);
        dishesEntity=new DishesEntity();
        //类型加载
        dishesTypeList=dishesEntity.getAllDishesType();
        orderEntity=new OrderEntity(merchantRegister,dishesTypeList,merchantDesk);

        LinearLayoutManager dishesLayoutManager = new LinearLayoutManager(this);
        dishesLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        dishesTypeView.setLayoutManager(dishesLayoutManager);
        mDishesTypeAdapter=new DishesTypeAdapter(inflater,dishesTypeList,res);
        mDishesTypeAdapter.setmOnItemClickListenerT(dishesTypeOnItemClick);
        dishesTypeView.setAdapter(mDishesTypeAdapter);
        //菜品加载
        mDishesAdapter=new DishesAdapter<MerchantDishes>(inflater,res,mActivity);
        mDishesAdapter.setOnPropertyItemClickListener(addDishesOnItemClick);
        refreshDishesItem(0);
        updateNumAndPrice();
    }

    private void initListener(){
        clear.setOnClickListener(mOnClickListener);
        clear_init.setOnClickListener(mOnClickListener);
        next.setOnClickListener(mOnClickListener);
        next_init.setOnClickListener(mOnClickListener);
    }

    /**
     * 属性对应位置处的类型菜
     * @param position
     */
    public void refreshDishesItem(int position){
        if(dishesTypeList!=null&&dishesTypeList.size()>0) {
            MerchantDishesType merchantDishesType=dishesTypeList.get(position);
            if(merchantDishesType.getDishesInfoList()!=null&&merchantDishesType.getDishesInfoList().size()<1){
                merchantDishesType.setDishesInfoList(dishesEntity.sqliteMerchantDishesByType(merchantDishesType.getDishesTypeCode()));
                dishesTypeList.set(position,merchantDishesType);
                //查询一次后保存
            }
            mDishesAdapter.refreshData(merchantDishesType.getDishesInfoList());
            StaggeredGridLayoutManager mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            dishesView.setLayoutManager(mStaggeredGridLayoutManager);
            dishesView.setAdapter(mDishesAdapter);
        }
    }

    View.OnClickListener mOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.dishesmenu_bottom_clear:
                    orderEntity.clearOrder();
                    updateNumAndPrice();
                    clearTOinit();
                    mDishesAdapter.clearNum();
                    break;
                case R.id.dishesmenu_next:
                    orderEntity.prepareNewOrderSummaryInfo();
                    mApp.saveData(mApp.KEY_CURORDER_ENTITY,orderEntity);
                    getOperation().forwardForResult(MakeOrderActivity.class, Constants.RequestCode_DishesMenuToMakeorder);
                    break;
                case R.id.dishesmenu_next_init:
                    showShortTip(next_init_tips);
                    break;
                case R.id.dishesmenu_bottom_clear_init:
                    showShortTip(clear_init_tips);
                    break;
            }
        }
    };

    public void clearTOinit(){
        clear_init.setVisibility(View.VISIBLE);
        next_init.setVisibility(View.VISIBLE);
    }

    public void initTOclear(){
        clear_init.setVisibility(View.GONE);
        next_init.setVisibility(View.GONE);
    }

    /**
     * 处理dishesType,点击事件
     */
    OnItemClickListener<MerchantDishesType> dishesTypeOnItemClick=new OnItemClickListener<MerchantDishesType>() {
        @Override
        public void onItemClick(View view, int position, MerchantDishesType dishesType) {
            refreshDishesItem(position);
        }
    };

    /**
     * 处理dishes初始点击事件
     */
    OnPropertyItemClickListener<MerchantDishes> addDishesOnItemClick=new OnPropertyItemClickListener<MerchantDishes>() {
        @Override
        public void onItemClick(View view, int num, MerchantDishes merchantDishes,List<PropertySelectEntity> selectedPropertyItems) {
            if(view!=null){
                orderEntity.updateOrderGoodsListData(merchantDishes,num,selectedPropertyItems);
                updateNumAndPrice();
            }else{
//                    if(num==0){
                    orderEntity.clearPropetyItem(merchantDishes);
                    updateNumAndPrice();
//                }else
//                showLongTip("有多条"+merchantDishes.getDishesName()+",存在不同属性,请至订单删除!");
            }
        }
    };

    public void updateNumAndPrice(){
        int num=orderEntity.getOrderSubmitAllGoodsNum();
        shoppingNum.setText(num+"");
        shoppingPrice.setText(orderEntity.getOrderSubmitOriginalPrice());
        if(num==0&&clear_init.getVisibility()==View.GONE){
            clearTOinit();
        }
        else if(num>0&&clear_init.getVisibility()==View.VISIBLE){
            initTOclear();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.RequestCode_DishesMenuToMakeorder&&resultCode==Constants.ResultCode_MakeorderToDishesMenu_Back){
            orderEntity=(OrderEntity)mApp.getData(mApp.KEY_CURORDER_ENTITY);
            mDishesAdapter.refreshByOrderList(orderEntity.getOrderList());
            updateNumAndPrice();
        }
        if(requestCode==Constants.RequestCode_DishesMenuToMakeorder&&resultCode==Constants.ResultCode_MakeorderToDishesMenu_Clear){
            orderEntity=(OrderEntity)mApp.getData(mApp.KEY_CURORDER_ENTITY);
            orderEntity.clearOrder();
            updateNumAndPrice();
            clearTOinit();
            mDishesAdapter.clearNum();
        }
    }

    @Override
    public boolean onEventMainThread(EventMain event) {
        boolean isRun=super.onEventMainThread(event);
        if (isRun) {
            switch (event.getType()) {
                case EventMain.TYPE_FIRST:
                    //点餐完毕后清空
                    orderEntity=(OrderEntity)mApp.getData(mApp.KEY_CURORDER_ENTITY);
                    orderEntity.clearOrder();
                    updateNumAndPrice();
                    clearTOinit();
                    mDishesAdapter.clearNum();
                    break;
                default:
                    break;
            }}
        return isRun;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            mApp.removeData(mApp.KEY_CURORDER_ENTITY);
            getOperation().finish();
            return  true;
        }
        return  super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
