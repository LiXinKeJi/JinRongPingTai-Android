package com.finance.client.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Slingge on 2017/4/24 0024.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentLst;
    private List<String> strList;

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentLst) {
        super(fm);
        this.fragmentLst = fragmentLst;
    }

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentLst, List<String> strList) {
        super(fm);
        this.fragmentLst = fragmentLst;
        this.strList = strList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentLst.get(position);
    }

    @Override
    public int getCount() {
        return fragmentLst.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if (strList != null) {
            return strList.get(position);
        } else {
            return "";
        }
    }

}
