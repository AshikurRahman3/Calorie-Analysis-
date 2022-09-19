package com.example.calorieanalysis;

/*
to show general informations of all users
here, all users are sorted according to lost weight descending order
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class GlobalUsersAdapter extends BaseAdapter {
    private Context context;
    ArrayList<AllUserGeneralData> allUserGeneralData;

    LayoutInflater layoutInflater;

    public GlobalUsersAdapter(Context context, ArrayList<AllUserGeneralData> allUserGeneralData) {
        this.context = context;
        this.allUserGeneralData = allUserGeneralData;
    }

    public void refresh_on_search(ArrayList<AllUserGeneralData> search_results)
    {
        allUserGeneralData = new ArrayList<>();
        allUserGeneralData.addAll(search_results);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return allUserGeneralData.size();
    }

    @Override
    public AllUserGeneralData getItem(int i) {
        return allUserGeneralData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view == null)
        {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.global_users_info,viewGroup,false);
        }

        TextView name = (TextView) view.findViewById(R.id.global_user_name_textview_id);
        TextView current_weight = (TextView) view.findViewById(R.id.global_user_current_weight_textview_id);
        TextView start_weight = (TextView) view.findViewById(R.id.global_user_start_weight_textview_id);
        TextView lost_weight = (TextView) view.findViewById(R.id.global_user_lost_weight_value_textview_id);
        TextView lost_gained = (TextView) view.findViewById(R.id.global_user_lost_gained_textview_id);
        TextView serial = (TextView) view.findViewById(R.id.global_user_serial_textview_id);
        LinearLayout rootview = (LinearLayout) view.findViewById(R.id.global_user_info_rootview_id);

        SharedPreferences sharedPreferences = context.getSharedPreferences("previous_account", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("user_id",null);

        serial.setText(""+(i + 1) + ".");


        if(allUserGeneralData.get(i).getUser_id().equals(id))
        {
            name.setText(allUserGeneralData.get(i).getName() + " ( you )");
            rootview.setBackgroundColor(context.getResources().getColor(R.color.gray_light));
        }
        else
        {
            name.setText(allUserGeneralData.get(i).getName());
        }
        current_weight.setText("Current_weight: "+String.format("%.1f",allUserGeneralData.get(i).getCurrent_weight()) + " kg");
        start_weight.setText("Start_weight: "+String.format("%.1f",allUserGeneralData.get(i).getStart_weight()) + " kg");

        if(allUserGeneralData.get(i).getLost_weight() < 0.0)
        {
            lost_weight.setTextColor(context.getResources().getColor(R.color.holo_green_light));
            lost_weight.setText(""+String.format("%.1f",allUserGeneralData.get(i).getLost_weight()));
            lost_gained.setText("lost");
        }
        else if(allUserGeneralData.get(i).getLost_weight() == 0.0)
        {
            lost_weight.setTextColor(context.getResources().getColor(R.color.black));
            lost_weight.setText(""+String.format("%.1f",allUserGeneralData.get(i).getLost_weight()));
            lost_gained.setText("lost");
        }
        else
        {
            lost_weight.setTextColor(context.getResources().getColor(R.color.red_light));
            lost_weight.setText("+"+String.format("%.1f",allUserGeneralData.get(i).getLost_weight()));
            lost_gained.setText("gained");
        }


        return view;
    }
}
