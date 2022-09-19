package com.example.calorieanalysis;

/*
to show weights in weight history
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WeightDisplayAdapter extends BaseAdapter {

    String months[] = {"0","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec","0","0"};

    private Context context;
    ArrayList<WeightDetails> weightDetails;

    LayoutInflater layoutInflater;
    public WeightDisplayAdapter(Context context, ArrayList<WeightDetails> weightDetails) {
        this.context = context;
        this.weightDetails = weightDetails;
    }



    @Override
    public int getCount() {
        return weightDetails.size();
    }

    @Override
    public WeightDetails getItem(int i) {
        return weightDetails.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
        {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.weight_history,viewGroup,false);
        }
        TextView date_textview = (TextView) view.findViewById(R.id.weight_history_date_textview_id);
        TextView current_weight_textview = (TextView) view.findViewById(R.id.weight_history_current_weight_textview_id);
        TextView weight_difference_textview = (TextView) view.findViewById(R.id.weight_history_weight_difference_textview_id);

        current_weight_textview.setText(String.format("%.1f",weightDetails.get(i).getWeight()));
        weight_difference_textview.setText(""+String.format("%.1f",weightDetails.get(i).getWeight_difference()));
        date_textview.setText(weightDetails.get(i).getDay() + " " + months[weightDetails.get(i).month] + ", " + weightDetails.get(i).getYear());
        double weight_differnce = weightDetails.get(i).weight_difference;
        if(weight_differnce > 0.0)
        {
            weight_difference_textview.setTextColor(context.getResources().getColor(R.color.red_light));
            weight_difference_textview.setText("+ "+ String.format("%.1f",weightDetails.get(i).getWeight_difference()));
        }
        else if(weight_differnce < 0.0)
        {
            weight_difference_textview.setTextColor(context.getResources().getColor(R.color.holo_green_light));
        }

        else if(weight_differnce == 0.0)
        {
            weight_difference_textview.setTextColor(context.getResources().getColor(R.color.black));
        }

        return view;
    }
}
