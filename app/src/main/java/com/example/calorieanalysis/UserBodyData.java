package com.example.calorieanalysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
to show users basic info,

at first , we have used sqlite database, but later we have implemented firebase, so no need to use sqlite , but
we have not removed sqlite code from files, so, if any sqlite code removed without related all sqlite code there, app may crash.
 */
public class UserBodyData extends AppCompatActivity {

    String months[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    String[] gender = {"Male"};

    WeightDatabaseHelper weightDatabaseHelper;
    DatabaseReference databaseReference;


    double start_weight,current_weight,target_weight;

        int feet_height,
        inch_height,daily_calorie_goals,birth_day,birth_month,birth_year;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_user_body_details_entry);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        weightDatabaseHelper = new WeightDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase2 = weightDatabaseHelper.getWritableDatabase();

        databaseReference = FirebaseDatabase.getInstance().getReference();



        Button date_button = (Button) findViewById(R.id.insert_user_body_data_date_button_id);
        Button save_button = (Button) findViewById(R.id.insert_user_body_data_save_button_id);
        Button recommend_calorie_goal_button = (Button) findViewById(R.id.insert_user_body_data_recommend_calorie_button_id);

        TextView male_textviw = (TextView) findViewById(R.id.insert_user_body_data_male_textview_id);
        TextView female_textviw = (TextView) findViewById(R.id.insert_user_body_data_female_textview_id);

        EditText feet_edittext = (EditText) findViewById(R.id.insert_user_body_data_feet_edittext_id);
        EditText inch_edittext = (EditText) findViewById(R.id.insert_user_body_data_inch_edittext_id);
        EditText current_weight_edittext = (EditText) findViewById(R.id.insert_user_body_data_current_weight_edittext_id);
        EditText start_weight_edittext = (EditText) findViewById(R.id.insert_user_body_data_start_weight_edittext_id);
        EditText desired_weight_edittext = (EditText) findViewById(R.id.insert_user_body_data_desired_weight_edittext_id);
        EditText calorie_goal_edittext = (EditText) findViewById(R.id.insert_user_body_data_calorie_goal_edittext_id);
        EditText users_name_edittext = (EditText) findViewById(R.id.insert_user_body_data_name_edittext_id);

        TextView welcome_textview = (TextView) findViewById(R.id.insert_user_body_data_welcome_textview_id);
        TextView before_textview = (TextView) findViewById(R.id.insert_user_body_data_before_textview_id);

