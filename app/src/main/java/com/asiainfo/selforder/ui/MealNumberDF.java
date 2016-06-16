package com.asiainfo.selforder.ui;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.asiainfo.selforder.R;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 16/6/16 下午3:49
 */
public class MealNumberDF extends DialogFragment {

    private View mView;
    private TextView title;
    private ImageButton closeBtn;
    private EditText editText;

    @SuppressLint("InlinedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        mView = inflater.inflate(R.layout.dfragment_makeorder_settle, null); //在layout文件中设置也无效
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        initView();
    }

    private void initView() {
        title = (TextView) mView.findViewById(R.id.meal_title);
        editText = (EditText) mView.findViewById(R.id.meal_edit);
    }
}
