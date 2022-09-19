package com.example.calorieanalysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*
this activity is not used anymore, at the initial stage of developing , we have used this , but later , we have implement others activity instead of using this
 */
public class BmiActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView bmiTextView,weight_textview,height_textview;
    private Button calculateBmiButton;
    String starterBmiString = "Your Bmi is ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);


        bmiTextView = (TextView) findViewById(R.id.bmi_Textview_Id);
        weight_textview = (TextView) findViewById(R.id.weight_textview_id);
        height_textview = (TextView) findViewById(R.id.Height_textview_id);
        calculateBmiButton = (Button) findViewById(R.id.calculateBmiButtonId);

        calculateBmiButton.setOnClickListener(this);

        bmiTextView.setText(starterBmiString);
        SharedPreferences sharedPreferences = getSharedPreferences("Bmi_details",Context.MODE_PRIVATE);
        if(sharedPreferences.contains("weight_value"))
        {
            weight_textview.setText("Weight: " + sharedPreferences.getInt("weight_value",0) + " kg");

        }
        else
        {
            weight_textview.setText("Weight is not entered yet");
        }
        if(sharedPreferences.contains("height_feet") && sharedPreferences.contains("height_inch"))
        {
            height_textview.setText("Height:\t" + sharedPreferences.getInt("height_feet",0) + " feet " + sharedPreferences.getInt("height_inch",0) + " inch");
        }
        else
        {
            height_textview.setText("Height is not entered yet");
        }
        if(sharedPreferences.contains("bmi_value"))
        {
            bmiTextView.setText(starterBmiString + sharedPreferences.getString("bmi_value",""));
        }
        else
        {
            bmiTextView.setText("BMI is not calculated yet!");
        }
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
            Intent intent = new Intent(BmiActivity.this,MainActivity.class);
            if(intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
        }
        if(item.getItemId() == R.id.actionbar_about_button_id)
        {
            Intent intent = new Intent(BmiActivity.this,AppDetails.class);
            if(intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.calculateBmiButtonId)
        {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            View calculateBmiView = getLayoutInflater().inflate(R.layout.calculate_bmi_layout,null);

            alertBuilder.setView(calculateBmiView);


            EditText weightEditText = (EditText) calculateBmiView.findViewById(R.id.weightEditextId);
            EditText feetEdittext = (EditText) calculateBmiView.findViewById(R.id.feetEditTextId);
            EditText inchEdittext = (EditText) calculateBmiView.findViewById(R.id.inchEditTextId);








            alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            alertBuilder.setPositiveButton("Calculate",null);
            alertBuilder.setTitle("BMI Calculator");

            AlertDialog alertDialog = alertBuilder.create();

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            String weightString = weightEditText.getText().toString();
                            String feetString = feetEdittext.getText().toString();
                            String inchString = inchEdittext.getText().toString();
                            if(weightString.length() < 1 || feetString.length() < 1 || inchString.length() < 1)
                            {
                                if (weightString.length() < 1)
                                {
                                    weightEditText.setError("Enter weight");
                                }
                                if(feetString.length() < 1)
                                {
                                    feetEdittext.setError("Enter height in feet");
                                }
                                if(inchString.length() < 1)
                                {
                                    inchEdittext.setError("Enter height in inch");
                                }
                            }

                            else
                            {
                                try {
                                    int weight = Integer.parseInt(weightString);
                                    int feet = Integer.parseInt(feetString);
                                    int inch = Integer.parseInt(inchString);

                                    int total_inch = (feet * 12) + inch;
                                    double height_in_cm = (double)total_inch * 2.54;
                                    double heiht_in_m = (double) height_in_cm / 100;
                                    double bmi = (double) weight / (double) (heiht_in_m * heiht_in_m);

                                    String bmi_string = String.format("%.1f",bmi);

                                    SharedPreferences sharedPreferences = getSharedPreferences("Bmi_details", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("bmi_value",bmi_string);
                                    editor.putInt("weight_value",weight);
                                    editor.putInt("height_feet",feet);
                                    editor.putInt("height_inch",inch);
                                    editor.commit();

                                    bmiTextView.setText(starterBmiString + bmi_string);
                                    weight_textview.setText("Weight: " + weight + " kg");
                                    height_textview.setText("Height:\t" + feet + " feet " + inch + " inch");
                                    dialogInterface.dismiss();
                                }catch (Exception e)
                                {
                                    Toast.makeText(getApplicationContext(), "Exception: "+e, Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    });
                }
            });
            alertDialog.show();


        }
    }
}