package com.example.calorieanalysis;

/*
this is the homescreen of the app

at first , we have used sqlite database, but later we have implemented firebase, so no need to use sqlite , but
we have not removed sqlite code from files, so, if any sqlite code removed without related all sqlite code there, app may crash.
 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity implements View.OnClickListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;





    String months[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    String gender = "Male";

//    Button home_dashboard_button,home_diary_button,home_bmi_button,home_food_button;


    MyDiaryDatabaseHelper myDiaryDatabaseHelper;
    MyFoodDatabaseHelper myFoodDatabaseHelper;
    WeightDatabaseHelper weightDatabaseHelper;


    private static final String TOTAL_CALORIE = "total_Calories";
    private static final String TABLE_NAME = "food_diary_details";
    private static final String ID = "_id";
    private static final String FOOD_NAME = "food_name";
    private static final String FOOD_PER_UNIT_CALORIE = "food_calorie_per_unit";
    private static final String FOOD_UNIT_NAME = "food_unit_name";
    private static final String DAY = "day";
    private static final String MONTH = "month";
    private static final String YEAR = "year";
    private static final String BREAKFAST_TOTAL_CALORIE = "breakfast_total_calories";
    private static final String LUNCH_TOTAL_CALORIE = "lunch_total_calories";
    private static final String DINNER_TOTAL_CALORIE = "dinner_total_calories";
    private static final String SNACKS_TOTAL_CALORIE = "snacks_total_calories";
    private static final String ITEMS_QUANTITY = "items_quantity";
    private static final String SELECTED_MEAL = "selected_meal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);





        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_id);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        navigationView = (NavigationView) findViewById(R.id.navigation_view_id);
        navigationView.setItemIconTintList(null);

        View header = navigationView.getHeaderView(0);
        TextView drawer_name_textview = (TextView) header.findViewById(R.id.drawer_name_textview_id);
        TextView drawer_email_textview = (TextView) header.findViewById(R.id.drawer_email_textview_id);
        ImageView drawer_image = (ImageView) header.findViewById(R.id.drawer_imageview_id);

        SharedPreferences sharedPreferences = getSharedPreferences("previous_account", Context.MODE_PRIVATE);
        String account_type = sharedPreferences.getString("account_type",null);

        if(account_type.equals("google"))
        {
            GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(HomeScreen.this);

            if(googleSignInAccount != null)
            {
                String name = googleSignInAccount.getDisplayName();
                String email = googleSignInAccount.getEmail();
                Picasso.get().load(googleSignInAccount.getPhotoUrl()).placeholder(R.mipmap.ic_launcher).into(drawer_image);
                drawer_email_textview.setText(email);
                drawer_name_textview.setText(name);
            }
        }
        else
        {
            drawer_email_textview.setText("");
            drawer_name_textview.setText("guest");
        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if(id == R.id.drawer_dashboard_id)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_screen_fragment_container_view_id,new DashboardFragment()).commit();
                    drawerLayout.closeDrawers();
                    change_activity_title("Dashboard");
                }

                if(id == R.id.drawer_diary_id)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_screen_fragment_container_view_id,new DiaryFragment()).commit();
                    drawerLayout.closeDrawers();
                    change_activity_title("Diary");
                }

                if(id == R.id.drawer_overall_statistics_id)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_screen_fragment_container_view_id,new DashboardHomeFragment()).commit();
                    drawerLayout.closeDrawers();
                    change_activity_title("Statistics");
                }

                if(id == R.id.drawer_item_bmi_id)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_screen_fragment_container_view_id,new BmiBmiFragment()).commit();
                    drawerLayout.closeDrawers();
                    change_activity_title("BMI");

                }
                if(id == R.id.drawer_item_weight_history_id)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_screen_fragment_container_view_id,new BmiWeightFragment()).commit();
                    drawerLayout.closeDrawers();
                    change_activity_title("Weight History");
                }


                if(id == R.id.drawer_item_main_food_id)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_screen_fragment_container_view_id,new MainFoodFragment()).commit();
                    drawerLayout.closeDrawers();
                    change_activity_title("Main Foods");
                }
                if(id == R.id.drawer_item_snack_id)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_screen_fragment_container_view_id,new SnacksFragment()).commit();
                    drawerLayout.closeDrawers();
                    change_activity_title("Snacks");
                }

                if(id == R.id.drawer_item_dessert_id)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_screen_fragment_container_view_id,new DessertFragment()).commit();
                    drawerLayout.closeDrawers();
                    change_activity_title("Desserts");
                }
                if(id == R.id.drawer_item_drink_id)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_screen_fragment_container_view_id,new DrinksFragment()).commit();
                    drawerLayout.closeDrawers();
                    change_activity_title("Drinks");
                }

                if(id == R.id.drawer_global_users_id)
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_screen_fragment_container_view_id,new GlobalUsersFragment()).commit();
                    drawerLayout.closeDrawers();
                    change_activity_title("Global Users");
                }

                if(id == R.id.drawer_item_user_detail_id)
                {
                    startActivity(new Intent(HomeScreen.this,UserBodyData.class));
                    drawerLayout.closeDrawers();
                }
                if(id == R.id.drawer_item_about_id)
                {
                    startActivity(new Intent(HomeScreen.this,AppDetails.class));
                    drawerLayout.closeDrawers();
                }

                if(id == R.id.drawer_item_sign_out_id)
                {
                    AlertDialog.Builder alertbuilder = new AlertDialog.Builder(HomeScreen.this);
                    alertbuilder.setTitle("Are you sure you want to sign out?");
                    alertbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences sharedPreferences = getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                            if(sharedPreferences.getString("account_type",null).equals("google"))
                            {
                                FirebaseAuth.getInstance().signOut();
                            }

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("has_account","no");
                            editor.commit();
                            startActivity(new Intent(HomeScreen.this,SignIn.class));
                            Toast.makeText(HomeScreen.this, "Signed Out", Toast.LENGTH_SHORT).show();
                        }
                    });

                    alertbuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    AlertDialog alertDialog = alertbuilder.create();
                    alertDialog.show();
                }



                return true;
            }
        });

        myDiaryDatabaseHelper = new MyDiaryDatabaseHelper(this);
        myFoodDatabaseHelper = new MyFoodDatabaseHelper(this);
        weightDatabaseHelper = new WeightDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = myFoodDatabaseHelper.getWritableDatabase();
        SQLiteDatabase sqLiteDatabase2 = myDiaryDatabaseHelper.getWritableDatabase();
        SQLiteDatabase sqLiteDatabase3 = weightDatabaseHelper.getWritableDatabase();


        getSupportFragmentManager().beginTransaction().replace(R.id.home_screen_fragment_container_view_id,new DashboardFragment()).commit();
        this.setTitle("Dashboard");

    }

    @Override
    public void onClick(View view) {

    }

    //this is no more used, we have implemented this function in sign in activity, where new user or existing users sign in
    public void check_user_data_inserted()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("User_body_data_inserted", Context.MODE_PRIVATE);
        if(!sharedPreferences.contains("is_user_body_data_inserted"))
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setCancelable(false);
            View enter_user_body_data_view = getLayoutInflater().inflate(R.layout.initial_user_body_details_entry,null);
            alertBuilder.setView(enter_user_body_data_view);

            alertBuilder.setPositiveButton("Continue",null);
            AlertDialog alertDialog = alertBuilder.create();


            Button date_button = (Button) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_date_button_id);
            Button save_button = (Button) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_save_button_id);
            Button recommend_calorie_goal_button = (Button) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_recommend_calorie_button_id);

            TextView male_textviw = (TextView) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_male_textview_id);
            TextView female_textviw = (TextView) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_female_textview_id);

            EditText feet_edittext = (EditText) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_feet_edittext_id);
            EditText inch_edittext = (EditText) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_inch_edittext_id);
            EditText weight_edittext = (EditText) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_current_weight_edittext_id);
            EditText desired_weight_edittext = (EditText) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_desired_weight_edittext_id);
            EditText calorie_goal_edittext = (EditText) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_calorie_goal_edittext_id);

            LinearLayout start_weight_linearLayout = (LinearLayout) enter_user_body_data_view.findViewById(R.id.start_weight_rootview_id) ;
            start_weight_linearLayout.setVisibility(View.GONE);

            male_textviw.setBackgroundColor(getResources().getColor(R.color.holo_green_light));

            calorie_goal_edittext.setText(""+2000);

            save_button.setVisibility(View.GONE);

            male_textviw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    male_textviw.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                    female_textviw.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                    gender = "Male";
                }
            });

            female_textviw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    female_textviw.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                    male_textviw.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                    gender = "Female";
                }
            });


            int day = 1;
            int month = 0;
            int year = 1998;

            final int[] selected_day = {day};
            final int[] selected_month = {month};
            final int[] selected_year = {year};

            date_button.setText(day + " " + months[month] + ", " + year);

            date_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(HomeScreen.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            date_button.setText(i2 + " " + months[i1] + ", " + i);
                            selected_day[0] = i2;
                            selected_month[0] = i1;
                            selected_year[0] = i;
                        }
                    },year,month,day);
                    datePickerDialog.show();
                }
            });


            recommend_calorie_goal_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String feet_string = feet_edittext.getText().toString();
                    String inch_string = inch_edittext.getText().toString();
                    String current_weight_string = weight_edittext.getText().toString();

                    if(feet_string.length() < 1 || inch_string.length() < 1 || current_weight_string.length() < 1)
                    {
                        if(feet_string.length() < 1)
                        {
                            feet_edittext.setError("Enter height first");
                        }

                        if(inch_string.length() < 1)
                        {
                            inch_edittext.setError("Enter height first");
                        }

                        if(current_weight_string.length() < 1)
                        {
                            weight_edittext.setError("Enter weight first");
                        }

                    }

                    else
                    {
                        int feet_height = Integer.parseInt(feet_string);
                        int inch_height = Integer.parseInt(inch_string);
                        float current_weight = Float.parseFloat(current_weight_string);



                        AlertDialog.Builder recommend_calorie_alertbuilder = new AlertDialog.Builder(HomeScreen.this);
                        View recommend_calorie_view = getLayoutInflater().inflate(R.layout.recommend_daily_calorie,null);

                        recommend_calorie_alertbuilder.setView(recommend_calorie_view);

                        Button recommend_calorie_date_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_date_button_id);
                        Button recommend_calorie_male_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_male_button_id);
                        Button recommend_calorie_female_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_female_button_id);
                        Button recommend_calorie_sedendary_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_sedendary_button_id);
                        Button recommend_calorie_light_active_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_light_active_button_id);
                        Button recommend_calorie_moderate_active_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_moderate_active_button_id);
                        Button recommend_calorie_very_active_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_very_active_button_id);
                        Button recommend_calorie_maintain_weight_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_maintain_weight_button_id);
                        Button recommend_calorie_loss_weight_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_loss_weight_button_id);
                        Button recommend_calorie_gain_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_gain_weight_button_id);
                        Button recommend_calorie_half_kg_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_half_kg_button_id);
                        Button recommend_calorie_one_kg_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_one_kg_button_id);


                        recommend_calorie_half_kg_button.setEnabled(false);
                        recommend_calorie_one_kg_button.setEnabled(false);

                        final int[] maintain_loss_gain = {0};
                        if(gender.equals("Male"))
                        {
                            recommend_calorie_male_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                            recommend_calorie_female_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                        }
                        else if(gender.equals("Female"))
                        {
                            recommend_calorie_female_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                            recommend_calorie_male_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                        }

                        final int[] life_style = {0};
                        recommend_calorie_sedendary_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                        recommend_calorie_maintain_weight_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));

                        recommend_calorie_date_button.setText(selected_day[0] + " " + months[selected_month[0]] + ", " + selected_year[0]);


                        recommend_calorie_date_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                DatePickerDialog recommend_calorie_datepicker_dialog = new DatePickerDialog(HomeScreen.this, new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                        selected_day[0] = i2;
                                        selected_month[0] = i1;
                                        selected_year[0] = i;
                                        recommend_calorie_date_button.setText(selected_day[0] + " " + months[selected_month[0]] + ", " + selected_year[0]);
                                        date_button.setText(selected_day[0] + " " + months[selected_month[0]] + ", " + selected_year[0]);
                                    }
                                },selected_year[0],selected_month[0],selected_day[0]);
                                recommend_calorie_datepicker_dialog.show();

                            }
                        });

                        recommend_calorie_male_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                gender = "Male";
                                recommend_calorie_male_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                                recommend_calorie_female_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            }
                        });

                        recommend_calorie_female_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                gender = "Female";
                                recommend_calorie_female_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                                recommend_calorie_male_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            }
                        });


                        recommend_calorie_sedendary_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                life_style[0] = 0;
                                recommend_calorie_sedendary_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                                recommend_calorie_light_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                                recommend_calorie_moderate_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                                recommend_calorie_very_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            }
                        });

                        recommend_calorie_light_active_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                life_style[0] = 1;
                                recommend_calorie_sedendary_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                                recommend_calorie_light_active_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                                recommend_calorie_moderate_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                                recommend_calorie_very_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            }
                        });

                        recommend_calorie_moderate_active_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                life_style[0] = 2;
                                recommend_calorie_sedendary_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                                recommend_calorie_light_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                                recommend_calorie_moderate_active_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                                recommend_calorie_very_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            }
                        });
                        recommend_calorie_very_active_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                life_style[0] = 3;
                                recommend_calorie_sedendary_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                                recommend_calorie_light_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                                recommend_calorie_moderate_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                                recommend_calorie_very_active_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                            }
                        });


                        recommend_calorie_maintain_weight_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                maintain_loss_gain[0] = 0;
                                recommend_calorie_maintain_weight_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                                recommend_calorie_loss_weight_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                                recommend_calorie_gain_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));


                                recommend_calorie_half_kg_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                                recommend_calorie_one_kg_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));

                                recommend_calorie_half_kg_button.setEnabled(false);
                                recommend_calorie_one_kg_button.setEnabled(false);
                            }
                        });
                        final int[] weight_loss_gain_per_week = {0};

                        recommend_calorie_loss_weight_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                maintain_loss_gain[0] = 1;
                                recommend_calorie_maintain_weight_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                                recommend_calorie_loss_weight_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                                recommend_calorie_gain_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                                weight_loss_gain_per_week[0] = 0;
                                recommend_calorie_half_kg_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                                recommend_calorie_one_kg_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));

                                recommend_calorie_half_kg_button.setEnabled(true);
                                recommend_calorie_one_kg_button.setEnabled(true);
                            }
                        });

                        recommend_calorie_gain_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                maintain_loss_gain[0] = 2;
                                recommend_calorie_maintain_weight_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                                recommend_calorie_loss_weight_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                                recommend_calorie_gain_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                                weight_loss_gain_per_week[0] = 0;
                                recommend_calorie_half_kg_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                                recommend_calorie_one_kg_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));

                                recommend_calorie_half_kg_button.setEnabled(true);
                                recommend_calorie_one_kg_button.setEnabled(true);
                            }
                        });


                        recommend_calorie_half_kg_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                weight_loss_gain_per_week[0] = 0;
                                recommend_calorie_half_kg_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                                recommend_calorie_one_kg_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            }
                        });

                        recommend_calorie_one_kg_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                weight_loss_gain_per_week[0] = 1;
                                recommend_calorie_one_kg_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                                recommend_calorie_half_kg_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            }
                        });



                        recommend_calorie_alertbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                        recommend_calorie_alertbuilder.setPositiveButton("Recommend",null);

                        AlertDialog recommend_calorie_alertdialog = recommend_calorie_alertbuilder.create();


                        recommend_calorie_alertdialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                Button recommend_button = recommend_calorie_alertdialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                recommend_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                        DatePicker datePicker = new DatePicker(HomeScreen.this);
                                        int current_month = datePicker.getMonth() + 1;
                                        int current_year = datePicker.getYear();

                                        int birth_month = selected_month[0] + 1;
                                        int birth_year = selected_year[0];

                                        float age = (float) ((current_year -1) - birth_year) + ((float) ((current_month -1 ) + (12 - birth_month))/12);

                                        double bmr = 1700.0;

                                        if(gender.equals("Male"))
                                        {
                                            bmr = 66 + (6.3 * (current_weight * 2.21)) + ( 12.9 * ((feet_height * 12) + inch_height)) - ( 6.8 * age);
                                        }
                                        else if(gender.equals("Female"))
                                        {
                                            bmr = 655 + (4.3 * (current_weight * 2.21)) + ( 4.7 * ((feet_height * 12) + inch_height)) - ( 4.7 * age);
                                        }

                                        double calories_for_maintain_weight = 2000.0;

                                        if(life_style[0] == 0)
                                        {
                                            calories_for_maintain_weight = bmr * 1.2;
                                        }
                                        else if(life_style[0] == 1)
                                        {
                                            calories_for_maintain_weight = bmr * 1.375;
                                        }
                                        else if(life_style[0] == 2)
                                        {
                                            calories_for_maintain_weight = bmr * 1.55;
                                        }
                                        else if(life_style[0] == 3)
                                        {
                                            calories_for_maintain_weight = bmr * 1.725;
                                        }

                                        double calculated_recommended_calorie = calories_for_maintain_weight;

                                        if(maintain_loss_gain[0] == 0)
                                        {
                                            calculated_recommended_calorie = calories_for_maintain_weight;
                                        }
                                        else if(maintain_loss_gain[0] == 1)
                                        {
                                            if(weight_loss_gain_per_week[0] == 0)
                                            {
                                                double pounds = 0.5 * 2.21;
                                                double reduce_calories = (double) (pounds * 3500) / 7;
                                                calculated_recommended_calorie -= reduce_calories;
                                            }
                                            else if(weight_loss_gain_per_week[0] == 1)
                                            {
                                                double pounds = 1.0 * 2.21;
                                                double reduce_calories = (double) (pounds * 3500) / 7;
                                                calculated_recommended_calorie -= reduce_calories;
                                            }
                                        }

                                        else if(maintain_loss_gain[0] == 2)
                                        {
                                            if(weight_loss_gain_per_week[0] == 0)
                                            {
                                                double pounds = 0.5 * 2.21;
                                                double reduce_calories = (double) (pounds * 3500) / 7;
                                                calculated_recommended_calorie += reduce_calories;
                                            }
                                            else if(weight_loss_gain_per_week[0] == 1)
                                            {
                                                double pounds = 1.0 * 2.21;
                                                double reduce_calories = (double) (pounds * 3500) / 7;
                                                calculated_recommended_calorie += reduce_calories;
                                            }
                                        }

                                        calorie_goal_edittext.setText("" + (int) calculated_recommended_calorie);

                                        dialogInterface.dismiss();


                                    }
                                });
                            }
                        });


                        recommend_calorie_alertdialog.show();
                    }

                }
            });

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button continue_button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    continue_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String feet_string = feet_edittext.getText().toString();
                            String inch_string = inch_edittext.getText().toString();
                            String weight_string = weight_edittext.getText().toString();
                            String desired_weight_string = desired_weight_edittext.getText().toString();
                            String calorie_goal_string = calorie_goal_edittext.getText().toString();

                            if(feet_string.length() < 1 || inch_string.length() < 1 || weight_string.length() < 1 || desired_weight_string.length() < 1 || calorie_goal_string.length() < 1)
                            {
                                if(feet_string.length() < 1)
                                {
                                    feet_edittext.setError("Enter feet value");
                                }

                                if(inch_string.length() < 1)
                                {
                                    inch_edittext.setError("Enter inch value");
                                }

                                if(weight_string.length() < 1)
                                {
                                    weight_edittext.setError("Enter weight");
                                }

                                if(desired_weight_string.length() < 1)
                                {
                                    desired_weight_edittext.setError("Enter desired weight");
                                }

                                if(calorie_goal_string.length() < 1)
                                {
                                    calorie_goal_edittext.setError("Enter daily calorie goal");
                                }
                            }


                            else
                            {

                                dialogInterface.dismiss();

                                int feet_height = Integer.parseInt(feet_string);
                                int inch_height = Integer.parseInt(inch_string);
                                int birth_day = selected_day[0];
                                int birth_month = selected_month[0];
                                int birth_year = selected_year[0];
                                int calorie_goals = Integer.parseInt(calorie_goal_string);
                                float weight = Float.parseFloat(weight_string);
                                float desired_weight = Float.parseFloat(desired_weight_string);

                                DatePicker datePicker = new DatePicker(HomeScreen.this);
                                int current_day = datePicker.getDayOfMonth();
                                int current_month = datePicker.getMonth() + 1;
                                int current_year = datePicker.getYear();

                                WeightDetails weightDetails = new WeightDetails(current_day,current_month,current_year,weight);
                                weightDatabaseHelper.insert_weight_data(weightDetails);

                                editor.putString("is_user_body_data_inserted","inserted");
                                editor.putString("gender",gender);
                                editor.putInt("feet_height",feet_height);
                                editor.putInt("inch_height",inch_height);
                                editor.putFloat("current_weight",weight);
                                editor.putFloat("start_weight",weight);
                                editor.putFloat("desired_weight",desired_weight);

                                editor.putInt("birth_day",birth_day);
                                editor.putInt("birth_month",birth_month);
                                editor.putInt("birth_year",birth_year);
                                editor.putInt("daily_calorie_goals",calorie_goals);
                                editor.commit();

                                getSupportFragmentManager().beginTransaction().replace(R.id.home_screen_fragment_container_view_id,new DashboardHomeFragment()).commit();
                            }
                        }
                    });
                }
            });

            alertDialog.show();


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_layout,menu);

        MenuItem done_item = menu.findItem(R.id.actionbar_done_button_id);
        done_item.setVisible(false);

        MenuItem redesigned_item = menu.findItem(R.id.redesigned_button_id);
        redesigned_item.setVisible(false);

        MenuItem home_button = menu.findItem(R.id.actionbarHomeButtonId);
        home_button.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {



        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        if(item.getItemId() == R.id.actionbar_about_button_id)
        {
            Intent intent = new Intent(HomeScreen.this,AppDetails.class);
            if(intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
        }

        if(item.getItemId() == R.id.sign_out_button_id)
        {
            AlertDialog.Builder alertbuilder = new AlertDialog.Builder(HomeScreen.this);
            alertbuilder.setTitle("Are you sure you want to sign out?");
            alertbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SharedPreferences sharedPreferences = getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                    if(sharedPreferences.getString("account_type",null).equals("google"))
                    {
                        FirebaseAuth.getInstance().signOut();
                    }

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("has_account","no");
                    editor.commit();
                    startActivity(new Intent(HomeScreen.this,SignIn.class));
                    Toast.makeText(HomeScreen.this, "Signed Out", Toast.LENGTH_SHORT).show();
                }
            });

            alertbuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog alertDialog = alertbuilder.create();
            alertDialog.show();
        }

        if(item.getItemId() == R.id.actionbar_user_details_button_id)
        {
            Intent intent = new Intent(HomeScreen.this,UserBodyData.class);
            if(intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public void insert_default_food_to_database()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("DefaulFoodInsertion",Context.MODE_PRIVATE);


        if(!sharedPreferences.contains("defaultFoodInserted"))
        {
            //main food
            ArrayList<FoodDetails> foods = new ArrayList<>();
            foods.add(new FoodDetails("Cooked Rice",205,"Cup",0));
            foods.add(new FoodDetails("Bread",77,"Slice",0));
            foods.add(new FoodDetails("Egg",72,"Piece",0));
            foods.add(new FoodDetails("Chicken breast,without skin",144,"100 grams",0));
            foods.add(new FoodDetails("Chicken drumstic,with skin",268,"100 grams",0));
            foods.add(new FoodDetails("Chicken drumstic,without skin",195,"100 grams",0));
            foods.add(new FoodDetails("Chicken thigh,with skin",277,"100 grams",0));
            foods.add(new FoodDetails("Chicken thigh,without skin",218,"100 grams",0));
            foods.add(new FoodDetails("Chicken wings,with skin",254,"100 grams",0));
            foods.add(new FoodDetails("Chicken wings,without skin",211,"100 grams",0));
            foods.add(new FoodDetails("Beef with fat",259,"100 grams",0));
            foods.add(new FoodDetails("Beef meat only",149,"100 grams",0));
            foods.add(new FoodDetails("Beef sausage",332,"100 grams",0));
            foods.add(new FoodDetails("Mutton with fat",295,"100 grams",0));
            foods.add(new FoodDetails("Mutton meat only",180,"100 grams",0));
            foods.add(new FoodDetails("Milk",122,"Cup",0));
            foods.add(new FoodDetails("Fish",117,"100 grams",0));
            foods.add(new FoodDetails("Fish fry,carp",162,"100 grams",0));
            foods.add(new FoodDetails("Fish,salmon",184,"100 grams",0));
            foods.add(new FoodDetails("Fish fry,tuna",184,"100 grams",0));
            foods.add(new FoodDetails("Fish,tilapia",128,"100 grams",0));
            foods.add(new FoodDetails("Cooked Dal",150,"Cup",0));
            foods.add(new FoodDetails("Mixed Vegetables",150,"Cup",0));






            //snacks
            foods.add(new FoodDetails("Beef Burger",540,"Piece",1));
            foods.add(new FoodDetails("Hamburger",297,"100 grams",1));
            foods.add(new FoodDetails("Chicken Burger",535,"Piece",1));
            foods.add(new FoodDetails("Chicken Nuggest",270,"100 grams",1));
            foods.add(new FoodDetails("Cheese Burger",308,"100 grams",1));
            foods.add(new FoodDetails("Hotdog",247,"100 grams",1));
            foods.add(new FoodDetails("Onion rings",411,"100 grams",1));
            foods.add(new FoodDetails("French Toast",264,"100 grams",1));
            foods.add(new FoodDetails("Popcorn",387,"100 grams",1));
            foods.add(new FoodDetails("Potato Chips",487,"100 grams",1));
            foods.add(new FoodDetails("Sandwich",155,"Piece",1));
            foods.add(new FoodDetails("Nachos",343,"100 grams",1));
            foods.add(new FoodDetails("Pizza",285,"Slice",1));
            foods.add(new FoodDetails("Pasta",196,"Cup",1));
            foods.add(new FoodDetails("Noddles",196,"Cup",1));
            foods.add(new FoodDetails("Chocolate Cookie Medium",148,"Piece",1));

            foods.add(new FoodDetails("Chocolate Cookie Medium",148,"Piece",1));
            foods.add(new FoodDetails("Croissant",304,"100 grams",1));





            // desserts
            foods.add(new FoodDetails("Ice Cream,Vanilla,rich",259,"100 grams",2));
            foods.add(new FoodDetails("Ice Cream,Vanilla,fat free",138,"100 grams",2));
            foods.add(new FoodDetails("Ice Cream,Strawberry,light",192,"100 grams",2));
            foods.add(new FoodDetails("Peanut bar",522,"100 grams",2));
            foods.add(new FoodDetails("Chocolate Cake,without frosting",371,"100 grams",2));
            foods.add(new FoodDetails("Fruit Cake",324,"100 grams",2));
            foods.add(new FoodDetails("Pound Cake",353,"100 grams",2));
            foods.add(new FoodDetails("Sponge Cake",290,"100 grams",2));
            foods.add(new FoodDetails("Fat free Cake",283,"100 grams",2));
            foods.add(new FoodDetails("Chesse Cake",321,"100 grams",2));
            foods.add(new FoodDetails("Chocolate Doughnuts",417,"100 grams",2));
            foods.add(new FoodDetails("Pancake,plain",227,"100 grams",2));
            foods.add(new FoodDetails("Puff Pastry",280,"Piece",2));
            foods.add(new FoodDetails("Fruit Custard",225,"Cup",2));
            foods.add(new FoodDetails("Muffin Medium",424,"Piece",2));
            foods.add(new FoodDetails("Egg Pudding",344,"Cup",2));

            foods.add(new FoodDetails("Dark Chocolate",598,"100 grams",2));
            foods.add(new FoodDetails("White Chocolate",539,"100 grams",2));
            foods.add(new FoodDetails("Honey",304,"100 grams",2));
            foods.add(new FoodDetails("Jellies",266,"100 grams",2));
            foods.add(new FoodDetails("Jellies,sugar free",121,"100 grams",2));
            foods.add(new FoodDetails("Candies,toffee",560,"100 grams",2));
            foods.add(new FoodDetails("Ice Cream,Chocolate,rich",251,"100 grams",2));
            foods.add(new FoodDetails("Ice Cream,Chocolate,sugar free",173,"100 grams",2));


            // drinks
            foods.add(new FoodDetails("Coffee with milk and sugar",65,"Cup",3));
            foods.add(new FoodDetails("Milk,whole",150,"Cup",3));
            foods.add(new FoodDetails("Green Tea,sugar free",0,"Cup",3));
            foods.add(new FoodDetails("Green Tea,with sugar",67,"Cup",3));
            foods.add(new FoodDetails("Black Coffee",3,"Cup",3));
            foods.add(new FoodDetails("Black Tea,sugar free",3,"Cup",3));
            foods.add(new FoodDetails("Lemon tea,sugar free",5,"Cup",3));
            foods.add(new FoodDetails("Lemon tea with sugar",88,"Cup",3));
            foods.add(new FoodDetails("Coke",110,"Glass",3));
            foods.add(new FoodDetails("Coconut Water",45,"Glass",3));
            foods.add(new FoodDetails("Apple Juice,light",55,"Glass",3));
            foods.add(new FoodDetails("Vanilla Milkshake",280,"Glass",3));
            foods.add(new FoodDetails("Strawberry shake",113,"100 grams",3));
            foods.add(new FoodDetails("Mixed Fruit Juice",135,"Glass",3));

            foods.add(new FoodDetails("Instant Coffee,powder",353,"100 grams",3));

            for(int i = 0; i < foods.size(); i++)
            {
                myFoodDatabaseHelper.insertFoodData(foods.get(i));
            }
            //sharedPreferences = getSharedPreferences("DefaulFoodInsertion", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("defaultFoodInserted",true);
            editor.commit();
        }
    }

    public void change_activity_title(String title)
    {
        this.setTitle(title);
    }

}