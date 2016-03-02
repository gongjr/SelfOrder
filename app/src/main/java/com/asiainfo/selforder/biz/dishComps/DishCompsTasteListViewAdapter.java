package com.asiainfo.selforder.biz.dishComps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.asiainfo.selforder.R;
import com.asiainfo.selforder.model.dishes.DishesProperty;
import com.asiainfo.selforder.model.dishes.DishesPropertyItem;

import java.util.List;

/**
 * .
 *
 * @author 姚海林(skynight@dingtalk.com)
 * @version V1.0, 16/2/29 上午9:36
 */
public class DishCompsTasteListViewAdapter extends BaseAdapter {

    private Context context;
    private List<DishesProperty> dishesPropertyList;

    public DishCompsTasteListViewAdapter(Context context, List<DishesProperty> dishesPropertyList) {
        this.context = context;
        this.dishesPropertyList = dishesPropertyList;
    }

    private class DishCompsTasteListViewHolder {
        private TextView textView;
        private GridView gridView;
    }

    @Override
    public int getCount() {
        return dishesPropertyList.size();
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
        DishCompsTasteListViewHolder holder = null;
        if (view == null) {
            holder = new DishCompsTasteListViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.dish_comps_taste_listview, null);
            holder.textView = (TextView) view.findViewById(R.id.dish_comp_taste_name);
            holder.gridView = (GridView) view.findViewById(R.id.dish_comp_taste_gridview);
            view.setTag(holder);
        } else {
            holder = (DishCompsTasteListViewHolder) view.getTag();
        }
        DishesProperty dishesProperty = dishesPropertyList.get(i);
        holder.textView.setText(dishesProperty.getItemTypeName());
        final List<DishesPropertyItem> dishesPropertyItemList = dishesProperty.getItemlist();
        final DishCompsTasteGridViewAdapter adapter = new DishCompsTasteGridViewAdapter(context, dishesPropertyItemList);
        holder.gridView.setAdapter(adapter);

        holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (DishesPropertyItem dishesPropertyItem : dishesPropertyItemList) {
                    dishesPropertyItem.setIsChecked(false);
                }
                dishesPropertyItemList.get(i).setIsChecked(true);
                adapter.notifyDataSetChanged();
            }
        });
        return view;
    }
}
