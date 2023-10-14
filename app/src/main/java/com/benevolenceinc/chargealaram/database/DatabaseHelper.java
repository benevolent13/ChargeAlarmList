package com.benevolenceinc.chargealaram.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.benevolenceinc.chargealaram.model.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hitesh on 3/4/19.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chargealarm.db";
    private static final int DATABASE_VERSION = 1;

    private static final String MAIN_TABLE = "maintable";

    //column name of contact table
    private static final String _ID = "_id";
    private static final String BATTERY_PERCENTAGE = "battery_percentage";
    private static final String STATUS = "status";
    private static final String ONCE_NOTIFIED = "once_notified";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MAIN_TABLE);
        onCreate(db);
    }

    private void createTables(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_MAINTABLE = "CREATE TABLE " + MAIN_TABLE + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + STATUS + " INTEGER DEFAULT 1,"
                + ONCE_NOTIFIED + " INTEGER,"
                + BATTERY_PERCENTAGE + " INTEGER)";
        sqLiteDatabase.execSQL(CREATE_TABLE_MAINTABLE);
    }

    public boolean setValue(Data data) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BATTERY_PERCENTAGE, data.getPercentage());
        contentValues.put(STATUS, data.getStatus());
        contentValues.put(ONCE_NOTIFIED,data.getOnce_notified());
        sqLiteDatabase.insert(MAIN_TABLE, null, contentValues);
        return true;
    }

    public void updateStatus(Data data) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS, data.getStatus());
        sqLiteDatabase.update(MAIN_TABLE, contentValues, _ID + " = ?", new String[]{String.valueOf(data.get_id())});
    }

    public void updateOnceNotified(Data data) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ONCE_NOTIFIED, data.getOnce_notified());
        sqLiteDatabase.update(MAIN_TABLE, contentValues, _ID + " = ?", new String[]{String.valueOf(data.get_id())});
    }

    public void delete(Data data){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(MAIN_TABLE,_ID + " = ?", new String[]{String.valueOf(data.get_id())});
    }

    public List<Data> getData() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+MAIN_TABLE+" ORDER BY "+BATTERY_PERCENTAGE,null);
        List<Data> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Log.e("SORT ID", String.valueOf(cursor.getInt(0)));
                Log.e("SORT NOTIFY ONCE", String.valueOf(cursor.getInt(2)));
                Log.e("SORT STATUS", String.valueOf(cursor.getInt(1)));
                Log.e("SORT PERCENTAGE", String.valueOf(cursor.getInt(3)));
                Data data = new Data();
                data.set_id(cursor.getInt(0));
                data.setStatus(cursor.getInt(1));
                data.setOnce_notified(cursor.getInt(2));
                data.setPercentage(cursor.getInt(3));
                list.add(data);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

}
