package com.example.calorieanalysis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/*
to hold history and food fragments in diary , when user tries to add food that he have taken
 */

public class Diary_add_food_fragment_adapter extends FragmentPagerAdapter {

    public Diary_add_food_fragment_adapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if(position == 0)
        {
            fragment = new AddToDiaryHistoryFragment();
        }
        else if(position == 1)
        {
            fragment = new AddToDiaryMyFoodsFragment();
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
            title = "History";
        }
        else if(position == 1)
        {
            title = "My Foods";
        }

        return title;
    }
}
