package com.example.calorieanalysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

/*
it is not used anymore,
we have implemented this in our previous design while developing


 */
public class FoodActivity extends AppCompatActivity {

    MyFoodDatabaseHelper myFoodDatabaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        myFoodDatabaseHelper = new MyFoodDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = myFoodDatabaseHelper.getWritableDatabase();


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPagerId);
                FoodFragmentAdapter foodFragmentAdapter = new FoodFragmentAdapter(getSupportFragmentManager());
                viewPager.setAdapter(foodFragmentAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayoutId);
        tabLayout.setupWithViewPager(viewPager);

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
            Intent intent = new Intent(FoodActivity.this,MainActivity.class);
            if(intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
        }
        if(item.getItemId() == R.id.actionbar_about_button_id)
        {
            Intent intent = new Intent(FoodActivity.this,AppDetails.class);
            if(intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}