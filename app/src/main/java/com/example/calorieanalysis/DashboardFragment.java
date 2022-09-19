package com.example.calorieanalysis;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
this holds four different fragments to show data

at first , we have used sqlite database, but later we have implemented firebase, so no need to use sqlite , but
we have not removed sqlite code from files, so, if any sqlite code removed without related all sqlite code there, app may crash.

 */
public class DashboardFragment extends Fragment implements View.OnClickListener {

    String months[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    FloatingActionButton parent_fab,breakfast_fab,lunch_fab,dinner_fab,snacks_fab,weight_fab;

    TextView add_breakfast_textview,add_lunch_textview,add_dinner_textview,add_snacks_textview,add_weight_textview;

    FragmentContainerView fragmentContainerView;

    boolean is_all_fab_and_textview_visible;
    boolean is_fragment_opacity_full;

    WeightDatabaseHelper weightDatabaseHelper;

    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        boolean enabled = bottomNavigationView.getMenu().getItem(2).isEnabled();
        enabled = false;

        weightDatabaseHelper = new WeightDatabaseHelper(getActivity());
        databaseReference = FirebaseDatabase.getInstance().getReference();



        getChildFragmentManager().beginTransaction().replace(R.id.dashboard_fragment_container_view_id,new DashboardHomeFragment()).commit();

        parent_fab = (FloatingActionButton) view.findViewById(R.id.dashboard_parent_add_fab_button_id);
        breakfast_fab = (FloatingActionButton) view.findViewById(R.id.dashboard_breakfast_add_fab_button_id);
        lunch_fab = (FloatingActionButton) view.findViewById(R.id.dashboard_lunch_add_fab_button_id);
        dinner_fab = (FloatingActionButton) view.findViewById(R.id.dashboard_dinner_add_fab_button_id);
        snacks_fab = (FloatingActionButton) view.findViewById(R.id.dashboard_snacks_add_fab_button_id);
        weight_fab = (FloatingActionButton) view.findViewById(R.id.dashboard_weight_add_fab_button_id);


        add_breakfast_textview = (TextView) view.findViewById(R.id.dashboard_breakfast_add_textview_id);
        add_lunch_textview = (TextView) view.findViewById(R.id.dashboard_lunch_add_textview_id);
        add_dinner_textview = (TextView) view.findViewById(R.id.dashboard_dinner_add_textview_id);
        add_snacks_textview = (TextView) view.findViewById(R.id.dashboard_snacks_add_textview_id);
        add_weight_textview = (TextView) view.findViewById(R.id.dashboard_weight_add_textview_id);

        fragmentContainerView = (FragmentContainerView) view.findViewById(R.id.dashboard_fragment_container_view_id);





        breakfast_fab.setVisibility(View.GONE);
        lunch_fab.setVisibility(View.GONE);
        dinner_fab.setVisibility(View.GONE);
        snacks_fab.setVisibility(View.GONE);
        weight_fab.setVisibility(View.GONE);

        add_breakfast_textview.setVisibility(View.GONE);
        add_lunch_textview.setVisibility(View.GONE);
        add_dinner_textview.setVisibility(View.GONE);
        add_snacks_textview.setVisibility(View.GONE);
        add_weight_textview.setVisibility(View.GONE);


        is_all_fab_and_textview_visible = false;
        is_fragment_opacity_full = true;


        breakfast_fab.setImageResource(R.drawable.breakfast_logo);

        parent_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!is_all_fab_and_textview_visible)
                {
                    breakfast_fab.show();
                    lunch_fab.show();
                    dinner_fab.show();
                    snacks_fab.show();
                    weight_fab.show();


                    add_breakfast_textview.setVisibility(View.VISIBLE);
                    add_lunch_textview.setVisibility(View.VISIBLE);
                    add_dinner_textview.setVisibility(View.VISIBLE);
                    add_snacks_textview.setVisibility(View.VISIBLE);
                    add_weight_textview.setVisibility(View.VISIBLE);

                    fragmentContainerView.setAlpha(.1f);


                    is_all_fab_and_textview_visible = true;
                    parent_fab.setImageResource(R.drawable.close_logo);
                }

