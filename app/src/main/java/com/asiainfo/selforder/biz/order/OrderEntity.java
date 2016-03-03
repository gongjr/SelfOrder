package com.asiainfo.selforder.biz.order;

import com.asiainfo.selforder.model.MerchantDesk;
import com.asiainfo.selforder.model.MerchantRegister;
import com.asiainfo.selforder.model.dishComps.DishesCompSelectionEntity;
import com.asiainfo.selforder.model.dishes.DishesPropertyItem;
import com.asiainfo.selforder.model.dishes.MerchantDishes;
import com.asiainfo.selforder.model.dishes.MerchantDishesType;
import com.asiainfo.selforder.model.order.OrderGoodsItem;
import com.asiainfo.selforder.model.order.OrderSubmit;
import com.asiainfo.selforder.model.order.PropertySelectEntity;
import com.asiainfo.selforder.model.order.ServerOrder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import kxlive.gjrlibrary.utils.ArithUtils;
import kxlive.gjrlibrary.utils.KLog;
import kxlive.gjrlibrary.utils.StringUtils;

/**
 * Created by gjr on 2015/12/3.
 */
public class OrderEntity {
    private Gson gson;
    private List<OrderGoodsItem> orderGoodsList;
    /**
     * 已选套餐菜
     **/
    private List<DishesCompSelectionEntity> orderCompGoodsList;
    private OrderSubmit mOrderSubmit;
    private MerchantRegister merchantRegister;
    private List<MerchantDishesType> dishesTypeList;
    private MerchantDesk merchantDesk;

    public OrderEntity(MerchantRegister merchantRegister, List<MerchantDishesType> dishesTypeList, MerchantDesk merchantDesk) {
        this.gson = new Gson();
        this.merchantRegister = merchantRegister;
        this.orderGoodsList = new ArrayList<OrderGoodsItem>();
        this.mOrderSubmit = new OrderSubmit();
        this.dishesTypeList = dishesTypeList;
        this.merchantDesk = merchantDesk;
        this.orderCompGoodsList = new ArrayList<DishesCompSelectionEntity>();
        initNewOrderSummaryInfo();

    }

    public void clearOrder() {
        orderGoodsList.clear();
    }

    public List<OrderGoodsItem> getOrderList() {
        return orderGoodsList;
    }

    /**
     * 填充订单概要信息，准备下单
     */
    public void initNewOrderSummaryInfo() {
        mOrderSubmit.setChildMerchantId(StringUtils.str2Long(merchantRegister.getChildMerchantId())); /**子商户id**/
        if (merchantDesk == null) {
            mOrderSubmit.setDeskId("1");
        } else {
            mOrderSubmit.setDeskId(merchantDesk.getDeskId());
        }
        mOrderSubmit.setGiftMoney("0");
        mOrderSubmit.setInMode("2");//2pad点餐,1点餐宝
        mOrderSubmit.setInvoId("0");
        mOrderSubmit.setInvoPrice("0");
        mOrderSubmit.setInvoTitle("发票抬头");
        mOrderSubmit.setIsNeedInvo("1");
        mOrderSubmit.setLinkName("客户姓名");
        mOrderSubmit.setLinkPhone("11111111111");
        mOrderSubmit.setMerchantId(StringUtils.str2Long(merchantRegister.getMerchantId()));
        mOrderSubmit.setOrderState("B");//自主点餐都是保留订单,待通知后厨
        mOrderSubmit.setOrderType("0");
        mOrderSubmit.setOrderTypeName("点餐");
        mOrderSubmit.setOrderid(null); /**商品原价**/
        mOrderSubmit.setPaidPrice("0");
        mOrderSubmit.setPayType("0");
        // mOrderSubmit.setUserId("");
        mOrderSubmit.setPostAddrId("0");
        mOrderSubmit.setRemark(null); //备注在订单确认页面添加
        mOrderSubmit.setTradeStsffId(merchantRegister.getStaffId()); /**操作员工ID**/
        mOrderSubmit.setPersonNum(1); /**订单人数**/
    }


