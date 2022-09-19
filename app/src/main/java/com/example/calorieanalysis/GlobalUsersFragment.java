package com.example.calorieanalysis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/*
to show all users general informations
 */

public class GlobalUsersFragment extends Fragment {
    ListView listView;
    DatabaseReference databaseReference;
    ArrayList<AllUserGeneralData> allUserGeneralData;
    GlobalUsersAdapter globalUsersAdapter;

    SearchView searchView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_global_users, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();


        allUserGeneralData = new ArrayList<>();

        globalUsersAdapter = new GlobalUsersAdapter(getActivity().getApplicationContext(),allUserGeneralData);

        read_database();
        listView = (ListView) view.findViewById(R.id.fragment_global_listview_id);
        listView.setAdapter(globalUsersAdapter);

        searchView = (SearchView) view.findViewById(R.id.fragment_global_user_searchview_id);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<AllUserGeneralData> search_history_results = new ArrayList<>();
                for(AllUserGeneralData x : allUserGeneralData)
                {
                    if(x.getName().toLowerCase().contains(newText.toLowerCase()))
                    {
                        search_history_results.add(x);
                    }
                }

                globalUsersAdapter.refresh_on_search(search_history_results);



                return false;
            }
        });



        return view;
    }


    public void read_database()
    {
        SharedPreferences sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("previous_account", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("user_id",null);

        databaseReference.child("all_users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allUserGeneralData.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    for ( DataSnapshot snapshot2 : snapshot1.getChildren())
                    {
                        AllUserGeneralData allUserGeneralData1 = snapshot2.getValue(AllUserGeneralData.class);
                        allUserGeneralData.add(allUserGeneralData1);
                    }
                }
                Collections.sort(allUserGeneralData, new Comparator<AllUserGeneralData>() {
                    @Override
                    public int compare(AllUserGeneralData allUserGeneralData, AllUserGeneralData t1) {
                        return Double.compare(allUserGeneralData.getLost_weight(),t1.getLost_weight());
                    }
                });
                globalUsersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}