                else
                {
                    breakfast_fab.hide();
                    lunch_fab.hide();
                    dinner_fab.hide();
                    snacks_fab.hide();
                    weight_fab.hide();

                    add_breakfast_textview.setVisibility(View.GONE);
                    add_lunch_textview.setVisibility(View.GONE);
                    add_dinner_textview.setVisibility(View.GONE);
                    add_snacks_textview.setVisibility(View.GONE);
                    add_weight_textview.setVisibility(View.GONE);

                    fragmentContainerView.setAlpha(1.0f);

                    is_all_fab_and_textview_visible = false;
                    parent_fab.setImageResource(R.drawable.ic_baseline_add_24);

                }


            }
        });

        breakfast_fab.setOnClickListener(this);
        lunch_fab.setOnClickListener(this);
        dinner_fab.setOnClickListener(this);
        snacks_fab.setOnClickListener(this);
        weight_fab.setOnClickListener(this);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.Diary)
                {
                    getChildFragmentManager().beginTransaction().replace(R.id.dashboard_fragment_container_view_id,new DiaryFragment()).commit();


                    if(is_all_fab_and_textview_visible == true)
                    {
                        breakfast_fab.hide();
                        lunch_fab.hide();
                        dinner_fab.hide();
                        snacks_fab.hide();
                        weight_fab.hide();

                        add_breakfast_textview.setVisibility(View.GONE);
                        add_lunch_textview.setVisibility(View.GONE);
                        add_dinner_textview.setVisibility(View.GONE);
                        add_snacks_textview.setVisibility(View.GONE);
                        add_weight_textview.setVisibility(View.GONE);

                        fragmentContainerView.setAlpha(1.0f);

                        is_all_fab_and_textview_visible = false;
                        parent_fab.setImageResource(R.drawable.ic_baseline_add_24);
                    }

                }

                if(id == R.id.Bmi)
                {
                    getChildFragmentManager().beginTransaction().replace(R.id.dashboard_fragment_container_view_id,new BmiFragment()).commit();

                    if(is_all_fab_and_textview_visible == true)
                    {
                        breakfast_fab.hide();
                        lunch_fab.hide();
                        dinner_fab.hide();
                        snacks_fab.hide();
                        weight_fab.hide();

                        add_breakfast_textview.setVisibility(View.GONE);
                        add_lunch_textview.setVisibility(View.GONE);
                        add_dinner_textview.setVisibility(View.GONE);
                        add_snacks_textview.setVisibility(View.GONE);
                        add_weight_textview.setVisibility(View.GONE);

                        fragmentContainerView.setAlpha(1.0f);

                        is_all_fab_and_textview_visible = false;
                        parent_fab.setImageResource(R.drawable.ic_baseline_add_24);
                    }

                }

                if(id == R.id.dashboard_home)
                {
                    getChildFragmentManager().beginTransaction().replace(R.id.dashboard_fragment_container_view_id,new DashboardHomeFragment()).commit();

                    if(is_all_fab_and_textview_visible == true)
                    {
                        breakfast_fab.hide();
                        lunch_fab.hide();
                        dinner_fab.hide();
                        snacks_fab.hide();
                        weight_fab.hide();

                        add_breakfast_textview.setVisibility(View.GONE);
                        add_lunch_textview.setVisibility(View.GONE);
                        add_dinner_textview.setVisibility(View.GONE);
                        add_snacks_textview.setVisibility(View.GONE);
                        add_weight_textview.setVisibility(View.GONE);

                        fragmentContainerView.setAlpha(1.0f);

                        is_all_fab_and_textview_visible = false;
                        parent_fab.setImageResource(R.drawable.ic_baseline_add_24);
                    }
                }

                if(id == R.id.Foods)
                {
                    getChildFragmentManager().beginTransaction().replace(R.id.dashboard_fragment_container_view_id,new FoodFragment()).commit();

                    if(is_all_fab_and_textview_visible == true)
                    {
                        breakfast_fab.hide();
                        lunch_fab.hide();
                        dinner_fab.hide();
                        snacks_fab.hide();
                        weight_fab.hide();

                        add_breakfast_textview.setVisibility(View.GONE);
                        add_lunch_textview.setVisibility(View.GONE);
                        add_dinner_textview.setVisibility(View.GONE);
                        add_snacks_textview.setVisibility(View.GONE);
                        add_weight_textview.setVisibility(View.GONE);

                        fragmentContainerView.setAlpha(1.0f);

                        is_all_fab_and_textview_visible = false;
                        parent_fab.setImageResource(R.drawable.ic_baseline_add_24);
                    }
                }

                return true;
            }
        });


        return view;
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.dashboard_breakfast_add_fab_button_id || view.getId() == R.id.dashboard_lunch_add_fab_button_id
                || view.getId() == R.id.dashboard_dinner_add_fab_button_id
                || view.getId() == R.id.dashboard_snacks_add_fab_button_id || view.getId() == R.id.dashboard_weight_add_fab_button_id)
        {


            DatePicker datePicker = new DatePicker(getActivity());
            final int[] selected_day = {datePicker.getDayOfMonth()};
            final int[] selected_month = {(datePicker.getMonth() +1)};
            final int[] selected_year = {datePicker.getYear()};


            Intent intent  = new Intent(getActivity(),DiaryAddFood.class);
            intent.putExtra("day", selected_day[0]);
            intent.putExtra("month",selected_month[0]);
            intent.putExtra("year",selected_year[0]);

            if(view.getId() == R.id.dashboard_weight_add_fab_button_id)
            {

                AlertDialog.Builder alertBuiler = new AlertDialog.Builder(getActivity());

                View enter_weight_view = getLayoutInflater().inflate(R.layout.enter_weight,null);
                alertBuiler.setView(enter_weight_view);
                alertBuiler.setTitle("Add Weight");

                Button date_button = (Button) enter_weight_view.findViewById(R.id.enter_weight_date_button_id);
                EditText enter_weight_edittext = (EditText) enter_weight_view.findViewById(R.id.enter_weight_weight_entry_edittext_id);

                alertBuiler.setPositiveButton("Save",null);
                alertBuiler.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });



                date_button.setText(selected_day[0] + " " + months[selected_month[0] - 1] + ", " + selected_year[0]);

                date_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                selected_day[0] = i2;
                                selected_month[0] = i1 + 1;
                                selected_year[0] = i;

                                date_button.setText(selected_day[0] + " " + months[selected_month[0] - 1] + ", " + selected_year[0]);
                            }
                        },selected_year[0],selected_month[0]-1,selected_day[0]);
                        datePickerDialog.show();
                    }
                });


                AlertDialog alertDialog = alertBuiler.create();


                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button save_button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        save_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String weight_string = enter_weight_edittext.getText().toString();
                                if(weight_string.length() < 1)
                                {
                                    enter_weight_edittext.setError("Enter weight ");
                                }
                                else
                                {
                                    if(Double.parseDouble(weight_string) < 10 )
                                    {
                                        enter_weight_edittext.setError("Enter correct weight");
                                    }
                                    else
                                    {

                                        DatePicker datePicker = new DatePicker(getActivity());
                                        int current_day = datePicker.getDayOfMonth();
                                        int current_month = datePicker.getMonth() + 1;
                                        int current_year = datePicker.getYear();

                                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                                        String id = sharedPreferences.getString("user_id",null);

                                        String database_key = databaseReference.push().getKey();
                                        double weight = Double.parseDouble(weight_string);
                                        WeightDetails weightDetails = new WeightDetails(selected_day[0],(selected_month[0]),selected_year[0],weight);
                                        weightDetails.setDatabase_key(database_key);

                                        databaseReference.child("users").child(id).child("weights").child(database_key).setValue(weightDetails);

                                        databaseReference.child("users").child(id).child("basics").child("current_weight").setValue(weight);
                                        databaseReference.child("users").child(id).child("basics").child("current_weight_day").setValue(current_day);
                                        databaseReference.child("users").child(id).child("basics").child("current_weight_month").setValue(current_month);
                                        databaseReference.child("users").child(id).child("basics").child("current_weight_year").setValue(current_year);


                                        final double[] start_weights = new double[1];
                                        databaseReference.child("all_users").child(id).child("general_info").child("current_weight").setValue(weight);

                                        databaseReference.child("all_users").child(id).child("general_info").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                start_weights[0] = snapshot.getValue(AllUserGeneralData.class).getStart_weight();
                                                double lost_weights = weight - start_weights[0];
                                                databaseReference.child("all_users").child(id).child("general_info").child("lost_weight").setValue(lost_weights);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });





