package com.nutmeg.mydish;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by chris on 10/27/13.
 */

public class DatabaseModel extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "entries";
    public static final String ENTRY_TITLE  = "title";
    public static final String ENTRY_NOTES  = "notes";
    public static final String ENTRY_RECIPE  = "recipe";
    public static final String ENTRY_DATE = "date";
    public static final String ENTRY_CATEGORIES  = "categories";
    public static final String ENTRY_PICTURE  = "picture";
    public static final String ENTRY_ID  = "_id";


    private static final String DATABASE_NAME = "MyDish";
    private static final int DATABASE_VERSION = 1;

    // DatabaseModel creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "("
            + ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ENTRY_TITLE + " TEXT NOT NULL, "
            + ENTRY_NOTES + " TEXT NOT NULL, "
            + ENTRY_RECIPE + " TEXT NOT NULL, "
            + ENTRY_DATE + " TEXT NOT NULL, "
            + ENTRY_CATEGORIES + " TEXT NOT NULL, "
            + ENTRY_PICTURE + " TEXT NOT NULL);";

    //Default Constructor
    public DatabaseModel(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    //OnCreate Method - creates the DatabaseModel
    public void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE);

    }
    @Override
    //OnUpgrade Method - upgrades DatabaseModel if applicable
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        Log.w(DatabaseModel.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

}
