package com.example.calorieanalysis;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

/*
this holds four food fragments
 */

public class FoodFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_foods, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.food_fragment_viewPager_Id);
        FoodFragmentAdapter foodFragmentAdapter = new FoodFragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(foodFragmentAdapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.food_fragment_tabLayout_Id);
        tabLayout.setupWithViewPager(viewPager);

        return  view;
    }
}