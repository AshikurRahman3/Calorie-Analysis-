package com.example.calorieanalysis;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/*
to show consumed foods in driay

at first , we have used sqlite database, but later we have implemented firebase, so no need to use sqlite , but
we have not removed sqlite code from files, so, if any sqlite code removed without related all sqlite code there, app may crash.

 */

public class DiaryFragment extends Fragment implements View.OnClickListener {

    DatabaseReference databaseReference;
    ArrayList<String> meal_names;

    Diary_food_display_adapter breakfast_adapter,lunch_adapter,dinner_adapter,snacks_adapter;
    String[] month_names = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

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
    private static final String TOTAL_CALORIE = "total_Calories";
    private static final String FOOD_TOTAL_CALORIE = "food_total_Calories";

    MyDiaryDatabaseHelper myDiaryDatabaseHelper;

    private TextView total_calorie_textview, breakfast_total_caloire_textview, lunch_total_caloire_textview, dinner_total_caloire_textview, snacks_total_caloire_textview;
    private ListView breakfast_listview, lunch_listview, dinner_listview,snacks_listview;
    private Button add_breakfast_button,add_lunch_button,add_dinner_button,add_snacks_button;
    private TextView date_picker_button;


    ArrayList<DiaryFoodDetails> breakfast_diary,lunch_diary,dinner_diary,snacks_diary;


    DatePicker datePicker;
    int current_day,current_month,current_year , selected_day,selected_year,selected_month;
    Cursor breakfast_cursor,lunch_cursor,dinner_cursor,snacks_cursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();


        meal_names = new ArrayList<>();
        meal_names.add("Breakfast");
        meal_names.add("Lunch");
        meal_names.add("Dinner");
        meal_names.add("Snacks");

        datePicker = new DatePicker(getActivity());

        current_day = datePicker.getDayOfMonth();
        current_month = datePicker.getMonth() + 1;
        current_year = datePicker.getYear();
        selected_day = current_day;
        selected_month = current_month;
        selected_year = current_year;

        myDiaryDatabaseHelper = new MyDiaryDatabaseHelper(getActivity());
        SQLiteDatabase sqLiteDatabase = myDiaryDatabaseHelper.getWritableDatabase();

        total_calorie_textview = (TextView) view.findViewById(R.id.total_calorie_textview_id);
        breakfast_total_caloire_textview = (TextView) view.findViewById(R.id.breakfast_total_calorie_textview_id);
        lunch_total_caloire_textview = (TextView) view.findViewById(R.id.lunch_total_calorie_textview_id);
        dinner_total_caloire_textview = (TextView) view.findViewById(R.id.dinner_total_calorie_textview_id);
        snacks_total_caloire_textview = (TextView) view.findViewById(R.id.snacks_total_calorie_textview_id);
        date_picker_button = (Button) view.findViewById(R.id.date_picker_button_id);

        if(selected_year == current_year && selected_month == current_month && selected_day == current_day)
        {
            date_picker_button.setText("Today");
        }



        breakfast_listview = (ListView) view.findViewById(R.id.breakfast_listview_id);
        lunch_listview = (ListView) view.findViewById(R.id.lunch_listview_id);
        dinner_listview = (ListView) view.findViewById(R.id.dinner_listview_id);
        snacks_listview = (ListView) view.findViewById(R.id.snacks_listview_id);

        add_breakfast_button = (Button) view.findViewById(R.id.add_breakfast_button_id);
        add_lunch_button = (Button) view.findViewById(R.id.add_lunch_button_id);
        add_dinner_button = (Button) view.findViewById(R.id.add_dinner_button_id);
        add_snacks_button = (Button) view.findViewById(R.id.add_snacks_button_id);

        add_breakfast_button.setOnClickListener(this);
        add_lunch_button.setOnClickListener(this);
        add_dinner_button.setOnClickListener(this);
        add_snacks_button.setOnClickListener(this);

        breakfast_diary = new ArrayList<>();
        lunch_diary = new ArrayList<>();
        dinner_diary = new ArrayList<>();
        snacks_diary = new ArrayList<>();


        read_diary();

        breakfast_adapter = new Diary_food_display_adapter(getActivity(),breakfast_diary);
        breakfast_listview.setAdapter(breakfast_adapter);

        lunch_adapter = new Diary_food_display_adapter(getActivity(),lunch_diary);
        lunch_listview.setAdapter(lunch_adapter);

        dinner_adapter = new Diary_food_display_adapter(getActivity(),dinner_diary);
        dinner_listview.setAdapter(dinner_adapter);

        snacks_adapter = new Diary_food_display_adapter(getActivity(),snacks_diary);
        snacks_listview.setAdapter(snacks_adapter);



        breakfast_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                DiaryFoodDetails diaryFoodDetails = breakfast_adapter.getItem(index);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

                View diary_food_details_after_click_view = getLayoutInflater().inflate(R.layout.diary_food_details_after_click,null);

