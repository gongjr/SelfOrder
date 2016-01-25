package com.asiainfo.selforder.biz.dishes;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asiainfo.selforder.R;
import com.asiainfo.selforder.model.Listener.OnItemClickListener;
import com.asiainfo.selforder.model.dishes.MerchantDishesType;

import java.util.List;

/**
 * Created by gjr on 2015/12/1.
 */
public class DishesTypeAdapter extends Adapter<DishesTypeAdapter.ViewHolder>{

    private LayoutInflater mInflater;
    private Resources res;
    private List<MerchantDishesType> dishesTypeList;
    private int selectedPos = -1;
    private OnItemClickListener<MerchantDishesType> mOnItemClickListenerT;
    private int sBgColor, nBgColor,sTextColor,nTextColor;
    private Drawable sBgDrawable;

    public DishesTypeAdapter(LayoutInflater inflater,List<MerchantDishesType> typeList,Resources mRes){
        this.mInflater=inflater;
        this.dishesTypeList=typeList;
        this.res=mRes;
        this.selectedPos=0;
        this.sBgColor =res.getColor(R.color.item_dishes_type_name_bg_s);
        this.nBgColor =res.getColor(R.color.item_dishes_type_name_bg);
        this.sTextColor =res.getColor(R.color.item_dishes_type_name_text_s);
        this.nTextColor =res.getColor(R.color.item_dishes_type_name_text);
        this.sBgDrawable=res.getDrawable(R.drawable.dishes_type_bg_s_);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view=mInflater.inflate(R.layout.item_dishes_type,null);
        ViewHolder holder = new ViewHolder(view);
        holder.setIsRecyclable(true);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
         viewHolder.dishesTypeName.setText(dishesTypeList.get(position).getDishesTypeName());
        if(selectedPos==position){
            viewHolder.dishesTypeName.setBackground(sBgDrawable);
            viewHolder.dishesTypeName.setTextColor(sTextColor);
        }else{
            viewHolder.dishesTypeName.setBackgroundColor(nBgColor);
            viewHolder.dishesTypeName.setTextColor(nTextColor);

        }
        viewHolder.dishesTypeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListenerT.onItemClick(view,position,dishesTypeList.get(position));
                setSelectedPos(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dishesTypeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView dishesTypeName;

        public ViewHolder(View itemView){
            super(itemView);
            dishesTypeName=(TextView)itemView.findViewById(R.id.item_dishes_type_name);
        }
    }

    public void setmOnItemClickListenerT(OnItemClickListener<MerchantDishesType> mOnItemClickListener){
         this.mOnItemClickListenerT=mOnItemClickListener;
    }

    public void setSelectedPos(int pos){
        this.selectedPos=pos;
        notifyDataSetChanged();
    }
}
