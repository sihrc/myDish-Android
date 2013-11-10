package com.nutmeg.mydish;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by chris on 10/27/13.
 */
public class DBHandler {
    private DatabaseModel model;
    private SQLiteDatabase database;

    //Columns in the database
    private String[] allColumns = {
            DatabaseModel.ENTRY_TITLE ,
            DatabaseModel.ENTRY_NOTES ,
            DatabaseModel.ENTRY_RECIPE ,
            DatabaseModel.ENTRY_DATE,
            DatabaseModel.ENTRY_CATEGORIES ,
            DatabaseModel.ENTRY_PICTURE,
            DatabaseModel.ENTRY_ID,
    };

    //Public Constructor
    public DBHandler(Context context){
            model = new DatabaseModel(context);
    }

    //Opening the Database (Getting the writable Database)
    public void open(){
        database = model.getWritableDatabase();
    }

    public Entry getEntryById (String id){
        return sweepCursor(database.query(DatabaseModel.TABLE_NAME, allColumns, DatabaseModel.ENTRY_ID + " = " + id , null, null, null, DatabaseModel.ENTRY_DATE  + " DESC")).get(0);
    }

    public ArrayList<Entry> getAllEntries(){
        return sweepCursor(database.query(DatabaseModel.TABLE_NAME, allColumns, null, null, null, null, DatabaseModel.ENTRY_DATE  + " DESC"));
    }

    public void close(){
        database.close();
    }

    //Adding a Post
    public void addEntry(Entry newEntry){
        //Creating a value holder
        ContentValues values = new ContentValues();
        //Unpacking Post information to holder
        values.put(DatabaseModel.ENTRY_TITLE, newEntry.title);
        values.put(DatabaseModel.ENTRY_NOTES, newEntry.notes);
        values.put(DatabaseModel.ENTRY_RECIPE, newEntry.recipe);
        values.put(DatabaseModel.ENTRY_DATE, newEntry.date);
        values.put(DatabaseModel.ENTRY_CATEGORIES, newEntry.categories);
        values.put(DatabaseModel.ENTRY_PICTURE, newEntry.picture);

        //Inserting into database
        long id = this.database.insert(DatabaseModel.TABLE_NAME, null, values);
    }

    //Delete Posts by ID
    public void deleteEntryById(String id){
        database.delete(DatabaseModel.TABLE_NAME, DatabaseModel.ENTRY_ID + " = " + id, null);
    }
    public void updateEntry(Entry entry){
        deleteEntryById(entry.id);
        addEntry(entry);
    }

    public ArrayList<Entry> getEntriesByCat(String category){
        return sweepCursor(database.query(DatabaseModel.TABLE_NAME, allColumns, DatabaseModel.ENTRY_CATEGORIES + " like '%" + category + "%'", null, null, null, DatabaseModel.ENTRY_DATE  + " DESC"));
    }

    //Get Posts from Cursor
    public ArrayList<Entry> sweepCursor (Cursor cursor) {
        ArrayList<Entry> entries = new ArrayList<Entry>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            entries.add(cursorToEntry(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return entries;
    }

    //Get Posts from Cursor
    public Entry cursorToEntry(Cursor cursor){
        Entry entry = new Entry(
            cursor.getString(0),
            cursor.getString(1),
            cursor.getString(2),
            cursor.getString(3),
            cursor.getString(4),
            cursor.getString(5)
        );

        entry.setId(cursor.getString(6));
        return entry;
    }
}
