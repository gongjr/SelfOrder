package com.asiainfo.selforder.biz.dishComps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.asiainfo.selforder.R;
import com.asiainfo.selforder.model.dishComps.DishesCompItem;

import java.util.List;

/**
 * .
 *
 * @author 姚海林(skynight@dingtalk.com)
 * @version V1.0, 16/2/26 下午4:34
 */
public class DishCompsAdapter extends BaseAdapter {

    private Context context;
    private List<DishesCompItem> dishesCompItemList;

    public DishCompsAdapter(Context context, List<DishesCompItem> dishesCompItemList) {
        this.context = context;
        this.dishesCompItemList = dishesCompItemList;
    }

    private class DishCompsViewHolder {
        Button typeBtn;
    }

    @Override
    public int getCount() {
        return dishesCompItemList.size();
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
        DishCompsViewHolder holder = null;
        if (view == null) {
            holder = new DishCompsViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.dish_comps_type_gridview, null);
            holder.typeBtn = (Button) view.findViewById(R.id.dish_comp_type_btn);
            view.setTag(holder);
        } else {
            holder = (DishCompsViewHolder) view.getTag();
        }
        DishesCompItem dishesCompItem = dishesCompItemList.get(i);
        boolean isChecked = dishesCompItem.isChecked();
        if (isChecked) {
            holder.typeBtn.setBackgroundResource(R.drawable.dish_type_selected_bg);
        } else {
            holder.typeBtn.setBackgroundResource(R.drawable.dish_type_bg);
        }
        holder.typeBtn.setText(dishesCompItem.getDishesName());
        return view;
    }
}
