package com.example.calorieanalysis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

// opeing screen of the app
public class SplashScreen extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        if(!isTaskRoot())
        {
            finish();
            return;
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBarId);
        progressBar.setMax(40);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i =1;i<=40;i++)
                {
                    try {
                        Thread.sleep(30);
                        progressBar.setProgress(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                Intent intent = new Intent(SplashScreen.this,SignIn.class);
                startActivity(intent);
                finish();
            }
        });
        thread.start();
    }
}