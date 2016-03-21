package com.asiainfo.selforder.model.Listener;

/**
 * 列表子元素点击事件监听器
 * @author gjr
 */
public interface OnStickyHeaderChangeListener<T> {
    public void onItemClick(int position, T t);
}
