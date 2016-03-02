package com.asiainfo.selforder.biz.db;

import android.util.Log;

import com.asiainfo.selforder.model.dishes.DishesData;
import com.asiainfo.selforder.model.dishes.DishesProperty;
import com.asiainfo.selforder.model.dishes.DishesPropertyItem;
import com.asiainfo.selforder.model.dishes.MerchantDishes;
import com.asiainfo.selforder.model.dishes.MerchantDishesType;
import com.asiainfo.selforder.model.dishes.TypeSection;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import kxlive.gjrlibrary.db.DataBinder;
import kxlive.gjrlibrary.utils.ArithUtils;

/**
 * 将数据库操作和视图activity分离 在此实体类中统一处理菜品相关的业务逻辑 通过DataBinder来绑定表对象，进行数据库操作
 *
 * @author gjr
 */
public class DishesEntity {
    /**
     * 用于绑定表对象，完成DishesType对象对应表的操作
     */
    public DataBinder<MerchantDishesType> dataSetDishesType;
    /**
     * 用于绑定表对象，完成DishesInfo对象对应表的操作
     */
    public DataBinder<MerchantDishes> dataSetDishes;
    /**
     * 用于绑定表对象，完成DishesItemType对象对应表的操作
     */
    public DataBinder<DishesProperty> dataSetProperty;
    /**
     * 用于绑定表对象，完成DishesItem对象对应表的操作
     */
    public DataBinder<DishesPropertyItem> dataSetPropertyItem;

    public DishesEntity() {
        initDataBinder();
    }

    public void initDataBinder() {
        dataSetDishesType = new DataBinder<MerchantDishesType>();
        dataSetDishes = new DataBinder<MerchantDishes>();
        dataSetProperty = new DataBinder<DishesProperty>();
        dataSetPropertyItem = new DataBinder<DishesPropertyItem>();
    }

    /**
     * 更新本地数据库菜品表信息 增加的时候同表可以批量插入，但是多表不能关联插入， 多表关联的只能取其对应表列自行单表插入
     * 删除的时候会将其所关联的多条数据一起删除 但是也只能删除一级关联表，二级三级关联表无法自动删除，所以
     * 所以有多级关联表时，必须要自己查询其多级关联关系，来删除多级以免数据混乱
     */
    public void updateDb(ArrayList<MerchantDishesType> dishesTypeList,
                         ArrayList<MerchantDishes> all) {
        ArrayList<MerchantDishes> cur = new ArrayList<MerchantDishes>();
        for (MerchantDishesType dishesType : dishesTypeList) {
            getDishesInfobyType(all, cur, dishesType.getDishesTypeCode());
            for (MerchantDishes dishesInfo : cur) {
                DataSupport.saveAll(dishesInfo.getDishesItemTypelist());
                for (DishesProperty dishesItemType : dishesInfo
                        .getDishesItemTypelist()) {
                    DataSupport.saveAll(dishesItemType.getItemlist());
                }
            }
            dishesType.setDishesInfoList(cur);
            DataSupport.saveAll(cur);
            dataSetDishesType.saveThrows(dishesType);
        }
    }

    /**
     * 从菜品列表中检索出特点type类型的菜品列表
     *
     * @param type
     * @return
     */
    private List<MerchantDishes> getDishesInfobyType(ArrayList<MerchantDishes> all,
                                                 ArrayList<MerchantDishes> cur, String type) {
        if (cur == null) {
            cur = new ArrayList<MerchantDishes>();
        } else {
            cur.clear();
        }
        for (int i = 0; i < all.size(); i++) {
            MerchantDishes dish = all.get(i);
            if (dish.getDishesTypeCode().equals(type)) {
                cur.add(dish);
            }
        }
        return cur;
    }

    /**
     * 获取所有的菜品类型
     */
    public ArrayList<MerchantDishesType> getAllDishesType() {
        ArrayList<MerchantDishesType> dishesTypeList = (ArrayList<MerchantDishesType>) dataSetDishesType.findAll(MerchantDishesType.class);
        return dishesTypeList;
    }

