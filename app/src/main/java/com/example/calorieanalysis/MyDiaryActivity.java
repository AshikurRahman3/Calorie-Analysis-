package com.example.calorieanalysis;

/*
this is no more used, we have used this in our previous design
 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MyDiaryActivity extends AppCompatActivity implements View.OnClickListener {

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

    MyDiaryDatabaseHelper myDiaryDatabaseHelper;

    private TextView total_calorie_textview, breakfast_total_caloire_textview, lunch_total_caloire_textview, dinner_total_caloire_textview, snacks_total_caloire_textview;
    private ListView breakfast_listview, lunch_listview, dinner_listview,snacks_listview;
    private Button add_breakfast_button,add_lunch_button,add_dinner_button,add_snacks_button;
    private TextView date_picker_textview;


    ArrayList<DiaryFoodDetails> breakfast_diary,lunch_diary,dinner_diary,snacks_diary;


    DatePicker datePicker;
    int current_day,current_month,current_year , selected_day,selected_year,selected_month;
    Cursor breakfast_cursor,lunch_cursor,dinner_cursor,snacks_cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_diary);

        datePicker = new DatePicker(this);

        current_day = datePicker.getDayOfMonth();
        current_month = datePicker.getMonth() + 1;
        current_year = datePicker.getYear();
        selected_day = current_day;
        selected_month = current_month;
        selected_year = current_year;

        myDiaryDatabaseHelper = new MyDiaryDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = myDiaryDatabaseHelper.getWritableDatabase();

        total_calorie_textview = (TextView) findViewById(R.id.total_calorie_textview_id);
        breakfast_total_caloire_textview = (TextView) findViewById(R.id.breakfast_total_calorie_textview_id);
        lunch_total_caloire_textview = (TextView) findViewById(R.id.lunch_total_calorie_textview_id);
        dinner_total_caloire_textview = (TextView) findViewById(R.id.dinner_total_calorie_textview_id);
        snacks_total_caloire_textview = (TextView) findViewById(R.id.snacks_total_calorie_textview_id);
        date_picker_textview = (TextView) findViewById(R.id.date_picker_textview_id);

        if(selected_year == current_year && selected_month == current_month && selected_day == current_day)
        {
            date_picker_textview.setText("Today");
        }



        breakfast_listview = (ListView) findViewById(R.id.breakfast_listview_id);
        lunch_listview = (ListView) findViewById(R.id.lunch_listview_id);
        dinner_listview = (ListView) findViewById(R.id.dinner_listview_id);
        snacks_listview = (ListView) findViewById(R.id.snacks_listview_id);

        add_breakfast_button = (Button) findViewById(R.id.add_breakfast_button_id);
        add_lunch_button = (Button) findViewById(R.id.add_lunch_button_id);
        add_dinner_button = (Button) findViewById(R.id.add_dinner_button_id);
        add_snacks_button = (Button) findViewById(R.id.add_snacks_button_id);

        add_breakfast_button.setOnClickListener(this);
        add_lunch_button.setOnClickListener(this);
        add_dinner_button.setOnClickListener(this);
        add_snacks_button.setOnClickListener(this);

        breakfast_diary = new ArrayList<>();
        lunch_diary = new ArrayList<>();
        dinner_diary = new ArrayList<>();
        snacks_diary = new ArrayList<>();


        read_diary();

     breakfast_adapter = new Diary_food_display_adapter(this,breakfast_diary);
      breakfast_listview.setAdapter(breakfast_adapter);

        lunch_adapter = new Diary_food_display_adapter(this,lunch_diary);
        lunch_listview.setAdapter(lunch_adapter);

       dinner_adapter = new Diary_food_display_adapter(this,dinner_diary);
        dinner_listview.setAdapter(dinner_adapter);

        snacks_adapter = new Diary_food_display_adapter(this,snacks_diary);
        snacks_listview.setAdapter(snacks_adapter);



        breakfast_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                DiaryFoodDetails diaryFoodDetails = breakfast_diary.get(index);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MyDiaryActivity.this);

                alertBuilder.setTitle(diaryFoodDetails.getFood_name() + "\t" + (diaryFoodDetails.getFood_per_unit_calorie() * diaryFoodDetails.getFood_quantity()) +
                " calories\n" + diaryFoodDetails.getFood_quantity() + " servings");


                alertBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        myDiaryDatabaseHelper.delete_entried_food(String.valueOf(diaryFoodDetails.getId()));
                        breakfast_diary.remove(index);
                        double breakfast_total = Integer.parseInt(breakfast_total_caloire_textview.getText().toString()) - (diaryFoodDetails.getFood_quantity()
                        * diaryFoodDetails.getFood_per_unit_calorie());
                        breakfast_total_caloire_textview.setText(""+breakfast_total);
                        double total_calorie = Integer.parseInt(total_calorie_textview.getText().toString()) - (diaryFoodDetails.getFood_quantity()
                                * diaryFoodDetails.getFood_per_unit_calorie());
                        total_calorie_textview.setText(""+total_calorie);
                        breakfast_adapter.notifyDataSetChanged();
                    }
                });

                alertBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
            }
        });


        lunch_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                DiaryFoodDetails diaryFoodDetails = lunch_diary.get(index);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MyDiaryActivity.this);

                alertBuilder.setTitle(diaryFoodDetails.getFood_name() + "\t" + (diaryFoodDetails.getFood_per_unit_calorie() * diaryFoodDetails.getFood_quantity()) +
                        " calories\n" + diaryFoodDetails.getFood_quantity() + " servings");


                alertBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        myDiaryDatabaseHelper.delete_entried_food(String.valueOf(diaryFoodDetails.getId()));
                        lunch_diary.remove(index);
                        double lunch_total = Integer.parseInt(lunch_total_caloire_textview.getText().toString()) - (diaryFoodDetails.getFood_quantity()
                                * diaryFoodDetails.getFood_per_unit_calorie());
                        lunch_total_caloire_textview.setText(""+lunch_total);
                        double total_calorie = Integer.parseInt(total_calorie_textview.getText().toString()) - (diaryFoodDetails.getFood_quantity()
                                * diaryFoodDetails.getFood_per_unit_calorie());
                        total_calorie_textview.setText(""+total_calorie);
                        lunch_adapter.notifyDataSetChanged();
                    }
                });

                alertBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
            }
        });

        dinner_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                DiaryFoodDetails diaryFoodDetails = dinner_diary.get(index);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MyDiaryActivity.this);

                alertBuilder.setTitle(diaryFoodDetails.getFood_name() + "\t" + (diaryFoodDetails.getFood_per_unit_calorie() * diaryFoodDetails.getFood_quantity()) +
                        " calories\n" + diaryFoodDetails.getFood_quantity() + " servings");


                alertBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        myDiaryDatabaseHelper.delete_entried_food(String.valueOf(diaryFoodDetails.getId()));
                        dinner_diary.remove(index);
                        double dinner_total = Integer.parseInt(dinner_total_caloire_textview.getText().toString()) - (diaryFoodDetails.getFood_quantity()
                                * diaryFoodDetails.getFood_per_unit_calorie());
                        dinner_total_caloire_textview.setText(""+dinner_total);
                        double total_calorie = Integer.parseInt(total_calorie_textview.getText().toString()) - (diaryFoodDetails.getFood_quantity()
                                * diaryFoodDetails.getFood_per_unit_calorie());
                        total_calorie_textview.setText(""+total_calorie);
                        dinner_adapter.notifyDataSetChanged();
                    }
                });

                alertBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
            }
        });


        snacks_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                DiaryFoodDetails diaryFoodDetails = snacks_diary.get(index);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MyDiaryActivity.this);

                alertBuilder.setTitle(diaryFoodDetails.getFood_name() + "\t" + (diaryFoodDetails.getFood_per_unit_calorie() * diaryFoodDetails.getFood_quantity()) +
                        " calories\n" + diaryFoodDetails.getFood_quantity() + " servings");


                alertBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        myDiaryDatabaseHelper.delete_entried_food(String.valueOf(diaryFoodDetails.getId()));
                        snacks_diary.remove(index);
                        double snacks_total = Integer.parseInt(snacks_total_caloire_textview.getText().toString()) - (diaryFoodDetails.getFood_quantity()
                                * diaryFoodDetails.getFood_per_unit_calorie());
                        snacks_total_caloire_textview.setText(""+snacks_total);
                        double total_calorie = Integer.parseInt(total_calorie_textview.getText().toString()) - (diaryFoodDetails.getFood_quantity()
                                * diaryFoodDetails.getFood_per_unit_calorie());
                        total_calorie_textview.setText(""+total_calorie);
                        snacks_adapter.notifyDataSetChanged();
                    }
                });

                alertBuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();
            }
        });

        date_picker_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MyDiaryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        selected_year = i;
                        selected_month = i1 + 1;
                        selected_day = i2;

                        breakfast_diary.clear();
                        lunch_diary.clear();
                        dinner_diary.clear();
                        snacks_diary.clear();
                        read_diary();
                        breakfast_adapter.notifyDataSetChanged();
                        lunch_adapter.notifyDataSetChanged();
                        dinner_adapter.notifyDataSetChanged();
                        snacks_adapter.notifyDataSetChanged();
                        if(!(selected_year == current_year && selected_month == current_month && selected_day == current_day))
                        {
                            date_picker_textview.setText(selected_day + "-" + month_names[selected_month - 1] + "-" + selected_year);

                        }
                        else
                        {
                            date_picker_textview.setText("Today");
                        }


                    }
                },current_year,current_month-1,current_day);
                datePickerDialog.show();
            }
        });

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_layout,menu);

        MenuItem done_item = menu.findItem(R.id.actionbar_done_button_id);
        done_item.setVisible(false);

        MenuItem redesigned_item = menu.findItem(R.id.redesigned_button_id);
        redesigned_item.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.actionbarHomeButtonId)
        {
            Intent intent = new Intent(MyDiaryActivity.this,MainActivity.class);
            if(intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
        }
        if(item.getItemId() == R.id.actionbar_about_button_id)
        {
            Intent intent = new Intent(MyDiaryActivity.this,AppDetails.class);
            if(intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.add_breakfast_button_id || view.getId() == R.id.add_lunch_button_id || view.getId() == R.id.add_dinner_button_id
        || view.getId() == R.id.add_snacks_button_id)
        {


            Intent intent  = new Intent(MyDiaryActivity.this,DiaryAddFood.class);
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

                    if(intent.resolveActivity(getPackageManager()) != null)
                    {
                        startActivity(intent);
                    }
        }

    }

    public void read_diary()
    {
        breakfast_diary.clear();
        lunch_diary.clear();
        dinner_diary.clear();
        snacks_diary.clear();
        breakfast_cursor = myDiaryDatabaseHelper.read_food_diary("SELECT " +ID + ","+ FOOD_NAME + "," + ITEMS_QUANTITY
                + "," + FOOD_PER_UNIT_CALORIE + "," + BREAKFAST_TOTAL_CALORIE +   " FROM " + TABLE_NAME + " WHERE " + SELECTED_MEAL + " == 0 AND " + DAY
                + " == " + selected_day + " AND " + MONTH
                + " == " + selected_month + " AND " + YEAR
                + " == " + selected_year + " ORDER BY " + ID + " DESC");

        lunch_cursor = myDiaryDatabaseHelper.read_food_diary("SELECT " +ID + "," + FOOD_NAME + "," + ITEMS_QUANTITY
                + "," + FOOD_PER_UNIT_CALORIE + "," + LUNCH_TOTAL_CALORIE +   " FROM " + TABLE_NAME + " WHERE " + SELECTED_MEAL + " == 1 AND " + DAY
                + " == " + selected_day + " AND " + MONTH
                + " == " + selected_month + " AND " + YEAR
                + " == " + selected_year + " ORDER BY " + ID + " DESC");

        dinner_cursor = myDiaryDatabaseHelper.read_food_diary("SELECT " +ID + "," + FOOD_NAME + "," + ITEMS_QUANTITY
                + "," + FOOD_PER_UNIT_CALORIE + "," + DINNER_TOTAL_CALORIE +   " FROM " + TABLE_NAME + " WHERE " + SELECTED_MEAL + " == 2 AND " + DAY
                + " == " + selected_day + " AND " + MONTH
                + " == " + selected_month + " AND " + YEAR
                + " == " + selected_year + " ORDER BY " + ID + " DESC");

        snacks_cursor = myDiaryDatabaseHelper.read_food_diary("SELECT " +ID + "," + FOOD_NAME + "," + ITEMS_QUANTITY
                + "," + FOOD_PER_UNIT_CALORIE + "," + SNACKS_TOTAL_CALORIE +   " FROM " + TABLE_NAME + " WHERE " + SELECTED_MEAL + " == 3 AND " + DAY
                + " == " + selected_day + " AND " + MONTH
                + " == " + selected_month + " AND " + YEAR
                + " == " + selected_year + " ORDER BY " + ID + " DESC");

        int breakfast_total=0,counter = 0;
        int lunch_total = 0,dinner_total = 0,snacks_total = 0, total = 0;
        while (breakfast_cursor.moveToNext())
        {
            ++counter;
            if(counter == 1)
            {
                breakfast_total = breakfast_cursor.getInt(4);
            }
            DiaryFoodDetails diaryFoodDetails = new DiaryFoodDetails(breakfast_cursor.getString(1),breakfast_cursor.getInt(2),breakfast_cursor.getInt(3));
            diaryFoodDetails.setId(breakfast_cursor.getInt(0));
            breakfast_diary.add(diaryFoodDetails);
        }

        counter = 0;
        while (lunch_cursor.moveToNext())
        {
            ++counter;
            if(counter == 1)
            {
                lunch_total = lunch_cursor.getInt(4);
            }
            DiaryFoodDetails diaryFoodDetails = new DiaryFoodDetails(lunch_cursor.getString(1),lunch_cursor.getInt(2),lunch_cursor.getInt(3));
            diaryFoodDetails.setId(lunch_cursor.getInt(0));
            lunch_diary.add(diaryFoodDetails);
        }
        counter = 0;
        while (dinner_cursor.moveToNext())
        {
            ++counter;
            if(counter == 1)
            {
                dinner_total = dinner_cursor.getInt(4);
            }
            DiaryFoodDetails diaryFoodDetails = new DiaryFoodDetails(dinner_cursor.getString(1),dinner_cursor.getInt(2),dinner_cursor.getInt(3));
            diaryFoodDetails.setId(dinner_cursor.getInt(0));
            dinner_diary.add(diaryFoodDetails);
        }

        counter = 0;
        while (snacks_cursor.moveToNext())
        {
            ++counter;
            if(counter == 1)
            {
                snacks_total = snacks_cursor.getInt(4);
            }
            DiaryFoodDetails diaryFoodDetails = new DiaryFoodDetails(snacks_cursor.getString(1),snacks_cursor.getInt(2),snacks_cursor.getInt(3));
            diaryFoodDetails.setId(snacks_cursor.getInt(0));
            snacks_diary.add(diaryFoodDetails);
        }

        breakfast_total_caloire_textview.setText(""+breakfast_total);
        lunch_total_caloire_textview.setText(""+lunch_total);
        dinner_total_caloire_textview.setText(""+dinner_total);
        snacks_total_caloire_textview.setText(""+snacks_total);

        total_calorie_textview.setText(""+ (breakfast_total + lunch_total + dinner_total + snacks_total));


    }

    @Override
    protected void onResume() {
        super.onResume();
        read_diary();
        breakfast_adapter.notifyDataSetChanged();
        lunch_adapter.notifyDataSetChanged();
        dinner_adapter.notifyDataSetChanged();
        snacks_adapter.notifyDataSetChanged();
    }


}