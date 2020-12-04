package com.jxd.fc.fuzhu.Adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import java.util.ArrayList;

public class MyFragmemtPagerAdapter extends FragmentPagerAdapter {
    
    ArrayList<Fragment> mFragmentList;

    public MyFragmemtPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        mFragmentList = list;
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }

    @Override
    public int getCount() {
        return mFragmentList != null ?mFragmentList.size(): 0;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
    }



}
