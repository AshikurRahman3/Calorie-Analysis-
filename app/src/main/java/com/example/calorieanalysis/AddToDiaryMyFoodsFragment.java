package com.example.calorieanalysis;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

/*
this fragment holds four different fragments that hold food items of different catagories for showing in the diary when user tries to add food that he have consumed.
 */
public class AddToDiaryMyFoodsFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_to_diary_my_foods, container, false);


        ViewPager viewPager = (ViewPager) view.findViewById(R.id.fragment_add_to_diary_my_foods_viewPager_Id);
        Add_to_Diary_my_foods_fragment_adapter add_to_diary_my_foods_fragment_adapter = new Add_to_Diary_my_foods_fragment_adapter(getChildFragmentManager());
        viewPager.setAdapter(add_to_diary_my_foods_fragment_adapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.fragment_add_to_diary_my_foods_tabLayout_Id);
        tabLayout.setupWithViewPager(viewPager);

        return  view;
    }
}