    /**
     * 获取所有的菜品按类型排序
     */
    public DishesData getAllDishesOrderbyType() {
        ArrayList<TypeSection> sectionList = new ArrayList<TypeSection>();
        DishesData mDishesData=new DishesData();
        ArrayList<MerchantDishes> dishes=new ArrayList<MerchantDishes>();
        ArrayList<MerchantDishesType> dishesTypeList = (ArrayList<MerchantDishesType>) dataSetDishesType.findAll(MerchantDishesType.class);
        mDishesData.setDishesTypeList(dishesTypeList);
        int SectionSetTO=0;
        int Section=1;
        int typeIndex=0;
        for (MerchantDishesType mMerchantDishesType:dishesTypeList){
            TypeSection typeSection=new TypeSection();
            typeSection.setStartIndex(SectionSetTO);
            mMerchantDishesType.setSectionPosition(SectionSetTO);
            List<MerchantDishes> dishesList=sqliteMerchantDishesByTypeWithSection(mMerchantDishesType.getDishesTypeCode(),Section,typeIndex);

            dishes.addAll(dishesList);
            if(dishesList!=null) Section+=dishesList.size();
            if (dishesList!=null&&dishesList.size()!=0){
            Double offsetNum=dishesList.size()/3.0;
            Double offset1= ArithUtils.round(offsetNum, 2);
            int offset=offset1.intValue() ;
            if(offset1-offset>0){
                offset+=1;
            }
            Log.i("refreshDishesItem", "offset:" + offset);
            SectionSetTO=SectionSetTO+offset*3+3;
                //由于加入了header类型头部,其实际position每次都被占了1行,需要加3偏移
                typeSection.setEndIndex(SectionSetTO);
                typeSection.setSize(typeSection.getEndIndex()-typeSection.getStartIndex());
                typeSection.setDishesName(mMerchantDishesType.getDishesTypeName());
                typeSection.setDishesTypeCode(mMerchantDishesType.getDishesTypeCode());
                typeSection.setTypeIndex(typeIndex);
                sectionList.add(typeSection);
            }
            typeIndex++;
        }
        mDishesData.setSectionList(sectionList);
        mDishesData.setDishesList(dishes);
        return mDishesData;
    }

    /**
     * 填充某类型菜品列表
     *
     * @param dishesType
     * @return
     */
    public List<MerchantDishes> getDishesTypeItemlist(MerchantDishesType dishesType) {
        dishesType.getItemlistDb();
        for (MerchantDishes dishesInfo : dishesType.getDishesInfoList()) {
            dishesInfo.getItemlistDb();
            for (DishesProperty dishesItemType : dishesInfo
                    .getDishesItemTypelist()) {
                dishesItemType.getItemlistDb();
            }
        }
        return dishesType.getDishesInfoList();
    }

    /**
     * 从菜品数据库中检索出特点type类型的菜品列表
     *
     * @param index 分组位置
     * @return DishesType
     */
    public MerchantDishesType getDishesTypebyIndex(int index) {
        MerchantDishesType dishesType = dataSetDishesType.find(MerchantDishesType.class,
                dataSetDishesType.findFirst(MerchantDishesType.class).getId() + index);
        dishesType.getItemlistDb();
        for (MerchantDishes dishesInfo : dishesType.getDishesInfoList()) {
            dishesInfo.getItemlistDb();
            for (DishesProperty dishesItemType : dishesInfo
                    .getDishesItemTypelist()) {
                dishesItemType.getItemlistDb();
            }
        }
        return dishesType;
    }

    /**
     * 根据菜品类型编码与id，获取对应的菜品
     * @param dishesTypeCode
     * @return
     */
    public MerchantDishes sqliteMerchantDishesById(String dishesTypeCode, String dishesId){
        List<MerchantDishes> dishes=dataSetDishes.findWithWhere(MerchantDishes.class,"dishesid=? and dishestypecode=?",
                dishesTypeCode,dishesId);
        if(dishes.size()>0) {
            MerchantDishes merchantDishes=dishes.get(0);
            merchantDishes.getItemlistDb();
            for (DishesProperty dishesProperty:merchantDishes.getDishesItemTypelist()){
                dishesProperty.getItemlistDb();
            }
            return merchantDishes;
        }else return null;
    }

    /**
     * 根据菜品类型编码，获取对应的菜品
     * @param dishesTypeCode
     * @return
     */
    public List<MerchantDishes> sqliteMerchantDishesByType(String dishesTypeCode){
        List<MerchantDishes> dishes=dataSetDishes.findWithWhere(MerchantDishes.class,"dishestypecode=?",
                dishesTypeCode);
        for (MerchantDishes dishesInfo : dishes) {
            dishesInfo.getItemlistDb();
            for (DishesProperty dishesItemType : dishesInfo
                    .getDishesItemTypelist()) {
                dishesItemType.getItemlistDb();
            }
        }
        return dishes;
    }

    /**
     * 根据菜品类型编码，获取对应的菜品,加入动态section
     * @param dishesTypeCode
     * @return
     */
    public List<MerchantDishes> sqliteMerchantDishesByTypeWithSection(String dishesTypeCode,int section,int typeIndex){
        List<MerchantDishes> dishes=dataSetDishes.findWithWhere(MerchantDishes.class,"dishestypecode=?",
                dishesTypeCode);
        for (MerchantDishes dishesInfo : dishes) {
            dishesInfo.setSection(section);
            dishesInfo.setTypeIndex(typeIndex);
            dishesInfo.getItemlistDb();
            for (DishesProperty dishesItemType : dishesInfo
                    .getDishesItemTypelist()) {
                dishesItemType.getItemlistDb();
            }
        }
        return dishes;
    }

}
