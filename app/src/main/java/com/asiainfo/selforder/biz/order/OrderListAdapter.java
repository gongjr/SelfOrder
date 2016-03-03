package com.asiainfo.selforder.biz.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.asiainfo.selforder.R;
import com.asiainfo.selforder.model.Listener.OnItemClickListener;
import com.asiainfo.selforder.model.dishes.DishesPropertyItem;
import com.asiainfo.selforder.model.order.OrderGoodsItem;
import com.asiainfo.selforder.model.order.PropertySelectEntity;
import com.asiainfo.selforder.ui.MakeOrderActivity;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by gjr on 2015/12/8.
 */
public class OrderListAdapter extends BaseAdapter {

    private List<OrderGoodsItem> orderGoodsList;
    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;
    //    private List<DishesCompSelectionEntity> orderCompsGoodsList;
    //    private Map<String,Boolean> checkMap =new HashMap<String,Boolean>();
    private Gson gson = new Gson();
    private MakeOrderActivity context;

    public OrderListAdapter(LayoutInflater mInflater) {
        this.mInflater = mInflater;
    }

    public void setData(List<OrderGoodsItem> orderGoodsList) {
        this.orderGoodsList = orderGoodsList;
    }

//    public void setOrderCompsData(List<DishesCompSelectionEntity> orderCompsGoodsList) {
//        this.orderCompsGoodsList = orderCompsGoodsList;
//    }

    public void setContext(MakeOrderActivity context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return orderGoodsList.size();
    }

    @Override
    public Object getItem(int i) {
        return orderGoodsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(
                    R.layout.item_ordergoodsitem, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView
                    .findViewById(R.id.ordergoodsitem_dishesname);
            viewHolder.price = (TextView) convertView
                    .findViewById(R.id.ordergoodsitem_dishesprice);
            viewHolder.num = (TextView) convertView
                    .findViewById(R.id.ordergoodsitem_dishesnum);
            viewHolder.istakeaway = (CheckBox) convertView
                    .findViewById(R.id.makeorder_item_takeaway_check);
            viewHolder.delete = (Button) convertView
                    .findViewById(R.id.makeorder_item_delete);
            viewHolder.remark = (TextView) convertView
                    .findViewById(R.id.ordergoodsitem_remark);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final OrderGoodsItem orderGoodsItem = orderGoodsList.get(position);
        viewHolder.name.setText(orderGoodsItem.getSalesName());
        viewHolder.price.setText(orderGoodsItem.getSalesPrice());
        viewHolder.num.setText(orderGoodsItem.getSalesNum() + "");
        viewHolder.istakeaway.setChecked(orderGoodsItem.isTakeaway());
        List<String> remarkList = orderGoodsItem.getRemark();
        if (orderGoodsItem.isComp()) {
            String str = "配置: ";
            for (String remark : remarkList) {
                str += remark;
            }
            viewHolder.remark.setText(str);
            viewHolder.remark.setVisibility(View.VISIBLE);
        } else {
            if (remarkList != null && remarkList.size() > 0) {
                viewHolder.remark.setVisibility(View.VISIBLE);
                String remarkBuffer = listToString(remarkList);
                viewHolder.remark.setText(remarkBuffer);
            } else {
                viewHolder.remark.setVisibility(View.GONE);
            }
        }

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(view, position, orderGoodsItem);
            }
        });
        viewHolder.istakeaway.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                orderGoodsItem.setTakeaway(b);
                compoundButton.setTag(b);
                mOnItemClickListener.onItemClick(compoundButton, position, orderGoodsItem);
            }
        });
        return convertView;
    }

    public void clearAllChecked() {
        for (OrderGoodsItem orderGoodsItem : orderGoodsList) {
            orderGoodsItem.setTakeaway(false);
        }
        notifyDataSetChanged();
    }

    public void setAllChecked() {
        for (OrderGoodsItem orderGoodsItem : orderGoodsList) {
            orderGoodsItem.setTakeaway(true);
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public String listToString(List<String> remarkList) {
        String remarkBuffer = "";
        for (int i = 0; i < remarkList.size(); i++) {
            String itemProperty = remarkList.get(i);
            PropertySelectEntity psEntity = gson.fromJson(itemProperty, PropertySelectEntity.class);
            if (psEntity.getmSelectedItemsList() != null && psEntity.getmSelectedItemsList().size() > 0) {
                String property_type = psEntity.getmSelectedItemsList().get(0).getItemTypeName();
                if (i != 0)
                    remarkBuffer += " | " + property_type + ":";
                else remarkBuffer += property_type + ":";
                for (int m = 0; m < psEntity.getmSelectedItemsList().size(); m++) {
                    DishesPropertyItem dishesPropertyItem = psEntity.getmSelectedItemsList().get(m);
                    if (m == psEntity.getmSelectedItemsList().size() - 1) {
                        remarkBuffer += dishesPropertyItem.getItemName();
                    } else {
                        remarkBuffer += dishesPropertyItem.getItemName() + "、";
                    }
                }
            }
        }
        return remarkBuffer;
    }

    class ViewHolder {
        TextView name, price, num, remark;
        CheckBox istakeaway;
        Button delete;
    }
}
