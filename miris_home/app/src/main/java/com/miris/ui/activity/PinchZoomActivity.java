package com.miris.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.miris.R;
import com.miris.ui.adapter.BlurBehind;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by fantastic on 2016-07-20.
 */
public class PinchZoomActivity extends BaseDrawerActivity{

    ImageView m_imageView;
    PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view);

        BlurBehind.getInstance()
                .withAlpha(100)
                .withFilterColor(Color.parseColor("#000000"))
                .setBackground(this);

        m_imageView = (ImageView) findViewById(R.id.imageView);

        Glide.with(this).load(userProfileListData.get(0).getuser_img_url())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String s,
                                               Target<GlideDrawable> target, boolean b) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable glideDrawable,
                                                   String s, Target<GlideDrawable> target, boolean b,
                                                   boolean b1) {
                        if (mAttacher != null) {
                            mAttacher.update();
                        } else {
                            mAttacher = new PhotoViewAttacher(m_imageView);
                        }
                        return false;
                    }
                }).diskCacheStrategy(DiskCacheStrategy.ALL).into(m_imageView);
    }
}