    /**
     * 填充订单概要信息，准备下单
     */
    public void prepareNewOrderSummaryInfo() {
        mOrderSubmit.setAllGoodsNum(getOrderSubmitAllGoodsNum()); /**商品数量**/
        mOrderSubmit.setCreateTime(StringUtils.date2Str(new Date(), StringUtils.TIME_FORMAT_1)); /**订单创建时间**/
        //新单提交，没有已点菜，不计算现有订单的价格
        mOrderSubmit.setOriginalPrice(getOrderSubmitOriginalPrice() + ""); /**商品原价**/
        mOrderSubmit.setOrderGoods(orderGoodsList); /**设置订单商品**/
    }


    /**
     * 根据当前点的菜，更新订单所点菜品信息
     *
     * @param dishesItem
     * @param selectedCount
     */
    public void updateOrderGoodsListData(MerchantDishes dishesItem, int selectedCount, List<PropertySelectEntity> mDishesPropertyChoice) {
        Boolean isExist = false;
        //(1)先判断修改,没有属性的菜,直接增加数量
        for (int i = 0; i < orderGoodsList.size(); i++) {
            OrderGoodsItem goodsItem = orderGoodsList.get(i);
            String gitc = goodsItem.getDishesTypeCode();
            String ditc = dishesItem.getDishesTypeCode();
            String gidid = goodsItem.getSalesId();
            String didid = dishesItem.getDishesId();
            boolean hasProp = true;
            if (goodsItem.getRemark() == null || goodsItem.getRemark().size() == 0) {
                hasProp = false;
                //没有属性细项标签
            }

            /**
             * 如果该菜在所选菜中已经存在，并且没有属性，则直接设置数量
             */
            if (gitc != null && ditc != null && gitc.equals(ditc)
                    && gidid != null && didid != null && gidid.equals(didid) && !hasProp) {
                goodsItem.setSalesNum(selectedCount);
                goodsItem.setSalesPrice("" + ArithUtils.d2str(selectedCount * StringUtils.str2Double(goodsItem.getDishesPrice())));
                List<String> remarkList = updateOrderGoodsRemarkTypeObj(mDishesPropertyChoice);
                goodsItem.setRemark(remarkList); //全量更新
                if (selectedCount == 0) {
                    orderGoodsList.remove(i); //如果为0， 则从订单菜品列表中删除
                } else {
                    orderGoodsList.set(i, goodsItem);
                }
                isExist = true;
                break;
            }
        }

        /**
         * 如果该菜没有选过，或者选过，但是有属性，则重新添加一份
         */
        if (!isExist) {
            OrderGoodsItem goodsItem = new OrderGoodsItem();
            goodsItem.setCompId("0");  //非套餐
            goodsItem.setTradeStaffId(merchantRegister.getStaffId());
            if (merchantDesk == null) {
                goodsItem.setDeskId("1");
            } else {
                goodsItem.setDeskId(merchantDesk.getDeskId());
            }
            goodsItem.setDishesPrice(dishesItem.getDishesPrice());
            goodsItem.setDishesTypeCode(dishesItem.getDishesTypeCode());
            goodsItem.setExportId(dishesItem.getExportId());
            goodsItem.setInstanceId("" + System.currentTimeMillis());
            goodsItem.setInterferePrice("0");
            goodsItem.setTakeaway(false);
            goodsItem.setOrderId("");
            List<String> remarkList = updateOrderGoodsRemarkTypeObj(mDishesPropertyChoice);
            goodsItem.setRemark(remarkList);
            goodsItem.setSalesId(dishesItem.getDishesId());
            goodsItem.setSalesName(dishesItem.getDishesName());
            goodsItem.setSalesNum(selectedCount);
            goodsItem.setSalesPrice("" + ArithUtils.d2str(selectedCount * StringUtils.str2Double(dishesItem.getDishesPrice())));
            goodsItem.setSalesState("0");  //0稍后下单  1立即下单
            Boolean isComp = false;
            if (dishesItem.getIsComp() == 1) {
                isComp = true;
            }
            goodsItem.setIsCompDish("" + isComp);
            goodsItem.setAction("1"); //订单操作类型，增删改
            goodsItem.setIsZdzk(dishesItem.getIsZdzk()); //是否参与整单折扣
            goodsItem.setMemberPrice(dishesItem.getMemberPrice()); //会员价
            orderGoodsList.add(goodsItem);
        }
    }

