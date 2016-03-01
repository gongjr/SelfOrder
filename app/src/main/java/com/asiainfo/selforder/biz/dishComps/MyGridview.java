package com.asiainfo.selforder.biz.dishComps;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * .
 *
 * @author 姚海林(skynight@dingtalk.com)
 * @version V1.0, 16/2/26 下午4:57
 */
public class MyGridview extends GridView {

    public MyGridview(Context context) {
        super(context);
    }

    public MyGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
