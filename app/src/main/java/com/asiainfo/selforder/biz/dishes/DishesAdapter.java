package com.asiainfo.selforder.biz.dishes;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.asiainfo.selforder.R;
import com.asiainfo.selforder.model.Listener.OnChangeWithPropertyListener;
import com.asiainfo.selforder.model.Listener.OnPropertyItemClickListener;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kxlive.gjrlibrary.utils.KLog;

/**
 * Created by gjr on 2015/12/1.
 */
public class DishesAdapter<T> extends RecyclerView.Adapter<DishesAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Resources res;
    private List<MerchantDishes> itemList;
    private OnPropertyItemClickListener<MerchantDishes> numChangeOnItemClick;
    private Map<String, Integer> numMap;
    private FragmentActivity mActivity;
    private DishesMenuActivity context;

    public DishesAdapter(LayoutInflater inflater, Resources mRes, FragmentActivity mActivity, DishesMenuActivity context) {
        this.mInflater = inflater;
        this.res = mRes;
        this.mActivity = mActivity;
        this.numMap = new HashMap<String, Integer>();
        this.context = context;
    }

    public DishesAdapter(LayoutInflater inflater, Resources mRes, FragmentActivity mActivity) {
        this.mInflater = inflater;
        this.res = mRes;
        this.mActivity = mActivity;
        this.numMap = new HashMap<String, Integer>();
    }

    public DishesAdapter(LayoutInflater inflater, List<MerchantDishes> typeList, Resources mRes, FragmentActivity mActivity) {
        this.mInflater = inflater;
        this.itemList = typeList;
        this.res = mRes;
        this.numMap = new HashMap<String, Integer>();
        this.mActivity = mActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_dishes, null);
        ViewHolder holder = new ViewHolder(view);
        holder.setIsRecyclable(true);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final MerchantDishes merchantDishes = itemList.get(position);
        int curNum = getNum(merchantDishes);
        if (curNum > 0) {
            if (!viewHolder.isLoad) {
                viewHolder.initViewStud();
            }
            viewHolder.dishesInit.setVisibility(View.INVISIBLE);
            viewHolder.dishesSelected.setVisibility(View.VISIBLE);
            loadViewHolderWithDishes(viewHolder, merchantDishes, position);
        } else {
            viewHolder.dishesInit.setVisibility(View.VISIBLE);
            if (viewHolder.isLoad) {
                viewHolder.dishesSelected.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.dishesName.setText(merchantDishes.getDishesName());
                viewHolder.dishesDescribe.setText(merchantDishes.getDishesTypeName());
                viewHolder.dishesPrice.setText(merchantDishes.getDishesPrice());
                viewHolder.dishesImage.setTag(R.id.tag_first, position);
                viewHolder.dishesImage.setTag(R.id.tag_second, merchantDishes);
//            ImageLoader.getInstance().displayImage(
//                    merchantDishes.getDishesUrl(), viewHolder.dishesImage,
//                    options, mImageLoadingListener);
                viewHolder.dishesInit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Log.d("222222222", "iscomps is: " + merchantDishes.getIsComp() + ", the dish id is: " +
//                                merchantDishes.getDishesId() + ", and the merchantid is: " + merchantDishes.getMerchantId());
                        List<DishesProperty> propertyList = merchantDishes.getDishesItemTypelist();
                        if (!viewHolder.isLoad) {
                            viewHolder.initViewStud();
                        }

                        if (merchantDishes.getIsComp() != 0) {
                            view.setVisibility(View.VISIBLE);
                            viewHolder.dishesSelected.setVisibility(View.INVISIBLE);
                            context.test(viewHolder, position);
                            DishCompsActivity.actionStart(mActivity, merchantDishes);

                        } else if (propertyList != null && propertyList.size() > 0) {
                            if (ChoosePropertyFragment.getInstance() != null && !ChoosePropertyFragment.getInstance().isVisible()) {
                                loadViewHolderWithDishes(viewHolder, merchantDishes, position);
                                view.setVisibility(View.VISIBLE);
                                viewHolder.dishesSelected.setVisibility(View.INVISIBLE);
                                ChoosePropertyFragment mChoosePropertyValueDF = ChoosePropertyFragment.newInstance(merchantDishes);
                                mChoosePropertyValueDF.setOnCheckedPropertyItemsListener(mOnChangeWithPropertyListener, viewHolder);
                                mChoosePropertyValueDF.show(mActivity.getSupportFragmentManager(), "choose_property");
                            } else {
                                KLog.i("窗口已经显示,无视此次事件");
                            }
                        } else {
                            int num = addNum(merchantDishes);
                            numChangeOnItemClick.onItemClick(view, num, merchantDishes, null);
                            view.setVisibility(View.INVISIBLE);
                            viewHolder.dishesSelected.setVisibility(View.VISIBLE);
                            loadViewHolderWithDishes(viewHolder, merchantDishes, position);
                        }

                    }
                });
            }
        }
    }

    public void loadViewHolderWithDishes(final ViewHolder viewHolder, final MerchantDishes merchantDishes, final int position) {
        int num = getNum(merchantDishes);
        if (viewHolder.isLoad) {
            viewHolder.dishesSelectedName.setText(merchantDishes.getDishesName());
            viewHolder.dishesSelectedDescribe.setText(merchantDishes.getDishesTypeName());
            viewHolder.dishesSelectedPrice.setText(merchantDishes.getDishesPrice());
            viewHolder.dishesSelectedNum.setText(num + "");
            viewHolder.numAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    List<DishesProperty> propertyList = merchantDishes.getDishesItemTypelist();
                    if (merchantDishes.getIsComp() != 0) {
                        DishCompsActivity.actionStart(mActivity, merchantDishes);
                    } else if (propertyList != null && propertyList.size() > 0) {
                        if (ChoosePropertyFragment.getInstance() != null && !ChoosePropertyFragment.getInstance().isVisible()) {
                            ChoosePropertyFragment mChoosePropertyValueDF = ChoosePropertyFragment.newInstance(merchantDishes);
                            mChoosePropertyValueDF.setOnCheckedPropertyItemsListener(mOnChangeWithPropertyListener, viewHolder);
                            mChoosePropertyValueDF.show(mActivity.getSupportFragmentManager(), "choose_property");
                        } else {
                            KLog.i("窗口已经显示,无视此次事件");
                        }
                    } else {
                        int num = addNum(merchantDishes);
                        viewHolder.dishesSelectedNum.setText(num + "");
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
                            notifyItemChanged(position);
                            context.deleteOrderCompGoods();
                            context.updateNumAndPrice();
                        } else {
                            int num = minusNum(merchantDishes);
                            viewHolder.dishesSelectedNum.setText(num + "");
                            context.deleteOrderCompGoods();
                            context.updateNumAndPrice();
                        }

                    } else if (propertyList != null && propertyList.size() > 0) {
                        if (getNum(merchantDishes) == 1) {
                            int num = minusNum(merchantDishes);
                            viewHolder.dishesInit.setVisibility(View.VISIBLE);
                            viewHolder.dishesSelected.setVisibility(View.INVISIBLE);
                            notifyItemChanged(position);//由于视图复用,恢复显示的时候要单独刷新对应item
                            numChangeOnItemClick.onItemClick(null, num, merchantDishes, null);
                        } else {
                            int num = minusNum(merchantDishes);
                            viewHolder.dishesSelectedNum.setText(num + "");
                            numChangeOnItemClick.onItemClick(null, 1, merchantDishes, null);
                        }
                    } else {
                        int num = minusNum(merchantDishes);
                        if (num == 0) {
                            viewHolder.dishesInit.setVisibility(View.VISIBLE);
                            viewHolder.dishesSelected.setVisibility(View.INVISIBLE);
                            notifyItemChanged(position);//由于视图复用,恢复显示的时候要单独刷新对应item
                        } else {
                            viewHolder.dishesSelectedNum.setText(num + "");
                        }
                        numChangeOnItemClick.onItemClick(view, num, merchantDishes, null);
                    }
                }
            });
        }
    }

    public void addNumberTest(ViewHolder viewHolder, MerchantDishes merchantDishes, int position) {
        int num = addNum(merchantDishes);
        loadViewHolderWithDishes(viewHolder, merchantDishes, position);
        notifyItemChanged(position);
//        context.updateNumAndPrice();
    }

    public int addNum(MerchantDishes merchantDishes) {
        if (numMap.containsKey(merchantDishes.getDishesId())) {
            int num = numMap.get(merchantDishes.getDishesId());
            numMap.put(merchantDishes.getDishesId(), ++num);
            return num;
        } else {
            numMap.put(merchantDishes.getDishesId(), 1);
            return 1;
        }
    }

    public int minusNum(MerchantDishes merchantDishes) {
        if (numMap.containsKey(merchantDishes.getDishesId())) {
            int num = numMap.get(merchantDishes.getDishesId());
            if (num > 1) {
                numMap.put(merchantDishes.getDishesId(), --num);
                return num;
            } else {
                numMap.remove(merchantDishes.getDishesId());
                return 0;
            }
        }
        return 0;
    }

    public int getNum(MerchantDishes merchantDishes) {
        if (numMap.containsKey(merchantDishes.getDishesId())) {
            return numMap.get(merchantDishes.getDishesId());
        }
        return 0;
    }

    public void clearNum() {
        numMap.clear();
        notifyDataSetChanged();
    }

    public void refreshByOrderList(List<OrderGoodsItem> dishesItems, List<DishesCompSelectionEntity> compDishes) {
        numMap.clear();
        for (OrderGoodsItem orderGoodsItem : dishesItems) {
            if (numMap.containsKey(orderGoodsItem.getSalesId())) {
                int curNum = numMap.get(orderGoodsItem.getSalesId());
                numMap.put(orderGoodsItem.getSalesId(), curNum + orderGoodsItem.getSalesNum());
            } else {
                numMap.put(orderGoodsItem.getSalesId(), orderGoodsItem.getSalesNum());
            }
        }

        for (DishesCompSelectionEntity compDish: compDishes) {
            OrderGoodsItem orderGoodsItem = compDish.getmCompMainDishes();
            if (numMap.containsKey(orderGoodsItem.getSalesId())) {
                int curNum = numMap.get(orderGoodsItem.getSalesId());
                numMap.put(orderGoodsItem.getSalesId(), curNum + orderGoodsItem.getSalesNum());
            } else {
                numMap.put(orderGoodsItem.getSalesId(), orderGoodsItem.getSalesNum());
            }
        }
        notifyDataSetChanged();
    }

    OnChangeWithPropertyListener mOnChangeWithPropertyListener = new OnChangeWithPropertyListener<ViewHolder>() {
        @Override
        public void onChangeCount(MerchantDishes dishesItem, int selectedCount, List<PropertySelectEntity> selectedPropertyItems, ViewHolder viewHolder) {
            int num = addNum(dishesItem);
            viewHolder.dishesSelectedNum.setText(num + "");
            viewHolder.dishesInit.setVisibility(View.INVISIBLE);
            viewHolder.dishesSelected.setVisibility(View.VISIBLE);
            numChangeOnItemClick.onItemClick(viewHolder.dishesInit, 1, dishesItem, selectedPropertyItems);
        }
    };

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void refreshData(List<MerchantDishes> typeList) {
        this.itemList = typeList;
        notifyDataSetChanged();
    }

    ;

    public void setOnPropertyItemClickListener(OnPropertyItemClickListener<MerchantDishes> mOnItemClickListener) {
        this.numChangeOnItemClick = mOnItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dishesName, dishesSelectedName, dishesDescribe,
                dishesSelectedDescribe, dishesPrice, dishesSelectedPrice, dishesSelectedNum;
        public ImageView dishesImage;
        public RelativeLayout dishesInit, dishesSelected;
        public Button numAdd, numMinus;
        public ViewStub seletedView;
        public boolean isLoad = false;

        public ViewHolder(View itemView) {
            super(itemView);
            dishesInit = (RelativeLayout) itemView.findViewById(R.id.item_dishes_init);
            dishesName = (TextView) itemView.findViewById(R.id.item_dishes_name);
            dishesDescribe = (TextView) itemView.findViewById(R.id.item_dishes_describe);
            dishesPrice = (TextView) itemView.findViewById(R.id.item_dishes_price);
            dishesImage = (ImageView) itemView.findViewById(R.id.item_dishes_image);
            seletedView = (ViewStub) itemView.findViewById(R.id.item_dishes_selected);
        }

        public void initViewStud() {
//            对ViewStub的inflate操作只能进行一次，因为inflate的时候是将其指向的布局文件解析inflate并替换掉当前ViewStub本身
//           （由此体现出了ViewStub“占位符”性质），一旦替换后，此时原来的布局文件中就没有ViewStub控件了,而是载入的布局
//            所以要复用的话,需要记录延迟加载的视图,初始化视图中的控件,进行事件处理.
            dishesSelected = (RelativeLayout) seletedView.inflate();
            isLoad = true;
            dishesSelectedName = (TextView) dishesSelected.findViewById(R.id.item_dishess_selected_name);
            dishesSelectedDescribe = (TextView) dishesSelected.findViewById(R.id.item_dishes_selected_describe);
            dishesSelectedPrice = (TextView) dishesSelected.findViewById(R.id.item_dishes_selected_price);
            dishesSelectedNum = (TextView) dishesSelected.findViewById(R.id.item_dishes_selected_num);
            numAdd = (Button) dishesSelected.findViewById(R.id.item_disehs_selected_add);
            numMinus = (Button) dishesSelected.findViewById(R.id.item_disehs_selected_minus);
        }
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
            if (img != null) {
                int position = (Integer) img.getTag(R.id.tag_first);
                MerchantDishes merchantDishes = (MerchantDishes) img.getTag(R.id.tag_second);
                switch (failReason.getType()) {
                    case IO_ERROR:
                        KLog.i("图片加载IO异常");
                        //服务器下载异常,修改缓存中数据,避免重复请求
                        merchantDishes.setDishesUrl(null);
                        if (itemList != null && itemList.size() > position)
                            itemList.set(position, merchantDishes);
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
                        if (itemList != null && itemList.size() > position)
                            itemList.set(position, merchantDishes);
                        break;
                    default:
                        break;
                }
            } else {
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
