package com.asiainfo.selforder.model.Listener;

import android.view.View;

import com.asiainfo.selforder.model.order.PropertySelectEntity;

import java.util.List;

/**
 * 列表子元素点击事件监听器
 * T t 泛型数据类型传递
 * @author gjr
 */
public interface OnPropertyItemClickListener<T> {
    public void onItemClick(View view, int position,T t,List<PropertySelectEntity> selectedPropertyItems);
}
