package com.asiainfo.selforder.biz.dishComps;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.asiainfo.selforder.R;
import com.asiainfo.selforder.model.dishComps.DishesComp;
import com.asiainfo.selforder.model.dishComps.DishesCompItem;
import com.asiainfo.selforder.model.dishes.DishesProperty;
import com.asiainfo.selforder.model.dishes.DishesPropertyItem;

import java.util.List;

/**
 * .
 *
 * @author 姚海林(skynight@dingtalk.com)
 * @version V1.0, 16/2/26 下午4:24
 */
public class DishCompsTypeAdapter extends BaseAdapter {

    private Context context;
    private List<DishesComp> dishesCompList;
    private List<DishesProperty> dishesPropertyList;

    public DishCompsTypeAdapter(Context context, List<DishesComp> dishesCompList) {
        this.context = context;
        this.dishesCompList = dishesCompList;
    }

    private class DishCompsTypeViewHolder {
        TextView titleNameText;
        GridView typeGridView;
        ListView tasteListView;
    }

    @Override
    public int getCount() {
        return dishesCompList.size();
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
        DishCompsTypeViewHolder holder = null;
        if (view == null) {
            holder = new DishCompsTypeViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.dish_comps_listview, null);
            holder.titleNameText = (TextView) view.findViewById(R.id.dish_comp_type_name);
            holder.typeGridView = (GridView) view.findViewById(R.id.dish_comp_type_gridview);
            holder.tasteListView = (ListView) view.findViewById(R.id.dish_comp_taste_listview);
            view.setTag(holder);
        } else {
            holder = (DishCompsTypeViewHolder) view.getTag();
        }
        DishesComp dishesComp = dishesCompList.get(i);
        String disheTypeName = dishesComp.getDishesTypeName(); //菜单类型名称
        final String maxSize = dishesComp.getMaxSelect();
        final List<DishesCompItem> dishesCompItemList = dishesComp.getDishesInfoList();//每个类型包含的菜品
        dishesCompItemList.get(0).setIsChecked(true); //默认第一项为选中
        holder.titleNameText.setText(disheTypeName); //设置每一项的名称
        //设置每类菜品的具体菜式
        final DishCompsAdapter adapter = new DishCompsAdapter(context, dishesCompItemList);
        holder.typeGridView.setAdapter(adapter);
        //获取每样菜的口味列表
        dishesPropertyList = dishesCompItemList.get(0).getDishesItemTypelist();
        isListViewVisible(holder.tasteListView, dishesPropertyList);

        final DishCompsTypeViewHolder finalHolder = holder;
        holder.typeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int selectedItem = 0;
                if (Integer.valueOf(maxSize) == 1) {
                    for (DishesCompItem dishesCompItem : dishesCompItemList) {
                        dishesCompItem.setIsChecked(false);
                    }
                    dishesCompItemList.get(i).setIsChecked(true);
                    isListViewVisible(finalHolder.tasteListView, dishesCompItemList.get(i).getDishesItemTypelist());
                    adapter.notifyDataSetChanged();
                } else {
                    for (DishesCompItem dishesCompItem : dishesCompItemList) {
                        if (dishesCompItem.isChecked()) {
                            selectedItem++;
                        }
                    }
                    if (dishesCompItemList.get(i).isChecked()) {
                        selectedItem--;
                        dishesCompItemList.get(i).setIsChecked(false);
                        isListViewVisible(finalHolder.tasteListView, dishesCompItemList.get(i).getDishesItemTypelist());
                        adapter.notifyDataSetChanged();

                    } else {
                        if (selectedItem >= Integer.valueOf(maxSize)) {
                            Toast.makeText(context, "数量不能大于" + maxSize, Toast.LENGTH_SHORT).show();
                        } else {
                            dishesCompItemList.get(i).setIsChecked(true);
                            isListViewVisible(finalHolder.tasteListView, dishesCompItemList.get(i).getDishesItemTypelist());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

        return view;
    }

    /*
    * 判断口味列表是否为空,若为空,将口味布局隐藏,反之显示列表
    * */
    private void isListViewVisible(ListView listView, List<DishesProperty> dishesPropertyList) {
        if (dishesPropertyList.size() != 0) {
            Log.d("1111", "The size is: " + dishesPropertyList.size());
            listView.setVisibility(View.VISIBLE);

            for (DishesProperty dishesProperty : dishesPropertyList) {
                for (DishesPropertyItem dishesPropertyItem : dishesProperty.getItemlist()) {
                    if (dishesPropertyItem.getIsChecked() != 0) {
                        return;
                    }
                }
                dishesProperty.getItemlist().get(0).setIsChecked(1);
            }
            DishCompsTasteListViewAdapter adapter = new DishCompsTasteListViewAdapter(context, dishesPropertyList);
            listView.setAdapter(adapter);
        } else {
            listView.setVisibility(View.GONE);
        }

    }

}
