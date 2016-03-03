package com.asiainfo.selforder.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;

import com.asiainfo.selforder.R;
import com.asiainfo.selforder.biz.db.DishesEntity;
import com.asiainfo.selforder.biz.dishes.DishesTypeAdapter;
import com.asiainfo.selforder.biz.dishes.StickyDishesAdapter;
import com.asiainfo.selforder.biz.dishes.ViewHolder;
import com.asiainfo.selforder.biz.order.OrderEntity;
import com.asiainfo.selforder.config.Constants;
import com.asiainfo.selforder.model.Listener.OnItemClickListener;
import com.asiainfo.selforder.model.Listener.OnPropertyItemClickListener;
import com.asiainfo.selforder.model.MerchantDesk;
import com.asiainfo.selforder.model.MerchantRegister;
import com.asiainfo.selforder.model.dishComps.DishesCompSelectionEntity;
import com.asiainfo.selforder.model.dishes.DishesData;
import com.asiainfo.selforder.model.dishes.MerchantDishes;
import com.asiainfo.selforder.model.dishes.MerchantDishesType;
import com.asiainfo.selforder.model.dishes.TypeSection;
import com.asiainfo.selforder.model.order.OrderGoodsItem;
import com.asiainfo.selforder.model.order.PropertySelectEntity;
import com.asiainfo.selforder.ui.base.mBaseActivity;
import com.google.inject.Inject;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import java.util.ArrayList;
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
public class DishesMenuActivity extends mBaseActivity {
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
//    @InjectView(R.id.dishes_group)
//    private RecyclerView dishesView;
    @InjectView(R.id.sticky_dishes_group)
    private StickyGridHeadersGridView dishesGridView;
    @Inject
    Resources res;
    @Inject
    LayoutInflater inflater;
    @InjectResource(R.string.clear_init_tips)
    String clear_init_tips;
    @InjectResource(R.string.next_init_tips)
    String next_init_tips;
    private DishesTypeAdapter mDishesTypeAdapter;
//    private DishesAdapter mDishesAdapter;
    private StickyDishesAdapter mStickyDishesAdapter;
    private ArrayList<MerchantDishesType> dishesTypeList;
    private ArrayList<MerchantDishes> dishesList;
    private DishesData mDishesData;
    private int curFirstVisibleItemPosition=0;
    private int curVisibleItemCount=9;
    private boolean isAutoScroll=false;
    private DishesEntity dishesEntity;
    private OrderEntity orderEntity;
    private ViewHolder viewHolder;
    private int position;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_dishes_menu);
        initData();
        initListener();
    }

    private void initData() {
        EventBus.getDefault().register(this);
        MerchantRegister merchantRegister = (MerchantRegister) mApp.getData(mApp.KEY_GLOABLE_LOGININFO);
        MerchantDesk merchantDesk = (MerchantDesk) mApp.getData(mApp.KEY_GLOABLE_MERCHANTDESk);
        KLog.i("merchantDesk:" + merchantDesk);
        dishesEntity = new DishesEntity();
        //类型加载
        dishesTypeList=dishesEntity.getAllDishesType();
        mDishesData=dishesEntity.getAllDishesOrderbyType();
        orderEntity=new OrderEntity(merchantRegister,mDishesData.getDishesTypeList(),merchantDesk);
        LinearLayoutManager dishesLayoutManager = new LinearLayoutManager(this);
        dishesLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        dishesTypeView.setLayoutManager(dishesLayoutManager);

        mDishesTypeAdapter=new DishesTypeAdapter(inflater,mDishesData.getDishesTypeList(),res);
        mDishesTypeAdapter.setmOnItemClickListenerT(dishesTypeOnItemClick);
        dishesTypeView.setAdapter(mDishesTypeAdapter);
        //菜品加载
//        mDishesAdapter=new DishesAdapter<MerchantDishes>(inflater,res,mActivity);
//        mDishesAdapter.setOnPropertyItemClickListener(addDishesOnItemClick);
        mStickyDishesAdapter=new StickyDishesAdapter(inflater,res,mActivity,this);
        mStickyDishesAdapter.setOnPropertyItemClickListener(addDishesOnItemClick);
        mStickyDishesAdapter.refreshData(mDishesData.getDishesList());
        dishesGridView.setAdapter(mStickyDishesAdapter);
    }

    private void initListener() {
        clear.setOnClickListener(mOnClickListener);
        clear_init.setOnClickListener(mOnClickListener);
        next.setOnClickListener(mOnClickListener);
        next_init.setOnClickListener(mOnClickListener);
        dishesGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                if (curFirstVisibleItemPosition != firstVisibleItem) {
                    curFirstVisibleItemPosition = firstVisibleItem;
                    curVisibleItemCount=visibleItemCount;
                    Log.i("onScroll", "curFirstVisibleItemPosition:" + curFirstVisibleItemPosition);
                    for (TypeSection mTypeSection : mDishesData.getSectionList()) {
                        if (curFirstVisibleItemPosition < mTypeSection.getEndIndex() && curFirstVisibleItemPosition >= mTypeSection.getStartIndex()&&!isAutoScroll) {
                            if (mDishesTypeAdapter.getSelectedPos() != mTypeSection.getTypeIndex()) {
                                mDishesTypeAdapter.setSelectedPos(mTypeSection.getTypeIndex());
                                final int position = mTypeSection.getTypeIndex();
                                //定位头部类型位置
                                if (position == 0 && dishesGridView.getChildAt(0).getVisibility() == View.VISIBLE) {
                                    dishesTypeView.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                        }
                                    }, 300);
                                } else if (position > 0) {
                                    dishesTypeView.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //dishesTypeView.getLayoutManager().scrollToPosition(position-1);
                                            //scrollToPosition(position),移动靠左或靠右,或者完全显示的时候不移动,不计算偏移量
                                            ((LinearLayoutManager) dishesTypeView.getLayoutManager()).scrollToPositionWithOffset(position - 1, 0);
                                        }
                                    }, 300);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        });
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.dishesmenu_bottom_clear:
                    orderEntity.clearOrder();
                    updateNumAndPrice();
                    clearTOinit();
