package com.asiainfo.selforder.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.asiainfo.selforder.R;
import com.asiainfo.selforder.config.Constants;
import com.asiainfo.selforder.http.HttpHelper;
import com.asiainfo.selforder.model.MerchantDesk;
import com.asiainfo.selforder.model.MerchantRegister;
import com.asiainfo.selforder.model.dishComps.DishesComp;
import com.asiainfo.selforder.model.dishComps.DishesCompItem;
import com.asiainfo.selforder.model.dishes.DishesProperty;
import com.asiainfo.selforder.model.dishes.DishesPropertyItem;
import com.asiainfo.selforder.model.dishes.MerchantDishes;
import com.asiainfo.selforder.model.dishes.MerchantDishesType;
import com.asiainfo.selforder.model.eventbus.post.DishesListEntity;
import com.asiainfo.selforder.model.net.HttpMerchantDishes;
import com.asiainfo.selforder.service.ScreenService;
import com.asiainfo.selforder.ui.base.mBaseActivity;
import com.asiainfo.selforder.widget.HttpDialogLogin;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import kxlive.gjrlibrary.config.SysEnv;
import kxlive.gjrlibrary.entity.AppUpdate;
import kxlive.gjrlibrary.entity.eventbus.EventBackground;
import kxlive.gjrlibrary.entity.eventbus.EventMain;
import kxlive.gjrlibrary.http.ImageInfo;
import kxlive.gjrlibrary.http.PostSingleUploadRequest;
import kxlive.gjrlibrary.http.ResponseListener;
import kxlive.gjrlibrary.http.VolleyErrorHelper;
import kxlive.gjrlibrary.utils.JPushUtils;
import kxlive.gjrlibrary.utils.KLog;
import kxlive.gjrlibrary.utils.ToolUpdateChecker;
import kxlive.gjrlibrary.utils.Tools;
import roboguice.inject.InjectView;

/**
 * @author gjr
 * 2015年6月18日
 * 登录页面
 */
public class LoginActivity extends mBaseActivity {
    @InjectView(R.id.edit_username)
    private EditText edit_username;
    @InjectView(R.id.edit_password)
    private EditText edit_password;
    @InjectView(R.id.btn_login)
    private Button btn_login;
    @InjectView(R.id.remember_password_check)
    private CheckBox remember_password;

