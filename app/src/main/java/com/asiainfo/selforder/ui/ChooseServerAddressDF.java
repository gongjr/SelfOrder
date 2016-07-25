package com.asiainfo.selforder.ui;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.asiainfo.selforder.R;
import com.asiainfo.selforder.config.ChooseServerAddressAdapter;
import com.asiainfo.selforder.http.HttpHelper;
import com.asiainfo.selforder.model.AddressState;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 16/7/25 下午2:23
 */
public class ChooseServerAddressDF extends DialogFragment implements View.OnClickListener {
    private View view;
    private TextView title;
    private ImageView closeBtn;
    private ListView listView;
    private Button submitBtn;
    private ChooseServerAddressAdapter adapter;
    private OnFinifhBackListener onFinifhBackListener;
    private AddressState curAddressState= HttpHelper.newInstance().getAddress();
    private AddressState[] lAddressState=AddressState.values();


    public  static ChooseServerAddressDF chooseServerAddressDF;

    public static ChooseServerAddressDF newInstance() {
        if (chooseServerAddressDF==null)
            chooseServerAddressDF = new ChooseServerAddressDF();
        return chooseServerAddressDF;
    }

    public interface OnFinifhBackListener {
        public void onFinifhBack(AddressState pAddressState);
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.df_choose_desk_order, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    /*
    * 初始化控件
    * */
    private void initView() {
        title = (TextView) view.findViewById(R.id.tv_desk_info);
        closeBtn = (ImageView) view.findViewById(R.id.img_close);
        listView = (ListView) view.findViewById(R.id.lv_desk_orders);
        submitBtn = (Button) view.findViewById(R.id.btn_ensure);
    }

    /*
    * 相关数据操作
    * */
    private void initData() {
        int initSelected=0;
        for (int i=0;i<lAddressState.length;i++){
            if (curAddressState.getValue().equals(lAddressState[i].getValue())){
                initSelected=i;break;
            }
        }
        adapter = new ChooseServerAddressAdapter(view.getContext(),lAddressState, initSelected);
        listView.setAdapter(adapter);
        title.setText("选择服务器环境");
    }

    /*
    * 设置相关点击事件
    * */
    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.changeSelectPosition(position);
                curAddressState=lAddressState[position];
            }
        });
        closeBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    public void setOnFinishBackListener(OnFinifhBackListener onFinishBackListener) {
        this.onFinifhBackListener = onFinishBackListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_close:
                dismiss();
                break;
            case R.id.btn_ensure:
                onFinifhBackListener.onFinifhBack(curAddressState);
                dismiss();

                break;
        }
    }
}