//                    mDishesAdapter.clearNum();
                    mStickyDishesAdapter.clearNum();
                    break;
                case R.id.dishesmenu_next:
                    orderEntity.prepareNewOrderSummaryInfo();
                    mApp.saveData(mApp.KEY_CURORDER_ENTITY, orderEntity);
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

    public void clearTOinit() {
        clear_init.setVisibility(View.VISIBLE);
        next_init.setVisibility(View.VISIBLE);
    }

    public void initTOclear() {
        clear_init.setVisibility(View.GONE);
        next_init.setVisibility(View.GONE);
    }

    /**
     * 处理dishesType,点击事件
     */
    OnItemClickListener<MerchantDishesType> dishesTypeOnItemClick = new OnItemClickListener<MerchantDishesType>() {
        @Override
        public void onItemClick(View view, int position, MerchantDishesType dishesType) {
            refreshDishesItem(position,dishesType);
        }
    };


    /**
     * 属性对应位置处的类型菜,自动数据定位,但是头部精准定位失效
     * 只能进行数据估算,上下位置下不同偏移量来平衡
     * @param position
     */
    public void refreshDishesItem(final int position,final MerchantDishesType dishesType){
        //定位菜品dishesGridView对应位置
        if(dishesType.getSectionPosition()==0){
        dishesGridView.postDelayed(new Runnable() {
            @Override
            public void run() {
                  dishesGridView.smoothScrollToPosition(dishesType.getSectionPosition());
            }
        },200);
        }else{
            isAutoScroll=true;
            int numScroll=1;
            if(dishesType.getSectionPosition()>curFirstVisibleItemPosition)
                numScroll=dishesType.getSectionPosition()-curFirstVisibleItemPosition;
            else if(dishesType.getSectionPosition()<curFirstVisibleItemPosition)
                numScroll=curFirstVisibleItemPosition-dishesType.getSectionPosition();

            if(dishesType.getSectionPosition()>=curFirstVisibleItemPosition+curVisibleItemCount){
                //若果要定位的目标位置在显示区域下面外面,先强制粗略定位到头部或尾部上下最近位置,后进行精准头部定位
                //否则直接进行精准头部定位,有时候不一定能够直接偏移定位到头部,偏移到中间就直接事件冲突停止了
                dishesGridView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 精准头部定位失效,只能定位到上下最近位置
                        if(dishesGridView.getFirstVisiblePosition()<dishesType.getSectionPosition())
                            dishesGridView.smoothScrollToPosition(dishesType.getSectionPosition()+6);
                        else dishesGridView.smoothScrollToPosition(dishesType.getSectionPosition()+3);
                    }
                },10);

                //精准头部偏移定位,给粗略定位滚动留一定空余时间,根据目标滚动距离,估算时间
                dishesGridView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(curFirstVisibleItemPosition!=dishesType.getSectionPosition()){
                            dishesGridView.smoothScrollToPositionFromTop(dishesType.getSectionPosition(),0);
                        }
                    }
                },numScroll*10);
            }else{
                //精准头部偏移定位,在上面位置是,直接定位到位
                dishesGridView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(curFirstVisibleItemPosition!=dishesType.getSectionPosition()){
                            dishesGridView.smoothScrollToPositionFromTop(dishesType.getSectionPosition(),0);
                        }
                    }
                },10);
            }
            //给自动滚动一定时间限制,此时屏蔽滚动事件对头部类型的影响
            if(numScroll<50)numScroll=50;
            dishesGridView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isAutoScroll=false;
                }
            },numScroll*12);
        }
        //定位头部类型位置
        if(position==0){
            dishesTypeView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dishesTypeView.getLayoutManager().scrollToPosition(0);
                }
            },300);
        }else if(position>0){
            dishesTypeView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((LinearLayoutManager)dishesTypeView.getLayoutManager()).scrollToPositionWithOffset(position-1,0);
                }
            },300);
        }
    }

    /**
     * 处理dishes初始点击事件
     */
    OnPropertyItemClickListener<MerchantDishes> addDishesOnItemClick = new OnPropertyItemClickListener<MerchantDishes>() {
        @Override
        public void onItemClick(View view, int num, MerchantDishes merchantDishes, List<PropertySelectEntity> selectedPropertyItems) {
            if (view != null) {
                orderEntity.updateOrderGoodsListData(merchantDishes, num, selectedPropertyItems);
                updateNumAndPrice();
            } else {
//                    if(num==0){
                orderEntity.clearPropetyItem(merchantDishes);
                updateNumAndPrice();
//                }else
//                showLongTip("有多条"+merchantDishes.getDishesName()+",存在不同属性,请至订单删除!");
            }
        }
    };

    public void updateNumAndPrice() {
        int num = orderEntity.getOrderSubmitAllGoodsNum();
        shoppingNum.setText(num + "");
        shoppingPrice.setText(orderEntity.getOrderSubmitOriginalPrice());
        if (num == 0 && clear_init.getVisibility() == View.GONE) {
            clearTOinit();
        } else if (num > 0 && clear_init.getVisibility() == View.VISIBLE) {
            initTOclear();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.RequestCode_DishesMenuToMakeorder&&resultCode==Constants.ResultCode_MakeorderToDishesMenu_Back){
            orderEntity=(OrderEntity)mApp.getData(mApp.KEY_CURORDER_ENTITY);
//            mDishesAdapter.refreshByOrderList(orderEntity.getOrderList());
            mStickyDishesAdapter.refreshByOrderList(orderEntity.getOrderList());

            updateNumAndPrice();
        }
        if (requestCode == Constants.RequestCode_DishesMenuToMakeorder && resultCode == Constants.ResultCode_MakeorderToDishesMenu_Clear) {
            orderEntity = (OrderEntity) mApp.getData(mApp.KEY_CURORDER_ENTITY);
            orderEntity.clearOrder();
            updateNumAndPrice();
            clearTOinit();
//            mDishesAdapter.clearNum();
            mStickyDishesAdapter.clearNum();
        }
        if (requestCode == DishCompsActivity.DISHE_COMPS && resultCode == DishCompsActivity.DISHE_COMPS) {
            MerchantDishes merchantDishes = (MerchantDishes) data.getSerializableExtra("merchantDishes");
            List<OrderGoodsItem> list = (List<OrderGoodsItem>) data.getSerializableExtra("OrderGoodsList");
            DishesCompSelectionEntity dishesCompSelectionEntity = new DishesCompSelectionEntity();
            dishesCompSelectionEntity.setCompItemDishes(list);
            orderEntity.addOrderCompGoods(dishesCompSelectionEntity);
            mStickyDishesAdapter.addNumberTest(viewHolder, merchantDishes, position);
        }
    }

    @Override
    public boolean onEventMainThread(EventMain event) {
        boolean isRun = super.onEventMainThread(event);
        if (isRun) {
            switch (event.getType()) {
                case EventMain.TYPE_FIRST:
                    //点餐完毕后清空
                    orderEntity = (OrderEntity) mApp.getData(mApp.KEY_CURORDER_ENTITY);
                    orderEntity.clearOrder();
                    updateNumAndPrice();
                    clearTOinit();
//                    mDishesAdapter.clearNum();
                    mStickyDishesAdapter.clearNum();
                    break;
                default:
                    break;
            }
        }
        return isRun;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mApp.removeData(mApp.KEY_CURORDER_ENTITY);
            getOperation().finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void deleteOrderCompGoods() {
        orderEntity.deleteOrderCompGoods();
    }

    public void test(ViewHolder viewHolder, int position) {
        this.viewHolder = viewHolder;
        this.position = position;
    }
}
