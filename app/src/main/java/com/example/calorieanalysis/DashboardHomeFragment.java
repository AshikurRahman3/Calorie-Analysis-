package com.example.calorieanalysis;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
this shows summery of user like weight, body fat percentage, meals total colories, etc

at first , we have used sqlite database, but later we have implemented firebase, so no need to use sqlite , but
we have not removed sqlite code from files, so, if any sqlite code removed without related all sqlite code there, app may crash.
 */

public class DashboardHomeFragment extends Fragment {

    DatabaseReference databaseReference;

    int current_day,current_month,current_year;
    int consumed_calories = 0,calorie_goal=2000;

    TextView bmi_textview,weight_textview,body_type_textview,target_weight_textview,breakfast_textview,
            lunch_textview,dinner_textview,snacks_textview,calorie_goal_textview,Cosumed_calorie_textview,left_calorie_textview
            ,body_fat_textview,lost_weight_textview,remaining_weight_textview,weight_loss_status_textview;

    ProgressBar progressBar;
    DatePicker datePicker;
    double consumed_percentage = 0.0;

    MyDiaryDatabaseHelper myDiaryDatabaseHelper;
    WeightDatabaseHelper weightDatabaseHelper;
    Cursor breakfast_cursor,lunch_cursor,dinner_cursor,snacks_cursor;

    private static final String TABLE_NAME = "food_diary_details";
    private static final String ID = "_id";
    private static final String DAY = "day";
    private static final String MONTH = "month";
    private static final String YEAR = "year";
    private static final String BREAKFAST_TOTAL_CALORIE = "breakfast_total_calories";
    private static final String LUNCH_TOTAL_CALORIE = "lunch_total_calories";
    private static final String DINNER_TOTAL_CALORIE = "dinner_total_calories";
    private static final String SNACKS_TOTAL_CALORIE = "snacks_total_calories";
    private static final String ITEMS_QUANTITY = "items_quantity";
    private static final String SELECTED_MEAL = "selected_meal";
    private static final String TOTAL_CALORIE = "total_Calories";
    private static final String FOOD_TOTAL_CALORIE = "food_total_Calories";


    double current_weight;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard_home, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();


        myDiaryDatabaseHelper = new MyDiaryDatabaseHelper(getActivity().getApplicationContext());
        SQLiteDatabase sqLiteDatabase = myDiaryDatabaseHelper.getWritableDatabase();

        weightDatabaseHelper = new WeightDatabaseHelper(getActivity().getApplicationContext());
        SQLiteDatabase sqLiteDatabase2 = weightDatabaseHelper.getWritableDatabase();

        bmi_textview = (TextView) view.findViewById(R.id.dashboard_bmi_value_textview_id);
         weight_textview = (TextView) view.findViewById(R.id.dashboard_weight_textview_id);
         body_type_textview = (TextView) view.findViewById(R.id.dashboard_body_type_id);
        target_weight_textview = (TextView) view.findViewById(R.id.dashboard_target_weight_textview_id);
         breakfast_textview = (TextView) view.findViewById(R.id.dashboard_breakfast_textview_id);
         lunch_textview = (TextView) view.findViewById(R.id.dashboard_lunch_textview_id);
        dinner_textview = (TextView) view.findViewById(R.id.dashboard_dinner_textview_id);
         snacks_textview = (TextView) view.findViewById(R.id.dashboard_snacks_textview_id);
         calorie_goal_textview = (TextView) view.findViewById(R.id.dashboard_calorie_goal_value_textview_id);
         Cosumed_calorie_textview = (TextView) view.findViewById(R.id.dashboard_calorie_consumed_textview_id);
         left_calorie_textview = (TextView) view.findViewById(R.id.dashboard_left_calorie_textview_id);
        body_fat_textview = (TextView) view.findViewById(R.id.dashboard_body_fat_textview_id);
        lost_weight_textview = (TextView) view.findViewById(R.id.dashboard_lost_weight_textview_id);
         remaining_weight_textview = (TextView) view.findViewById(R.id.dashboard_remaining_weight_textview_id);
          weight_loss_status_textview = (TextView) view.findViewById(R.id.dash_board_weight_loss_status_textview_id);

        progressBar = (ProgressBar) view.findViewById(R.id.dashboard_progressBar_id);

        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("previous_account", Context.MODE_PRIVATE);

        calorie_goal = sharedPreferences.getInt("calorie_goal",2000);

