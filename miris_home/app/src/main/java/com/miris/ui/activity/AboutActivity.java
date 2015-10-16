package com.miris.ui.activity;

import android.graphics.Color;
import android.os.Bundle;

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
                //.withFilterColor(Color.parseColor("#0075c0"))
                .withFilterColor(Color.parseColor("#000000"))
                .setBackground(this);


    }
}
