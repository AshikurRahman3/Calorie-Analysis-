package com.example.calorieanalysis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

/*
no more used, but in my files , this sqlite database is used,
if you try to remove, remove with caution

at first , we have used sqlite database, but later we have implemented firebase, so no need to use sqlite , but
we have not removed sqlite code from files, so, if any sqlite code removed without related all sqlite code there, app may crash.
 */
public class MyDiaryDatabaseHelper extends SQLiteOpenHelper {

    FoodDetails foodDetails;

    private Context context;
    private static final String DATABASE_NAME = "My_Diary.db";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "food_diary_details";
    private static final String ID = "_id";
    private static final String FOOD_NAME = "food_name";
    private static final String FOOD_PER_UNIT_CALORIE = "food_calorie_per_unit";
    private static final String FOOD_UNIT_NAME = "food_unit_name";
    private static final String DAY = "day";
    private static final String MONTH = "month";
    private static final String YEAR = "year";
    private static final String BREAKFAST_TOTAL_CALORIE = "breakfast_total_calories";
    private static final String LUNCH_TOTAL_CALORIE = "lunch_total_calories";
    private static final String DINNER_TOTAL_CALORIE = "dinner_total_calories";
    private static final String SNACKS_TOTAL_CALORIE = "snacks_total_calories";
   private static final String ITEMS_QUANTITY = "items_quantity";
   private static final String SELECTED_MEAL = "selected_meal";
    private static final String TOTAL_CALORIE = "total_Calories";
    private static final String FOOD_TOTAL_CALORIE = "food_total_Calories";


    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FOOD_NAME + " TEXT," + ITEMS_QUANTITY + " DOUBLE," + FOOD_TOTAL_CALORIE + " INTEGER," + FOOD_PER_UNIT_CALORIE + " INTEGER," + FOOD_UNIT_NAME + " TEXT,"
            + SELECTED_MEAL + " INTEGER," + BREAKFAST_TOTAL_CALORIE + " INTEGER DEFAULT 0," + LUNCH_TOTAL_CALORIE + " INTEGER DEFAULT 0,"
            + DINNER_TOTAL_CALORIE + " INTEGER DEFAULT 0," + SNACKS_TOTAL_CALORIE + " INTEGER DEFAULT 0," + TOTAL_CALORIE + " INTEGER DEFAULT 0,"
            + DAY + " INTEGER," + MONTH + " INTEGER," + YEAR + " INTEGER)";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;




    public MyDiaryDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        try {
            sqLiteDatabase.execSQL(CREATE_TABLE);
        }catch (Exception e)
        {
            Toast.makeText(context, "OnCreate Exception: "+e, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        try {
            sqLiteDatabase.execSQL(DROP_TABLE);
            onCreate(sqLiteDatabase);
        }catch (Exception e)
        {
            Toast.makeText(context, "OnUpgrade Exception: "+e, Toast.LENGTH_LONG).show();
        }

    }

    public long insertFood(String food_name,double food_quantity,int selected_meal,int food_per_unit_calorie,String food_unit_name
            ,int day,int month,int year)
    {
        SQLiteDatabase sqLiteDatabase  = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        double food_calorie_total = food_quantity * food_per_unit_calorie;

        contentValues.put(FOOD_NAME,food_name);
        contentValues.put(ITEMS_QUANTITY,food_quantity);
        contentValues.put(SELECTED_MEAL,selected_meal);
        contentValues.put(FOOD_PER_UNIT_CALORIE,food_per_unit_calorie);
        contentValues.put(FOOD_UNIT_NAME,food_unit_name);
        contentValues.put(DAY,day);
        contentValues.put(MONTH,month);
        contentValues.put(YEAR,year);
        contentValues.put(FOOD_TOTAL_CALORIE,(int)food_calorie_total);



        int breakfast_total_calorie = 0, lunch_total_calorie = 0,dinner_total_calorie = 0,snacks_total_calorie = 0,total_calorie = 0;

        Cursor cursor = read_food_diary("SELECT " + BREAKFAST_TOTAL_CALORIE + "," + LUNCH_TOTAL_CALORIE + "," +
                DINNER_TOTAL_CALORIE + "," + SNACKS_TOTAL_CALORIE + "," +TOTAL_CALORIE + "," +FOOD_TOTAL_CALORIE + " FROM "+TABLE_NAME +" WHERE " +DAY + " == " +day+ " AND " + MONTH +
                " == " + month + " AND " + YEAR + " == " + year + " ORDER BY " + ID + " DESC");

        int num = 0;
        while (cursor.moveToNext())
        {
            ++num;
            if(num == 1)
            {
                breakfast_total_calorie = cursor.getInt(0);
                lunch_total_calorie = cursor.getInt(1);
                dinner_total_calorie = cursor.getInt(2);
                snacks_total_calorie = cursor.getInt(3);
                total_calorie = cursor.getInt(4);
                break;
            }
        }

        if(selected_meal == 0)
        {
            breakfast_total_calorie += food_quantity * food_per_unit_calorie;
        }
        else if(selected_meal == 1)
        {
            lunch_total_calorie += food_quantity * food_per_unit_calorie;
        }
        else if(selected_meal == 2)
        {
            dinner_total_calorie += food_quantity * food_per_unit_calorie;
        }
        else if(selected_meal == 3)
        {
            snacks_total_calorie += food_quantity * food_per_unit_calorie;
        }



        total_calorie += food_per_unit_calorie * food_quantity;

        contentValues.put(BREAKFAST_TOTAL_CALORIE,breakfast_total_calorie);
        contentValues.put(LUNCH_TOTAL_CALORIE,lunch_total_calorie);
        contentValues.put(DINNER_TOTAL_CALORIE,dinner_total_calorie);
        contentValues.put(SNACKS_TOTAL_CALORIE,snacks_total_calorie);
        contentValues.put(TOTAL_CALORIE,total_calorie);

        long row_number = sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        return row_number;
    }

    public Cursor read_food_diary(String query)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        return cursor;
    }


    public void delete_entried_food(String id)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME,ID + " = ?",new String[] {id});
    }

}