//                                        weightDatabaseHelper.insert_weight_data(weightDetails);

                                        Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                                        dialogInterface.dismiss();

                                    }

                                }
                            }
                        });
                    }
                });

                alertDialog.show();



            }
            else
            {
                if(view.getId() == R.id.dashboard_breakfast_add_fab_button_id)
                {
                    intent.putExtra("meal_id",0);
                }
                else if(view.getId() == R.id.dashboard_lunch_add_fab_button_id)
                {
                    intent.putExtra("meal_id",1);
                }
                else if(view.getId() == R.id.dashboard_dinner_add_fab_button_id)
                {
                    intent.putExtra("meal_id",2);
                }
                else
                {
                    intent.putExtra("meal_id",3);
                }

                if(intent.resolveActivity(getActivity().getPackageManager()) != null)
                {
                    startActivity(intent);
                }
            }

            breakfast_fab.hide();
            lunch_fab.hide();
            dinner_fab.hide();
            snacks_fab.hide();
            weight_fab.hide();

            add_breakfast_textview.setVisibility(View.GONE);
            add_lunch_textview.setVisibility(View.GONE);
            add_dinner_textview.setVisibility(View.GONE);
            add_snacks_textview.setVisibility(View.GONE);
            add_weight_textview.setVisibility(View.GONE);

            fragmentContainerView.setAlpha(1.0f);

            is_all_fab_and_textview_visible = false;
            parent_fab.setImageResource(R.drawable.ic_baseline_add_24);


        }



    }
}