package com.example.calorieanalysis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/*
no more used, but in my files , this sqlite database is used,
if you try to remove, remove with caution

at first , we have used sqlite database, but later we have implemented firebase, so no need to use sqlite , but
we have not removed sqlite code from files, so, if any sqlite code removed without related all sqlite code there, app may crash.
 */
public class MyFoodDatabaseHelper extends SQLiteOpenHelper {
    private Context context;

    private static final String DATABASE_NAME = "Food_Database.db";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "Food_table";
    private static final String ID = "_id";
    private static final String NAME = "Name";
    private static final String CALORIES = "Calories";
    private static final String UNIT = "Unit";
    private static final String FOOD_TYPE = "Food_type";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NAME + " TEXT," + CALORIES + " INTEGER," + UNIT + " TEXT," + FOOD_TYPE + " INTEGER)";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+ TABLE_NAME;

    public MyFoodDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        try {
            sqLiteDatabase.execSQL(CREATE_TABLE);
        }catch (Exception e)
        {

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        try {
            sqLiteDatabase.execSQL(DROP_TABLE);
            onCreate(sqLiteDatabase);
        }catch (Exception e)
        {

        }
    }

    public long insertFoodData(FoodDetails foods)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME,foods.getName());
        contentValues.put(CALORIES,foods.getCalories());
        contentValues.put(UNIT,foods.getUnit());
        contentValues.put(FOOD_TYPE,foods.getFood_type());

        long rowNumber = sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        return rowNumber;
    }
    public Cursor read_food_database(int food_type)
    {
        Cursor cursor;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME + " WHERE " + FOOD_TYPE + " == " + food_type,null);
        return cursor;
    }

    public void updateDataBase(FoodDetails foodDetails)
    {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(ID,foodDetails.getId());
            contentValues.put(NAME,foodDetails.getName());
            contentValues.put(CALORIES,foodDetails.getCalories());
            contentValues.put(UNIT,foodDetails.getUnit());
            sqLiteDatabase.update(TABLE_NAME,contentValues,ID + " = ?",new String[] {String.valueOf(foodDetails.getId())});
    }

    public int  deleteFromDatabase(String id)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int rowdeleted = sqLiteDatabase.delete(TABLE_NAME,ID + " = ?",new String[] {id});
        return rowdeleted;
    }
    public Cursor readCustomColumn()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + UNIT + "," + CALORIES + " FROM " + TABLE_NAME,null);
        return cursor;
    }

}
