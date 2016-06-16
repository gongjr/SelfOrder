package com.asiainfo.selforder.ui;

import android.os.Bundle;
import android.widget.EditText;

import com.asiainfo.selforder.R;
import com.asiainfo.selforder.widget.NumberKeyBoard;

import kxlive.gjrlibrary.base.BaseActivity;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 16/6/16 下午2:44
 */
public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.meal_number);
        EditText editText = (EditText) findViewById(R.id.meal_edit);
        NumberKeyBoard numberKeyBoard = new NumberKeyBoard(this, this, editText);
    }
}
