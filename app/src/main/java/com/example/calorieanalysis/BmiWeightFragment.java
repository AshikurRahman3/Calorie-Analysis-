package com.example.calorieanalysis;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/*
this shows users weight history in latest order.

at first , we have used sqlite database, but later we have implemented firebase, so no need to use sqlite , but
we have not removed sqlite code from files, so, if any sqlite code removed without related all sqlite code there, app may crash.
 */

public class BmiWeightFragment extends Fragment {

    String months[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec","0","0"};

    SharedPreferences sharedPreferences;

    Cursor cursor;
    ArrayList<WeightDetails> arrayList,weights;
    WeightDatabaseHelper weightDatabaseHelper;

    WeightDisplayAdapter weightDisplayAdapter;

    Button enter_weight_button;

    TextView start_weight_textview,current_weight_textview,lost_weight_textview,weight_loss_status_textview;

    DatabaseReference databaseReference;
    double starting_weight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bmi_weight, container, false);

        sharedPreferences = getActivity().getSharedPreferences("User_body_data_inserted", Context.MODE_PRIVATE);

        start_weight_textview = (TextView) view.findViewById(R.id.bmi_fragment_weight_start_weight_textview_id);
        current_weight_textview = (TextView) view.findViewById(R.id.bmi_fragment_weight_current_weight_textview_id);
        lost_weight_textview = (TextView) view.findViewById(R.id.bmi_fragment_weight_lost_weight_textview_id);
        weight_loss_status_textview = (TextView) view.findViewById(R.id.bmi_fragment_weight_weight_lost_status_textview_id);

        enter_weight_button = (Button) view.findViewById(R.id.bmi_weight_fragment_enter_weight_button_id);

        weightDatabaseHelper = new WeightDatabaseHelper(getActivity());
        databaseReference = FirebaseDatabase.getInstance().getReference();

        arrayList = new ArrayList<>();
        weights = new ArrayList<>();

        readdatabase();


        ListView weight_history_listview = (ListView) view.findViewById(R.id.bmi_fragment_weight_history_listview_id);

        weightDisplayAdapter = new WeightDisplayAdapter(getActivity(),weights);
        weight_history_listview.setAdapter(weightDisplayAdapter);



