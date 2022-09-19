package com.example.calorieanalysis;

/*
to show food data of every individual foods
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FoodDisplayAdapter extends BaseAdapter {
    private Context context;
    ArrayList<FoodDetails> foodDetails;

    LayoutInflater layoutInflater;
    public FoodDisplayAdapter(Context context, ArrayList<FoodDetails> foodDetails) {
        this.context = context;
        this.foodDetails = foodDetails;
    }


    public void refresh_on_search(ArrayList<FoodDetails> search_results)
    {
        foodDetails = new ArrayList<>();
        foodDetails.addAll(search_results);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return foodDetails.size();
    }

    @Override
    public FoodDetails getItem(int i) {
        return foodDetails.get(i);
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
            view = layoutInflater.inflate(R.layout.food_details_layout,viewGroup,false);
        }
        TextView foodName = (TextView) view.findViewById(R.id.foodDetailsFoodNameTextviewId);
        TextView foodCalorieUnit = (TextView) view.findViewById(R.id.foodDetailsFoodCalorieUnitTextviewId);

        foodName.setText(foodDetails.get(i).getName());
        foodCalorieUnit.setText(foodDetails.get(i).getCalories() + " Kcals / " + foodDetails.get(i).getUnit());
        return view;
    }

}
