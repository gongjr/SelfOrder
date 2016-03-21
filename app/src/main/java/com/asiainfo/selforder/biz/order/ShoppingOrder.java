package com.asiainfo.selforder.biz.order;

import com.asiainfo.selforder.model.dishComps.DishesCompSelectionEntity;
import com.asiainfo.selforder.model.order.OrderGoodsItem;

import java.io.Serializable;
import java.util.ArrayList;

public class ShoppingOrder implements Serializable {

    public ArrayList<String> getmHeaderNames() {
        return mHeaderNames;
    }

    public void setmHeaderNames(ArrayList<String> mHeaderNames) {
        this.mHeaderNames = mHeaderNames;
    }

    public ArrayList<Integer> getmHeaderPositions() {
        return mHeaderPositions;
    }

    public void setmHeaderPositions(ArrayList<Integer> mHeaderPositions) {
        this.mHeaderPositions = mHeaderPositions;
    }

    public ArrayList<OrderGoodsItem> getOrderGoods() {
        return orderGoods;
    }

    public void setOrderGoods(ArrayList<OrderGoodsItem> orderGoods) {
        this.orderGoods = orderGoods;
    }

    public ArrayList<DishesCompSelectionEntity> getOrderComps() {
        return this.orderComps;
    }

    ArrayList<String> mHeaderNames;
    ArrayList<Integer> mHeaderPositions;
    ArrayList<OrderGoodsItem> orderGoods;
    ArrayList<DishesCompSelectionEntity> orderComps;

    public ShoppingOrder() {
        mHeaderNames = new ArrayList<String>();
        mHeaderPositions = new ArrayList<Integer>();
        orderGoods = new ArrayList<OrderGoodsItem>();
        orderComps = new ArrayList<DishesCompSelectionEntity>();
    }


}