    //删除有属性菜,删除最后加入的
    public void clearPropetyItem(MerchantDishes merchantDishes) {
        Collections.reverse(orderGoodsList);//倒序
        for (int i = 0; i < orderGoodsList.size(); i++) {
            OrderGoodsItem goodsItem = orderGoodsList.get(i);
            String gitc = goodsItem.getDishesTypeCode();
            String ditc = merchantDishes.getDishesTypeCode();
            String gidid = goodsItem.getSalesId();
            String didid = merchantDishes.getDishesId();
            if (gitc != null && ditc != null && gitc.equals(ditc)
                    && gidid != null && didid != null && gidid.equals(didid)) {
                orderGoodsList.remove(i); //则从订单菜品列表中删除
                break;
            }
        }
        Collections.reverse(orderGoodsList);//删除对应元素后在倒序恢复原来顺序
    }


    /**
     * 将属性一属性对象的json字符串形式保存
     *
     * @param mDishesPropertyChoice
     * @return
     */
    private List<String> updateOrderGoodsRemarkTypeObj(List<PropertySelectEntity> mDishesPropertyChoice) {
        List<String> remarkList = new ArrayList<String>();
        if (mDishesPropertyChoice != null)
            for (int m = 0; m < mDishesPropertyChoice.size(); m++) {
                PropertySelectEntity psEntity = mDishesPropertyChoice.get(m);
                String propertyItem = gson.toJson(psEntity);
                KLog.i("选中--" + psEntity.getItemType() + ":" + propertyItem);
                remarkList.add(propertyItem);
            }
        return remarkList;
    }

    /**
     * 计算订单的商品总数
     *
     * @return
     */
    public int getOrderSubmitAllGoodsNum() {
        int num = 0;
        for (int i = 0; i < orderGoodsList.size(); i++) {
            OrderGoodsItem goodsItem = orderGoodsList.get(i);
            num = num + goodsItem.getSalesNum();
        }
        for (DishesCompSelectionEntity dishesCompSelectionEntity : orderCompGoodsList) {
            num = num + dishesCompSelectionEntity.getmCompMainDishes().getSalesNum();
        }
        return num;
    }

    /**
     * 计算订单的商品总价格
     *
     * @return
     */
    public String getOrderSubmitOriginalPrice() {
        Double sumPrice = 0.0;
        for (int i = 0; i < orderGoodsList.size(); i++) {
            OrderGoodsItem goodsItem1 = orderGoodsList.get(i);
            sumPrice = sumPrice + StringUtils.str2Double(goodsItem1.getSalesPrice());
        }
        for (DishesCompSelectionEntity dishesCompSelectionEntity : orderCompGoodsList) {
            sumPrice = sumPrice + StringUtils.str2Double(dishesCompSelectionEntity.getmCompMainDishes().getSalesPrice());
        }
        return ArithUtils.d2str(sumPrice);
    }

    public ShoppingOrder getShoppingOrderByList() {
        ShoppingOrder shopping = new ShoppingOrder();
        for (MerchantDishesType dishesType : dishesTypeList) {
            ArrayList<OrderGoodsItem> goods = new ArrayList<OrderGoodsItem>();
            //过滤出对应类型的菜品数据
            for (OrderGoodsItem ordergoods : orderGoodsList) {
                if (ordergoods.getDishesTypeCode().equals(dishesType.getDishesTypeCode()))
                    goods.add(ordergoods);
            }

            //获取套餐列表
            for (DishesCompSelectionEntity orderCompsGoods : orderCompGoodsList) {
                //套餐主菜
                OrderGoodsItem compMainDishes = orderCompsGoods.getmCompMainDishes();
                //套餐子菜
                List<OrderGoodsItem> compItemDishes = orderCompsGoods.getCompItemDishes();
                if (compMainDishes.getDishesTypeCode().equals(dishesType.getDishesTypeCode())) {
                    String mRemark = "";
                    List<String> remarkList;
                    if (compMainDishes.getRemark() != null) {
                        remarkList = compMainDishes.getRemark();
                    } else {
                        remarkList = new ArrayList<String>();
                    }
                    for (OrderGoodsItem compItemDish : compItemDishes) {
                        mRemark += " " + compItemDish.getSalesName();
                        if (compItemDish.getRemark() != null) {
                            String tasteString = "";
                            for (String remark : compItemDish.getRemark()) {
                                tasteString += " " + remark;
                            }
                            mRemark += "(" + tasteString + ")";
                        }
                    }
                    remarkList.add(mRemark);
                    compMainDishes.setRemark(remarkList);
                    goods.add(compMainDishes);
                }
            }

            //倘若有,则配置头部,加入对应数据
            if (goods.size() > 0) {
                shopping.getmHeaderPositions().add(shopping.getOrderGoods().size());
                shopping.getmHeaderNames().add(dishesType.getDishesTypeName());
                for (OrderGoodsItem ordergoods : goods) {
                    shopping.getOrderGoods().add(ordergoods);
                }
            }
        }
        return shopping;
    }

