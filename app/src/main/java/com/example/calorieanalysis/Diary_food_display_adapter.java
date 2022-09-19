package com.example.calorieanalysis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/*
to show cosumed foods in diary


 */
public class Diary_food_display_adapter extends BaseAdapter {

    private Context context;
    ArrayList<DiaryFoodDetails> diaryFoodDetails;
    LayoutInflater layoutInflater;

    public Diary_food_display_adapter(Context context, ArrayList<DiaryFoodDetails> diaryFoodDetails) {
        this.context = context;
        this.diaryFoodDetails = diaryFoodDetails;
    }

    @Override
    public int getCount() {
        return diaryFoodDetails.size();
    }

    @Override
    public DiaryFoodDetails getItem(int i) {
        return diaryFoodDetails.get(i);
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
            view = layoutInflater.inflate(R.layout.food_displaying_in_diary_layout,viewGroup,false);
        }

        TextView food_name = (TextView) view.findViewById(R.id.food_name_textview_id);
        TextView food_quantity = (TextView) view.findViewById(R.id.food_quantity_textview_id);
        TextView total_calorie = (TextView) view.findViewById(R.id.food_item_total_calorie_textview_id);

        food_name.setText(diaryFoodDetails.get(i).getFood_name());
        food_quantity.setText(diaryFoodDetails.get(i).getFood_quantity() + " servings (" + diaryFoodDetails.get(i).getFood_per_unit_calorie() + " Kcals per " +
                 diaryFoodDetails.get(i).getFood_unit_name()+ " )");
        double food_item_total_calorie = (double) diaryFoodDetails.get(i).getFood_quantity() * diaryFoodDetails.get(i).getFood_per_unit_calorie();
        total_calorie.setText(""+diaryFoodDetails.get(i).getFood_totoal_calories());
        return view;
    }


    public void refresh_on_search(ArrayList<DiaryFoodDetails> search_results)
    {
        diaryFoodDetails = new ArrayList<>();
        diaryFoodDetails.addAll(search_results);
        notifyDataSetChanged();
    }
}
