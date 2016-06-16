package com.asiainfo.selforder.widget;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.widget.EditText;

import com.asiainfo.selforder.R;

/**
 * @author skynight(skynight@dingtalk.com)
 * @creatTime 16/6/16 下午2:29
 */
public class NumberKeyBoard implements KeyboardView.OnKeyboardActionListener {

    private Activity act;
    private Context ctx;
    private EditText editText;
    private Keyboard keyboard;
    private KeyboardView keyboardView;


    public NumberKeyBoard(Activity act, Context ctx, EditText editText) {
        this.act = act;
        this.ctx = ctx;
        this.editText = editText;
        keyboard = new Keyboard(ctx, R.xml.number);
        keyboardView = (KeyboardView) act.findViewById(R.id.keyboard_view);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(this);
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        Editable editable = editText.getText();
        int start = editText.getSelectionStart();
        if (primaryCode == Keyboard.KEYCODE_DELETE) {
            if (editable != null && editable.length() > 0) {
                if (start > 0) {
                    editable.delete(start - 1, start);
                }
            }
        } else if (primaryCode == 100001) {

        } else {
            editable.insert(start, Character.toString((char) primaryCode));
        }

    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
