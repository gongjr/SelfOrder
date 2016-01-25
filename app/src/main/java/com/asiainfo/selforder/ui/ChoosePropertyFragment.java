package com.asiainfo.selforder.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asiainfo.selforder.R;
import com.asiainfo.selforder.biz.dishes.DishesAdapter;
import com.asiainfo.selforder.model.Listener.OnChangeWithPropertyListener;
import com.asiainfo.selforder.model.dishes.DishesProperty;
import com.asiainfo.selforder.model.dishes.DishesPropertyItem;
import com.asiainfo.selforder.model.dishes.MerchantDishes;
import com.asiainfo.selforder.model.order.PropertySelectEntity;
import com.asiainfo.selforder.widget.FlowLayoutMargin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kxlive.gjrlibrary.base.DialogFragmentBase;
import roboguice.inject.InjectView;

/**
 * Created by gjr on 2015/12/4.
 */
public class ChoosePropertyFragment extends DialogFragmentBase {
    @InjectView(R.id.choose_property_close)
    private Button close;
    @InjectView(R.id.choose_property_ok)
    private Button ok;
    @InjectView(R.id.choose_property_group)
    private LinearLayout propertyGroup;
    private LayoutInflater mInflater;
    private MerchantDishes merchantDishes;
    /**
     * 更新选择的属性数据
     */
    private List<PropertySelectEntity> curPropertyChocie = new ArrayList<PropertySelectEntity>();
    private OnChangeWithPropertyListener mOnChangeWithPropertyListener;
    private DishesAdapter.ViewHolder viewHolder;
    private ConcurrentHashMap<DishesPropertyItem, CompoundButton> checkDishesItem = new ConcurrentHashMap<DishesPropertyItem, CompoundButton>();
    public static ChoosePropertyFragment propertyDF;
    public static ChoosePropertyFragment newInstance(MerchantDishes merchantDishes) {
        if(propertyDF==null){
            propertyDF = new ChoosePropertyFragment();
        }
        Bundle args = new Bundle();
        args.putSerializable("MerchantDishes", merchantDishes);
        propertyDF.setArguments(args);
        return propertyDF;
    }

    public static ChoosePropertyFragment getInstance(){
        if(propertyDF==null){
            propertyDF = new ChoosePropertyFragment();
        }
        return propertyDF;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        merchantDishes = (MerchantDishes) getArguments().getSerializable("MerchantDishes");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View mView = inflater.inflate(R.layout.dfragment_choose_property, null);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInflater = LayoutInflater.from(mActivity);
        initData();
        initListener();
    }

    private void initData(){
        checkDishesItem.clear();
        setDefProperty(merchantDishes);
        if(merchantDishes!=null&&merchantDishes.getDishesItemTypelist()!=null&&merchantDishes.getDishesItemTypelist().size()>0)
        for (DishesProperty dishesProperty : merchantDishes.getDishesItemTypelist()){
            addRemark(mInflater,propertyGroup,dishesProperty,merchantDishes,oncheckedChangeListener);
        }
    }

    private void initListener(){
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapToCurPropertyChocie();
                if (checkDishesItem.size()>0){
                mOnChangeWithPropertyListener.onChangeCount(merchantDishes, 1, curPropertyChocie,viewHolder);
                dismiss();
                }else{
                    showShortToast("请选择属性!");
                }
            }
        });
    }

    /**
     * 设置返回所选属性的监听函数
     */
    public void setOnCheckedPropertyItemsListener(OnChangeWithPropertyListener nOnCheckedPropertyListener,DishesAdapter.ViewHolder viewHolder){
        this.mOnChangeWithPropertyListener = nOnCheckedPropertyListener;
        this.viewHolder =viewHolder;
    }

    //设置默认选中数据
    private void setDefProperty(MerchantDishes mMerchantDishes){
        curPropertyChocie.clear();
        //初始化时只将类型放进去,具体内容最后在根据选择提交转化
        List<DishesProperty> dpList = mMerchantDishes.getDishesItemTypelist();
        if(dpList!=null && dpList.size()>0){
            for(int i=0; i<dpList.size(); i++){
                DishesProperty dp = dpList.get(i);
                PropertySelectEntity psEntity = new PropertySelectEntity();
                psEntity.setItemType(dp.getItemType());
                curPropertyChocie.add(psEntity);
            }
        }
    }

    public void addRemark(LayoutInflater mInflater, LinearLayout item_group, DishesProperty dishesItemType, MerchantDishes merchantDishes, CompoundButton.OnCheckedChangeListener oncheckedChangeListener) {
        LinearLayout layout = (LinearLayout) mInflater.inflate(R.layout.dishes_property_type, null);
        item_group.addView(layout);
        TextView title = (TextView) layout.findViewById(R.id.dishesitemtype_name);
        title.setText(dishesItemType.getItemTypeName() + ":");
        FlowLayoutMargin item = (FlowLayoutMargin) layout.findViewById(R.id.dishesitemtype_flowLayout);
        for (int i=0;i<dishesItemType.getItemlist().size();i++){
            DishesPropertyItem dishesItem=dishesItemType.getItemlist().get(i);
            CheckBox tv = (CheckBox) mInflater.inflate(R.layout.dishesproperty_item_check,
                    item, false);
            tv.setText(dishesItem.getItemName());
            dishesItem.setDishesId(merchantDishes.getDishesId());
            dishesItem.setItemType(dishesItemType.getItemType());
            dishesItem.setItemTypeName(dishesItemType.getItemTypeName());
            tv.setOnCheckedChangeListener(oncheckedChangeListener);
            tv.setTag(R.id.tag_first, dishesItem);
            tv.setTag(R.id.tag_second, merchantDishes);
            item.addView(tv);
            if(i==0)tv.setChecked(true);
        }
    }

    CompoundButton.OnCheckedChangeListener oncheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            DishesPropertyItem dishesItem = (DishesPropertyItem) buttonView.getTag(R.id.tag_first);
            if (isChecked) {
                clearropertyByType(dishesItem);
                if (!checkDishesItem.containsKey(dishesItem))
                    checkDishesItem.put(dishesItem, buttonView);
            } else {
                if (checkDishesItem.containsKey(dishesItem))
                    checkDishesItem.remove(dishesItem);
            }
        }
    };

    public void mapToCurPropertyChocie(){
        Iterator<Map.Entry<DishesPropertyItem, CompoundButton>> iter = checkDishesItem.entrySet().iterator();
        while (iter.hasNext()) {
                Map.Entry<DishesPropertyItem, CompoundButton> curiter=iter.next();
            for (int i=0;i<curPropertyChocie.size();i++){
                PropertySelectEntity propertySelectEntity=curPropertyChocie.get(i);
                if(curiter.getKey().getItemType().equals(propertySelectEntity.getItemType()))
                    propertySelectEntity.getmSelectedItemsList().add(curiter.getKey());
            }
        }
    }

    //保持同类型单选,清除列表中类型相同的项,同类型保有一个
    public void clearropertyByType(DishesPropertyItem dishesPropertyItem){
        Iterator<Map.Entry<DishesPropertyItem, CompoundButton>> iter = checkDishesItem.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<DishesPropertyItem, CompoundButton> curiter=iter.next();
            if(dishesPropertyItem.getItemType().equals(curiter.getKey().getItemType())){
                curiter.getValue().setChecked(false);
            }
        }
    }
}
