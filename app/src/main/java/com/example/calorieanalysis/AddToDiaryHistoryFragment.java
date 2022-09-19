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
import androidx.appcompat.widget.SearchView;
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
import java.util.Comparator;


/*
this shows history of foods user have consumed . in latest to oldest order.
at first , we have used sqlite database, but later we have implemented firebase, so no need to use sqlite , but
we have not removed sqlite code from files, so, if any sqlite code removed without related all sqlite code there, app may crash.

 */

public class AddToDiaryHistoryFragment extends Fragment {

    String[] month_names = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    MyDiaryDatabaseHelper myDiaryDatabaseHelper;
    ArrayList<DiaryFoodDetails> diary_food_history;

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

    Diary_food_display_adapter diary_food_display_adapter;

    ListView add_to_diary_history_fragment_listview;
    SearchView add_to_diary_history_fragment_searchview;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_to_diary_history, container, false);

        myDiaryDatabaseHelper = new MyDiaryDatabaseHelper(getContext());
        SQLiteDatabase sqLiteDatabase = myDiaryDatabaseHelper.getWritableDatabase();


        databaseReference = FirebaseDatabase.getInstance().getReference();


        diary_food_history = new ArrayList<>();
        add_to_diary_history_fragment_listview = (ListView) view.findViewById(R.id.add_to_diary_fragment_history_fragment_listview_id);

        add_to_diary_history_fragment_searchview = (SearchView) view.findViewById(R.id.add_to_diary_fragment_history_fragment_searchview_id);


        ArrayList<String> meal_names = new ArrayList<>();
        meal_names.add("Breakfast");
        meal_names.add("Lunch");
        meal_names.add("Dinner");
        meal_names.add("Snacks");

        diary_food_display_adapter = new Diary_food_display_adapter(getContext(),diary_food_history);

        read_diary_database(); // reading the database


        add_to_diary_history_fragment_listview.setAdapter(diary_food_display_adapter);


        add_to_diary_history_fragment_searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ArrayList<DiaryFoodDetails> search_history_results = new ArrayList<>();
                for(DiaryFoodDetails x : diary_food_history)
                {
                    if(x.getFood_name().toLowerCase().contains(newText.toLowerCase()))
                    {
                        search_history_results.add(x);
                    }
                }


                diary_food_display_adapter.refresh_on_search(search_history_results);

                return false;
            }
        });


        add_to_diary_history_fragment_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {



                AlertDialog.Builder add_to_diary_alertBuilder = new AlertDialog.Builder(getContext());
                View add_to_diary_view = getLayoutInflater().inflate(R.layout.add_to_diary_layout,null);
                add_to_diary_alertBuilder.setView(add_to_diary_view);

                TextView food_name_textview = (TextView) add_to_diary_view.findViewById(R.id.add_to_diary_foodname_textview_id);
                TextView food_details_textview = (TextView) add_to_diary_view.findViewById(R.id.add_to_diary_food_details_textview_id);
                TextView date_textview = (TextView) add_to_diary_view.findViewById(R.id.add_to_diary_date_view_textview_id);
                Spinner meal_selecting_spinner = (Spinner) add_to_diary_view.findViewById(R.id.add_to_diary_meal_select_spinner_id);
                EditText quantity_edittext = (EditText) add_to_diary_view.findViewById(R.id.add_to_diary_quantity_edittext_id);

                ArrayAdapter<String> add_to_diary_spinner_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,meal_names);


                DatePicker datePicker = new DatePicker(getContext());
                int current_day = datePicker.getDayOfMonth();
                int current_month = datePicker.getMonth() + 1;
                int current_year = datePicker.getYear();

                Intent intent = getActivity().getIntent();
                final int[] selected_day = {intent.getIntExtra("day", current_day)};
                final int[] selected_month = {intent.getIntExtra("month", current_month)};
                final int[] selected_year = {intent.getIntExtra("year", current_year)};

                int meal = intent.getIntExtra("meal_id",0);

                DiaryFoodDetails diaryFoodDetails = diary_food_display_adapter.getItem(i);
                food_name_textview.setText(diaryFoodDetails.getFood_name());
                food_details_textview.setText(diaryFoodDetails.getFood_per_unit_calorie()+" calories per "+diaryFoodDetails.getFood_unit_name());

                meal_selecting_spinner.setAdapter(add_to_diary_spinner_adapter);

                meal_selecting_spinner.setSelection(meal);

                if(selected_day[0] == current_day && selected_month[0] == current_month && selected_year[0] == current_year)
                {
                    date_textview.setText("Date:\t\t\t\tToday");
                }
                else
                {
                    date_textview.setText("Date:\t\t\t\t" + selected_day[0] + "-" + month_names[selected_month[0] - 1] + "-" + selected_year[0]);
                }


                date_textview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DatePickerDialog datePickerDialog1 = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                                selected_day[0] = i2;
                                selected_month[0] = i1 + 1;
                                selected_year[0] = i;


                                if(selected_day[0] == current_day && selected_month[0] == current_month && selected_year[0] == current_year)
                                {
                                    date_textview.setText("Date:\t\t\t\tToday");
                                }
                                else
                                {
                                    date_textview.setText("Date:\t\t\t\t" + selected_day[0] + "-" + month_names[selected_month[0] - 1] + "-" + selected_year[0]);
                                }

                            }
                        }, selected_year[0],(selected_month[0] - 1), selected_day[0]);

                        datePickerDialog1.show();


                    }
                });


                quantity_edittext.setText(""+1);
                add_to_diary_alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                add_to_diary_alertBuilder.setPositiveButton("Add",null);
                add_to_diary_alertBuilder.setTitle("Add to Diary");
                AlertDialog add_to_diary_alertdialog = add_to_diary_alertBuilder.create();

                add_to_diary_alertdialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button diary_add_food_button = add_to_diary_alertdialog.getButton(add_to_diary_alertdialog.BUTTON_POSITIVE);
                        diary_add_food_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String quantity_text = quantity_edittext.getText().toString();
                                if(quantity_text.length() < 1)
                                {
                                    quantity_edittext.setError("enter quantity");
                                }
                                else
                                {
                                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                                    String id = sharedPreferences.getString("user_id",null);
                                    String database_key = databaseReference.push().getKey();
                                    String date = String.valueOf(selected_day[0]) + String.valueOf(selected_month[0]) + String.valueOf(selected_year[0]);

                                    double quantity_food = Double.parseDouble(quantity_text);
                                    int selected_meal_id = meal_selecting_spinner.getSelectedItemPosition();
                                    double added_total_calories = (double) quantity_food * diaryFoodDetails.getFood_per_unit_calorie();

                                    DiaryFoodDetails diaryFoodDetails1 = new DiaryFoodDetails(selected_meal_id,diaryFoodDetails.getFood_name(),diaryFoodDetails.getFood_unit_name(),
                                            database_key,diaryFoodDetails.getFood_per_unit_calorie(),quantity_food,selected_day[0],selected_month[0],selected_year[0],
                                            (int)added_total_calories);

                                    databaseReference.child("users").child(id).child("meal_diary").child(date).child(database_key).setValue(diaryFoodDetails1);
                                    read_diary_database();

                                    String toast_string = diaryFoodDetails.getFood_name() + " added to diary\nQuantity: "
                                            + quantity_food + "\nTotal calories: " + String.format("%.0f",added_total_calories);
                                    Toast.makeText(getContext(), toast_string, Toast.LENGTH_LONG).show();
                                    dialogInterface.dismiss();
                                }
                            }
                        });
                    }
                });
                add_to_diary_alertdialog.show();

            }
        });

        return view;
    }

    //this read data from databae
    public  void read_diary_database()
    {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("user_id",null); // user id of the user , which is needed to get data

        databaseReference.child("users").child(id).child("meal_diary").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                diary_food_history.clear();

                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    for(DataSnapshot snapshot2 : snapshot1.getChildren())
                    {
                        DiaryFoodDetails diaryFoodDetails = snapshot2.getValue(DiaryFoodDetails.class);
                        diary_food_history.add(diaryFoodDetails);
                    }
                }

                // sorting the list according to database key , so that latest consumed food comes first
                Collections.sort(diary_food_history, new Comparator<DiaryFoodDetails>() {
                    @Override
                    public int compare(DiaryFoodDetails diaryFoodDetails, DiaryFoodDetails t1) {
                        return t1.database_key.compareTo(diaryFoodDetails.database_key);
                    }
                });
                diary_food_display_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        read_diary_database();
    }
}