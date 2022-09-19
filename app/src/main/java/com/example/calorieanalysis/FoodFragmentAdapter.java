package com.example.calorieanalysis;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/*
to show food fragments
 */
public class FoodFragmentAdapter extends FragmentPagerAdapter {

    public FoodFragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if(position == 0)
        {
            fragment = new MainFoodFragment();
        }
        else if(position == 1)
        {
            fragment = new SnacksFragment();
        }
        else if(position == 2)
        {
            fragment = new DessertFragment();
        }
        else
        {
            fragment = new DrinksFragment();
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