        weight_history_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int item_index, long l) {


                WeightDetails weightDetails = weightDisplayAdapter.getItem(item_index);

                AlertDialog.Builder weight_details_after_click_alertbuilder = new AlertDialog.Builder(getActivity());

                View weight_details_after_click_view = getLayoutInflater().inflate(R.layout.weight_details_after_click,null);
                weight_details_after_click_alertbuilder.setView(weight_details_after_click_view);


                TextView weight_details_after_click_weight_textview = (TextView) weight_details_after_click_view.findViewById(R.id.weight_details_after_click_weight_TextviewId);
                TextView weight_details_after_click_date_textview = (TextView) weight_details_after_click_view.findViewById(R.id.weight_details_after_click_date_TextviewId);

                weight_details_after_click_weight_textview.setText(String.format("%.1f",weightDetails.getWeight()));
                weight_details_after_click_date_textview.setText(weightDetails.getDay() + " " + months[weightDetails.getMonth() - 1] + ", " + weightDetails.getYear());

                weight_details_after_click_alertbuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                        String id = sharedPreferences.getString("user_id",null);
                        if(item_index == 0)
                        {
                            WeightDetails next_weight_details = weightDisplayAdapter.getItem(1);
                            databaseReference.child("users").child(id).child("basics").child("current_weight").setValue(next_weight_details.getWeight());
                            databaseReference.child("users").child(id).child("basics").child("current_weight_day").setValue(next_weight_details.getDay());
                            databaseReference.child("users").child(id).child("basics").child("current_weight_month").setValue(next_weight_details.getMonth());
                            databaseReference.child("users").child(id).child("basics").child("current_weight_year").setValue(next_weight_details.getYear());

                            current_weight_textview.setText(String.format("%.1f",next_weight_details.getWeight()));

                            double lost_weight = next_weight_details.getWeight() - starting_weight;
                            lost_weight_textview.setText(String.format("%.1f",lost_weight));

                            if(lost_weight < 0.0)
                            {
                                lost_weight_textview.setTextColor(getResources().getColor(R.color.holo_green_light));
                                weight_loss_status_textview.setText("Lost Weight");
                            }
                            else if(lost_weight > 0.0)
                            {
                                lost_weight_textview.setTextColor(getResources().getColor(R.color.red_light));
                                weight_loss_status_textview.setText("Gained Weight");
                            }
                            else
                            {
                                lost_weight_textview.setTextColor(getResources().getColor(R.color.black));
                                weight_loss_status_textview.setText("Lost Weight");
                            }


                        }

                        String database_key = weightDetails.getDatabase_key();
                        databaseReference.child("users").child(id).child("weights").child(database_key).removeValue();
                        Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });

                weight_details_after_click_alertbuilder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });


                weight_details_after_click_alertbuilder.setPositiveButton("Edit",null);


                AlertDialog weight_details_after_click_alertdialog = weight_details_after_click_alertbuilder.create();


                weight_details_after_click_alertdialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface1) {

                        Button weight_details_after_click_edit_button = weight_details_after_click_alertdialog.getButton(AlertDialog.BUTTON_POSITIVE);

                        weight_details_after_click_edit_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                double weight = weightDetails.getWeight();
                                int day = weightDetails.getDay();
                                int month = weightDetails.getMonth();
                                int year = weightDetails.getYear();



                                AlertDialog.Builder alertBuiler = new AlertDialog.Builder(getActivity());

                                View enter_weight_view = getLayoutInflater().inflate(R.layout.enter_weight,null);
                                alertBuiler.setView(enter_weight_view);

                                Button date_button = (Button) enter_weight_view.findViewById(R.id.enter_weight_date_button_id);
                                EditText enter_weight_edittext = (EditText) enter_weight_view.findViewById(R.id.enter_weight_weight_entry_edittext_id);


                                enter_weight_edittext.setText(String.format("%.1f",weightDetails.getWeight()));
                                date_button.setText(weightDetails.getDay() + " " + months[weightDetails.getMonth() - 1] + ", " + weightDetails.getYear());

                                alertBuiler.setPositiveButton("Update",null);
                                alertBuiler.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                                DatePicker datePicker = new DatePicker(getActivity());
                                int current_day = datePicker.getDayOfMonth();
                                int current_month = datePicker.getMonth();
                                int current_year = datePicker.getYear();

                                final int[] selected_day = {current_day};
                                final int[] selected_month = {current_month};
                                final int[] selected_year = {current_year};


                                date_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                                selected_day[0] = i2;
                                                selected_month[0] = i1;
                                                selected_year[0] = i;

                                                date_button.setText(selected_day[0] + " " + months[selected_month[0]] + ", " + selected_year[0]);
                                            }
                                        },current_year,current_month,current_day);
                                        datePickerDialog.show();
                                    }
                                });


                                AlertDialog alertDialog = alertBuiler.create();


                                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialogInterface2) {
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
                                                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                                                        String id = sharedPreferences.getString("user_id",null);


                                                        double weight = Double.parseDouble(weight_string);
                                                        weightDetails.setWeight(weight);
                                                        weightDetails.setDay(selected_day[0]);
                                                        weightDetails.setMonth(selected_month[0] + 1);
                                                        weightDetails.setYear(selected_year[0]);

                                                        if(item_index == 0)
                                                        {
                                                            databaseReference.child("users").child(id).child("basics").child("current_weight").setValue(weightDetails.getWeight());
                                                            databaseReference.child("users").child(id).child("basics").child("current_weight_day").setValue(weightDetails.getDay());
                                                            databaseReference.child("users").child(id).child("basics").child("current_weight_month").setValue(weightDetails.getMonth());
                                                            databaseReference.child("users").child(id).child("basics").child("current_weight_year").setValue(weightDetails.getYear());


                                                            current_weight_textview.setText(String.format("%.1f",weightDetails.getWeight()));

                                                            double lost_weight = weightDetails.getWeight() - starting_weight;
                                                            lost_weight_textview.setText(String.format("%.1f",lost_weight));

                                                            if(lost_weight < 0.0)
                                                            {
                                                                lost_weight_textview.setTextColor(getResources().getColor(R.color.holo_green_light));
                                                                weight_loss_status_textview.setText("Lost Weight");
                                                            }
                                                            else if(lost_weight > 0.0)
                                                            {
                                                                lost_weight_textview.setTextColor(getResources().getColor(R.color.red_light));
                                                                weight_loss_status_textview.setText("Gained Weight");
                                                            }
                                                            else
                                                            {
                                                                lost_weight_textview.setTextColor(getResources().getColor(R.color.black));
                                                                weight_loss_status_textview.setText("Lost Weight");
                                                            }
                                                        }

                                                        String database_key = weightDetails.getDatabase_key();
                                                        databaseReference.child("users").child(id).child("weights").child(database_key).setValue(weightDetails);

                                                        Toast.makeText(getActivity(), "Updated", Toast.LENGTH_LONG).show();
                                                        dialogInterface2.dismiss();
                                                        dialogInterface1.dismiss();
                                                    }

                                                }
                                            }
                                        });
                                    }
                                });

                                alertDialog.show();


                            }
                        });
                    }
                });
                weight_details_after_click_alertdialog.show();

            }
        });







        enter_weight_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder alertBuiler = new AlertDialog.Builder(getActivity());

                View enter_weight_view = getLayoutInflater().inflate(R.layout.enter_weight,null);
                alertBuiler.setView(enter_weight_view);

                Button date_button = (Button) enter_weight_view.findViewById(R.id.enter_weight_date_button_id);
                EditText enter_weight_edittext = (EditText) enter_weight_view.findViewById(R.id.enter_weight_weight_entry_edittext_id);

                alertBuiler.setPositiveButton("Save",null);
                alertBuiler.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                DatePicker datePicker = new DatePicker(getActivity());
                int current_day = datePicker.getDayOfMonth();
                int current_month = datePicker.getMonth();
                int current_year = datePicker.getYear();

                final int[] selected_day = {current_day};
                final int[] selected_month = {current_month};
                final int[] selected_year = {current_year};

                date_button.setText(current_day + " " + months[current_month] + ", " + current_year);

                date_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                selected_day[0] = i2;
                                selected_month[0] = i1;
                                selected_year[0] = i;

                                date_button.setText(selected_day[0] + " " + months[selected_month[0]] + ", " + selected_year[0]);
                            }
                        },current_year,current_month,current_day);
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
                                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                                        String id = sharedPreferences.getString("user_id",null);
                                        double weight = Double.parseDouble(weight_string);

                                        WeightDetails weightDetails = new WeightDetails(selected_day[0],(selected_month[0] + 1),selected_year[0],weight);
                                        String weight_record_key = databaseReference.push().getKey();
                                        weightDetails.setDatabase_key(weight_record_key);
                                        databaseReference.child("users").child(id).child("weights").child(weight_record_key).setValue(weightDetails);

                                        databaseReference.child("users").child(id).child("basics").child("current_weight").setValue(weight);
                                        databaseReference.child("users").child(id).child("basics").child("current_weight_day").setValue(selected_day[0]);
                                        databaseReference.child("users").child(id).child("basics").child("current_weight_month").setValue((selected_month[0] + 1));
                                        databaseReference.child("users").child(id).child("basics").child("current_weight_year").setValue(selected_year[0]);




                                        current_weight_textview.setText(String.format("%.1f",weight));

                                        double lost_weight = weight - starting_weight;

                                        databaseReference.child("all_users").child(id).child("general_info").child("current_weight").setValue(weight);
                                        databaseReference.child("all_users").child(id).child("general_info").child("lost_weight").setValue(lost_weight);



                                        lost_weight_textview.setText(String.format("%.1f",lost_weight));

                                        if(lost_weight < 0.0)
                                        {
                                            lost_weight_textview.setTextColor(getResources().getColor(R.color.holo_green_light));
                                            weight_loss_status_textview.setText("Lost Weight");
                                        }
                                        else if(lost_weight > 0.0)
                                        {
                                            lost_weight_textview.setTextColor(getResources().getColor(R.color.red_light));
                                            weight_loss_status_textview.setText("Gained Weight");
                                        }
                                        else
                                        {
                                            lost_weight_textview.setTextColor(getResources().getColor(R.color.black));
                                            weight_loss_status_textview.setText("Lost Weight");
                                        }

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
        });

        return view;
    }

    public void readdatabase() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("user_id",null); // user id of the user , which is needed to get data

        databaseReference.child("users").child(id).child("basics").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserBasics userBasics = snapshot.getValue(UserBasics.class);

                double current_weight = userBasics.getCurrent_weight();
                double start_weight = userBasics.getStart_weight();
                current_weight_textview.setText(String.format("%.1f",current_weight));

                starting_weight = start_weight;

                double lost_weight = current_weight - start_weight;
                start_weight_textview.setText(String.format("%.1f",start_weight));
                lost_weight_textview.setText(String.format("%.1f",lost_weight));

                if(lost_weight < 0.0)
                {
                    lost_weight_textview.setTextColor(getResources().getColor(R.color.holo_green_light));
                    weight_loss_status_textview.setText("Lost Weight");
                }
                else if(lost_weight > 0.0)
                {
                    lost_weight_textview.setTextColor(getResources().getColor(R.color.red_light));
                    weight_loss_status_textview.setText("Gained Weight");
                }
                else
                {
                    lost_weight_textview.setTextColor(getResources().getColor(R.color.black));
                    weight_loss_status_textview.setText("Lost Weight");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference.child("users").child(id).child("weights").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                arrayList.clear();
                weights.clear();
                double weight_difference = 0.0;
                double initial_weight = 0.0;
                int counter = 0;

               for(DataSnapshot snapshot1 : snapshot.getChildren())
               {
                   ++counter;
                   if(counter == 1)
                   {
                       WeightDetails weightDetails = snapshot1.getValue(WeightDetails.class);
                       weightDetails.setWeight_difference(weight_difference);
                       initial_weight = weightDetails.getWeight();
                       arrayList.add(weightDetails);
                   }
                   else
                   {
                       WeightDetails weightDetails = snapshot1.getValue(WeightDetails.class);
                       weight_difference = weightDetails.getWeight() - initial_weight;
                       weightDetails.setWeight_difference(weight_difference);
                       initial_weight = weightDetails.getWeight();
                       arrayList.add(weightDetails);
                   }
               }
                for(int i = arrayList.size() - 1;i >= 0;i--)
                {
                    weights.add(arrayList.get(i));
                }

                weightDisplayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







    }

    @Override
    public void onResume() {
        super.onResume();
        readdatabase();
    }
}