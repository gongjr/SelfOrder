package com.asiainfo.selforder.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asiainfo.selforder.R;
import com.asiainfo.selforder.http.HttpHelper;
import com.asiainfo.selforder.model.AddressState;
import com.asiainfo.selforder.ui.base.mBaseActivity;

import kxlive.gjrlibrary.config.SysEnv;
import kxlive.gjrlibrary.utils.Tools;
import roboguice.inject.InjectView;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 16/7/25 下午1:42
 */
public class SystemActivity extends mBaseActivity implements View.OnClickListener {

    @InjectView(R.id.title_leftbtn)
    private Button backBtn;
    @InjectView(R.id.title_righttxt)
    private TextView rightBtn;
    @InjectView(R.id.system_versionName)
    private TextView versionNameTxt;
    @InjectView(R.id.system_versionCode)
    private TextView versionCodeTxt;
    @InjectView(R.id.system_packagename)
    private TextView packageNameTxt;
    @InjectView(R.id.system_server_address)
    private TextView serverAddressTxt;
    @InjectView(R.id.system_server_toinit)
    private Button initBtn;
    @InjectView(R.id.marketing_underline06)
    private View underline;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_system);
        initData();
        initListener();
    }

    private void initData() {
        int versionCode = Tools.getVersionCode(getApplicationContext());
        String versionName = Tools.getVersionName(getApplicationContext());
        String packageName = Tools.getPackageName(getApplicationContext());
        isInitBtnVisible();
        versionCodeTxt.setText("版本号: " + versionCode);
        versionNameTxt.setText("版本名称: " + versionName);
        packageNameTxt.setText("包名: " + packageName);
    }

    private void initListener() {
        backBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        initBtn.setOnClickListener(this);
        serverAddressTxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showCheckUserPwdDF();
                return false;
            }
        });
    }

    private void isInitBtnVisible() {
        if (HttpHelper.newInstance().isInit()) {
            serverAddressTxt.setText("服务器地址: " + HttpHelper.newInstance().getAddress().getTitle());
            initBtn.setVisibility(View.GONE);
            underline.setVisibility(View.GONE);
        } else {
            serverAddressTxt.setText("服务器地址: " + HttpHelper.newInstance().getAddress().getTitle() + " (非默认升级无法自动更新环境地址)");
            initBtn.setVisibility(View.VISIBLE);
            underline.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_leftbtn:
            case R.id.title_righttxt:
                finish();
                break;
            case R.id.system_server_toinit:
                Boolean isInit = HttpHelper.newInstance().toInitAddress(mActivity);
                if (isInit) {
                    showShortTip("已经是默认配置了~~");
                } else {
                    showShortTip("恢复默认设置成功!");
                }
                isInitBtnVisible();
                break;

        }
    }

    /**
     * 显示选择服务器环境窗口
     */
    private void showChooseServerAddressDF() {
        try {
            ChooseServerAddressDF lChooseCardLevelDF = ChooseServerAddressDF.newInstance();
            lChooseCardLevelDF.setOnFinishBackListener(mOnFinifhBackListener);
            lChooseCardLevelDF.show(getFragmentManager(), "ChooseServerAddressDF");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ChooseServerAddressDF.OnFinifhBackListener mOnFinifhBackListener = new ChooseServerAddressDF.OnFinifhBackListener() {
        @Override
        public void onFinifhBack(AddressState pAddressState) {
            if (pAddressState == AddressState.wxRelease || pAddressState == AddressState.wxTst) {
                HttpHelper.newInstance().setWXAddress(pAddressState);
            } else {
                HttpHelper.newInstance().setAddress(pAddressState);
                HttpHelper.newInstance().saveAddress(mActivity, pAddressState);
                isInitBtnVisible();
            }
        }
    };

    /**
     * 显示系统密码验证窗口
     */
    private void showCheckUserPwdDF() {
        CheckUserPwdDF lCheckUserPwdDF = CheckUserPwdDF.newInstance();
        lCheckUserPwdDF.setOnCheckUserPwdListener(mOnCheckUserPwdListener);
        lCheckUserPwdDF.show(getFragmentManager(), "CheckSysPwdDF");
    }

    CheckUserPwdDF.OnCheckUserPwdListener mOnCheckUserPwdListener = new CheckUserPwdDF.OnCheckUserPwdListener() {
        @Override
        public void onSelectBack(String pwd) {
            if (pwd.equals(SysEnv.ChangeServerAddressPwd)) {
                showShortTip("修改服务器地址通行密码正确!");
                showChooseServerAddressDF();
            } else {
                showShortTip("修改服务器地址通行密码不正确!");
            }
        }
    };
}
