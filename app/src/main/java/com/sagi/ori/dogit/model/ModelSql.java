package com.sagi.ori.dogit.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ori and sagi on 17/01/2018.
 */

public class ModelSql extends SQLiteOpenHelper {
    ModelSql(Context context) {
        super(context, "database.db", null, 7);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DogSitterSql.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DogSitterSql.onUpgrade(db, oldVersion, newVersion);
    }

}
