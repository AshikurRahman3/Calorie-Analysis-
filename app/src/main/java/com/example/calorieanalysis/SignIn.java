package com.example.calorieanalysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/*
to sign in existing users or new users with google sign in

or user can use this app as guest,
but guest users can not resign in
 */
public class SignIn extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;

    String months[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    String gender = "Male";

    Button sign_in_google_button,use_as_guest;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    int sign_in_method = 0;
    ProgressBar progressBar;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        sign_in_google_button = findViewById(R.id.sign_in_google);
        use_as_guest = findViewById(R.id.sign_in_guest);


        databaseReference = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        createRequest();

        sign_in_google_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
                sign_in_method = 0;
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        progressBar = findViewById(R.id.sign_in_progressbar_id);
        progressBar.setVisibility(View.INVISIBLE);


        use_as_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sign_in_method = 1;

                load_for_new_user();
            }
        });
    }

    private void createRequest() {

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            
                            FirebaseUser user = mAuth.getCurrentUser();


                            final int[] calorie_goals = new int[1];

                            progressBar.setVisibility(View.INVISIBLE);
                            SharedPreferences sharedPreferences = getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("has_account","yes");
                            editor.putString("account_type","google");
                            String user_id = "1";

                                GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(SignIn.this);

                                if(googleSignInAccount != null)
                                {
                                    String email = googleSignInAccount.getId();
                                    user_id = email;
                                }

                                final String user_ids[] = {user_id};


                            editor.putString("user_id",user_id);
                            editor.commit();

                            databaseReference.child("users").child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists() == true)
                                    {
                                        SharedPreferences sharedPreferences = getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        databaseReference.child("users").child(user_ids[0]).child("basics").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                UserBasics userBasics = snapshot.getValue(UserBasics.class);
                                                calorie_goals[0] = userBasics.getDaily_calorie_goal();
                                                editor.putInt("calorie_goal",calorie_goals[0]);
                                                editor.commit();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        startActivity(new Intent(SignIn.this,HomeScreen.class));
                                    }
                                    else
                                    {
                                        load_for_new_user();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                            Toast.makeText(SignIn.this, "sign in successful", Toast.LENGTH_LONG).show();


                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.INVISIBLE);

                            Toast.makeText(SignIn.this, "Authentication failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();



        FirebaseUser user = mAuth.getCurrentUser();

        SharedPreferences sharedPreferences = getSharedPreferences("previous_account", Context.MODE_PRIVATE);
        String logged_in_with_guest = sharedPreferences.getString("has_account","nokia");
        if(user != null || logged_in_with_guest.equals("yes"))
        {
            startActivity(new Intent(SignIn.this,HomeScreen.class));

        }
    }


    public  void load_for_new_user()
    {
        String user_id = "1";
        if(sign_in_method == 0)
        {
            GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(SignIn.this);

            if(googleSignInAccount != null)
            {
                String email = googleSignInAccount.getId();
                user_id = email;
            }
        }
        else
        {
            String key = databaseReference.push().getKey();
            user_id = key;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("previous_account", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id",user_id);
        editor.commit();
        
        // insert basic body user data for new user
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SignIn.this);
        alertBuilder.setCancelable(false);
        View enter_user_body_data_view = getLayoutInflater().inflate(R.layout.initial_user_body_details_entry,null);
        alertBuilder.setView(enter_user_body_data_view);

        alertBuilder.setPositiveButton("Continue",null);
        AlertDialog alertDialog = alertBuilder.create();


        Button date_button = (Button) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_date_button_id);
        Button save_button = (Button) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_save_button_id);
        Button recommend_calorie_goal_button = (Button) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_recommend_calorie_button_id);

        TextView male_textviw = (TextView) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_male_textview_id);
        TextView female_textviw = (TextView) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_female_textview_id);

        EditText feet_edittext = (EditText) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_feet_edittext_id);
        EditText inch_edittext = (EditText) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_inch_edittext_id);
        EditText weight_edittext = (EditText) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_current_weight_edittext_id);
        EditText desired_weight_edittext = (EditText) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_desired_weight_edittext_id);
        EditText calorie_goal_edittext = (EditText) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_calorie_goal_edittext_id);
        EditText users_name_edittext = (EditText) enter_user_body_data_view.findViewById(R.id.insert_user_body_data_name_edittext_id);

        LinearLayout start_weight_linearLayout = (LinearLayout) enter_user_body_data_view.findViewById(R.id.start_weight_rootview_id) ;
        start_weight_linearLayout.setVisibility(View.GONE);

        male_textviw.setBackgroundColor(getResources().getColor(R.color.holo_green_light));

        calorie_goal_edittext.setText(""+2000);

        save_button.setVisibility(View.GONE);

        male_textviw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male_textviw.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                female_textviw.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                gender = "Male";
            }
        });

        female_textviw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                female_textviw.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                male_textviw.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                gender = "Female";
            }
        });


        int day = 1;
        int month = 0;
        int year = 1998;

        final int[] selected_day = {day};
        final int[] selected_month = {month};
        final int[] selected_year = {year};

        date_button.setText(day + " " + months[month] + ", " + year);

        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(SignIn.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        date_button.setText(i2 + " " + months[i1] + ", " + i);
                        selected_day[0] = i2;
                        selected_month[0] = i1;
                        selected_year[0] = i;
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });


        recommend_calorie_goal_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String feet_string = feet_edittext.getText().toString();
                String inch_string = inch_edittext.getText().toString();
                String current_weight_string = weight_edittext.getText().toString();

                if(feet_string.length() < 1 || inch_string.length() < 1 || current_weight_string.length() < 1)
                {
                    if(feet_string.length() < 1)
                    {
                        feet_edittext.setError("Enter height first");
                    }

                    if(inch_string.length() < 1)
                    {
                        inch_edittext.setError("Enter height first");
                    }

                    if(current_weight_string.length() < 1)
                    {
                        weight_edittext.setError("Enter weight first");
                    }

                }

                else
                {
                    int feet_height = Integer.parseInt(feet_string);
                    int inch_height = Integer.parseInt(inch_string);
                    float current_weight = Float.parseFloat(current_weight_string);



                    AlertDialog.Builder recommend_calorie_alertbuilder = new AlertDialog.Builder(SignIn.this);
                    View recommend_calorie_view = getLayoutInflater().inflate(R.layout.recommend_daily_calorie,null);

                    recommend_calorie_alertbuilder.setView(recommend_calorie_view);

                    Button recommend_calorie_date_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_date_button_id);
                    Button recommend_calorie_male_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_male_button_id);
                    Button recommend_calorie_female_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_female_button_id);
                    Button recommend_calorie_sedendary_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_sedendary_button_id);
                    Button recommend_calorie_light_active_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_light_active_button_id);
                    Button recommend_calorie_moderate_active_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_moderate_active_button_id);
                    Button recommend_calorie_very_active_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_very_active_button_id);
                    Button recommend_calorie_maintain_weight_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_maintain_weight_button_id);
                    Button recommend_calorie_loss_weight_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_loss_weight_button_id);
                    Button recommend_calorie_gain_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_gain_weight_button_id);
                    Button recommend_calorie_half_kg_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_half_kg_button_id);
                    Button recommend_calorie_one_kg_button = (Button) recommend_calorie_view.findViewById(R.id.recommend_calorie_one_kg_button_id);


                    recommend_calorie_half_kg_button.setEnabled(false);
                    recommend_calorie_one_kg_button.setEnabled(false);

                    final int[] maintain_loss_gain = {0};
                    if(gender.equals("Male"))
                    {
                        recommend_calorie_male_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                        recommend_calorie_female_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                    }
                    else if(gender.equals("Female"))
                    {
                        recommend_calorie_female_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                        recommend_calorie_male_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                    }

                    final int[] life_style = {0};
                    recommend_calorie_sedendary_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                    recommend_calorie_maintain_weight_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));

                    recommend_calorie_date_button.setText(selected_day[0] + " " + months[selected_month[0]] + ", " + selected_year[0]);


                    recommend_calorie_date_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DatePickerDialog recommend_calorie_datepicker_dialog = new DatePickerDialog(SignIn.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                    selected_day[0] = i2;
                                    selected_month[0] = i1;
                                    selected_year[0] = i;
                                    recommend_calorie_date_button.setText(selected_day[0] + " " + months[selected_month[0]] + ", " + selected_year[0]);
                                    date_button.setText(selected_day[0] + " " + months[selected_month[0]] + ", " + selected_year[0]);
                                }
                            },selected_year[0],selected_month[0],selected_day[0]);
                            recommend_calorie_datepicker_dialog.show();

                        }
                    });

                    recommend_calorie_male_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gender = "Male";
                            recommend_calorie_male_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                            recommend_calorie_female_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                        }
                    });

                    recommend_calorie_female_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            gender = "Female";
                            recommend_calorie_female_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                            recommend_calorie_male_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                        }
                    });


                    recommend_calorie_sedendary_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            life_style[0] = 0;
                            recommend_calorie_sedendary_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                            recommend_calorie_light_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            recommend_calorie_moderate_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            recommend_calorie_very_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                        }
                    });

                    recommend_calorie_light_active_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            life_style[0] = 1;
                            recommend_calorie_sedendary_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            recommend_calorie_light_active_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                            recommend_calorie_moderate_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            recommend_calorie_very_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                        }
                    });

                    recommend_calorie_moderate_active_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            life_style[0] = 2;
                            recommend_calorie_sedendary_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            recommend_calorie_light_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            recommend_calorie_moderate_active_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                            recommend_calorie_very_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                        }
                    });
                    recommend_calorie_very_active_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            life_style[0] = 3;
                            recommend_calorie_sedendary_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            recommend_calorie_light_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            recommend_calorie_moderate_active_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            recommend_calorie_very_active_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                        }
                    });


                    recommend_calorie_maintain_weight_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            maintain_loss_gain[0] = 0;
                            recommend_calorie_maintain_weight_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                            recommend_calorie_loss_weight_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            recommend_calorie_gain_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));


                            recommend_calorie_half_kg_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            recommend_calorie_one_kg_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));

                            recommend_calorie_half_kg_button.setEnabled(false);
                            recommend_calorie_one_kg_button.setEnabled(false);
                        }
                    });
                    final int[] weight_loss_gain_per_week = {0};

                    recommend_calorie_loss_weight_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            maintain_loss_gain[0] = 1;
                            recommend_calorie_maintain_weight_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            recommend_calorie_loss_weight_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                            recommend_calorie_gain_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            weight_loss_gain_per_week[0] = 0;
                            recommend_calorie_half_kg_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                            recommend_calorie_one_kg_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));

                            recommend_calorie_half_kg_button.setEnabled(true);
                            recommend_calorie_one_kg_button.setEnabled(true);
                        }
                    });

                    recommend_calorie_gain_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            maintain_loss_gain[0] = 2;
                            recommend_calorie_maintain_weight_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            recommend_calorie_loss_weight_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                            recommend_calorie_gain_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                            weight_loss_gain_per_week[0] = 0;
                            recommend_calorie_half_kg_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                            recommend_calorie_one_kg_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));

                            recommend_calorie_half_kg_button.setEnabled(true);
                            recommend_calorie_one_kg_button.setEnabled(true);
                        }
                    });


                    recommend_calorie_half_kg_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            weight_loss_gain_per_week[0] = 0;
                            recommend_calorie_half_kg_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                            recommend_calorie_one_kg_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                        }
                    });

                    recommend_calorie_one_kg_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            weight_loss_gain_per_week[0] = 1;
                            recommend_calorie_one_kg_button.setBackgroundColor(getResources().getColor(R.color.holo_green_light));
                            recommend_calorie_half_kg_button.setBackgroundColor(getResources().getColor(R.color.darker_gray));
                        }
                    });



                    recommend_calorie_alertbuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    recommend_calorie_alertbuilder.setPositiveButton("Recommend",null);

                    AlertDialog recommend_calorie_alertdialog = recommend_calorie_alertbuilder.create();


                    recommend_calorie_alertdialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            Button recommend_button = recommend_calorie_alertdialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            recommend_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    DatePicker datePicker = new DatePicker(SignIn.this);
                                    int current_month = datePicker.getMonth() + 1;
                                    int current_year = datePicker.getYear();

                                    int birth_month = selected_month[0] + 1;
                                    int birth_year = selected_year[0];

                                    float age = (float) ((current_year -1) - birth_year) + ((float) ((current_month -1 ) + (12 - birth_month))/12);

                                    double bmr = 1700.0;

                                    if(gender.equals("Male"))
                                    {
                                        bmr = 66 + (6.3 * (current_weight * 2.21)) + ( 12.9 * ((feet_height * 12) + inch_height)) - ( 6.8 * age);
                                    }
                                    else if(gender.equals("Female"))
                                    {
                                        bmr = 655 + (4.3 * (current_weight * 2.21)) + ( 4.7 * ((feet_height * 12) + inch_height)) - ( 4.7 * age);
                                    }

                                    double calories_for_maintain_weight = 2000.0;

                                    if(life_style[0] == 0)
                                    {
                                        calories_for_maintain_weight = bmr * 1.2;
                                    }
                                    else if(life_style[0] == 1)
                                    {
                                        calories_for_maintain_weight = bmr * 1.375;
                                    }
                                    else if(life_style[0] == 2)
                                    {
                                        calories_for_maintain_weight = bmr * 1.55;
                                    }
                                    else if(life_style[0] == 3)
                                    {
                                        calories_for_maintain_weight = bmr * 1.725;
                                    }

                                    double calculated_recommended_calorie = calories_for_maintain_weight;

                                    if(maintain_loss_gain[0] == 0)
                                    {
                                        calculated_recommended_calorie = calories_for_maintain_weight;
                                    }
                                    else if(maintain_loss_gain[0] == 1)
                                    {
                                        if(weight_loss_gain_per_week[0] == 0)
                                        {
                                            double pounds = 0.5 * 2.21;
                                            double reduce_calories = (double) (pounds * 3500) / 7;
                                            calculated_recommended_calorie -= reduce_calories;
                                        }
                                        else if(weight_loss_gain_per_week[0] == 1)
                                        {
                                            double pounds = 1.0 * 2.21;
                                            double reduce_calories = (double) (pounds * 3500) / 7;
                                            calculated_recommended_calorie -= reduce_calories;
                                        }
                                    }

                                    else if(maintain_loss_gain[0] == 2)
                                    {
                                        if(weight_loss_gain_per_week[0] == 0)
                                        {
                                            double pounds = 0.5 * 2.21;
                                            double reduce_calories = (double) (pounds * 3500) / 7;
                                            calculated_recommended_calorie += reduce_calories;
                                        }
                                        else if(weight_loss_gain_per_week[0] == 1)
                                        {
                                            double pounds = 1.0 * 2.21;
                                            double reduce_calories = (double) (pounds * 3500) / 7;
                                            calculated_recommended_calorie += reduce_calories;
                                        }
                                    }

                                    calorie_goal_edittext.setText("" + (int) calculated_recommended_calorie);

                                    dialogInterface.dismiss();


                                }
                            });
                        }
                    });


                    recommend_calorie_alertdialog.show();
                }

            }
        });

        final String[] user_id1 = {user_id};
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button continue_button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                continue_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String feet_string = feet_edittext.getText().toString();
                        String inch_string = inch_edittext.getText().toString();
                        String weight_string = weight_edittext.getText().toString();
                        String desired_weight_string = desired_weight_edittext.getText().toString();
                        String calorie_goal_string = calorie_goal_edittext.getText().toString();
                        String users_name_string = users_name_edittext.getText().toString();

                        if(feet_string.length() < 1 || inch_string.length() < 1 || weight_string.length() < 1 || desired_weight_string.length() < 1
                                || calorie_goal_string.length() < 1 || users_name_string.length() < 1)
                        {
                            if(feet_string.length() < 1)
                            {
                                feet_edittext.setError("Enter feet value");
                            }

                            if(inch_string.length() < 1)
                            {
                                inch_edittext.setError("Enter inch value");
                            }

                            if(weight_string.length() < 1)
                            {
                                weight_edittext.setError("Enter weight");
                            }

                            if(desired_weight_string.length() < 1)
                            {
                                desired_weight_edittext.setError("Enter desired weight");
                            }

                            if(calorie_goal_string.length() < 1)
                            {
                                calorie_goal_edittext.setError("Enter daily calorie goal");
                            }
                            if(users_name_string.length() < 1)
                            {
                                users_name_edittext.setError("Enter name");
                            }
                        }


                        else
                        {

                            dialogInterface.dismiss();

                            int feet_height = Integer.parseInt(feet_string);
                            int inch_height = Integer.parseInt(inch_string);
                            int birth_day = selected_day[0];
                            int birth_month = selected_month[0] + 1;
                            int birth_year = selected_year[0];
                            int calorie_goals = Integer.parseInt(calorie_goal_string);
                            float weight = Float.parseFloat(weight_string);
                            float desired_weight = Float.parseFloat(desired_weight_string);

                            DatePicker datePicker = new DatePicker(SignIn.this);
                            int current_day = datePicker.getDayOfMonth();
                            int current_month = datePicker.getMonth() + 1;
                            int current_year = datePicker.getYear();

                          WeightDetails weightDetails = new WeightDetails(current_day,current_month,current_year,weight);


                            String weight_record_key = databaseReference.push().getKey();
                            weightDetails.setDatabase_key(weight_record_key);
                            databaseReference.child("users").child(user_id1[0]).child("weights").child(weight_record_key).setValue(weightDetails);
                            

                            
                            UserBasics userBasics = new UserBasics(gender,weight,weight,desired_weight,birth_day,birth_month,birth_year,calorie_goals,feet_height,inch_height
                            ,current_day,current_month,current_year);

                            databaseReference.child("users").child(user_id1[0]).child("basics").setValue(userBasics);


                            AllUserGeneralData allUserGeneralData = new AllUserGeneralData(users_name_string,user_id1[0],weight,weight,0.0);
                            databaseReference.child("all_users").child(user_id1[0]).child("general_info").setValue(allUserGeneralData);





                            //insert default foods for new user
                            ArrayList<FoodDetails> dessert_foods = new ArrayList<>();
                            ArrayList<FoodDetails> main_foods = new ArrayList<>();
                            ArrayList<FoodDetails> snacks_foods = new ArrayList<>();
                            ArrayList<FoodDetails> drink_foods = new ArrayList<>();

                            main_foods.add(new FoodDetails("Cooked Rice",205,"Cup",0));
                            main_foods.add(new FoodDetails("Bread",77,"Slice",0));
                            main_foods.add(new FoodDetails("Egg",72,"Piece",0));
                            main_foods.add(new FoodDetails("Chicken breast,without skin",144,"100 grams",0));
                            main_foods.add(new FoodDetails("Chicken drumstic,with skin",268,"100 grams",0));
                            main_foods.add(new FoodDetails("Chicken drumstic,without skin",195,"100 grams",0));
                            main_foods.add(new FoodDetails("Chicken thigh,with skin",277,"100 grams",0));
                            main_foods.add(new FoodDetails("Chicken thigh,without skin",218,"100 grams",0));
                            main_foods.add(new FoodDetails("Chicken wings,with skin",254,"100 grams",0));
                            main_foods.add(new FoodDetails("Chicken wings,without skin",211,"100 grams",0));
                            main_foods.add(new FoodDetails("Beef with fat",259,"100 grams",0));
                            main_foods.add(new FoodDetails("Beef meat only",149,"100 grams",0));
                            main_foods.add(new FoodDetails("Beef sausage",332,"100 grams",0));
                            main_foods.add(new FoodDetails("Mutton with fat",295,"100 grams",0));
                            main_foods.add(new FoodDetails("Mutton meat only",180,"100 grams",0));
                            main_foods.add(new FoodDetails("Milk",122,"Cup",0));
                            main_foods.add(new FoodDetails("Fish",117,"100 grams",0));
                            main_foods.add(new FoodDetails("Fish fry,carp",162,"100 grams",0));
                            main_foods.add(new FoodDetails("Fish,salmon",184,"100 grams",0));
                            main_foods.add(new FoodDetails("Fish fry,tuna",184,"100 grams",0));
                            main_foods.add(new FoodDetails("Fish,tilapia",128,"100 grams",0));
                            main_foods.add(new FoodDetails("Cooked Dal",150,"Cup",0));
                            main_foods.add(new FoodDetails("Mixed Vegetables",150,"Cup",0));






                            //snacks
                            snacks_foods.add(new FoodDetails("Beef Burger",540,"Piece",1));
                            snacks_foods.add(new FoodDetails("Hamburger",297,"100 grams",1));
                            snacks_foods.add(new FoodDetails("Chicken Burger",535,"Piece",1));
                            snacks_foods.add(new FoodDetails("Chicken Nuggest",270,"100 grams",1));
                            snacks_foods.add(new FoodDetails("Cheese Burger",308,"100 grams",1));
                            snacks_foods.add(new FoodDetails("Hotdog",247,"100 grams",1));
                            snacks_foods.add(new FoodDetails("Onion rings",411,"100 grams",1));
                            snacks_foods.add(new FoodDetails("French Toast",264,"100 grams",1));
                            snacks_foods.add(new FoodDetails("Popcorn",387,"100 grams",1));
                            snacks_foods.add(new FoodDetails("Potato Chips",487,"100 grams",1));
                            snacks_foods.add(new FoodDetails("Sandwich",155,"Piece",1));
                            snacks_foods.add(new FoodDetails("Nachos",343,"100 grams",1));
                            snacks_foods.add(new FoodDetails("Pizza",285,"Slice",1));
                            snacks_foods.add(new FoodDetails("Pasta",196,"Cup",1));
                            snacks_foods.add(new FoodDetails("Noddles",196,"Cup",1));
                            snacks_foods.add(new FoodDetails("Chocolate Cookie Medium",148,"Piece",1));

                            snacks_foods.add(new FoodDetails("Chocolate Cookie Medium",148,"Piece",1));
                            snacks_foods.add(new FoodDetails("Croissant",304,"100 grams",1));





                            // desserts
                            dessert_foods.add(new FoodDetails("Ice Cream,Vanilla,rich",259,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Ice Cream,Vanilla,fat free",138,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Ice Cream,Strawberry,light",192,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Peanut bar",522,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Chocolate Cake,without frosting",371,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Fruit Cake",324,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Pound Cake",353,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Sponge Cake",290,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Fat free Cake",283,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Chesse Cake",321,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Chocolate Doughnuts",417,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Pancake,plain",227,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Puff Pastry",280,"Piece",2));
                            dessert_foods.add(new FoodDetails("Fruit Custard",225,"Cup",2));
                            dessert_foods.add(new FoodDetails("Muffin Medium",424,"Piece",2));
                            dessert_foods.add(new FoodDetails("Egg Pudding",344,"Cup",2));

                            dessert_foods.add(new FoodDetails("Dark Chocolate",598,"100 grams",2));
                            dessert_foods.add(new FoodDetails("White Chocolate",539,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Honey",304,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Jellies",266,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Jellies,sugar free",121,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Candies,toffee",560,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Ice Cream,Chocolate,rich",251,"100 grams",2));
                            dessert_foods.add(new FoodDetails("Ice Cream,Chocolate,sugar free",173,"100 grams",2));


                            // drinks
                            drink_foods.add(new FoodDetails("Coffee with milk and sugar",65,"Cup",3));
                            drink_foods.add(new FoodDetails("Milk,whole",150,"Cup",3));
                            drink_foods.add(new FoodDetails("Green Tea,sugar free",0,"Cup",3));
                            drink_foods.add(new FoodDetails("Green Tea,with sugar",67,"Cup",3));
                            drink_foods.add(new FoodDetails("Black Coffee",3,"Cup",3));
                            drink_foods.add(new FoodDetails("Black Tea,sugar free",3,"Cup",3));
                            drink_foods.add(new FoodDetails("Lemon tea,sugar free",5,"Cup",3));
                            drink_foods.add(new FoodDetails("Lemon tea with sugar",88,"Cup",3));
                            drink_foods.add(new FoodDetails("Coke",110,"Glass",3));
                            drink_foods.add(new FoodDetails("Coconut Water",45,"Glass",3));
                            drink_foods.add(new FoodDetails("Apple Juice,light",55,"Glass",3));
                            drink_foods.add(new FoodDetails("Vanilla Milkshake",280,"Glass",3));
                            drink_foods.add(new FoodDetails("Strawberry shake",113,"100 grams",3));
                            drink_foods.add(new FoodDetails("Mixed Fruit Juice",135,"Glass",3));
                            drink_foods.add(new FoodDetails("Instant Coffee,powder",353,"100 grams",3));

                            for(int i = 0; i < main_foods.size(); i++)
                            {
                                String default_food_key = databaseReference.push().getKey();
                                FoodDetails foodDetails = main_foods.get(i);
                                foodDetails.setDatabase_key(default_food_key);
                                databaseReference.child("users").child(user_id1[0]).child("foods").child("main_foods").child(default_food_key).setValue(foodDetails);

                            }

                            for(int i = 0; i < snacks_foods.size(); i++)
                            {

                                String default_food_key = databaseReference.push().getKey();

                                FoodDetails foodDetails = snacks_foods.get(i);
                                foodDetails.setDatabase_key(default_food_key);
                                databaseReference.child("users").child(user_id1[0]).child("foods").child("snacks").child(default_food_key).setValue(foodDetails);

                            }

                            for(int i = 0; i < dessert_foods.size(); i++)
                            {

                                String default_food_key = databaseReference.push().getKey();

                                FoodDetails foodDetails = dessert_foods.get(i);
                                foodDetails.setDatabase_key(default_food_key);
                                databaseReference.child("users").child(user_id1[0]).child("foods").child("desserts").child(default_food_key).setValue(foodDetails);

                            }

                            for(int i = 0; i < drink_foods.size(); i++)
                            {

                                String default_food_key = databaseReference.push().getKey();

                                FoodDetails foodDetails = drink_foods.get(i);
                                foodDetails.setDatabase_key(default_food_key);
                                databaseReference.child("users").child(user_id1[0]).child("foods").child("drinks").child(default_food_key).setValue(foodDetails);

                            }


                            SharedPreferences sharedPreferences = getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("has_account","yes");
                            if(sign_in_method == 0)
                            {
                                editor.putString("account_type","google");
                            }
                            else
                            {
                                editor.putString("account_type","guest");
                            }

                            editor.putInt("calorie_goal",calorie_goals);
                            editor.commit();
                            startActivity(new Intent(SignIn.this,HomeScreen.class));


                        }
                    }
                });
            }
        });

        alertDialog.show();









    }


}