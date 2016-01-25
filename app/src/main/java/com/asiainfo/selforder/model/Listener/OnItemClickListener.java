package com.asiainfo.selforder.model.Listener;

import android.view.View;

/**
 * 列表子元素点击事件监听器
 * @author gjr
 */
public interface OnItemClickListener<T> {
    public void onItemClick(View view, int position,T t);
}