        return view;
    }

    @Override
    public void onStart() {
        read_users_basic_data();
        read_users_meal_diary();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    read_users_basic_data();
    read_users_meal_diary();
    }

    public void read_users_basic_data()
    {

        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("user_id",null); // user id of the user , which is needed to get data

        databaseReference.child("users").child(id).child("basics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserBasics userBasics = snapshot.getValue(UserBasics.class);

                double target_weight = userBasics.getTarget_weight();
                double start_weight = userBasics.getStart_weight();
                double current_weight = userBasics.getCurrent_weight();
                int daily_calorie_goals = userBasics.getDaily_calorie_goal();
                int feet_height = userBasics.getFeet_height();
                int inch_height = userBasics.getInch_height();
                int birth_month = userBasics.getBirth_month();
                int birth_year = userBasics.getBirth_year();
                String gender = userBasics.getGender();

                double weight_lost = (double) current_weight - start_weight;
                double weight_remaining = (double) current_weight - target_weight;
                calorie_goal = daily_calorie_goals;

                weight_textview.setText(String.format("%.1f",current_weight));
                lost_weight_textview.setText(String.format("%.1f",weight_lost));
                remaining_weight_textview.setText(String.format("%.1f",weight_remaining));
                target_weight_textview.setText(String.format("%.1f",target_weight));
                calorie_goal_textview.setText(""+daily_calorie_goals);


                if(weight_lost < 0.0)
                {
                    lost_weight_textview.setTextColor(getResources().getColor(R.color.dark_green));
                    weight_loss_status_textview.setText("You Lost");
                }
                else if(weight_lost >0.0)
                {
                    lost_weight_textview.setTextColor(getResources().getColor(R.color.red_light));
                    weight_loss_status_textview.setText("You Gained");
                    lost_weight_textview.setText("+" + String.format("%.1f",weight_lost));
                }
                else if(weight_lost == 0.0)
                {
                    lost_weight_textview.setTextColor(getResources().getColor(R.color.black));
                    weight_loss_status_textview.setText("You Lost");
                }


                float bmi = (float) (current_weight / ((((12*feet_height + inch_height) * 2.54)/100) * (((12*feet_height + inch_height) * 2.54)/100)));
                bmi_textview.setText(String.format("%.1f",bmi));
                remaining_weight_textview.setText(String.format("%.2f",current_weight - target_weight));

                DatePicker datePicker = new DatePicker(getActivity().getApplicationContext());
                int current_month = datePicker.getMonth() + 1;
                int current_year = datePicker.getYear();



                float age = (float) ((current_year -1) - birth_year) + ((float) ((current_month -1 ) + (12 - birth_month))/12);



                float body_fat_percentage = 20.0F;
                if(gender.equals("Female"))
                {
                    body_fat_percentage = (float) (1.20 * bmi + 0.23 * age - 5.4);
                }
                else if(gender.equals("Male"))
                {
                    body_fat_percentage = (float) (1.20 * bmi + 0.23 * age - 16.2);
                }
                body_fat_textview.setText(String.format("%.1f",body_fat_percentage) + " %");

                if(bmi >= 18.5 && bmi <= 24.9)
                {
                    body_type_textview.setText("Normal");
                    body_type_textview.setTextColor(getResources().getColor(R.color.holo_green_light));
                    bmi_textview.setTextColor(getResources().getColor(R.color.holo_green_light));
                }
                else if(bmi > 24.9)
                {
                    body_type_textview.setText("Obese");
                    body_type_textview.setTextColor(getResources().getColor(R.color.red_light));
                    bmi_textview.setTextColor(getResources().getColor(R.color.red_light));
                }
                else if(bmi < 18.5)
                {
                    body_type_textview.setText("Under Weight");
                    body_type_textview.setTextColor(getResources().getColor(R.color.blue_light));
                    bmi_textview.setTextColor(getResources().getColor(R.color.blue_light));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void read_users_meal_diary()
    {
        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("user_id",null); // user id of the user , which is needed to get data

        datePicker = new DatePicker(getActivity().getApplicationContext());

        current_day = datePicker.getDayOfMonth();
        current_month = datePicker.getMonth() + 1;
        current_year = datePicker.getYear();

        String current_date = String.valueOf(current_day) + String.valueOf(current_month) + String.valueOf(current_year);

        databaseReference.child("users").child(id).child("meal_diary").child(current_date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int breakfast_total=0,counter = 0;
                int lunch_total = 0,dinner_total = 0,snacks_total = 0, total = 0;

                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    DiaryFoodDetails diaryFoodDetails = snapshot1.getValue(DiaryFoodDetails.class);

                    if(diaryFoodDetails.getMeal_type() == 0)
                    {
                        breakfast_total += diaryFoodDetails.getFood_totoal_calories();
                        total += diaryFoodDetails.getFood_totoal_calories();
                    }
                    else if(diaryFoodDetails.getMeal_type() == 1)
                    {
                        lunch_total += diaryFoodDetails.getFood_totoal_calories();
                        total += diaryFoodDetails.getFood_totoal_calories();
                    }
                    else if(diaryFoodDetails.getMeal_type() == 2)
                    {
                        dinner_total += diaryFoodDetails.getFood_totoal_calories();
                        total += diaryFoodDetails.getFood_totoal_calories();
                    }
                    else if(diaryFoodDetails.getMeal_type() == 3)
                    {
                        snacks_total += diaryFoodDetails.getFood_totoal_calories();
                        total += diaryFoodDetails.getFood_totoal_calories();
                    }


                }
                breakfast_textview.setText(""+breakfast_total);
                lunch_textview.setText(""+lunch_total);
                dinner_textview.setText(""+dinner_total);
                snacks_textview.setText(""+snacks_total);

                consumed_calories = breakfast_total + lunch_total + dinner_total + snacks_total;
                Cosumed_calorie_textview.setText(""+consumed_calories);

                if(consumed_calories > calorie_goal)
                {
                    Cosumed_calorie_textview.setTextColor(getResources().getColor(R.color.red_light));
                }
                else if(consumed_calories <= calorie_goal)
                {
                    Cosumed_calorie_textview.setTextColor(getResources().getColor(R.color.holo_green_light));
                }

                int left_calories = calorie_goal - consumed_calories;


                left_calorie_textview.setText(""+left_calories);

                if(left_calories < 0)
                {
                    left_calorie_textview.setTextColor(getResources().getColor(R.color.red_light));
                }
                else
                {
                    left_calorie_textview.setTextColor(getResources().getColor(R.color.black));
                }


                double calorie_percentage_consumed =  (((double)consumed_calories / calorie_goal) * 100);
                consumed_percentage = calorie_percentage_consumed;
                double adder =(double) calorie_percentage_consumed / 40;
                final double[] progress = {0};

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for(int i =1;i<=40;i++)
                        {
                            try {
                                Thread.sleep(30);
                                progress[0] += adder;
                                progressBar.setProgress((int)progress[0]);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
                thread.start();

                progressBar.setProgress((int)calorie_percentage_consumed);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}