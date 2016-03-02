package com.asiainfo.selforder.biz.dishes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.asiainfo.selforder.R;

/**
 * Created by gjr on 2016/3/1.
 */

public class ViewHolder extends RecyclerView.ViewHolder{
    public TextView dishesName,dishesSelectedName,dishesDescribe,
            dishesSelectedDescribe,dishesPrice,dishesSelectedPrice,dishesSelectedNum;
    public ImageView dishesImage;
    public RelativeLayout dishesInit,dishesSelected;
    public Button numAdd,numMinus;
    public ViewStub seletedView;
    public boolean isLoad=false;

    public ViewHolder(View itemView){
        super(itemView);
        dishesInit=(RelativeLayout)itemView.findViewById(R.id.item_dishes_init);
        dishesName=(TextView)itemView.findViewById(R.id.item_dishes_name);
        dishesDescribe=(TextView)itemView.findViewById(R.id.item_dishes_describe);
        dishesPrice=(TextView)itemView.findViewById(R.id.item_dishes_price);
        dishesImage=(ImageView)itemView.findViewById(R.id.item_dishes_image);
        seletedView = (ViewStub) itemView.findViewById(R.id.item_dishes_selected);
    }

    public void initViewStud(){
//            对ViewStub的inflate操作只能进行一次，因为inflate的时候是将其指向的布局文件解析inflate并替换掉当前ViewStub本身
//           （由此体现出了ViewStub“占位符”性质），一旦替换后，此时原来的布局文件中就没有ViewStub控件了,而是载入的布局
//            所以要复用的话,需要记录延迟加载的视图,初始化视图中的控件,进行事件处理.
        dishesSelected=(RelativeLayout)seletedView.inflate();
        isLoad=true;
        dishesSelectedName=(TextView)dishesSelected.findViewById(R.id.item_dishess_selected_name);
        dishesSelectedDescribe=(TextView)dishesSelected.findViewById(R.id.item_dishes_selected_describe);
        dishesSelectedPrice=(TextView)dishesSelected.findViewById(R.id.item_dishes_selected_price);
        dishesSelectedNum=(TextView)dishesSelected.findViewById(R.id.item_dishes_selected_num);
        numAdd=(Button)dishesSelected.findViewById(R.id.item_disehs_selected_add);
        numMinus=(Button)dishesSelected.findViewById(R.id.item_disehs_selected_minus);
    }
}
