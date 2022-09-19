package com.example.calorieanalysis;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

// not used anymore
public class TestingActivity extends AppCompatActivity {


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

    private TextView textView;
    MyDiaryDatabaseHelper myDiaryDatabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        textView = (TextView) findViewById(R.id.textviewId);

        final int[] i = {0};
        myDiaryDatabaseHelper = new MyDiaryDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = myDiaryDatabaseHelper.getWritableDatabase();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                long row_number = myDiaryDatabaseHelper.insertFood("Burger",2,1,400,"Piece"
                ,14,11,2021);
                Cursor cursor = myDiaryDatabaseHelper.read_food_diary("SELECT " + FOOD_NAME + "," + ITEMS_QUANTITY + "," + FOOD_PER_UNIT_CALORIE +
                        "," + FOOD_UNIT_NAME + "," + LUNCH_TOTAL_CALORIE + "," + TOTAL_CALORIE + " FROM food_diary_details ");

                String part,full = "";
                while (cursor.moveToNext())
                {
                    part ="number: "+ (++i[0]) +"..." + cursor.getString(0) + "..." + cursor.getString(1)  +"..." + cursor.getString(2) + "..." + cursor.getString(3) +
                            "..." + cursor.getString(4) +
                    "..." + cursor.getString(5);
                    full += "\n" + part;
                }
                textView.setText(full);
            }
        });
    }
}