    private SharedPreferences login;
    private HttpDialogLogin mHttpDialogLogin;
    private JPushUtils mJPushUtils;
    private List<MerchantDishesType> mDishTypeDataList;
    private List<MerchantDishes> mAllDishesDataList=new ArrayList<MerchantDishes>();
    private String userName;
    private String passwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EventBus.getDefault().register(this);
        initData();
        initListener();
//        upLoadImageTest();
    }

    @Override
    public boolean onEventMainThread(EventMain event) {
        boolean isRun=super.onEventMainThread(event);
        if (isRun) {
            switch (event.getType()) {
                case EventMain.TYPE_FIRST:
                    showShortTip("菜单更新成功!");
                    dismissLoginDialog();
                    getOperation().forward(DishesMenuActivity.class);
//                    getOperation().forward(IflytekActivity.class);
                    break;
                default:
                    break;
            }
        }
        return isRun;
    }

    @Override
    public boolean onEventBackgroundThread(EventBackground event) {
        boolean isRun=super.onEventBackgroundThread(event);
        if (isRun) {
            switch (event.getType()) {
                case EventBackground.TYPE_FIRST:
                    DishesListEntity DishesListEntity = (DishesListEntity) event.getData();
                    mDishTypeDataList = DishesListEntity.getmDishTypeDataList();

                    if (mDishTypeDataList != null && mDishTypeDataList.size() > 0) {
                        DataSupport.deleteAll(MerchantDishesType.class);//清空菜品类型缓存表
                        DataSupport.saveAll(mDishTypeDataList);
                    }
                    if (mAllDishesDataList != null && mAllDishesDataList.size() > 0) {
                        DataSupport.deleteAll(MerchantDishes.class);//清空菜品缓存
                        DataSupport.deleteAll(DishesProperty.class);//清空菜品属性类型缓存
                        DataSupport.deleteAll(DishesPropertyItem.class);//清空菜品属性值缓存
                        DataSupport.saveAll(mAllDishesDataList);
                        for (int i = 0; i < mAllDishesDataList.size(); i++) {
                            MerchantDishes md = mAllDishesDataList.get(i);
                            List<DishesProperty> dpList = md.getDishesItemTypelist();
                            if (dpList != null && dpList.size() > 0) {
                                DataSupport.saveAll(dpList); //缓存菜品属性类型数据
                                for (int j = 0; j < dpList.size(); j++) {
                                    DishesProperty dpItem = dpList.get(j);
                                    List<DishesPropertyItem> dpiList = dpItem.getItemlist();
                                    DataSupport.saveAll(dpiList); //缓存菜品属性值数据
                                }
                            }
                        }
                    }
                    //更新时将套餐数据清空，后续缓存更新
                    DataSupport.deleteAll(DishesComp.class);
                    DataSupport.deleteAll(DishesCompItem.class);
                    Log.i("onEventBackgroundThread", "LoginActivity中数据库更新成功");

                    EventMain eventMain = new EventMain();
                    eventMain.setName(LoginActivity.class.getName());
                    eventMain.setType(EventMain.TYPE_FIRST);
                    eventMain.setDescribe("菜品更新成功后，通知消息发布到登陆页面主线程");
                    EventBus.getDefault().post(eventMain);
                    break;
                default:
                    break;
            }
        }
        return isRun;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(login.getBoolean(Constants.Preferences_Login_IsCheck,false)){
            remember_password.setChecked(true);
            String Login_userinfo=login.getString(Constants.Preferences_Login_UserInfo,"");
            edit_username.setText(Login_userinfo);
            String Login_password=login.getString(Constants.Preferences_Login_PassWord,"");
            edit_password.setText(Login_password);
            InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        httpAutoUpdate();
    }

    public void initData() {
        login=mActivity.getSharedPreferences(Constants.Preferences_Login, mActivity.MODE_PRIVATE);
        mJPushUtils = new JPushUtils(getApplicationContext());
        mJPushUtils.initJPush();
        startScreenService();
    }

    public void startScreenService(){
        Intent mService = new Intent(LoginActivity.this, ScreenService.class);
        mService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(mService);
    }

    public void initListener() {
        btn_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLoginInfo()) {
                    showLoginDialog();
                    httpAttendantLogin();
                } else {
                    showShortTip("请输入正确的用户名或密码!");
                }
            }
        });
        btn_login.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showCheckUserPwdDF();
                return false;
            }
        });

        remember_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor edit = login.edit();
                edit.putBoolean(Constants.Preferences_Login_IsCheck, b);
                if (!b) edit.clear();
                edit.apply();
            }
        });
    }

    /**
     * 登录请求
     */
    public void httpAttendantLogin() {
        userName = edit_username.getText().toString();
        passwd = edit_password.getText().toString();
        String url = "/appController/merchantLogin.do?userName=" + userName + "&passwd=" + passwd;
        Log.d(TAG, "Login URL:" + HttpHelper.HOST + url);
        JsonObjectRequest httpLogin = new JsonObjectRequest(
                HttpHelper.HOST + url, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject data) {
                        Log.d(TAG, "login resp data: " + data.toString());
                        try {
                            if (data.getString("msg").equals("ok")) {
                                mHttpDialogLogin.setNoticeText("正在查询更新数据...");
                                if(remember_password.isChecked()){
                                    SharedPreferences.Editor editor = login.edit();
                                    editor.putString(Constants.Preferences_Login_UserInfo, userName);
                                    editor.putString(Constants.Preferences_Login_PassWord, passwd);
                                    editor.apply();
                                }
                                Gson gson = new Gson();
                                MerchantRegister mRegister = null;
                                if (!data.getString("data").equals("")) {
                                    String info = data.getJSONObject("data").getString("info");
                                    mRegister = gson.fromJson(info, MerchantRegister.class);
                                    Log.i("tag","mRegister:"+mRegister);
                                    //代码混淆后,mRegister对象不为空,但是里面value为null
                                    mApp.saveData(mApp.KEY_GLOABLE_LOGININFO, mRegister);
                                    Log.i("tag","mRegister.getChildMerchantId():"+mRegister.getChildMerchantId());
                                    mJPushUtils.setJPushTag(mRegister.getChildMerchantId());//设置极光推送的标签
                                    httpGetMerchantDishes(mRegister.getChildMerchantId(),mRegister.getMerchantId());
                                }
                            } else {
                                dismissLoginDialog();
                                showShortTip("登录失败," + data.getString("msg") + "!");
                            }
                        } catch (JSONException e) {
                            dismissLoginDialog();
                            e.printStackTrace();
                        }
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dismissLoginDialog();
                        Log.e(TAG, "VolleyError:" + error.getMessage(), error);
                        showShortTip(VolleyErrorHelper.getMessage(error, mActivity));
                    }
                });
        executeRequest(httpLogin);
    }

    /**
     * 登录校验
     *
     * @return
     */
    private Boolean checkLoginInfo() {
        String uname = edit_username.getText().toString().trim();
        String upwd = edit_password.getText().toString().trim();
        if (uname.equals("") || upwd.equals("")) {
            return false;
        }
        return true;
    }


    protected void showLoginDialog() {
        try {
            if(mHttpDialogLogin==null)  mHttpDialogLogin = new HttpDialogLogin();
            if(mHttpDialogLogin!=null&&!mHttpDialogLogin.isAdded())
            mHttpDialogLogin.show(getSupportFragmentManager(), "dialog_fragment_http_login");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    protected void dismissLoginDialog() {
        try {
            if (mHttpDialogLogin != null&&mHttpDialogLogin.isAdded()) {
                mHttpDialogLogin.dismiss();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 应用自动更新检测
     */
    private void httpAutoUpdate() {
        //appKey是应用包名+工程名
        int curVersionCode = Tools.getVersionCode(mActivity);
        Log.d(TAG, "curVersionCode: " + curVersionCode);
        String url = "/appController/queryAppUpdate.do?appKey=com.asiainfo.selforder.SelfOrder";
        Log.d(TAG, "appAutoUpdate: " + HttpHelper.HOST + url);
        JsonObjectRequest httpAutoUpdate = new JsonObjectRequest(
                HttpHelper.HOST + url, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject data) {
                        Log.d(TAG, "http auto update data: " + data.toString());
                        try {
                            if (data.getString("msg").equals("ok")) {
                                Gson gson = new Gson();
                                AppUpdate appUpdate = gson.fromJson(data.getJSONObject("data").getString("info"), AppUpdate.class);
                                appAutoUpdate(appUpdate);
                            } else {
                                KLog.d("获取apk自动更新信息失败");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        KLog.d("VolleyError:" + error.getMessage(), error);
                        showShortTip(VolleyErrorHelper.getMessage(error, mActivity));
                    }
                });
        executeRequest(httpAutoUpdate);
    }

    private void appAutoUpdate(AppUpdate appUpdate) {
        int curVersionCode = Tools.getVersionCode(mActivity);
        int newVersionCode = appUpdate.getVersionCode();
        Log.d(TAG, "curVersionCode: " + curVersionCode);
        Log.d(TAG, "newVersionCode: " + newVersionCode);
        if (newVersionCode != 0 && newVersionCode > curVersionCode) {
            ToolUpdateChecker.checkForDialog(LoginActivity.this, appUpdate);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void httpGetMerchantDishes(String childMerchantId,String MerchantId) {
        String dishesWithAttrs = "/appController/queryDishesInfoNoComp.do?childMerchantId=" + childMerchantId+"&merchantId="+MerchantId;
        String url = "/appController/queryAllDishesInfoByMerchantId.do?childMerchantId=" + childMerchantId;
        Log.d(TAG, "httpGetMerchantDishes: " + HttpHelper.HOST + dishesWithAttrs);
        JsonObjectRequest httpGetMerchantDishes = new JsonObjectRequest(
                HttpHelper.HOST + dishesWithAttrs, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject data) {
                        try {
                            if (data.getString("msg").equals("ok")) {
                                JSONObject datainfo=data.getJSONObject("data");
                                mDishTypeDataList = gson.fromJson(datainfo.getString("types"), new TypeToken<List<MerchantDishesType>>() {
                                }.getType());
                                Log.d(TAG, "Dishes Type Count: " + mDishTypeDataList.size());
                                List<HttpMerchantDishes> lHttpMerchantDisheses = gson.fromJson(datainfo.getString("dishes"), new TypeToken<List<HttpMerchantDishes>>() {
                                }.getType());
                                for (HttpMerchantDishes lHttpMerchantDishes:lHttpMerchantDisheses){
                                    mAllDishesDataList.add(lHttpMerchantDishes.HttpMerchantDishesToMerchantDishes());
                                }
                                Log.d(TAG, "All Dishes Count: " + mAllDishesDataList.size());
                                if(datainfo.has("desks")){
                                    String desks=datainfo.getString("desks");
                                    ArrayList<MerchantDesk> desksList = gson.fromJson(desks, new TypeToken<List<MerchantDesk>>() {
                                    }.getType());
                                    if(desksList!=null&&desksList.size()>0) {
                                        MerchantDesk merchantDesk=desksList.get(0);
                                        mApp.saveData(mApp.KEY_GLOABLE_MERCHANTDESk,merchantDesk);
                                    }
                                }
                                EventBackground event = new EventBackground();
                                DishesListEntity DishesListEntity = new DishesListEntity();
                                DishesListEntity.setmDishTypeDataList(mDishTypeDataList);
                                DishesListEntity.setmAllDishesDataList(mAllDishesDataList);
                                event.setData(DishesListEntity);
                                event.setName(LoginActivity.class.getName());
                                event.setType(EventBackground.TYPE_FIRST);
                                event.setDescribe("菜单数据传入后台线程存入数据库");
                                EventBus.getDefault().post(event);

                            } else {
                                dismissLoginDialog();
                                showShortTip("菜品更新失败,请确认菜单! " + data.getString("msg"));
                                Log.d(TAG, "获取菜品数据有误!");
                            }
                        } catch (JSONException e) {
                            dismissLoginDialog();
                            showShortTip("菜品更新失败,请确认菜单! ");
                            e.printStackTrace();
                        }
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyLogTag", "VolleyError:" + error.getMessage(), error);
                        dismissLoginDialog();
                        showShortTip(VolleyErrorHelper.getMessage(error, mActivity));
                    }
                });
        executeRequest(httpGetMerchantDishes);
    }

    public void upLoadImageTest(){
        ImageInfo imageInfo=new ImageInfo();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo) ;
        imageInfo.setmBitmap(bitmap);
        imageInfo.setmName("mainFile");
        imageInfo.setFileName("ic_logo.png");
        imageInfo.setMime("image/png");
        String url="http://www.kxlive.com/tacos/uploadMainFile.do";
        Map<String, String> params=new HashMap<String, String>();
        params.put("postUploadRequest","图片上传");
        params.put("postUploadRequest-1","图片上传1");
        Log.i("s", "url:" + url);
        PostSingleUploadRequest postUploadRequest=new PostSingleUploadRequest(url,imageInfo,params,null,new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {

            }
        });
        executeRequest(postUploadRequest);
    }

    /*
    * 显示验证密码弹窗
    * */
    private void showCheckUserPwdDF() {
        CheckUserPwdDF lCheckUserPwdDF = CheckUserPwdDF.newInstance();
        lCheckUserPwdDF.setOnCheckUserPwdListener(mOnCheckUserPwdListener);
        lCheckUserPwdDF.show(getFragmentManager(), "CheckSysPwdDF");
    }

    CheckUserPwdDF.OnCheckUserPwdListener mOnCheckUserPwdListener=new CheckUserPwdDF.OnCheckUserPwdListener() {
        @Override
        public void onSelectBack(String pwd) {
            if (pwd.equals(SysEnv.SystemActivityPwd)){
                showShortTip("系统密码正确!");
                getOperation().forward(SystemActivity.class);
            }else{
                showShortTip("系统管理员密码不正确!");
            }
        }
    };
}