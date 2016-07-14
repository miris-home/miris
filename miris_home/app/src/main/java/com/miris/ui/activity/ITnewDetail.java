package com.miris.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
<<<<<<< HEAD
import android.support.design.widget.FloatingActionButton;
=======
>>>>>>> c6ef70079d064afbaa1da9f03cc7081bdae096f6
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

<<<<<<< HEAD
import com.bumptech.glide.Glide;
import com.miris.R;
import com.miris.ui.comp.DetailFragment;
=======
import com.miris.R;
import com.miris.ui.comp.DetailFragment;
import com.squareup.picasso.Picasso;
>>>>>>> c6ef70079d064afbaa1da9f03cc7081bdae096f6

import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
import butterknife.InjectView;
import butterknife.OnClick;

=======
>>>>>>> c6ef70079d064afbaa1da9f03cc7081bdae096f6
/**
 * Created by Miris on 09.02.15.
 */
public class ITnewDetail extends BaseDrawerActivity {

    private ViewPager mViewPager;
    String urlName;
    String urlTitle;
    String urlTimg;

<<<<<<< HEAD
    @InjectView(R.id.btnCreate)
    FloatingActionButton fabCreate;

=======
>>>>>>> c6ef70079d064afbaa1da9f03cc7081bdae096f6
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itnews_detatil);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Intent intent = getIntent();
        urlName = intent.getExtras().getString("url");
        urlTitle = intent.getExtras().getString("title");
        urlTimg = intent.getExtras().getString("img");

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
<<<<<<< HEAD

        collapsingToolbar.setTitle(urlTitle);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.CollapsedAppBar);
//        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        ImageView ivImage = (ImageView)findViewById(R.id.ivImage);
        if(!urlTimg.equals("")) {
            Glide.with(this)
                    .load(urlTimg)
                    .into(ivImage);
        }
        ivImage.setColorFilter(R.color.color_88000000);
=======
        collapsingToolbar.setTitle(urlTitle);

        ImageView ivImage = (ImageView)findViewById(R.id.ivImage);
        if(!urlTimg.equals("")) {
            Picasso.with(this)
                    .load(urlTimg)
                    .placeholder(R.drawable.img_circle_placeholder)
                    .resize(this.getResources().getDimensionPixelSize(R.dimen.img_size),
                            this.getResources().getDimensionPixelSize(R.dimen.img_size))
                    .centerInside()
                    .into(ivImage);
        }
>>>>>>> c6ef70079d064afbaa1da9f03cc7081bdae096f6
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);
    }

<<<<<<< HEAD
    @OnClick(R.id.btnCreate)
    public void onShareClick() {
        Intent msg = new Intent(Intent.ACTION_SEND);
        msg.addCategory(Intent.CATEGORY_DEFAULT);
        msg.putExtra(Intent.EXTRA_TEXT, urlName);
        msg.setType("text/plain");
        startActivity(Intent.createChooser(msg, "공유"));
    }

=======
>>>>>>> c6ef70079d064afbaa1da9f03cc7081bdae096f6
    private void setupViewPager(ViewPager mViewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(DetailFragment.newInstance("book_content.txt", urlName), "News");
        mViewPager.setAdapter(adapter);
    }

    static class MyPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
