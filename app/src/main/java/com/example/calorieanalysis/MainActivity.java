package com.example.calorieanalysis;

/*
this is no more used, we have used this in our previous design,
 */
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    MyDiaryDatabaseHelper myDiaryDatabaseHelper;
    MyFoodDatabaseHelper myFoodDatabaseHelper;


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
    private RelativeLayout bmiViewGroup,foodViewGroup,diaryViewGroup;
    private Button bmiButton,foodButton,diaryButton;

    private TextView diary_details_textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDiaryDatabaseHelper = new MyDiaryDatabaseHelper(this);
        myFoodDatabaseHelper = new MyFoodDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = myFoodDatabaseHelper.getWritableDatabase();
        SQLiteDatabase sqLiteDatabase2 = myDiaryDatabaseHelper.getWritableDatabase();

        insert_default_food_to_database();


        bmiViewGroup = (RelativeLayout) findViewById(R.id.bmiViewgroupId);
        foodViewGroup = (RelativeLayout) findViewById(R.id.foodViewgroupId);
        diaryViewGroup = (RelativeLayout) findViewById(R.id.diaryViewgroupId);

        diary_details_textview = (TextView) findViewById(R.id.home_screen_diary_data_textview_id);
        bmiButton = (Button) findViewById(R.id.bmiButtonId);
        foodButton = (Button) findViewById(R.id.foodsButtonId);
        diaryButton = (Button) findViewById(R.id.diaryButtonId);

        bmiViewGroup.setOnClickListener(this);
        bmiButton.setOnClickListener(this);
        foodButton.setOnClickListener(this);
        foodViewGroup.setOnClickListener(this);
        diaryButton.setOnClickListener(this);
        diaryViewGroup.setOnClickListener(this);

        String bmi_string = "";
        SharedPreferences sharedPreferences = getSharedPreferences("Bmi_details", Context.MODE_PRIVATE);
        if(sharedPreferences.contains("bmi_value") && sharedPreferences.contains("weight_value")
                && sharedPreferences.contains("height_feet") && sharedPreferences.contains("height_inch"))
        {
            bmi_string += "BMI: " + sharedPreferences.getString("bmi_value","") + "\n";
            bmi_string += "Weight: " + sharedPreferences.getInt("weight_value",0) + " kg\n";
            bmi_string += "Height: " + sharedPreferences.getInt("height_feet",0) + " feet " + sharedPreferences.getInt("height_inch",0) + " inch";
            bmiButton.setText(bmi_string);
        }
        else
        {
            bmiButton.setText("Calculate BMI");
        }
        read_diary();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.bmiViewgroupId || view.getId() == R.id.bmiButtonId)
        {
            Intent intent = new Intent(MainActivity.this,BmiActivity.class);
            if(intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
        }
        else if(view.getId() == R.id.foodViewgroupId || view.getId() == R.id.foodsButtonId)
        {
            Intent intent = new Intent(MainActivity.this,FoodActivity.class);
            if(intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
        }

        else if(view.getId() == R.id.diaryViewgroupId || view.getId() == R.id.diaryButtonId)
        {
            Intent intent = new Intent(MainActivity.this,MyDiaryActivity.class);
            if(intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
        }


    }

    public void read_diary()
    {
        DatePicker datePicker = new DatePicker(this);
        int current_day = datePicker.getDayOfMonth();
        int current_month = datePicker.getMonth() + 1;
        int current_year = datePicker.getYear();
        Cursor cursor = myDiaryDatabaseHelper.read_food_diary("SELECT "+TOTAL_CALORIE + " FROM " + TABLE_NAME + " WHERE " + DAY
                + " == " + current_day + " AND " + MONTH
                + " == " + current_month + " AND " + YEAR
                + " == " + current_year + " ORDER BY " + ID + " DESC");

        int counter = 0,calories=0;
        while (cursor.moveToNext())
        {
            counter++;
            if(counter == 1)
            {
                calories = cursor.getInt(0);
            }
        }
        diary_details_textview.setText(calories+" calories consumed today!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_layout,menu);

        MenuItem item = menu.findItem(R.id.actionbarHomeButtonId);
        item.setVisible(false);

        MenuItem done_item = menu.findItem(R.id.actionbar_done_button_id);
        done_item.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.actionbar_about_button_id)
        {
            Intent intent = new Intent(MainActivity.this,AppDetails.class);
            if(intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
        }
        if(item.getItemId() == R.id.redesigned_button_id)
        {
            Intent intent = new Intent(MainActivity.this,HomeScreen.class);
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
            foods.add(new FoodDetails("Chicken",239,"100 grams",0));
            foods.add(new FoodDetails("Beef",259,"100 grams",0));
            foods.add(new FoodDetails("Mutton",295,"100 grams",0));
            foods.add(new FoodDetails("Milk",122,"Cup",0));
            foods.add(new FoodDetails("Fish",117,"100 grams",0));
            foods.add(new FoodDetails("Cooked Dal",222,"Cup",0));

            //snacks
            foods.add(new FoodDetails("Beef Burger",540,"Piece",1));
            foods.add(new FoodDetails("Chicken Burger",535,"Piece",1));
            foods.add(new FoodDetails("Sandwich",155,"Piece",1));
            foods.add(new FoodDetails("Pizza",285,"Slice",1));
            foods.add(new FoodDetails("Pasta",196,"Cup",1));
            foods.add(new FoodDetails("Noddles",196,"Cup",1));
            foods.add(new FoodDetails("Chocolate Cookie Medium",148,"Piece",1));


            // desserts
            foods.add(new FoodDetails("Ice Cream",273,"Cup",2));
            foods.add(new FoodDetails("Puff Pastry",280,"Piece",2));
            foods.add(new FoodDetails("Fruit Custard",225,"Cup",2));
            foods.add(new FoodDetails("Muffin Medium",424,"Piece",2));
            foods.add(new FoodDetails("Egg Pudding",344,"Cup",2));


            // drinks
            foods.add(new FoodDetails("Coffee with milk and sugar",65,"Cup",3));
            foods.add(new FoodDetails("Black Coffee",3,"Cup",3));
            foods.add(new FoodDetails("Coke",110,"Glass",3));
            foods.add(new FoodDetails("Vanilla Milkshake",280,"Glass",3));
            foods.add(new FoodDetails("Mixed Fruit Juice",135,"Glass",3));

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
}