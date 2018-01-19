package com.sagi.ori.dogit.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ori and sagi on 17/01/2018.
 */
public class DogSitterSql {
    static final String DS_TABLE = "dogsitter";
    static final String DS_EMAIL = "email";

    static final String DS_PASSWORD = "password";
    static final String DS_NAME = "name";
    static final String DS_PHONE = "phone";
    static final String DS_ADDRESS = "address";
    static final String DS_SALARY = "salary";
    static final String DS_AGE = "age";
    static final String DS_AVAILABILITY = "availability";

    static final String DS_IMAGE_URL = "imageUrl";
    static final String DS_LAST_UPDATE = "lastUpdateDate";



    static List<DogSitter> getAllDogSitters(SQLiteDatabase db) {
        Cursor cursor = db.query(DS_TABLE, null, null, null, null, null, null);
        List<DogSitter> list = new LinkedList<DogSitter>();
        if (cursor.moveToFirst()) {
            int email_Index = cursor.getColumnIndex(DS_EMAIL);

            int password_Index = cursor.getColumnIndex(DS_PASSWORD);
            int name_Index = cursor.getColumnIndex(DS_NAME);
            int phone_Index = cursor.getColumnIndex(DS_PHONE);
            int address_Index = cursor.getColumnIndex(DS_ADDRESS);
            int salary_Index = cursor.getColumnIndex(DS_SALARY);
            int age_Index = cursor.getColumnIndex(DS_AGE);
            int availability_Index = cursor.getColumnIndex(DS_AVAILABILITY);

            int imageUrl_Index = cursor.getColumnIndex(DS_IMAGE_URL);
            int lastUpdateDate_Index = cursor.getColumnIndex(DS_LAST_UPDATE);

            do {
                DogSitter ds = new DogSitter();
                ds.email = cursor.getString(email_Index);
                ds.password = cursor.getString(password_Index);
                ds.name = cursor.getString(name_Index);
                ds.phone = cursor.getString(phone_Index);
                ds.address = cursor.getString(address_Index);
                ds.salary = cursor.getString(salary_Index);
                ds.age = cursor.getString(age_Index);
                ds.availability = cursor.getString(availability_Index);
                ds.imageUrl = cursor.getString(imageUrl_Index);
                ds.lastUpdateDate = cursor.getDouble(lastUpdateDate_Index);

                list.add(ds);
            } while (cursor.moveToNext());
        }
        return list;
    }

    static void addDogSitter(SQLiteDatabase db, DogSitter ds) {
        ContentValues values = new ContentValues();

        values.put(DS_EMAIL, ds.email);
        values.put(DS_PASSWORD, ds.password);
        values.put(DS_NAME, ds.name);
        values.put(DS_PHONE, ds.phone);
        values.put(DS_ADDRESS, ds.address);
        values.put(DS_SALARY, ds.salary);
        values.put(DS_AGE, ds.age);
        values.put(DS_AVAILABILITY, ds.availability);
        values.put(DS_IMAGE_URL, ds.imageUrl);
        values.put(DS_LAST_UPDATE, ds.lastUpdateDate);

        db.insert(DS_TABLE, DS_EMAIL, values);
    }


    static public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + DS_TABLE +
                " (" +
                DS_EMAIL + " TEXT PRIMARY KEY, " +
                DS_PASSWORD + " TEXT, " +
                DS_NAME + " TEXT, " +
                DS_PHONE + " TEXT, " +
                DS_ADDRESS + " TEXT, " +
                DS_SALARY + " TEXT, " +
                DS_AGE + " TEXT, " +
                DS_AVAILABILITY + " TEXT, " +
                DS_LAST_UPDATE + " NUMBER, " +
                DS_IMAGE_URL + " TEXT);";
        Log.d("TAG",sql);
        db.execSQL(sql);
    }

    static public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + DS_TABLE + ";");
        onCreate(db);
    }


}