        welcome_textview.setVisibility(View.GONE);
        before_textview.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getSharedPreferences("previous_account", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("user_id",null);

        databaseReference.child("users").child(id).child("basics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                UserBasics userBasics = snapshot.getValue(UserBasics.class);

                target_weight = userBasics.getTarget_weight();
                start_weight = userBasics.getStart_weight();
                current_weight = userBasics.getCurrent_weight();
                daily_calorie_goals = userBasics.getDaily_calorie_goal();
                feet_height = userBasics.getFeet_height();
                inch_height = userBasics.getInch_height();
                birth_month = userBasics.getBirth_month();
                birth_year = userBasics.getBirth_year();
                birth_day = userBasics.getBirth_day();
                gender[0] = userBasics.getGender();


                date_button.setText(birth_day + " "+months[birth_month - 1] + ", " + birth_year);
                if(gender[0].equals("Female"))
                {
                    female_textviw.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                    male_textviw.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                }
                else if(gender[0].equals("Male"))
                {
                    male_textviw.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                    female_textviw.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                }

                feet_edittext.setText(""+feet_height);
                inch_edittext.setText(""+inch_height);
                current_weight_edittext.setText(String.format("%.1f",current_weight));
                start_weight_edittext.setText(String.format("%.1f",start_weight));
                desired_weight_edittext.setText(String.format("%.1f",target_weight));
                calorie_goal_edittext.setText(""+daily_calorie_goals);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference.child("all_users").child(id).child("general_info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AllUserGeneralData allUserGeneralData = snapshot.getValue(AllUserGeneralData.class);
                String name = allUserGeneralData.getName();
                users_name_edittext.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });









        male_textviw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male_textviw.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                female_textviw.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                gender[0] = "Male";
            }
        });

        female_textviw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                female_textviw.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                male_textviw.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                gender[0] = "Female";
            }
        });

        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(UserBodyData.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        date_button.setText(i2 + " " + months[i1] + ", " + i);
                        birth_day = i2;
                        birth_month = i1 + 1;
                        birth_year = i;
                    }
                },birth_year,birth_month - 1,birth_day);
                datePickerDialog.show();
            }
        });


        recommend_calorie_goal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String feet_string = feet_edittext.getText().toString();
                String inch_string = inch_edittext.getText().toString();
                String current_weight_string = current_weight_edittext.getText().toString();

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
                        current_weight_edittext.setError("Enter weight first");
                    }

                }

                else
                {
                    int feet_height = Integer.parseInt(feet_string);
                    int inch_height = Integer.parseInt(inch_string);
                    float current_weight = Float.parseFloat(current_weight_string);



                    AlertDialog.Builder recommend_calorie_alertbuilder = new AlertDialog.Builder(UserBodyData.this);
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
                    if(gender[0].equals("Male"))
                    {
                        recommend_calorie_male_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                        recommend_calorie_female_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                    }
                    else if(gender[0].equals("Female"))
                    {
                        recommend_calorie_female_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                        recommend_calorie_male_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                    }

                    final int[] life_style = {0};
                    recommend_calorie_sedendary_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                    recommend_calorie_maintain_weight_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));

                    recommend_calorie_date_button.setText(birth_day + " " + months[birth_month - 1] + ", " + birth_year);


                    recommend_calorie_date_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DatePickerDialog recommend_calorie_datepicker_dialog = new DatePickerDialog(UserBodyData.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                    birth_day = i2;
                                    birth_month = i1 + 1;
                                    birth_year = i;
                                    recommend_calorie_date_button.setText(birth_day + " " + months[birth_month - 1] + ", " + birth_year);
                                    date_button.setText(birth_day + " " + months[birth_month - 1] + ", " + birth_year);
                                }
                            },birth_year,birth_month - 1,birth_day);
                            recommend_calorie_datepicker_dialog.show();

                        }
                    });

                    recommend_calorie_male_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gender[0] = "Male";
                            recommend_calorie_male_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                            recommend_calorie_female_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                        }
                    });

                    recommend_calorie_female_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gender[0] = "Female";
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


                                    DatePicker datePicker = new DatePicker(UserBodyData.this);
                                    int current_month = datePicker.getMonth() + 1;
                                    int current_year = datePicker.getYear();

                                    float age = (float) ((current_year -1) - birth_year) + ((float) ((current_month -1 ) + (12 - birth_month))/12);

                                    double bmr = 1700.0;

                                    if(gender[0].equals("Male"))
                                    {
                                        bmr = 66 + (6.3 * (current_weight * 2.21)) + ( 12.9 * ((feet_height * 12) + inch_height)) - ( 6.8 * age);
                                    }
                                    else if(gender[0].equals("Female"))
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



        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feet_string = feet_edittext.getText().toString();
                String inch_string = inch_edittext.getText().toString();
                String current_weight_string = current_weight_edittext.getText().toString();
                String start_weight_string = start_weight_edittext.getText().toString();
                String desired_weight_string = desired_weight_edittext.getText().toString();
                String calorie_goal_string = calorie_goal_edittext.getText().toString();
                String users_name_string = users_name_edittext.getText().toString();

                if(feet_string.length() < 1 || inch_string.length() < 1 || current_weight_string.length() < 1 || desired_weight_string.length() < 1
                        || calorie_goal_string.length() < 1 || start_weight_string.length() < 1 || users_name_string.length() < 1)
                {
                    if(feet_string.length() < 1)
                    {
                        feet_edittext.setError("Enter feet value");
                    }

                    if(inch_string.length() < 1)
                    {
                        inch_edittext.setError("Enter inch value");
                    }

                    if(current_weight_string.length() < 1)
                    {
                        current_weight_edittext.setError("Enter weight");
                    }

                    if(start_weight_string.length() < 1)
                    {
                        start_weight_edittext.setError("Enter weight");
                    }

                    if(desired_weight_string.length() < 1)
                    {
                        desired_weight_edittext.setError("Enter desired weight");
                    }

                    if(calorie_goal_string.length() < 1)
                    {
                        calorie_goal_edittext.setError("Enter daily calorie goal");
                    }
                    if(users_name_string.length() < 1)
                    {
                        users_name_edittext.setError("Enter name");
                    }
                }

                else
                {
                    int feet_height = Integer.parseInt(feet_string);
                    int inch_height = Integer.parseInt(inch_string);

                    int calorie_goals = Integer.parseInt(calorie_goal_string);
                    float current_weight = Float.parseFloat(current_weight_string);
                    float start_weight = Float.parseFloat(start_weight_string);
                    float desired_weight = Float.parseFloat(desired_weight_string);

                    DatePicker datePicker = new DatePicker(UserBodyData.this);
                    int current_day = datePicker.getDayOfMonth();
                    int current_month = datePicker.getMonth() + 1;
                    int current_year = datePicker.getYear();


                    String s = String.format("%.1f",current_weight);
                    current_weight = (float) Double.parseDouble(s);

                    SharedPreferences sharedPreferences = getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("calorie_goal",calorie_goals);
                    editor.commit();
                    String id = sharedPreferences.getString("user_id",null);
                    String database_key = databaseReference.push().getKey();

                    WeightDetails weightDetails = new WeightDetails(current_day,current_month,current_year,current_weight);
                    weightDetails.setDatabase_key(database_key);

                    databaseReference.child("users").child(id).child("weights").child(database_key).setValue(weightDetails);

                    UserBasics userBasics = new UserBasics(gender[0],start_weight,current_weight,desired_weight,birth_day,birth_month,
                            birth_year,calorie_goals,feet_height,inch_height,current_day,current_month,current_year);

                    databaseReference.child("users").child(id).child("basics").setValue(userBasics);
                    Toast.makeText(UserBodyData.this, "Saved", Toast.LENGTH_SHORT).show();

                    AllUserGeneralData allUserGeneralData = new AllUserGeneralData(users_name_string,id,current_weight,start_weight,(current_weight - start_weight));
                    databaseReference.child("all_users").child(id).child("general_info").setValue(allUserGeneralData);

                }
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_layout,menu);


        MenuItem done_item = menu.findItem(R.id.actionbar_done_button_id);
        done_item.setVisible(false);

        MenuItem home_button = menu.findItem(R.id.actionbarHomeButtonId);
        home_button.setVisible(false);

        MenuItem about_item = menu.findItem(R.id.actionbar_about_button_id);
        about_item.setVisible(false);

        MenuItem user_details_button = menu.findItem(R.id.actionbar_user_details_button_id);
        user_details_button.setVisible(false);

        MenuItem redesigned_item = menu.findItem(R.id.redesigned_button_id);
        redesigned_item.setVisible(false);

        MenuItem sign_out = menu.findItem(R.id.sign_out_button_id);
        sign_out.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

}