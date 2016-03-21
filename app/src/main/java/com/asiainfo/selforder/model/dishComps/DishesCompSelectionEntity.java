package com.asiainfo.selforder.model.dishComps;


import com.asiainfo.selforder.model.order.OrderGoodsItem;

import java.io.Serializable;
import java.util.List;

public class DishesCompSelectionEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OrderGoodsItem mCompMainDishes; // 套餐主菜
	private List<OrderGoodsItem> compItemDishes; // 套餐子菜列表

	public OrderGoodsItem getmCompMainDishes() {
		return mCompMainDishes;
	}

	public void setmCompMainDishes(OrderGoodsItem mCompMainDishes) {
		this.mCompMainDishes = mCompMainDishes;
	}

	public List<OrderGoodsItem> getCompItemDishes() {
		return compItemDishes;
	}

	public void setCompItemDishes(List<OrderGoodsItem> compItemDishes) {
		this.compItemDishes = compItemDishes;
	}

}
