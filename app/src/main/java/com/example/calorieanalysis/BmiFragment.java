package com.example.calorieanalysis;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

/*
this holds two fragments , bmi and weight

at first , we have used sqlite database, but later we have implemented firebase, so no need to use sqlite , but
we have not removed sqlite code from files, so, if any sqlite code removed without related all sqlite code there, app may crash.
 */

public class BmiFragment extends Fragment {

    String months[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    WeightDatabaseHelper weightDatabaseHelper;

    BmiFragmentAdapter bmi_fragment_Adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_bmi, container, false);

        weightDatabaseHelper = new WeightDatabaseHelper(getActivity());


        SQLiteDatabase sqLiteDatabase = weightDatabaseHelper.getWritableDatabase();

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.bmi_fragment_viewPager_Id);
         bmi_fragment_Adapter = new BmiFragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(bmi_fragment_Adapter);




        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.bmi_fragment_tabLayout_Id);
        tabLayout.setupWithViewPager(viewPager);

        


        return  view;
    }


}