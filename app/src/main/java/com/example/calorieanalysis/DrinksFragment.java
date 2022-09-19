package com.example.calorieanalysis;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.ImageView;
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
to show drinks foods

at first , we have used sqlite database, but later we have implemented firebase, so no need to use sqlite , but
we have not removed sqlite code from files, so, if any sqlite code removed without related all sqlite code there, app may crash.
 */

public class DrinksFragment extends Fragment {

    MyFoodDatabaseHelper myFoodDatabaseHelper;
    Cursor cursor;
    ArrayList<FoodDetails> arrayList;
    MyDiaryDatabaseHelper myDiaryDatabaseHelper;
    String[] month_names = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    SearchView drinks_fragment_searchview;
    DatabaseReference databaseReference;
    FoodDisplayAdapter foodDisplayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drinks, container, false);

        drinks_fragment_searchview = (SearchView) view.findViewById(R.id.food_fragment_drinks_fragment_searchview_id);
        databaseReference = FirebaseDatabase.getInstance().getReference();


        ArrayList<String> units = new ArrayList<>();
        units.add("Cup");
        units.add("100 grams");
        units.add("1 grams");
        units.add("Piece");
        units.add("Slice");
        units.add("Glass");

        ArrayList<String> meal_names = new ArrayList<>();
        meal_names.add("Breakfast");
        meal_names.add("Lunch");
        meal_names.add("Dinner");
        meal_names.add("Snacks");

        myFoodDatabaseHelper = new MyFoodDatabaseHelper(getActivity());
        myDiaryDatabaseHelper = new MyDiaryDatabaseHelper(getActivity());
        arrayList = new ArrayList<>();
        readdatabase();
        ListView listView = (ListView) view.findViewById(R.id.drinks_listview_id);
        foodDisplayAdapter = new FoodDisplayAdapter(getActivity(), arrayList);
        listView.setAdapter(foodDisplayAdapter);

        drinks_fragment_searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                ArrayList<FoodDetails> search_history_results = new ArrayList<>();
                for(FoodDetails x : arrayList)
                {
                    if(x.getName().toLowerCase().contains(newText.toLowerCase()))
                    {
                        search_history_results.add(x);
                    }
                }


                foodDisplayAdapter.refresh_on_search(search_history_results);

                return false;
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int listViewItemIndex, long l) {
                AlertDialog.Builder food_details_after_click_alertBuilder = new AlertDialog.Builder(getActivity());
                View food_details_after_click_view = getLayoutInflater().inflate(R.layout.food_details_after_click_layout, null);
                food_details_after_click_alertBuilder.setView(food_details_after_click_view);

                FoodDetails foods_for_after_click = foodDisplayAdapter.getItem(listViewItemIndex);

                String foodName = foods_for_after_click.getName();
                String UnitName = foods_for_after_click.getUnit();
                int calorieValue = foods_for_after_click.getCalories();

//                TextView catagoryTextview = (TextView) food_details_after_click_view.findViewById(R.id.food_details_after_click_food_catagory_TextviewId);
//                TextView foodDetailsTextView = (TextView) food_details_after_click_view.findViewById(R.id.food_details_after_click_food_details_textviewId);
                Button add_to_diary_button_after_click = (Button) food_details_after_click_view.findViewById(R.id.food_details_after_click_add_to_diary_buttonId);

                TextView food_details_after_click_food_name_textview = (TextView) food_details_after_click_view.findViewById(R.id.food_details_after_click_food_name_TextviewId);
                TextView food_details_after_click_food_catagory_name_textview = (TextView) food_details_after_click_view.findViewById(R.id.food_details_after_click_food_catagory_name_TextviewId);
                TextView food_details_after_click_food_unit_name_textview = (TextView) food_details_after_click_view.findViewById(R.id.food_details_after_click_food_unit_name_TextviewId);
                TextView food_details_after_click_food_calorie_value_textview = (TextView) food_details_after_click_view.findViewById(R.id.food_details_after_click_calorie_value_TextviewId);


                food_details_after_click_food_name_textview.setText(foodName);
                food_details_after_click_food_calorie_value_textview.setText(""+calorieValue);
                food_details_after_click_food_unit_name_textview.setText(UnitName);
                food_details_after_click_food_catagory_name_textview.setText("Drinks");

//                catagoryTextview.setText("Catagory: Drinks");
//                foodDetailsTextView.setText("Id: " + foods_for_after_click.getId() + "\nName: " + foodName + "\nCalories: " + calorieValue + "\nUnit: " + UnitName);

                food_details_after_click_alertBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                food_details_after_click_alertBuilder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        AlertDialog.Builder alertBuilder_to_edit_food = new AlertDialog.Builder(getActivity());
                        View editFoodView = getLayoutInflater().inflate(R.layout.add_food_layout, null);
                        alertBuilder_to_edit_food.setView(editFoodView);
                        EditText nameEditText_edit_food = (EditText) editFoodView.findViewById(R.id.addFoodNameEdittextId);
                        EditText calorieEdittext_edit_food = (EditText) editFoodView.findViewById(R.id.addFoodCalorieEdittextId);
                        Spinner unitSpinner_edit_food = (Spinner) editFoodView.findViewById(R.id.addFoodUnitSpinnerId);

                        ArrayAdapter<String> adapter_edit_food_spinner = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, units);
                        int spinner_index_selection = 0;
                        for(int index = 0;index<units.size();index++)
                        {
                            if(units.get(index).equals(UnitName))
                            {
                                spinner_index_selection = index;
                                break;
                            }
                        }

                        unitSpinner_edit_food.setAdapter(adapter_edit_food_spinner);
                        unitSpinner_edit_food.setSelection(spinner_index_selection);


                        nameEditText_edit_food.setText(foodName);
                        calorieEdittext_edit_food.setText("" + calorieValue);

                        alertBuilder_to_edit_food.setPositiveButton("Done", null);

                        alertBuilder_to_edit_food.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                        AlertDialog alertDialog_edit_food = alertBuilder_to_edit_food.create();

                        alertDialog_edit_food.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                Button editButton = alertDialog_edit_food.getButton(AlertDialog.BUTTON_POSITIVE);
                                editButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String foodName_after_edit = nameEditText_edit_food.getText().toString();
                                        String foodCalorie_after_edit = calorieEdittext_edit_food.getText().toString();

                                        if (foodName_after_edit.length() < 1 || foodCalorie_after_edit.length() < 1) {
                                            if (foodName_after_edit.length() < 1) {
                                                nameEditText_edit_food.setError("Enter food Name");
                                            }
                                            if (foodCalorie_after_edit.length() < 1) {
                                                calorieEdittext_edit_food.setError("Enter calorie value");
                                            }
                                        } else {
                                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                                            String id = sharedPreferences.getString("user_id",null);
                                            String database_key = foods_for_after_click.getDatabase_key();

                                            int calorie_after_edit = Integer.parseInt(foodCalorie_after_edit);
                                            String unit_after_edit = unitSpinner_edit_food.getSelectedItem().toString();
                                            FoodDetails foodDetails_after_edit = new FoodDetails(foodName_after_edit, calorie_after_edit, unit_after_edit,3);
                                            foodDetails_after_edit.setDatabase_key(database_key);
                                            databaseReference.child("users").child(id).child("foods").child("drinks").child(database_key).setValue(foodDetails_after_edit);

                                            Toast.makeText(getActivity(), "Food updated", Toast.LENGTH_SHORT).show();
                                            dialogInterface.dismiss();
                                        }
                                    }
                                });
                            }
                        });

                        alertDialog_edit_food.show();


                    }
                });

                food_details_after_click_alertBuilder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder alertBuilder_to_delete = new AlertDialog.Builder(getActivity());
                        alertBuilder_to_delete.setTitle("Are you sure to delete \"" + foodName + "\"");
                        alertBuilder_to_delete.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        alertBuilder_to_delete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                                String id = sharedPreferences.getString("user_id",null);
                                String database_key = foods_for_after_click.getDatabase_key();
                                databaseReference.child("users").child(id).child("foods").child("drinks").child(database_key).removeValue();
                                Toast.makeText(getActivity(), "You have deleted " + foodName + " from database.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        AlertDialog alertDialog_to_delete = alertBuilder_to_delete.create();
                        alertDialog_to_delete.show();
                    }
                });

                AlertDialog food_details_after_click_dialog = food_details_after_click_alertBuilder.create();


                add_to_diary_button_after_click.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alert_builder_add_to_diary = new AlertDialog.Builder(getActivity());
                        View add_to_diary_view = getLayoutInflater().inflate(R.layout.add_to_diary_layout,null);
                        alert_builder_add_to_diary.setView(add_to_diary_view);

                        Spinner add_to_diary_meal_select_spinner = (Spinner) add_to_diary_view.findViewById(R.id.add_to_diary_meal_select_spinner_id);
                        ArrayAdapter<String> add_to_diary_meal_select_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,meal_names);
                        add_to_diary_meal_select_spinner.setAdapter(add_to_diary_meal_select_adapter);

                        TextView add_to_diary_foodname_textview = (TextView) add_to_diary_view.findViewById(R.id.add_to_diary_foodname_textview_id);
                        TextView add_to_diary_fooddetails_textview = (TextView) add_to_diary_view.findViewById(R.id.add_to_diary_food_details_textview_id);
                        TextView add_to_diary_date_view_textview = (TextView) add_to_diary_view.findViewById(R.id.add_to_diary_date_view_textview_id);
                        EditText add_to_diary_quantity_edittext = (EditText) add_to_diary_view.findViewById(R.id.add_to_diary_quantity_edittext_id);
                        add_to_diary_quantity_edittext.setText(""+1);

                        add_to_diary_foodname_textview.setText(foodName);
                        add_to_diary_fooddetails_textview.setText(calorieValue + " per " + UnitName);
                        DatePicker datePicker = new DatePicker(getActivity());
                        final int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth() + 1;
                        int year = datePicker.getYear();
                        final int[] selected_day = new int[1];
                        final int[] selected_month = new int[1];
                        final int[] selected_year = new int[1];
                        selected_day[0] = day;
                        selected_month[0] = month;
                        selected_year[0] = year;

                        if(selected_day[0] == day && selected_month[0] == month && selected_year[0] == year)
                        {
                            add_to_diary_date_view_textview.setText("Date:\tToday");
                        }
                        else
                        {
                            add_to_diary_date_view_textview.setText("Date:\t" + selected_day[0] + "-" + month_names[month - 1] + "-" + selected_year[0]);
                        }

                        final DatePickerDialog[] datePickerDialog = new DatePickerDialog[1];
                        add_to_diary_date_view_textview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                datePickerDialog[0] = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                        selected_day[0] = i2;
                                        selected_month[0] = i1 + 1;
                                        selected_year[0] = i;

                                        if(selected_day[0] == day && selected_month[0] == month && selected_year[0] == year)
                                        {
                                            add_to_diary_date_view_textview.setText("Date:\tToday");
                                        }
                                        else
                                        {
                                            add_to_diary_date_view_textview.setText("Date:\t" + selected_day[0] + "-" + month_names[month - 1] + "-" + selected_year[0]);
                                        }
                                    }
                                },year,month-1, day);
                                datePickerDialog[0].show();

                            }
                        });

                        alert_builder_add_to_diary.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                food_details_after_click_dialog.cancel();
                            }
                        });

                        alert_builder_add_to_diary.setPositiveButton("Add",null);
                        AlertDialog add_to_diary_dialog = alert_builder_add_to_diary.create();

                        add_to_diary_dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                Button add_button = add_to_diary_dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                add_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                        String date_text = add_to_diary_date_view_textview.getText().toString();
                                        String quantity_text_string = add_to_diary_quantity_edittext.getText().toString();
                                        if(date_text.length() < 1 || quantity_text_string.length() < 1)
                                        {
                                            if(date_text.length() < 1)
                                            {
                                                add_to_diary_date_view_textview.setError("Choose Date");
                                            }
                                            if(quantity_text_string.length() < 1)
                                            {
                                                add_to_diary_quantity_edittext.setError("Enter quantity");
                                            }
                                        }else

                                        {

                                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                                            String id = sharedPreferences.getString("user_id",null);
                                            String database_key = databaseReference.push().getKey();

                                            int selected_meal = add_to_diary_meal_select_spinner.getSelectedItemPosition();
                                            double added_calories = calorieValue * (Double.parseDouble(quantity_text_string));
                                            double food_quantity = Double.parseDouble(quantity_text_string);
                                            String date = String.valueOf(selected_day[0]) + String.valueOf(selected_month[0]) + String.valueOf(selected_year[0]);

                                            DiaryFoodDetails diaryFoodDetails = new DiaryFoodDetails(selected_meal,foodName,UnitName,database_key,foods_for_after_click.getCalories(),
                                                    food_quantity,selected_day[0],selected_month[0],selected_year[0]
                                                    ,(int)added_calories);

                                            databaseReference.child("users").child(id).child("meal_diary").child(date).child(database_key).setValue(diaryFoodDetails);



                                            String toast_string = foods_for_after_click.getName() + " added to diary\nQuantity: "
                                                    + Double.parseDouble(quantity_text_string) + "\nTotal calories: " + String.format("%.0f",added_calories);
                                            Toast.makeText(getActivity(), toast_string, Toast.LENGTH_LONG).show();
                                            dialogInterface.cancel();
                                            food_details_after_click_dialog.cancel();
                                        }


                                    }
                                });
                            }
                        });
                        add_to_diary_dialog.show();
                    }
                });


                food_details_after_click_dialog.show();
            }
        });


        ImageView add_drinks_button = (ImageView) view.findViewById(R.id.add_drinks_button_id);
        add_drinks_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                View dialogView = getLayoutInflater().inflate(R.layout.add_food_layout, null);
                alertBuilder.setView(dialogView);

                alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                alertBuilder.setPositiveButton("Add", null);

                Spinner unitSpinner = (Spinner) dialogView.findViewById(R.id.addFoodUnitSpinnerId);
                ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, units);
                unitSpinner.setAdapter(unitAdapter);

                AlertDialog alertDialog = alertBuilder.create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EditText nameEdittext = (EditText) dialogView.findViewById(R.id.addFoodNameEdittextId);
                                EditText calorieEdittext = (EditText) dialogView.findViewById(R.id.addFoodCalorieEdittextId);

                                String name = nameEdittext.getText().toString();
                                String calorie_string = calorieEdittext.getText().toString();
                                if (name.length() < 1 || calorie_string.length() < 1) {
                                    if (name.length() < 1) {
                                        nameEdittext.setError("enter food name");
                                    }
                                    if (calorie_string.length() < 1) {
                                        calorieEdittext.setError("enter calorie value");
                                    }
                                } else {
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                                    String id = sharedPreferences.getString("user_id",null);
                                    String database_key = databaseReference.push().getKey();

                                    int calories = Integer.parseInt(calorie_string);
                                    String unitSelected = unitSpinner.getSelectedItem().toString();
                                    FoodDetails foodDetails = new FoodDetails(name, calories, unitSelected,0);
                                    foodDetails.setDatabase_key(database_key);
                                    databaseReference.child("users").child(id).child("foods").child("drinks").child(database_key).setValue(foodDetails);

                                    Toast.makeText(getActivity(), foodDetails.getName() + " has been added", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                }
                            }
                        });
                    }
                });
                alertDialog.show();
            }
        });



        return  view;
    }

    public void readdatabase() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("user_id",null); // user id of the user , which is needed to get data

        databaseReference.child("users").child(id).child("foods").child("drinks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    FoodDetails foodDetails = snapshot1.getValue(FoodDetails.class);
                    arrayList.add(foodDetails);
                }
                Collections.reverse(arrayList);
                foodDisplayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}