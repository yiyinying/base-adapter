package com.test;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.test.frag.BaseFrag;
import com.test.frag.Frag1;
import com.test.frag.Frag2;
import com.test.frag.Frag3;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    Context mContext;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    MyAdapter mAdapter;
    List<String> mTitles = new ArrayList<>();
    List<BaseFrag> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        initTitle();
        initFrag();
    }

    private void initTitle() {

        mTitles.add("有下拉有加载更多");
        mTitles.add("有下拉无加载更多");
        mTitles.add("无下拉无加载更多");
//        mTitles.add("Has Footer");

        mFragments.add(new Frag1());
        mFragments.add(new Frag2());
        mFragments.add(new Frag3());
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.id_TabLayout);
        mViewPager = (ViewPager) findViewById(R.id.id_ViewPager);
    }

    private void initFrag() {
        mAdapter = new MyAdapter(getSupportFragmentManager());
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(mAdapter);
    }

    class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mTitles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }
}
