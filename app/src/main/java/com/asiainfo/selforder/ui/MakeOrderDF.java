package com.asiainfo.selforder.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asiainfo.selforder.R;

import kxlive.gjrlibrary.utils.KLog;
import kxlive.gjrlibrary.widget.LeafDialog.DialogDelayListener;

/**
 * 订单提交页面提示弹窗
 * 
 * @author gjr
 *
 * 2015年7月15日
 */
public class MakeOrderDF extends DialogFragment {

	private View mView;
	private TextView mContextView;
	private String contentTxt;
	private Button btn_navToDesk;
	private ProgressBar prog_progBar;
	private FragmentActivity mActivity;
	
	@SuppressLint("InlinedApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, 0);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//setStyle(R.style.dialog_style, 0); //设置无效
	    getDialog().setCancelable(false);
	    getDialog().setCanceledOnTouchOutside(false);
//	    getDialog().getWindow().setBackgroundDrawable(
//	    		new ColorDrawable(Color.parseColor("#00009000"))); //设置有效，但是还是有阴影
		mView = inflater.inflate(R.layout.dfragment_makeorder_settle, null); //在layout文件中设置也无效
		return mView;
	}
	
	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		mActivity = getActivity();
		initView();
		initData();
		initListener();
	}
	
	public void initView(){
		prog_progBar = (ProgressBar)mView.findViewById(R.id.handle_prog);
		mContextView = (TextView)mView.findViewById(R.id.tv_content_txt);
		btn_navToDesk = (Button)mView.findViewById(R.id.btn_nav_to_desk);
		btn_navToDesk.setClickable(false);
		btn_navToDesk.setEnabled(false);
	}
	
	public void initData(){
		if(contentTxt!=null){
			mContextView.setText(contentTxt);
		}
	}
	
	public void initListener(){
		btn_navToDesk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	public void setNoticeText(String txt){
		contentTxt = txt;
	}
	
	public void updateNoticeText(String txt){
		contentTxt = txt;
		mContextView.setText(txt);
		prog_progBar.setVisibility(View.GONE);
		btn_navToDesk.setClickable(true);
		btn_navToDesk.setEnabled(true);
        btn_navToDesk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
	}

    public void updateNoticeText(String txt,final DialogDelayListener mListener){
        contentTxt = txt;
        KLog.i("mContextView:"+mContextView+"txt:"+txt);
        mContextView.setText(txt);
        prog_progBar.setVisibility(View.GONE);
        btn_navToDesk.setClickable(true);
        btn_navToDesk.setEnabled(true);
        btn_navToDesk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onexecute();
                dismiss();
            }
        });
    }

}
