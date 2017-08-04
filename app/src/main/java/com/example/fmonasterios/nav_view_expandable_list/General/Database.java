package com.example.fmonasterios.nav_view_expandable_list.General;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fmonasterios on 5/20/2017.
 */

public class Database extends SQLiteOpenHelper{

    private static String PPAL_DB_NAME="FAMM.db";

    public Database(Context context){
        super(context, PPAL_DB_NAME,null,1);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;
        query = "CREATE TABLE memories ("+ "_id" +"INTEGER PRIMARY KEY, DATE TEXT NOT NULL, TITLE TEXT NOT NULL, DESCRIPTION TEXT NOT NULL, IMAGE TEXT NOT NULL DEFAULT 'no')";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        String query;
        query = "DROP TABLE IF EXISTS memories";
        database.execSQL(query);


        onCreate(database);
    }


}

