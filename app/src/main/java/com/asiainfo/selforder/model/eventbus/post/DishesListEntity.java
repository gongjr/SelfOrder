package com.asiainfo.selforder.model.eventbus.post;

import com.asiainfo.selforder.model.dishes.MerchantDishes;
import com.asiainfo.selforder.model.dishes.MerchantDishesType;

import java.util.List;

/**
 * Created by gjr on 2015/10/22.
 */
public class DishesListEntity {
    private List<MerchantDishesType> mDishTypeDataList;
    private List<MerchantDishes> mAllDishesDataList;

    public List<MerchantDishesType> getmDishTypeDataList() {
        return mDishTypeDataList;
    }

    public void setmDishTypeDataList(List<MerchantDishesType> mDishTypeDataList) {
        this.mDishTypeDataList = mDishTypeDataList;
    }

    public List<MerchantDishes> getmAllDishesDataList() {
        return mAllDishesDataList;
    }

    public void setmAllDishesDataList(List<MerchantDishes> mAllDishesDataList) {
        this.mAllDishesDataList = mAllDishesDataList;
    }
}
