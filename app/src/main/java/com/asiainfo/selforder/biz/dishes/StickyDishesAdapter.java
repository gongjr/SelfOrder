package com.asiainfo.selforder.biz.dishes;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.asiainfo.selforder.R;
import com.asiainfo.selforder.model.Listener.OnChangeWithPropertyListener;
import com.asiainfo.selforder.model.Listener.OnPropertyItemClickListener;
import com.asiainfo.selforder.model.Listener.OnStickyHeaderChangeListener;
import com.asiainfo.selforder.model.dishComps.DishesCompSelectionEntity;
import com.asiainfo.selforder.model.dishes.DishesProperty;
import com.asiainfo.selforder.model.dishes.MerchantDishes;
import com.asiainfo.selforder.model.order.OrderGoodsItem;
import com.asiainfo.selforder.model.order.PropertySelectEntity;
import com.asiainfo.selforder.ui.ChoosePropertyFragment;
import com.asiainfo.selforder.ui.DishCompsActivity;
import com.asiainfo.selforder.ui.DishesMenuActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kxlive.gjrlibrary.utils.KLog;

/**
 * Created by gjr on 2015/12/1.
 */
public class StickyDishesAdapter extends BaseAdapter implements
        StickyGridHeadersSimpleAdapter {

    private LayoutInflater mInflater;
    private Resources res;
    private List<MerchantDishes> itemList;
    private OnPropertyItemClickListener<MerchantDishes> numChangeOnItemClick;
    private OnStickyHeaderChangeListener<MerchantDishes> stickyHeaderChangeListener;
    private Map<String,Integer> numMap;
    private FragmentActivity mActivity;
    private boolean init;
    private DishesMenuActivity context;

    public StickyDishesAdapter(LayoutInflater inflater, Resources mRes, FragmentActivity mActivity, DishesMenuActivity context){
        this.mInflater=inflater;
        this.res=mRes;
        this.mActivity=mActivity;
        this.numMap=new HashMap<String,Integer>();
        this.init=true;
        this.context = context;
    }

    public StickyDishesAdapter(LayoutInflater inflater, List<MerchantDishes> typeList, Resources mRes, FragmentActivity mActivity, DishesMenuActivity context){
        this.mInflater=inflater;
        this.itemList=typeList;
        this.res=mRes;
        this.numMap=new HashMap<String,Integer>();
        this.mActivity=mActivity;
        this.init=true;
        this.context = context;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public long getHeaderId(int position) {
        return Long.valueOf(itemList.get(position).getSection());
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {

        HeaderViewHolder mHeaderHolder;
        if (convertView == null) {
            mHeaderHolder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.dishes_header, parent, false);
            mHeaderHolder.mTextView = (TextView) convertView
                    .findViewById(R.id.header);
            convertView.setTag(mHeaderHolder);
        } else {
            mHeaderHolder = (HeaderViewHolder) convertView.getTag();
        }
        if (init) {
            init=false;
            mHeaderHolder.mTextView.setText(itemList.get(position).getDishesTypeName());
            mHeaderHolder.mTextView.setTag(itemList.get(position).getDishesTypeCode());
        }else{
            String curTag=(String)mHeaderHolder.mTextView.getTag();//Tag必须是类型唯一表示
            if(!itemList.get(position).getDishesTypeCode().equals(curTag)){
                mHeaderHolder.mTextView.setText(itemList.get(position).getDishesTypeName());
                mHeaderHolder.mTextView.setTag(itemList.get(position).getDishesTypeCode());
            }
        }
        return convertView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.item_dishes, parent, false);
            viewHolder=new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final MerchantDishes merchantDishes=itemList.get(position);
        int curNum=getNum(merchantDishes);
        if(curNum>0){
            if(!viewHolder.isLoad){
                viewHolder.initViewStud();
            }
            viewHolder.dishesInit.setVisibility(View.INVISIBLE);
            viewHolder.dishesSelected.setVisibility(View.VISIBLE);
            loadViewHolderWithDishes(viewHolder, merchantDishes,position);
        }else{
            viewHolder.dishesInit.setVisibility(View.VISIBLE);
            if (viewHolder.isLoad) viewHolder.dishesSelected.setVisibility(View.INVISIBLE);
            viewHolder.dishesName.setText(merchantDishes.getDishesName());
            viewHolder.dishesDescribe.setText(merchantDishes.getDishesTypeName());
            viewHolder.dishesPrice.setText(merchantDishes.getDishesPrice());
            viewHolder.dishesImage.setTag(R.id.tag_first, position);
            viewHolder.dishesImage.setTag(R.id.tag_second,merchantDishes);
//            ImageLoader.getInstance().displayImage(
//                    merchantDishes.getDishesUrl(), viewHolder.dishesImage,
//                    options, mImageLoadingListener);
            viewHolder.dishesInit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<DishesProperty> propertyList = merchantDishes.getDishesItemTypelist();
                    if(!viewHolder.isLoad){
                        viewHolder.initViewStud();
                    }

                    if (merchantDishes.getIsComp() != 0) {
                        view.setVisibility(View.VISIBLE);
                        viewHolder.dishesSelected.setVisibility(View.INVISIBLE);
                        context.test(viewHolder, position);
                        DishCompsActivity.actionStart(mActivity, merchantDishes);

                    } else if(propertyList!=null && propertyList.size()>0){
                        if(ChoosePropertyFragment.getInstance()!=null&&!ChoosePropertyFragment.getInstance().isVisible()){
                        loadViewHolderWithDishes(viewHolder, merchantDishes,position);
                        view.setVisibility(View.VISIBLE);
                        viewHolder.dishesSelected.setVisibility(View.INVISIBLE);
                        ChoosePropertyFragment mChoosePropertyValueDF = ChoosePropertyFragment.newInstance(merchantDishes);
                        mChoosePropertyValueDF.setOnCheckedPropertyItemsListener(mOnChangeWithPropertyListener,viewHolder);
                        mChoosePropertyValueDF.show(mActivity.getSupportFragmentManager(), "choose_property");
                        }else{
                            KLog.i("窗口已经显示,无视此次事件");
                        }
                    }else{
                        int num=addNum(merchantDishes);
                        numChangeOnItemClick.onItemClick(view, num, merchantDishes, null);
                        view.setVisibility(View.INVISIBLE);
                        viewHolder.dishesSelected.setVisibility(View.VISIBLE);
                        loadViewHolderWithDishes(viewHolder, merchantDishes,position);
                    }

                }
            });
        }
        return convertView;
    }

    public void loadViewHolderWithDishes(final ViewHolder viewHolder,final MerchantDishes merchantDishes,final int position){
        int num=getNum(merchantDishes);
        if(viewHolder.isLoad){
            viewHolder.dishesSelectedName.setText(merchantDishes.getDishesName());
            viewHolder.dishesSelectedDescribe.setText(merchantDishes.getDishesTypeName());
            viewHolder.dishesSelectedPrice.setText(merchantDishes.getDishesPrice());
            viewHolder.dishesSelectedNum.setText(num+"");
            viewHolder.numAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<DishesProperty> propertyList = merchantDishes.getDishesItemTypelist();
                    if (merchantDishes.getIsComp() != 0) {
                        DishCompsActivity.actionStart(mActivity, merchantDishes);
                    } else if(propertyList!=null && propertyList.size()>0){
                        if(ChoosePropertyFragment.getInstance()!=null&&!ChoosePropertyFragment.getInstance().isVisible()){
                        ChoosePropertyFragment mChoosePropertyValueDF = ChoosePropertyFragment.newInstance(merchantDishes);
                        mChoosePropertyValueDF.setOnCheckedPropertyItemsListener(mOnChangeWithPropertyListener,viewHolder);
                        mChoosePropertyValueDF.show(mActivity.getSupportFragmentManager(), "choose_property");
                        }else  KLog.i("窗口已经显示,无视此次事件");
                    }else{
                    int num=addNum(merchantDishes);
                    viewHolder.dishesSelectedNum.setText(num+"");
                    numChangeOnItemClick.onItemClick(view, num, merchantDishes, null);
                    }
                }
            });
            viewHolder.numMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<DishesProperty> propertyList = merchantDishes.getDishesItemTypelist();
                    if (merchantDishes.getIsComp() != 0) {
                        if (getNum(merchantDishes) == 1) {
                            int num = minusNum(merchantDishes);
                            viewHolder.dishesInit.setVisibility(View.VISIBLE);
                            viewHolder.dishesSelected.setVisibility(View.INVISIBLE);
                            context.deleteOrderCompGoods();
                            context.updateNumAndPrice();
                        } else {
                            int num = minusNum(merchantDishes);
                            viewHolder.dishesSelectedNum.setText(num + "");
                            context.deleteOrderCompGoods();
                            context.updateNumAndPrice();
                        }

                    } else if(propertyList!=null && propertyList.size()>0){
                        if(getNum(merchantDishes)==1){
                            int num=minusNum(merchantDishes);
                            viewHolder.dishesInit.setVisibility(View.VISIBLE);
                            viewHolder.dishesSelected.setVisibility(View.INVISIBLE);
                            numChangeOnItemClick.onItemClick(null, num, merchantDishes, null);
                        }else{
                            int num=minusNum(merchantDishes);
                            viewHolder.dishesSelectedNum.setText(num+"");
                            numChangeOnItemClick.onItemClick(null,1,merchantDishes,null);
                        }
                    }else{
                        int num=minusNum(merchantDishes);
                    if(num==0){
                        viewHolder.dishesInit.setVisibility(View.VISIBLE);
                        viewHolder.dishesSelected.setVisibility(View.INVISIBLE);
                    }else{
                        viewHolder.dishesSelectedNum.setText(num+"");
                    }
                    numChangeOnItemClick.onItemClick(view, num, merchantDishes, null);
                    }
                }
            });
        }
    }
    //套餐菜数量增加后回调更新视图
    public void addNumberTest(ViewHolder viewHolder, MerchantDishes merchantDishes, int position) {
        if(getNum(merchantDishes)==0&&viewHolder.isLoad){
            viewHolder.dishesInit.setVisibility(View.INVISIBLE);
            viewHolder.dishesSelected.setVisibility(View.VISIBLE);
        }
        int num = addNum(merchantDishes);
        loadViewHolderWithDishes(viewHolder, merchantDishes, position);
    }

    private int addNum(MerchantDishes merchantDishes){
        if(numMap.containsKey(merchantDishes.getDishesId())){
            int num=numMap.get(merchantDishes.getDishesId());
            numMap.put(merchantDishes.getDishesId(),++num);
            return num;
        }else {
            numMap.put(merchantDishes.getDishesId(),1);
            return 1;
        }
    }

    public int minusNum(MerchantDishes merchantDishes){
        if(numMap.containsKey(merchantDishes.getDishesId())){
            int num=numMap.get(merchantDishes.getDishesId());
            if(num>1){
                numMap.put(merchantDishes.getDishesId(),--num);
                return num;
            }
            else {
                numMap.remove(merchantDishes.getDishesId());
                return 0;
            }
        }
        return 0;
    }

    public int getNum(MerchantDishes merchantDishes){
          if(numMap.containsKey(merchantDishes.getDishesId())){
              return numMap.get(merchantDishes.getDishesId());
          }
        return 0;
    }

    public void clearNum(){
        numMap.clear();
        notifyDataSetChanged();
    }

    public void refreshByOrderList(List<OrderGoodsItem> dishesItems, List<DishesCompSelectionEntity> compDishesItems){
        numMap.clear();
        for (OrderGoodsItem orderGoodsItem:dishesItems){
            if (numMap.containsKey(orderGoodsItem.getSalesId())){
                int curNum=numMap.get(orderGoodsItem.getSalesId());
                numMap.put(orderGoodsItem.getSalesId(),curNum+orderGoodsItem.getSalesNum());
            }else{
                numMap.put(orderGoodsItem.getSalesId(),orderGoodsItem.getSalesNum());
            }
        }

        for (DishesCompSelectionEntity compDishesItem: compDishesItems) {
            if (numMap.containsKey(compDishesItem.getmCompMainDishes().getSalesId())){
                int curNum=numMap.get(compDishesItem.getmCompMainDishes().getSalesId());
                numMap.put(compDishesItem.getmCompMainDishes().getSalesId(),curNum+compDishesItem.getmCompMainDishes().getSalesNum());
            }else{
                numMap.put(compDishesItem.getmCompMainDishes().getSalesId(),compDishesItem.getmCompMainDishes().getSalesNum());
            }
        }
        notifyDataSetChanged();
    }

    OnChangeWithPropertyListener mOnChangeWithPropertyListener = new OnChangeWithPropertyListener<ViewHolder>() {
        @Override
        public void onChangeCount(MerchantDishes dishesItem, int selectedCount, List<PropertySelectEntity> selectedPropertyItems,ViewHolder viewHolder) {
            int num=addNum(dishesItem);
            viewHolder.dishesSelectedNum.setText(num+"");
            viewHolder.dishesInit.setVisibility(View.INVISIBLE);
            viewHolder.dishesSelected.setVisibility(View.VISIBLE);
            numChangeOnItemClick.onItemClick(viewHolder.dishesInit, 1, dishesItem, selectedPropertyItems);
        }
    };


    public void refreshData(List<MerchantDishes> typeList){
        this.itemList=typeList;
        notifyDataSetChanged();
    };

    public void setOnPropertyItemClickListener(OnPropertyItemClickListener<MerchantDishes> mOnItemClickListener){
         this.numChangeOnItemClick =mOnItemClickListener;
    }

    public void setStickyHeaderChangeListener(OnStickyHeaderChangeListener<MerchantDishes> mOnItemClickListener){
        this.stickyHeaderChangeListener =mOnItemClickListener;
    }

    public static class HeaderViewHolder {
        public TextView mTextView;
    }


    /**
     * 菜单显示图片的加载参数
     * 默认是ARGB_8888， 使用RGB_565会比使用ARGB_8888少消耗2倍的内存，但是这样会没有透明度
     */
    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.dishes_default)
            .showImageOnFail(R.drawable.dishes_default)
            .showImageForEmptyUri(R.drawable.dishes_default)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY)
            .displayer(new RoundedBitmapDisplayer(20))
            .build();

    ImageLoadingListener mImageLoadingListener = new ImageLoadingListener() {
        @Override
        public void onLoadingStarted(String arg0, View arg1) {

        }

        @Override
        public void onLoadingFailed(String arg0, View img, FailReason failReason) {
            if(img!=null){
            int position=(Integer)img.getTag(R.id.tag_first);
            MerchantDishes merchantDishes=(MerchantDishes)img.getTag(R.id.tag_second);
            switch (failReason.getType()) {
                case IO_ERROR:
                    KLog.i("图片加载IO异常");
                    //服务器下载异常,修改缓存中数据,避免重复请求
                    merchantDishes.setDishesUrl(null);
                    if(itemList!=null&&itemList.size()>position)
                    itemList.set(position,merchantDishes);
                    break;
                case DECODING_ERROR:
                    KLog.i("图片加载编码异常");
                    break;
                case NETWORK_DENIED:
                    KLog.i("图片加载网络异常");
                    break;
                case OUT_OF_MEMORY:
                    KLog.i("图片加载内存溢出");
                    break;
                case UNKNOWN:
                    KLog.i("图片加载未知异常");
                    //服务器地址无效,修改缓存中数据,避免重复请求
                    merchantDishes.setDishesUrl(null);
                    if(itemList!=null&&itemList.size()>position)
                        itemList.set(position,merchantDishes);
                    break;
                default:
                    break;
            }
            }else{
                KLog.i("视图已销毁,忽视!");
            }
        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
        }

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
        }
    };

}
