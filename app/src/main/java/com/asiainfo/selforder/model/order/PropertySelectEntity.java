package com.asiainfo.selforder.model.order;

import com.asiainfo.selforder.model.dishes.DishesPropertyItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PropertySelectEntity implements Serializable{
	private static final long serialVersionUID = 1L;
    private String itemType;
    private List<DishesPropertyItem> mSelectedItemsList=new ArrayList<DishesPropertyItem>();

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public List<DishesPropertyItem> getmSelectedItemsList() {
		return mSelectedItemsList;
	}

	public void setmSelectedItemsList(
			List<DishesPropertyItem> mSelectedItemsList) {
		this.mSelectedItemsList = mSelectedItemsList;
	}

}