    public void deleteOrderGoodsItem(OrderGoodsItem orderGoodsItem) {
        if (orderGoodsItem.getIsCompDish().equals("true")) {
            for (int i = 0; i < orderCompGoodsList.size(); i++) {
                if (orderGoodsItem.getSalesId().equals(orderCompGoodsList.get(i).getmCompMainDishes().getSalesId()) &&
                        orderGoodsItem.getDishesTypeCode().equals(orderCompGoodsList.get(i).getmCompMainDishes().getDishesTypeCode())
                        && orderGoodsItem.getInstanceId().equals(orderCompGoodsList.get(i).getmCompMainDishes().getInstanceId())) {
                    orderCompGoodsList.remove(i);
                    break;
                }
            }
        } else {
            for (int i = 0; i < orderGoodsList.size(); i++) {
                if (orderGoodsItem.getSalesId().equals(orderGoodsList.get(i).getSalesId()) &&
                        orderGoodsItem.getDishesTypeCode().equals(orderGoodsList.get(i).getDishesTypeCode())
                        && orderGoodsItem.getInstanceId().equals(orderGoodsList.get(i).getInstanceId())) {
                    orderGoodsList.remove(i);
                    break;
                }
            }
        }
    }

    public void changeOrderGoodsItemTakwawayState(OrderGoodsItem orderGoodsItem, boolean istakeaway) {
        if (orderGoodsItem.getIsCompDish().equals("true")) {
            for (int i = 0; i < orderCompGoodsList.size(); i++) {
                if (orderGoodsItem.getSalesId().equals(orderCompGoodsList.get(i).getmCompMainDishes().getSalesId()) &&
                        orderGoodsItem.getDishesTypeCode().equals(orderCompGoodsList.get(i).getmCompMainDishes().getDishesTypeCode())
                        && orderGoodsItem.getInstanceId().equals(orderCompGoodsList.get(i).getmCompMainDishes().getInstanceId())) {
                    orderCompGoodsList.get(i).getmCompMainDishes().setTakeaway(istakeaway);
                    break;
                }
            }
        } else {
            for (int i = 0; i < orderGoodsList.size(); i++) {
                if (orderGoodsItem.getSalesId().equals(orderGoodsList.get(i).getSalesId()) &&
                        orderGoodsItem.getDishesTypeCode().equals(orderGoodsList.get(i).getDishesTypeCode())
                        && orderGoodsItem.getInstanceId().equals(orderGoodsList.get(i).getInstanceId())) {
                    orderGoodsList.get(i).setTakeaway(istakeaway);
                    break;
                }
            }
        }

    }

    public void clearAllChecked() {
        for (OrderGoodsItem orderGoodsItem : orderGoodsList) {
            orderGoodsItem.setTakeaway(false);
        }
        for (DishesCompSelectionEntity orderCompGoods : orderCompGoodsList) {
            orderCompGoods.getmCompMainDishes().setTakeaway(false);
        }
    }

    public void setAllChecked() {
        for (OrderGoodsItem orderGoodsItem : orderGoodsList) {
            orderGoodsItem.setTakeaway(true);
        }
        for (DishesCompSelectionEntity orderCompGoods : orderCompGoodsList) {
            orderCompGoods.getmCompMainDishes().setTakeaway(true);
        }
    }


