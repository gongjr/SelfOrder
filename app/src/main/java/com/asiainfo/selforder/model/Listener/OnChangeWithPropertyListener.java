package com.asiainfo.selforder.model.Listener;

import com.asiainfo.selforder.model.dishes.MerchantDishes;
import com.asiainfo.selforder.model.order.PropertySelectEntity;

import java.util.List;

public interface OnChangeWithPropertyListener<T> {
   public void onChangeCount(MerchantDishes dishesItem, int selectedCount, List<PropertySelectEntity> selectedPropertyItems,T t);
}