                alertBuilder.setView(diary_food_details_after_click_view);

                String food_name = diaryFoodDetails.getFood_name();
                double food_quantity = diaryFoodDetails.getFood_quantity();
                String food_unit_name = diaryFoodDetails.getFood_unit_name();
                int food_per_unit_calorie = diaryFoodDetails.getFood_per_unit_calorie();
                double total_calorie = diaryFoodDetails.getFood_totoal_calories();

                final int[] day = {diaryFoodDetails.getDay()};
                final int[] month = {diaryFoodDetails.getMonth()};
                final int[] year = {diaryFoodDetails.getYear()};

                DatePicker datePicker = new DatePicker(getActivity());
                int current_day = datePicker.getDayOfMonth();
                int current_month = datePicker.getMonth() + 1;
                int current_year = datePicker.getYear();


                TextView food_details_after_click_food_name_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_food_name_TextviewId);
                TextView food_details_after_click_total_calorie_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_total_calorie_TextviewId);
                TextView food_details_after_click_quantity_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_quantity_TextviewId);
                TextView food_details_after_click_food_calorie_value_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_calorie_value_TextviewId);
                TextView food_details_after_click_food_unit_name_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_food_unit_name_TextviewId);
                TextView food_details_after_click_meal_type_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_meal_type_TextviewId);
                TextView food_details_after_click_date_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_date_TextviewId);

                food_details_after_click_meal_type_textview.setText("Breakfast");
                food_details_after_click_food_name_textview.setText(food_name);
                food_details_after_click_total_calorie_textview.setText(String.format("%.0f",total_calorie));
                food_details_after_click_quantity_textview.setText(""+food_quantity);
                food_details_after_click_food_calorie_value_textview.setText(""+food_per_unit_calorie);
                food_details_after_click_food_unit_name_textview.setText(food_unit_name);

                if(day[0] == current_day && month[0] == current_month && year[0] == current_year)
                {
                    food_details_after_click_date_textview.setText("Today");
                }
                else
                {
                    food_details_after_click_date_textview.setText( day[0] + "-" + month_names[month[0] - 1] + "-" + year[0]);
                }


                alertBuilder.setPositiveButton("Edit",null);



                alertBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                        String id = sharedPreferences.getString("user_id",null);
                        String database_key = diaryFoodDetails.getDatabase_key();
                        String date = String.valueOf(selected_day) + String.valueOf(selected_month) + String.valueOf(selected_year);

                        databaseReference.child("users").child(id).child("meal_diary").child(date).child(database_key).removeValue();
                        read_diary();
                        Toast.makeText(getActivity(), diaryFoodDetails.getFood_name() + " has been deleted", Toast.LENGTH_SHORT).show();
                    }
                });

                alertBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alertDialog = alertBuilder.create();



                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface1) {
                        Button edit_button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        edit_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                int id = diaryFoodDetails.getId();
                                String food_name = diaryFoodDetails.getFood_name();
                                String food_unit_name = diaryFoodDetails.getFood_unit_name();
                                int selected_meal = diaryFoodDetails.getSelected_meal();

                                double quantity = diaryFoodDetails.getFood_quantity();
                                int food_per_calorie = diaryFoodDetails.getFood_per_unit_calorie();





                                AlertDialog.Builder update_food_diary_alertbuilder = new AlertDialog.Builder(getActivity());
                                View update_food_diary_view = getLayoutInflater().inflate(R.layout.add_to_diary_layout,null);
                                update_food_diary_alertbuilder.setView(update_food_diary_view);

                                TextView food_name_textview = (TextView) update_food_diary_view.findViewById(R.id.add_to_diary_foodname_textview_id);
                                TextView food_details_textview = (TextView) update_food_diary_view.findViewById(R.id.add_to_diary_food_details_textview_id);
                                TextView date_textview = (TextView) update_food_diary_view.findViewById(R.id.add_to_diary_date_view_textview_id);
                                Spinner meal_selecting_spinner = (Spinner) update_food_diary_view.findViewById(R.id.add_to_diary_meal_select_spinner_id);
                                EditText quantity_edittext = (EditText) update_food_diary_view.findViewById(R.id.add_to_diary_quantity_edittext_id);

                                ArrayAdapter<String> update_diary_meal_spinner = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,meal_names);

                                food_name_textview.setText(food_name);
                                food_details_textview.setText(food_per_calorie+" calories per "+food_unit_name);

                                meal_selecting_spinner.setAdapter(update_diary_meal_spinner);

                                meal_selecting_spinner.setSelection(selected_meal);

                                if(day[0] == current_day && month[0] == current_month && year[0] == current_year)
                                {
                                    date_textview.setText("Date:\t\t\t\tToday");
                                }
                                else
                                {
                                    date_textview.setText("Date:\t\t\t\t" + day[0] + "-" + month_names[month[0] - 1] + "-" + year[0]);
                                }

                                date_textview.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                                                day[0] = i2;
                                                month[0] = i1 + 1;
                                                year[0] = i;

                                                if(day[0] == current_day && month[0] == current_month && year[0] == current_year)
                                                {
                                                    date_textview.setText("Date:\t\t\t\tToday");
                                                }
                                                else
                                                {
                                                    date_textview.setText("Date:\t\t\t\t" + day[0] + "-" + month_names[month[0] - 1] + "-" + year[0]);
                                                }

                                            }
                                        }, year[0],(month[0] - 1), day[0]);
                                        datePickerDialog.show();


                                    }
                                });

                                quantity_edittext.setText(String.format("%.1f",quantity));


                                update_food_diary_alertbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                                update_food_diary_alertbuilder.setPositiveButton("Done",null);

                                update_food_diary_alertbuilder.setTitle("Update Diary");

                                AlertDialog update_diary_alertdialog = update_food_diary_alertbuilder.create();

                                update_diary_alertdialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialogInterface2) {

                                        Button update_food_diary_button = update_diary_alertdialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                        update_food_diary_button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String quantity_text = quantity_edittext.getText().toString();
                                                if(quantity_text.length() < 1)
                                                {
                                                    quantity_edittext.setError("enter quantity");
                                                }
                                                else
                                                {
                                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                                                    String id = sharedPreferences.getString("user_id",null);
                                                    String database_key = diaryFoodDetails.getDatabase_key();
                                                    String date = String.valueOf(selected_day) + String.valueOf(selected_month) + String.valueOf(selected_year);

                                                    double quantity_food = Double.parseDouble(quantity_text);
                                                    int selected_meal_id = meal_selecting_spinner.getSelectedItemPosition();

                                                    DiaryFoodDetails diaryFoodDetails1 = new DiaryFoodDetails(selected_meal_id,food_name,food_unit_name,database_key,food_per_calorie
                                                    ,quantity_food,day[0],month[0],year[0],(int)((double)quantity_food * food_per_calorie));
                                                    databaseReference.child("users").child(id).child("meal_diary").child(date).child(database_key).setValue(diaryFoodDetails1);
                                                    read_diary();
                                                    Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                                                    dialogInterface2.dismiss();
                                                    dialogInterface1.dismiss();
                                                }
                                            }
                                        });


                                    }
                                });

                                update_diary_alertdialog.show();

                            }
                        });
                    }
                });


                alertDialog.show();
            }
        });


        lunch_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                DiaryFoodDetails diaryFoodDetails = lunch_adapter.getItem(index);



                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

                View diary_food_details_after_click_view = getLayoutInflater().inflate(R.layout.diary_food_details_after_click,null);

                alertBuilder.setView(diary_food_details_after_click_view);

                String food_name = diaryFoodDetails.getFood_name();
                double food_quantity = diaryFoodDetails.getFood_quantity();
                String food_unit_name = diaryFoodDetails.getFood_unit_name();
                int food_per_unit_calorie = diaryFoodDetails.getFood_per_unit_calorie();
                double total_calorie = diaryFoodDetails.getFood_totoal_calories();

                final int[] day = {diaryFoodDetails.getDay()};
                final int[] month = {diaryFoodDetails.getMonth()};
                final int[] year = {diaryFoodDetails.getYear()};

                DatePicker datePicker = new DatePicker(getActivity());
                int current_day = datePicker.getDayOfMonth();
                int current_month = datePicker.getMonth() + 1;
                int current_year = datePicker.getYear();


                TextView food_details_after_click_food_name_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_food_name_TextviewId);
                TextView food_details_after_click_total_calorie_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_total_calorie_TextviewId);
                TextView food_details_after_click_quantity_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_quantity_TextviewId);
                TextView food_details_after_click_food_calorie_value_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_calorie_value_TextviewId);
                TextView food_details_after_click_food_unit_name_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_food_unit_name_TextviewId);
                TextView food_details_after_click_meal_type_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_meal_type_TextviewId);
                TextView food_details_after_click_date_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_date_TextviewId);

                food_details_after_click_meal_type_textview.setText("Lunch");
                food_details_after_click_food_name_textview.setText(food_name);
                food_details_after_click_total_calorie_textview.setText(String.format("%.0f",total_calorie));
                food_details_after_click_quantity_textview.setText(""+food_quantity);
                food_details_after_click_food_calorie_value_textview.setText(""+food_per_unit_calorie);
                food_details_after_click_food_unit_name_textview.setText(food_unit_name);

                if(day[0] == current_day && month[0] == current_month && year[0] == current_year)
                {
                    food_details_after_click_date_textview.setText("Today");
                }
                else
                {
                    food_details_after_click_date_textview.setText( day[0] + "-" + month_names[month[0] - 1] + "-" + year[0]);
                }


                alertBuilder.setPositiveButton("Edit",null);



                alertBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                        String id = sharedPreferences.getString("user_id",null);
                        String database_key = diaryFoodDetails.getDatabase_key();
                        String date = String.valueOf(selected_day) + String.valueOf(selected_month) + String.valueOf(selected_year);

                        databaseReference.child("users").child(id).child("meal_diary").child(date).child(database_key).removeValue();
                        read_diary();
                        Toast.makeText(getActivity(), diaryFoodDetails.getFood_name() + " has been deleted", Toast.LENGTH_SHORT).show();
                    }
                });

                alertBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alertDialog = alertBuilder.create();



                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface1) {
                        Button edit_button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        edit_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                int id = diaryFoodDetails.getId();
                                String food_name = diaryFoodDetails.getFood_name();
                                String food_unit_name = diaryFoodDetails.getFood_unit_name();
                                int selected_meal = diaryFoodDetails.getSelected_meal();

                                double quantity = diaryFoodDetails.getFood_quantity();
                                int food_per_calorie = diaryFoodDetails.getFood_per_unit_calorie();





                                AlertDialog.Builder update_food_diary_alertbuilder = new AlertDialog.Builder(getActivity());
                                View update_food_diary_view = getLayoutInflater().inflate(R.layout.add_to_diary_layout,null);
                                update_food_diary_alertbuilder.setView(update_food_diary_view);

                                TextView food_name_textview = (TextView) update_food_diary_view.findViewById(R.id.add_to_diary_foodname_textview_id);
                                TextView food_details_textview = (TextView) update_food_diary_view.findViewById(R.id.add_to_diary_food_details_textview_id);
                                TextView date_textview = (TextView) update_food_diary_view.findViewById(R.id.add_to_diary_date_view_textview_id);
                                Spinner meal_selecting_spinner = (Spinner) update_food_diary_view.findViewById(R.id.add_to_diary_meal_select_spinner_id);
                                EditText quantity_edittext = (EditText) update_food_diary_view.findViewById(R.id.add_to_diary_quantity_edittext_id);

                                ArrayAdapter<String> update_diary_meal_spinner = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,meal_names);

                                food_name_textview.setText(food_name);
                                food_details_textview.setText(food_per_calorie+" calories per "+food_unit_name);

                                meal_selecting_spinner.setAdapter(update_diary_meal_spinner);

                                meal_selecting_spinner.setSelection(selected_meal);

                                if(day[0] == current_day && month[0] == current_month && year[0] == current_year)
                                {
                                    date_textview.setText("Date:\t\t\t\tToday");
                                }
                                else
                                {
                                    date_textview.setText("Date:\t\t\t\t" + day[0] + "-" + month_names[month[0] - 1] + "-" + year[0]);
                                }


                                date_textview.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                                                day[0] = i2;
                                                month[0] = i1 + 1;
                                                year[0] = i;

                                                if(day[0] == current_day && month[0] == current_month && year[0] == current_year)
                                                {
                                                    date_textview.setText("Date:\t\t\t\tToday");
                                                }
                                                else
                                                {
                                                    date_textview.setText("Date:\t\t\t\t" + day[0] + "-" + month_names[month[0] - 1] + "-" + year[0]);
                                                }

                                            }
                                        }, year[0],(month[0] - 1), day[0]);
                                        datePickerDialog.show();


                                    }
                                });



                                quantity_edittext.setText(String.format("%.1f",quantity));


                                update_food_diary_alertbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                                update_food_diary_alertbuilder.setPositiveButton("Done",null);

                                update_food_diary_alertbuilder.setTitle("Update Diary");

                                AlertDialog update_diary_alertdialog = update_food_diary_alertbuilder.create();

                                update_diary_alertdialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialogInterface2) {

                                        Button update_food_diary_button = update_diary_alertdialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                        update_food_diary_button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String quantity_text = quantity_edittext.getText().toString();
                                                if(quantity_text.length() < 1)
                                                {
                                                    quantity_edittext.setError("enter quantity");
                                                }
                                                else
                                                {
                                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                                                    String id = sharedPreferences.getString("user_id",null);
                                                    String database_key = diaryFoodDetails.getDatabase_key();
                                                    String date = String.valueOf(selected_day) + String.valueOf(selected_month) + String.valueOf(selected_year);

                                                    double quantity_food = Double.parseDouble(quantity_text);
                                                    int selected_meal_id = meal_selecting_spinner.getSelectedItemPosition();

                                                    DiaryFoodDetails diaryFoodDetails1 = new DiaryFoodDetails(selected_meal_id,food_name,food_unit_name,database_key,food_per_calorie
                                                            ,quantity_food,day[0],month[0],year[0],(int)((double)quantity_food * food_per_calorie));
                                                    databaseReference.child("users").child(id).child("meal_diary").child(date).child(database_key).setValue(diaryFoodDetails1);
                                                    read_diary();
                                                    Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                                                    dialogInterface2.dismiss();
                                                    dialogInterface1.dismiss();
                                                }
                                            }
                                        });


                                    }
                                });

                                update_diary_alertdialog.show();

                            }
                        });
                    }
                });


                alertDialog.show();




            }
        });

        dinner_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                DiaryFoodDetails diaryFoodDetails = dinner_adapter.getItem(index);






                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

                View diary_food_details_after_click_view = getLayoutInflater().inflate(R.layout.diary_food_details_after_click,null);

                alertBuilder.setView(diary_food_details_after_click_view);

                String food_name = diaryFoodDetails.getFood_name();
                double food_quantity = diaryFoodDetails.getFood_quantity();
                String food_unit_name = diaryFoodDetails.getFood_unit_name();
                int food_per_unit_calorie = diaryFoodDetails.getFood_per_unit_calorie();
                double total_calorie = diaryFoodDetails.getFood_totoal_calories();

                final int[] day = {diaryFoodDetails.getDay()};
                final int[] month = {diaryFoodDetails.getMonth()};
                final int[] year = {diaryFoodDetails.getYear()};

                DatePicker datePicker = new DatePicker(getActivity());
                int current_day = datePicker.getDayOfMonth();
                int current_month = datePicker.getMonth() + 1;
                int current_year = datePicker.getYear();


                TextView food_details_after_click_food_name_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_food_name_TextviewId);
                TextView food_details_after_click_total_calorie_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_total_calorie_TextviewId);
                TextView food_details_after_click_quantity_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_quantity_TextviewId);
                TextView food_details_after_click_food_calorie_value_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_calorie_value_TextviewId);
                TextView food_details_after_click_food_unit_name_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_food_unit_name_TextviewId);
                TextView food_details_after_click_meal_type_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_meal_type_TextviewId);
                TextView food_details_after_click_date_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_date_TextviewId);

                food_details_after_click_meal_type_textview.setText("Dinner");
                food_details_after_click_food_name_textview.setText(food_name);
                food_details_after_click_total_calorie_textview.setText(String.format("%.0f",total_calorie));
                food_details_after_click_quantity_textview.setText(""+food_quantity);
                food_details_after_click_food_calorie_value_textview.setText(""+food_per_unit_calorie);
                food_details_after_click_food_unit_name_textview.setText(food_unit_name);

                if(day[0] == current_day && month[0] == current_month && year[0] == current_year)
                {
                    food_details_after_click_date_textview.setText("Today");
                }
                else
                {
                    food_details_after_click_date_textview.setText( day[0] + "-" + month_names[month[0] - 1] + "-" + year[0]);
                }


                alertBuilder.setPositiveButton("Edit",null);



                alertBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                        String id = sharedPreferences.getString("user_id",null);
                        String database_key = diaryFoodDetails.getDatabase_key();
                        String date = String.valueOf(selected_day) + String.valueOf(selected_month) + String.valueOf(selected_year);

                        databaseReference.child("users").child(id).child("meal_diary").child(date).child(database_key).removeValue();
                        read_diary();
                        Toast.makeText(getActivity(), diaryFoodDetails.getFood_name() + " has been deleted", Toast.LENGTH_SHORT).show();
                    }
                });

                alertBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alertDialog = alertBuilder.create();



                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface1) {
                        Button edit_button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        edit_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                int id = diaryFoodDetails.getId();
                                String food_name = diaryFoodDetails.getFood_name();
                                String food_unit_name = diaryFoodDetails.getFood_unit_name();
                                int selected_meal = diaryFoodDetails.getSelected_meal();

                                double quantity = diaryFoodDetails.getFood_quantity();
                                int food_per_calorie = diaryFoodDetails.getFood_per_unit_calorie();





                                AlertDialog.Builder update_food_diary_alertbuilder = new AlertDialog.Builder(getActivity());
                                View update_food_diary_view = getLayoutInflater().inflate(R.layout.add_to_diary_layout,null);
                                update_food_diary_alertbuilder.setView(update_food_diary_view);

                                TextView food_name_textview = (TextView) update_food_diary_view.findViewById(R.id.add_to_diary_foodname_textview_id);
                                TextView food_details_textview = (TextView) update_food_diary_view.findViewById(R.id.add_to_diary_food_details_textview_id);
                                TextView date_textview = (TextView) update_food_diary_view.findViewById(R.id.add_to_diary_date_view_textview_id);
                                Spinner meal_selecting_spinner = (Spinner) update_food_diary_view.findViewById(R.id.add_to_diary_meal_select_spinner_id);
                                EditText quantity_edittext = (EditText) update_food_diary_view.findViewById(R.id.add_to_diary_quantity_edittext_id);

                                ArrayAdapter<String> update_diary_meal_spinner = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,meal_names);

                                food_name_textview.setText(food_name);
                                food_details_textview.setText(food_per_calorie+" calories per "+food_unit_name);

                                meal_selecting_spinner.setAdapter(update_diary_meal_spinner);

                                meal_selecting_spinner.setSelection(selected_meal);

                                if(day[0] == current_day && month[0] == current_month && year[0] == current_year)
                                {
                                    date_textview.setText("Date:\t\t\t\tToday");
                                }
                                else
                                {
                                    date_textview.setText("Date:\t\t\t\t" + day[0] + "-" + month_names[month[0] - 1] + "-" + year[0]);
                                }


                                date_textview.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                                                day[0] = i2;
                                                month[0] = i1 + 1;
                                                year[0] = i;

                                                if(day[0] == current_day && month[0] == current_month && year[0] == current_year)
                                                {
                                                    date_textview.setText("Date:\t\t\t\tToday");
                                                }
                                                else
                                                {
                                                    date_textview.setText("Date:\t\t\t\t" + day[0] + "-" + month_names[month[0] - 1] + "-" + year[0]);
                                                }

                                            }
                                        }, year[0],(month[0] - 1), day[0]);
                                        datePickerDialog.show();


                                    }
                                });


                                quantity_edittext.setText(String.format("%.1f",quantity));


                                update_food_diary_alertbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                                update_food_diary_alertbuilder.setPositiveButton("Done",null);

                                update_food_diary_alertbuilder.setTitle("Update Diary");

                                AlertDialog update_diary_alertdialog = update_food_diary_alertbuilder.create();

                                update_diary_alertdialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialogInterface2) {

                                        Button update_food_diary_button = update_diary_alertdialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                        update_food_diary_button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String quantity_text = quantity_edittext.getText().toString();
                                                if(quantity_text.length() < 1)
                                                {
                                                    quantity_edittext.setError("enter quantity");
                                                }
                                                else
                                                {

                                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                                                    String id = sharedPreferences.getString("user_id",null);
                                                    String database_key = diaryFoodDetails.getDatabase_key();
                                                    String date = String.valueOf(selected_day) + String.valueOf(selected_month) + String.valueOf(selected_year);

                                                    double quantity_food = Double.parseDouble(quantity_text);
                                                    int selected_meal_id = meal_selecting_spinner.getSelectedItemPosition();

                                                    DiaryFoodDetails diaryFoodDetails1 = new DiaryFoodDetails(selected_meal_id,food_name,food_unit_name,database_key,food_per_calorie
                                                            ,quantity_food,day[0],month[0],year[0],(int)((double)quantity_food * food_per_calorie));
                                                    databaseReference.child("users").child(id).child("meal_diary").child(date).child(database_key).setValue(diaryFoodDetails1);
                                                    read_diary();
                                                    Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                                                    dialogInterface2.dismiss();
                                                    dialogInterface1.dismiss();
                                                }
                                            }
                                        });


                                    }
                                });

                                update_diary_alertdialog.show();

                            }
                        });
                    }
                });


                alertDialog.show();
            }
        });


        snacks_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                DiaryFoodDetails diaryFoodDetails = snacks_adapter.getItem(index);




                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

                View diary_food_details_after_click_view = getLayoutInflater().inflate(R.layout.diary_food_details_after_click,null);

                alertBuilder.setView(diary_food_details_after_click_view);

                String food_name = diaryFoodDetails.getFood_name();
                double food_quantity = diaryFoodDetails.getFood_quantity();
                String food_unit_name = diaryFoodDetails.getFood_unit_name();
                int food_per_unit_calorie = diaryFoodDetails.getFood_per_unit_calorie();
                double total_calorie = diaryFoodDetails.getFood_totoal_calories();

                final int[] day = {diaryFoodDetails.getDay()};
                final int[] month = {diaryFoodDetails.getMonth()};
                final int[] year = {diaryFoodDetails.getYear()};

                DatePicker datePicker = new DatePicker(getActivity());
                int current_day = datePicker.getDayOfMonth();
                int current_month = datePicker.getMonth() + 1;
                int current_year = datePicker.getYear();


                TextView food_details_after_click_food_name_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_food_name_TextviewId);
                TextView food_details_after_click_total_calorie_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_total_calorie_TextviewId);
                TextView food_details_after_click_quantity_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_quantity_TextviewId);
                TextView food_details_after_click_food_calorie_value_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_calorie_value_TextviewId);
                TextView food_details_after_click_food_unit_name_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_food_unit_name_TextviewId);
                TextView food_details_after_click_meal_type_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_meal_type_TextviewId);
                TextView food_details_after_click_date_textview = (TextView) diary_food_details_after_click_view.findViewById(R.id.diary_food_details_after_click_date_TextviewId);

                food_details_after_click_meal_type_textview.setText("Snacks");
                food_details_after_click_food_name_textview.setText(food_name);
                food_details_after_click_total_calorie_textview.setText(String.format("%.0f",total_calorie));
                food_details_after_click_quantity_textview.setText(""+food_quantity);
                food_details_after_click_food_calorie_value_textview.setText(""+food_per_unit_calorie);
                food_details_after_click_food_unit_name_textview.setText(food_unit_name);

                if(day[0] == current_day && month[0] == current_month && year[0] == current_year)
                {
                    food_details_after_click_date_textview.setText("Today");
                }
                else
                {
                    food_details_after_click_date_textview.setText( day[0] + "-" + month_names[month[0] - 1] + "-" + year[0]);
                }


                alertBuilder.setPositiveButton("Edit",null);



                alertBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                        String id = sharedPreferences.getString("user_id",null);
                        String database_key = diaryFoodDetails.getDatabase_key();
                        String date = String.valueOf(selected_day) + String.valueOf(selected_month) + String.valueOf(selected_year);

                        databaseReference.child("users").child(id).child("meal_diary").child(date).child(database_key).removeValue();
                        read_diary();
                        Toast.makeText(getActivity(), diaryFoodDetails.getFood_name() + " has been deleted", Toast.LENGTH_SHORT).show();
                    }
                });

                alertBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alertDialog = alertBuilder.create();



                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface1) {
                        Button edit_button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        edit_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                int id = diaryFoodDetails.getId();
                                String food_name = diaryFoodDetails.getFood_name();
                                String food_unit_name = diaryFoodDetails.getFood_unit_name();
                                int selected_meal = diaryFoodDetails.getSelected_meal();

                                double quantity = diaryFoodDetails.getFood_quantity();
                                int food_per_calorie = diaryFoodDetails.getFood_per_unit_calorie();





                                AlertDialog.Builder update_food_diary_alertbuilder = new AlertDialog.Builder(getActivity());
                                View update_food_diary_view = getLayoutInflater().inflate(R.layout.add_to_diary_layout,null);
                                update_food_diary_alertbuilder.setView(update_food_diary_view);

                                TextView food_name_textview = (TextView) update_food_diary_view.findViewById(R.id.add_to_diary_foodname_textview_id);
                                TextView food_details_textview = (TextView) update_food_diary_view.findViewById(R.id.add_to_diary_food_details_textview_id);
                                TextView date_textview = (TextView) update_food_diary_view.findViewById(R.id.add_to_diary_date_view_textview_id);
                                Spinner meal_selecting_spinner = (Spinner) update_food_diary_view.findViewById(R.id.add_to_diary_meal_select_spinner_id);
                                EditText quantity_edittext = (EditText) update_food_diary_view.findViewById(R.id.add_to_diary_quantity_edittext_id);

                                ArrayAdapter<String> update_diary_meal_spinner = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,meal_names);

                                food_name_textview.setText(food_name);
                                food_details_textview.setText(food_per_calorie+" calories per "+food_unit_name);

                                meal_selecting_spinner.setAdapter(update_diary_meal_spinner);

                                meal_selecting_spinner.setSelection(selected_meal);

                                if(day[0] == current_day && month[0] == current_month && year[0] == current_year)
                                {
                                    date_textview.setText("Date:\t\t\t\tToday");
                                }
                                else
                                {
                                    date_textview.setText("Date:\t\t\t\t" + day[0] + "-" + month_names[month[0] - 1] + "-" + year[0]);
                                }


                                date_textview.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                                                day[0] = i2;
                                                month[0] = i1 + 1;
                                                year[0] = i;

                                                if(day[0] == current_day && month[0] == current_month && year[0] == current_year)
                                                {
                                                    date_textview.setText("Date:\t\t\t\tToday");
                                                }
                                                else
                                                {
                                                    date_textview.setText("Date:\t\t\t\t" + day[0] + "-" + month_names[month[0] - 1] + "-" + year[0]);
                                                }

                                            }
                                        }, year[0],(month[0] - 1), day[0]);
                                        datePickerDialog.show();


                                    }
                                });


                                quantity_edittext.setText(String.format("%.1f",quantity));


                                update_food_diary_alertbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                                update_food_diary_alertbuilder.setPositiveButton("Done",null);

                                update_food_diary_alertbuilder.setTitle("Update Diary");

                                AlertDialog update_diary_alertdialog = update_food_diary_alertbuilder.create();

                                update_diary_alertdialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialogInterface2) {

                                        Button update_food_diary_button = update_diary_alertdialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                        update_food_diary_button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                String quantity_text = quantity_edittext.getText().toString();
                                                if(quantity_text.length() < 1)
                                                {
                                                    quantity_edittext.setError("enter quantity");
                                                }
                                                else
                                                {

                                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                                                    String id = sharedPreferences.getString("user_id",null);
                                                    String database_key = diaryFoodDetails.getDatabase_key();
                                                    String date = String.valueOf(selected_day) + String.valueOf(selected_month) + String.valueOf(selected_year);

                                                    double quantity_food = Double.parseDouble(quantity_text);
                                                    int selected_meal_id = meal_selecting_spinner.getSelectedItemPosition();

                                                    DiaryFoodDetails diaryFoodDetails1 = new DiaryFoodDetails(selected_meal_id,food_name,food_unit_name,database_key,food_per_calorie
                                                            ,quantity_food,day[0],month[0],year[0],(int)((double)quantity_food * food_per_calorie));
                                                    databaseReference.child("users").child(id).child("meal_diary").child(date).child(database_key).setValue(diaryFoodDetails1);
                                                    read_diary();
                                                    Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                                                    dialogInterface2.dismiss();
                                                    dialogInterface1.dismiss();
                                                }
                                            }
                                        });


                                    }
                                });

                                update_diary_alertdialog.show();

                            }
                        });
                    }
                });


                alertDialog.show();



            }
        });

        date_picker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        selected_year = i;
                        selected_month = i1 + 1;
                        selected_day = i2;

                        read_diary();
                        if(!(selected_year == current_year && selected_month == current_month && selected_day == current_day))
                        {
                            date_picker_button.setText(selected_day + "-" + month_names[selected_month - 1] + "-" + selected_year);

                        }
                        else
                        {
                            date_picker_button.setText("Today");
                        }
                    }
                },current_year,current_month-1,current_day);
                datePickerDialog.show();
            }
        });


        return view;
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.add_breakfast_button_id || view.getId() == R.id.add_lunch_button_id || view.getId() == R.id.add_dinner_button_id
                || view.getId() == R.id.add_snacks_button_id)
        {


            // when user clicks add breakfast or other meals button in diary fragment, it sends date and meal_id with intents
            Intent intent  = new Intent(getActivity(),DiaryAddFood.class);
            intent.putExtra("day",selected_day);
            intent.putExtra("month",selected_month);
            intent.putExtra("year",selected_year);
            if(view.getId() == R.id.add_breakfast_button_id)
            {
                intent.putExtra("meal_id",0);
            }
            else if(view.getId() == R.id.add_lunch_button_id)
            {
                intent.putExtra("meal_id",1);
            }
            else if(view.getId() == R.id.add_dinner_button_id)
            {
                intent.putExtra("meal_id",2);
            }
            else
            {
                intent.putExtra("meal_id",3);
            }

            if(intent.resolveActivity(getActivity().getPackageManager()) != null)
            {
                startActivity(intent);
            }
        }

    }

    public void read_diary()
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("user_id",null);  // user id of the user , which is needed to get data

        String date = String.valueOf(selected_day) + String.valueOf(selected_month) + String.valueOf(selected_year);

        databaseReference.child("users").child(id).child("meal_diary").child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                breakfast_diary.clear();
                lunch_diary.clear();
                dinner_diary.clear();
                snacks_diary.clear();

                int breakfast_total=0,counter = 0;
                int lunch_total = 0,dinner_total = 0,snacks_total = 0, total = 0;
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    DiaryFoodDetails diaryFoodDetails = snapshot1.getValue(DiaryFoodDetails.class);

                    if(diaryFoodDetails.getMeal_type() == 0)
                    {
                        breakfast_total += diaryFoodDetails.getFood_totoal_calories();
                        total += diaryFoodDetails.getFood_totoal_calories();
                        breakfast_diary.add(diaryFoodDetails);
                    }
                    else if(diaryFoodDetails.getMeal_type() == 1)
                    {
                        lunch_total += diaryFoodDetails.getFood_totoal_calories();
                        total += diaryFoodDetails.getFood_totoal_calories();
                        lunch_diary.add(diaryFoodDetails);
                    }
                    else if(diaryFoodDetails.getMeal_type() == 2)
                    {
                        dinner_total += diaryFoodDetails.getFood_totoal_calories();
                        total += diaryFoodDetails.getFood_totoal_calories();
                        dinner_diary.add(diaryFoodDetails);
                    }
                    else if(diaryFoodDetails.getMeal_type() == 3)
                    {
                        snacks_total += diaryFoodDetails.getFood_totoal_calories();
                        total += diaryFoodDetails.getFood_totoal_calories();
                        snacks_diary.add(diaryFoodDetails);
                    }
                }

                // reversing so that latest consumed foods come first
                Collections.reverse(breakfast_diary);
                Collections.reverse(lunch_diary);
                Collections.reverse(dinner_diary);
                Collections.reverse(snacks_diary);
                breakfast_total_caloire_textview.setText(""+breakfast_total);
                lunch_total_caloire_textview.setText(""+lunch_total);
                dinner_total_caloire_textview.setText(""+dinner_total);
                snacks_total_caloire_textview.setText(""+snacks_total);
                total_calorie_textview.setText(""+total);

                breakfast_adapter.notifyDataSetChanged();
                lunch_adapter.notifyDataSetChanged();
                dinner_adapter.notifyDataSetChanged();
                snacks_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        read_diary();
    }

    public void refresh_data()
    {
        read_diary();
    }
}