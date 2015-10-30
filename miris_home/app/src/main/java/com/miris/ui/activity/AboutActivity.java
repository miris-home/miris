package com.miris.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

import com.miris.R;
import com.miris.ui.adapter.BlurBehind;

/**
 * Created by fantastic on 2015-10-16.
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        BlurBehind.getInstance()
                .withAlpha(100)
                .withFilterColor(Color.parseColor("#000000"))
                .setBackground(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (super.onTouchEvent(ev)) {
            return true;
        }

        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                finish();
                break;
        }
        return true;
    }
}
