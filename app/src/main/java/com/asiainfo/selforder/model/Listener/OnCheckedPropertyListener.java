package com.asiainfo.selforder.model.Listener;

import com.asiainfo.selforder.model.dishes.DishesPropertyItem;

import java.util.List;

public interface OnCheckedPropertyListener {
    public void returnCheckedItems(
                                   List<String> curSelectedPropertyList, List<DishesPropertyItem> checkedPropertyItems);
}
