package com.asiainfo.selforder.model.dishes;

import com.tonicartos.widget.stickygridheaders.TypeSection;

import java.util.ArrayList;

/**
 * 菜品总集
 * Created by gjr on 2016/3/1.
 */
public class DishesData {
    private ArrayList<MerchantDishesType> dishesTypeList;
    private ArrayList<MerchantDishes> dishesList;
    private ArrayList<TypeSection> sectionList;

    public ArrayList<TypeSection> getSectionList() {
        return sectionList;
    }

    public void setSectionList(ArrayList<TypeSection> sectionList) {
        this.sectionList = sectionList;
    }

    public ArrayList<MerchantDishesType> getDishesTypeList() {
        return dishesTypeList;
    }

    public void setDishesTypeList(ArrayList<MerchantDishesType> dishesTypeList) {
        this.dishesTypeList = dishesTypeList;
    }

    public ArrayList<MerchantDishes> getDishesList() {
        return dishesList;
    }

    public void setDishesList(ArrayList<MerchantDishes> dishesList) {
        this.dishesList = dishesList;
    }
}
