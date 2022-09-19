package com.example.calorieanalysis;

/*
This is an adapter which holds four fragments which represent different food catagories like mainfood,snacks etc.
this is used in add to diary fragment

 */

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class Add_to_Diary_my_foods_fragment_adapter extends FragmentPagerAdapter {
    public Add_to_Diary_my_foods_fragment_adapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if(position == 0)
        {
            fragment = new DiaryMainFoodFragment();
        }
        else if(position == 1)
        {
            fragment = new DiarySnacksFragment();
        }
        else if(position == 2)
        {
            fragment = new DiaryDessertFragment();
        }
        else
        {
            fragment = new DiaryDrinksFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        if(position == 0)
        {
            title = "Main Foods";
        }
        else if(position == 1)
        {
            title = "Snacks";
        }
        else if(position == 2)
        {
            title = "Desserts";
        }
        else
        {
            title = "Drinks";
        }
        return title;
    }
}
