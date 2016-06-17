package com.asiainfo.selforder.ui;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.asiainfo.selforder.R;
import com.asiainfo.selforder.model.Listener.OnDialogBackListener;
import com.asiainfo.selforder.widget.NumberKeyBoard;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 16/6/16 下午3:49
 */
public class MealNumberDF extends DialogFragment implements View.OnClickListener {

    private View mView;
    private TextView title;
    private ImageButton closeBtn;
    private EditText editText;
    private Button sureBtn;
    private NumberKeyBoard numberKeyBoard;
    private OnDialogBackListener onDialogBackListener;

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
        mView = inflater.inflate(R.layout.meal_number, null); //在layout文件中设置也无效
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        initView();
        initListener();
        initNumberBoard();
        setTextColor();
    }

    private void initView() {
        title = (TextView) mView.findViewById(R.id.meal_title);
        editText = (EditText) mView.findViewById(R.id.meal_edit);
        closeBtn = (ImageButton) mView.findViewById(R.id.meal_closebtn);
        sureBtn = (Button) mView.findViewById(R.id.meal_sure_btn);
    }

    private void initListener() {
        closeBtn.setOnClickListener(this);
        sureBtn.setOnClickListener(this);
    }

    private void initNumberBoard() {
        editText.setKeyListener(null);
        numberKeyBoard = new NumberKeyBoard(mView, getActivity(), editText);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meal_closebtn:
                dismiss();
                break;
            case R.id.meal_sure_btn:
                onDialogBackListener.onDialogBack(editText.getText().toString());
                break;
        }
    }

    private void setTextColor() {
        SpannableString spannableString = new SpannableString(title.getText().toString());
        spannableString.setSpan(new TextAppearanceSpan(getActivity(), R.style.TextStyle01), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new TextAppearanceSpan(getActivity(), R.style.TextStyle02), 3, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new TextAppearanceSpan(getActivity(), R.style.TextStyle03), 6, title.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    public void setOnDialogBackListener(OnDialogBackListener onDialogBackListener) {
        this.onDialogBackListener = onDialogBackListener;
    }
}
