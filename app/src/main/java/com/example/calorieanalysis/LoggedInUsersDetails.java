package com.example.calorieanalysis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

/*
this is no more used, but somewhere , it may be used, so,
if try to remove, remove with caution
 */
public class LoggedInUsersDetails extends AppCompatActivity {

    ImageView imageView;
    TextView textView;

    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_users_details);

        textView = findViewById(R.id.text);
        imageView = findViewById(R.id.image_id);

        logout = findViewById(R.id.log_out);

        SharedPreferences sharedPreferencess = getSharedPreferences("previous_account", Context.MODE_PRIVATE);
        String ids = sharedPreferencess.getString("has_account","nokia");

        Toast.makeText(LoggedInUsersDetails.this, "logged in: "+ids, Toast.LENGTH_SHORT).show();

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(LoggedInUsersDetails.this);

        if(googleSignInAccount != null)
        {
            String name = googleSignInAccount.getDisplayName();
            String email = googleSignInAccount.getEmail();




            SharedPreferences sharedPreferences = getSharedPreferences("previous_account", Context.MODE_PRIVATE);
            String id = sharedPreferences.getString("user_id",null);

            textView.setText("Name: "+name + "\nemail: "+email + "\nid: "+id);

            Picasso.get().load(googleSignInAccount.getPhotoUrl()).placeholder(R.mipmap.ic_launcher).into(imageView);
        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(LoggedInUsersDetails.this,SignIn.class));

                SharedPreferences sharedPreferences = getSharedPreferences("previous_account", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("has_account","no");
                editor.commit();
            }
        });


    }
}