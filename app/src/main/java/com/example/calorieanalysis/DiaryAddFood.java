package com.example.calorieanalysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

/*
this holds two fragments ,  history and foods
 */

public class DiaryAddFood extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_add_food_layout);

        ViewPager diary_add_food_viewpager = (ViewPager) findViewById(R.id.diary_add_food_viewpager_id);
            Diary_add_food_fragment_adapter diary_add_food_fragment_adapter = new Diary_add_food_fragment_adapter(getSupportFragmentManager());
            diary_add_food_viewpager.setAdapter(diary_add_food_fragment_adapter);

            TabLayout diary_add_food_tablayout = (TabLayout) findViewById(R.id.diary_add_food_tablayout_id);
            diary_add_food_tablayout.setupWithViewPager(diary_add_food_viewpager);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_layout,menu);

        MenuItem home_item= menu.findItem(R.id.actionbarHomeButtonId);
        home_item.setVisible(false);

        MenuItem about_item = menu.findItem(R.id.actionbar_about_button_id);
        about_item.setVisible(false);

        MenuItem sign_out = menu.findItem(R.id.sign_out_button_id);
        sign_out.setVisible(false);

        MenuItem redesigne_button = menu.findItem(R.id.redesigned_button_id);
        redesigne_button.setVisible(false);

        MenuItem user_details_button = menu.findItem(R.id.actionbar_user_details_button_id);
        user_details_button.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.actionbar_done_button_id)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}