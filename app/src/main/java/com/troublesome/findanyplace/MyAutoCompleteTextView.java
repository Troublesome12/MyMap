package com.troublesome.findanyplace;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

/**
 * Created by troublesome on 9/4/15.
 */
public class MyAutoCompleteTextView extends AutoCompleteTextView {

        Context context;

        public MyAutoCompleteTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.context = context;
        }

        @Override
        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK &&
                    event.getAction() == KeyEvent.ACTION_UP) {

                setText("");
                clearFocus();
                return false;
            }
            return super.dispatchKeyEvent(event);
        }
}
