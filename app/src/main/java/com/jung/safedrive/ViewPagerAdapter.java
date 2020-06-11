package com.jung.safedrive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> items;

    ViewPagerAdapter(FragmentManager fm){
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        items=new ArrayList<>();
        items.add(new FragmentInfo());
        items.add(new FragmentSetting());
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence title="";
        switch (position){
            case 0:
                title="정보 입력";
                break;
            case 1:
                title="설정";
                break;
        }
        return title;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }
}