    /**
     * 准备订单数据信息, 返回提交数据
     */
    public String prepareOrderSummaryInfo() {
        prepareNewOrderSummaryInfo();
        OrderSubmit Submit = mOrderSubmit;
        List<OrderGoodsItem> mCommitList = new ArrayList<OrderGoodsItem>();
        for (int i = 0; i < orderGoodsList.size(); i++) {
            OrderGoodsItem goodsItem = new OrderGoodsItem();
            goodsItem.setCompId(orderGoodsList.get(i).getCompId());  //非套餐
            goodsItem.setTradeStaffId(orderGoodsList.get(i).getTradeStaffId());
            goodsItem.setDeskId(orderGoodsList.get(i).getDeskId());
            goodsItem.setDishesPrice(orderGoodsList.get(i).getDishesPrice());
            goodsItem.setDishesTypeCode(orderGoodsList.get(i).getDishesTypeCode());
            goodsItem.setExportId(orderGoodsList.get(i).getExportId());
            goodsItem.setInstanceId(orderGoodsList.get(i).getInstanceId());
            goodsItem.setInterferePrice(orderGoodsList.get(i).getInterferePrice());
            goodsItem.setOrderId(orderGoodsList.get(i).getOrderId());
            goodsItem.setSalesId(orderGoodsList.get(i).getSalesId());
            goodsItem.setSalesName(orderGoodsList.get(i).getSalesName());
            goodsItem.setSalesNum(orderGoodsList.get(i).getSalesNum());
            goodsItem.setSalesPrice(orderGoodsList.get(i).getSalesPrice());
            goodsItem.setSalesState(orderGoodsList.get(i).getSalesState());
            goodsItem.setIsCompDish(orderGoodsList.get(i).getIsCompDish());
            goodsItem.setAction(orderGoodsList.get(i).getAction());
            goodsItem.setIsZdzk(orderGoodsList.get(i).getIsZdzk());
            goodsItem.setMemberPrice(orderGoodsList.get(i).getMemberPrice());
            List<String> remarkCommit = fromItemEntityList2RemarkCommit(orderGoodsList.get(i).getRemark());
            if (orderGoodsList.get(i).isTakeaway()) remarkCommit.add("外卖");
            goodsItem.setRemark(remarkCommit);
            mCommitList.add(goodsItem);
        }

        for (int i = 0; i < orderCompGoodsList.size(); i++) {
            DishesCompSelectionEntity orderCompGoods = orderCompGoodsList.get(i);
            OrderGoodsItem goodsItem = new OrderGoodsItem();
            goodsItem.setCompId(orderCompGoods.getmCompMainDishes().getCompId());
            goodsItem.setTradeStaffId(orderCompGoods.getmCompMainDishes().getTradeStaffId());
            goodsItem.setDeskId(orderCompGoods.getmCompMainDishes().getDeskId());
            goodsItem.setDishesPrice(orderCompGoods.getmCompMainDishes().getDishesPrice());
            goodsItem.setDishesTypeCode(orderCompGoods.getmCompMainDishes().getDishesTypeCode());
            goodsItem.setExportId(orderCompGoods.getmCompMainDishes().getExportId());
            goodsItem.setInstanceId(orderCompGoods.getmCompMainDishes().getInstanceId());
            goodsItem.setInterferePrice(orderCompGoods.getmCompMainDishes().getInterferePrice());
            goodsItem.setOrderId(orderCompGoods.getmCompMainDishes().getOrderId());
            goodsItem.setSalesId(orderCompGoods.getmCompMainDishes().getSalesId());
            goodsItem.setSalesName(orderCompGoods.getmCompMainDishes().getSalesName());
            goodsItem.setSalesNum(orderCompGoods.getmCompMainDishes().getSalesNum());
            goodsItem.setSalesPrice(orderCompGoods.getmCompMainDishes().getSalesPrice());
            goodsItem.setSalesState(orderCompGoods.getmCompMainDishes().getSalesState());
            goodsItem.setIsCompDish(orderCompGoods.getmCompMainDishes().getIsCompDish());
            goodsItem.setAction(orderCompGoods.getmCompMainDishes().getAction());
            goodsItem.setIsZdzk(orderCompGoods.getmCompMainDishes().getIsZdzk());
            goodsItem.setMemberPrice(orderCompGoods.getmCompMainDishes().getMemberPrice());
//            List<String> remarkCommit = fromItemEntityList2RemarkCommit(orderCompGoods.getmCompMainDishes().getRemark());
            List<String> remarkCommit;
            if (orderCompGoods.getmCompMainDishes().getRemark() == null) {
                remarkCommit = new ArrayList<String>();
            } else {
                remarkCommit = orderCompGoods.getmCompMainDishes().getRemark();
            }
            List<OrderGoodsItem> compItemDishes = orderCompGoods.getCompItemDishes();
            String remark="";
            for (OrderGoodsItem compItemDish: compItemDishes) {
                remark += " " + compItemDish.getSalesName();
                if (compItemDish.getRemark() != null) {
                    String tasteString = "";
                    for (String taste: compItemDish.getRemark()) {
                        tasteString += " " + taste;
                    }
                    remark += "(" + tasteString + ")";
                }

            }
            remarkCommit.add(remark);
            if (orderCompGoods.getmCompMainDishes().isTakeaway()) {
                remarkCommit.add("外卖");
            }
            goodsItem.setRemark(remarkCommit);
            mCommitList.add(goodsItem);
        }

        Submit.setOrderGoods(mCommitList);
        String orderData = gson.toJson(Submit);
        return orderData;
    }

