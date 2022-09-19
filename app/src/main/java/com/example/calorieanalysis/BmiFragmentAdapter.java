package com.example.calorieanalysis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

/*
to hold two fragments
 */

public class BmiFragmentAdapter extends FragmentPagerAdapter {
    public BmiFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if(position == 0)
        {
            fragment = new BmiBmiFragment();
        }
        else
        {
            fragment = new BmiWeightFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        if(position == 0)
        {
            title = "Bmi";
        }
        else
        {
            title = "Weights";
        }
        return title;
    }

}
