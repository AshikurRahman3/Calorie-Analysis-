package com.example.calorieanalysis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/*
not used anymore,but

at first , we have used sqlite database, but later we have implemented firebase, so no need to use sqlite , but
we have not removed sqlite code from files, so, if any sqlite code removed without related all sqlite code there, app may crash.
 */
public class WeightDatabaseHelper extends SQLiteOpenHelper {


    private Context context;

    private static final String DATABASE_NAME = "Weight_Database.db";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "Food_Table";
    private static final String ID = "_id";
    private static final String WEIGHT = "weight";
    private static final String DAY = "day";
    private static final String MONTH = "month";
    private static final String YEAR = "year";


    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + WEIGHT + " DOUBLE," + DAY + " INTEGER," + MONTH + " INTEGER," + YEAR + " INTEGER)";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+ TABLE_NAME;

    public WeightDatabaseHelper(@Nullable Context context) {
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


    public void insert_weight_data(WeightDetails weightDetails)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEIGHT,weightDetails.getWeight());
        contentValues.put(DAY,weightDetails.getDay());
        contentValues.put(MONTH,weightDetails.getMonth());
        contentValues.put(YEAR,weightDetails.getYear());

        long rowNumber = sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
    }

    public Cursor read_weight_database_descending_order()
    {
        Cursor cursor;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME + " ORDER BY " + ID + " DESC",null);
        return cursor;
    }

    public Cursor read_weight_database_ascending_order()
    {
        Cursor cursor;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME + " ORDER BY " + ID + " ASC",null);
        return cursor;
    }


    public void updateDataBase(WeightDetails weightDetails)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEIGHT,weightDetails.getWeight());
        contentValues.put(DAY,weightDetails.getDay());
        contentValues.put(MONTH,weightDetails.getMonth());
        contentValues.put(YEAR,weightDetails.getYear());
        sqLiteDatabase.update(TABLE_NAME,contentValues,ID + " = ?",new String[] {String.valueOf(weightDetails.getId())});
    }

    public void   deleteFromDatabase(String id)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        int rowdeleted = sqLiteDatabase.delete(TABLE_NAME,ID + " = ?",new String[] {id});
    }
}