    /**
     * 从属性实体的值中解析属性
     *
     * @param remarkList
     * @return
     */
    public List<String> fromItemEntityList2RemarkCommit(List<String> remarkList) {
        List<String> rmkList = new ArrayList<String>();
        if (remarkList != null && remarkList.size() > 0) {
            Gson gson = new Gson();
            for (int m = 0; m < remarkList.size(); m++) {
                try {
                    String reItem = remarkList.get(m);
                    if (reItem != null && !reItem.equals("")
                            && !reItem.equals("[]")) {
                        PropertySelectEntity entityItem = gson.fromJson(reItem, PropertySelectEntity.class);
                        if (entityItem != null) {
                            List<DishesPropertyItem> dpiList = entityItem.getmSelectedItemsList();
                            if (dpiList != null && dpiList.size() > 0) {
                                for (int n = 0; n < dpiList.size(); n++) {
                                    DishesPropertyItem dpItem = dpiList.get(n);
                                    rmkList.add(dpItem.getItemName());
                                }
                            }
                        }
                    }
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                    KLog.i("提交失败，请重试!");
                } finally {
                    KLog.i("执行了备注解析过程");
                }
            }
        }
        return rmkList;
    }

    public void updateOrderInfo(String orderid) {
        mOrderSubmit.setOrderid(orderid);
    }

    public ServerOrder getNotifyOrderInfo() {
        ServerOrder notifyOrder = new ServerOrder();
        notifyOrder.setDeskId(Long.valueOf(mOrderSubmit.getDeskId()));
        notifyOrder.setOrderId(Long.valueOf(mOrderSubmit.getOrderid()));
        notifyOrder.setPersonNum(mOrderSubmit.getPersonNum());
        notifyOrder.setTradeStsffId(mOrderSubmit.getTradeStsffId());
        notifyOrder.setCreateTime(mOrderSubmit.getCreateTime());
        notifyOrder.setOriginalPrice(Long.valueOf(mOrderSubmit.getOriginalPrice()));
        notifyOrder.setMerchantId(mOrderSubmit.getMerchantId());
        notifyOrder.setChildMerchantId(mOrderSubmit.getChildMerchantId() + "");
        return notifyOrder;
    }

    public String getOrderId() {
        return mOrderSubmit.getOrderid();
    }

    public List<DishesCompSelectionEntity> getOrderCompGoodsList() {
        return orderCompGoodsList;
    }

    public void addOrderCompGoods(DishesCompSelectionEntity dishesCompSelectionEntity) {
        this.orderCompGoodsList.add(dishesCompSelectionEntity);
//        this.orderGoodsList.add(dishesCompSelectionEntity.getmCompMainDishes());
    }

    public void deleteOrderCompGoods(int position) {
        this.orderCompGoodsList.remove(position);
    }

    public void deleteOrderCompGoods() {
        this.orderCompGoodsList.remove(this.orderCompGoodsList.size() - 1);
    }

    public void clearOrderCompGoods() {
        this.orderCompGoodsList.clear();
    }
}
