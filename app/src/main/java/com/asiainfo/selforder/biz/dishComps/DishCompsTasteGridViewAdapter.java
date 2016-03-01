package com.asiainfo.selforder.biz.dishComps;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.asiainfo.selforder.R;
import com.asiainfo.selforder.model.dishes.DishesPropertyItem;

import java.util.List;

/**
 * .
 *
 * @author 姚海林(skynight@dingtalk.com)
 * @version V1.0, 16/2/29 上午10:58
 */
public class DishCompsTasteGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<DishesPropertyItem> dishesPropertyItemList;

    public DishCompsTasteGridViewAdapter(Context context, List<DishesPropertyItem> dishesPropertyItemList) {
        this.context = context;
        this.dishesPropertyItemList = dishesPropertyItemList;
    }

    public class DishCompsTasteGridViewHolder {
        TextView textView;
    }

    @Override
    public int getCount() {
        return dishesPropertyItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        DishCompsTasteGridViewHolder holder = null;
        if (view == null) {
            holder = new DishCompsTasteGridViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.dish_comps_taste_gridview, null);
            holder.textView = (TextView) view.findViewById(R.id.dish_comp_taste_type_name);
            view.setTag(holder);
        } else {
            holder = (DishCompsTasteGridViewHolder) view.getTag();
        }
        DishesPropertyItem dishesPropertyItem = dishesPropertyItemList.get(i);
        holder.textView.setText(dishesPropertyItem.getItemName());
        //设置显示的图片
        Drawable drawable = context.getResources().getDrawable(R.drawable.taste_bg);
        Drawable drawable1 = context.getResources().getDrawable(R.drawable.taste_selected_bg);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawable1.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        holder.textView.setCompoundDrawables(null, null, drawable, null);
        //根据口味是否选中,设置对应图片
        if (dishesPropertyItem.isChecked()) {
            holder.textView.setCompoundDrawables(null, null, drawable1, null);
        } else {
            holder.textView.setCompoundDrawables(null, null, drawable, null);
        }
        return view;
    }
}
