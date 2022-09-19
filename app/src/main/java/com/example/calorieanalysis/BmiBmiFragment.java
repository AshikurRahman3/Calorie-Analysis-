package com.example.calorieanalysis;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
this shows users body related data like weight ,bmi etc.

at first , we have used sqlite database, but later we have implemented firebase, so no need to use sqlite , but
we have not removed sqlite code from files, so, if any sqlite code removing without related all sqlite code there, app may crash
 */

public class BmiBmiFragment extends Fragment {

    String months[] = {"0","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec","0","0"};

    SharedPreferences sharedPreferences;

    WeightDatabaseHelper weightDatabaseHelper;

    TextView bmi_textview,height_weight_for_bmi_textview,body_type_textview,
    difference_weight_than_normal_textview,current_weight_textview,
    desired_weight_textview,current_weight_date_textview,remaining_weight_textview,
    underweight_textview,normal_weight_textview,obese_weight_textview;


    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bmi_bmi, container, false);

        bmi_textview = (TextView) view.findViewById(R.id.bmi_bmi_fragment_bmi_textview_id);
        height_weight_for_bmi_textview = (TextView) view.findViewById(R.id.bmi_bmi_fragment_height_weight_for_bmi_textview_id);
        body_type_textview = (TextView) view.findViewById(R.id.bmi_bmi_fragment_body_type_textview_id);
        difference_weight_than_normal_textview = (TextView) view.findViewById(R.id.bmi_bmi_fragment_difference_weight_than_normal_textview_id);
        current_weight_textview = (TextView) view.findViewById(R.id.bmi_bmi_fragment_current_weight_textview_id);
        desired_weight_textview = (TextView) view.findViewById(R.id.bmi_bmi_fragment_desired_weight_textview_id);
        current_weight_date_textview = (TextView) view.findViewById(R.id.bmi_bmi_fragment_current_weight_date_textview_id);
        remaining_weight_textview = (TextView) view.findViewById(R.id.bmi_bmi_fragment_remaining_weight_textview_id);
        underweight_textview = (TextView) view.findViewById(R.id.bmi_bmi_fragment_underweight_textview_id);
        normal_weight_textview = (TextView) view.findViewById(R.id.bmi_bmi_fragment_normal_weight_textview_id);
        obese_weight_textview = (TextView) view.findViewById(R.id.bmi_bmi_fragment_obese_weight_textview_id);


        sharedPreferences = getActivity().getSharedPreferences("User_body_data_inserted", Context.MODE_PRIVATE);
        weightDatabaseHelper = new WeightDatabaseHelper(getActivity());

        databaseReference = FirebaseDatabase.getInstance().getReference();



        load_data();


        return  view;
    }


    public  void load_data()
    {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("user_id",null); // user id of the user , which is needed to get data


        databaseReference.child("users").child(id).child("basics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserBasics userBasics = snapshot.getValue(UserBasics.class);
                double current_weight = userBasics.getCurrent_weight();
                double desired_weight = userBasics.getTarget_weight();
                int current_day = userBasics.getCurrent_weight_day();
                int current_month = userBasics.getCurrent_weight_month();
                int current_year = userBasics.getCurrent_weight_year();
                int feet_height = userBasics.getFeet_height();
                int inch_height = userBasics.getInch_height();

                float bmi = (float) (current_weight / ((((12*feet_height + inch_height) * 2.54)/100) * (((12*feet_height + inch_height) * 2.54)/100)));
                bmi_textview.setText(String.format("%.1f",bmi));

                height_weight_for_bmi_textview.setText(feet_height + " ft " + inch_height + " inch, " + String.format("%.1f",current_weight) + " kg");

                if(bmi >= 18.5 && bmi <= 24.9)
                {
                    body_type_textview.setText("Normal");
                    body_type_textview.setTextColor(getResources().getColor(R.color.holo_green_light));
                    bmi_textview.setTextColor(getResources().getColor(R.color.holo_green_light));
                    difference_weight_than_normal_textview.setText("Normal Weight");
                    difference_weight_than_normal_textview.setTextColor(getResources().getColor(R.color.holo_green_light));
                }
                else if(bmi > 24.9)
                {
                    body_type_textview.setText("Obese");
                    body_type_textview.setTextColor(getResources().getColor(R.color.red_light));
                    bmi_textview.setTextColor(getResources().getColor(R.color.red_light));
                    double normal_weight = 24.9 * ((((12*feet_height + inch_height) * 2.54)/100) * (((12*feet_height + inch_height) * 2.54)/100));
                    double difference_than_normal  = current_weight - normal_weight;
                    difference_weight_than_normal_textview.setText(String.format("%.1f",difference_than_normal) + " kg more than normal");
                    difference_weight_than_normal_textview.setTextColor(getResources().getColor(R.color.red_light));
                }
                else if(bmi < 18.5)
                {
                    body_type_textview.setText("Under Weight");
                    body_type_textview.setTextColor(getResources().getColor(R.color.blue_light));
                    bmi_textview.setTextColor(getResources().getColor(R.color.blue_light));

                    double normal_weight = 18.5 * ((((12*feet_height + inch_height) * 2.54)/100) * (((12*feet_height + inch_height) * 2.54)/100));
                    double difference_than_normal  = normal_weight -  current_weight;
                    difference_weight_than_normal_textview.setText(String.format("%.1f",difference_than_normal) + " kg less than normal");
                    difference_weight_than_normal_textview.setTextColor(getResources().getColor(R.color.blue_light));
                }

                double under_weight = 18.5 * ((((12*feet_height + inch_height) * 2.54)/100) * (((12*feet_height + inch_height) * 2.54)/100));
                double over_weight = 24.9 * ((((12*feet_height + inch_height) * 2.54)/100) * (((12*feet_height + inch_height) * 2.54)/100));

                underweight_textview.setText("<" + String.format("%.0f",under_weight) + " kg");
                obese_weight_textview.setText(">" + String.format("%.0f",over_weight) + " kg");
                normal_weight_textview.setText(String.format("%.0f",under_weight) + " - " + String.format("%.0f",over_weight) + " kg");

                current_weight_textview.setText(String.format("%.1f",current_weight));
                current_weight_date_textview.setText(current_day + " " + months[current_month] + ", " + current_year);
                desired_weight_textview.setText(String.format("%.1f",desired_weight));
                remaining_weight_textview.setText(String.format("%.1f",(current_weight - desired_weight)) + " kg remaining");




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });








    }


    @Override
    public void onResume() {
        super.onResume();
        load_data();
    }

}