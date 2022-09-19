package com.example.calorieanalysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


/*
About app
 */
public class AppDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_layout,menu);

        MenuItem item = menu.findItem(R.id.actionbar_about_button_id);
        item.setVisible(false);

        MenuItem sign_out = menu.findItem(R.id.sign_out_button_id);
        sign_out.setVisible(false);

        MenuItem done_item = menu.findItem(R.id.actionbar_done_button_id);
        done_item.setVisible(false);

        MenuItem redesigned_item = menu.findItem(R.id.redesigned_button_id);
        redesigned_item.setVisible(false);


        MenuItem home_button = menu.findItem(R.id.actionbarHomeButtonId);
        home_button.setVisible(false);


        MenuItem about_item = menu.findItem(R.id.actionbar_user_details_button_id);
        about_item.setVisible(false);
        
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