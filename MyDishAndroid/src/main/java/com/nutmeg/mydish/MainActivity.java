package com.nutmeg.mydish;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends Activity {
    ListView entryList;
    ArrayList<Entry> entries;

    DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Getting Database
        db = new DBHandler(this);
        db.open();

        setupListView();
    }

    //Set up List View for Entries
    public void setupListView(){
        updateEntries();
        entryList = (ListView) findViewById(R.id.entries);
        EntryAdapter entryAdapter = new EntryAdapter(this,this.entries);
        entryList.setAdapter(entryAdapter);
        entryAdapter.notifyDataSetChanged();

        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Entry curEntry = ((Entry)adapterView.getItemAtPosition(i));
                Intent in = new Intent(MainActivity.this, EditEntry.class);
                in.putExtra("id",curEntry.id);
                startActivity(in);
            }
        });
    }

    //Update Entries from database
    public void updateEntries(){
        this.entries = db.getAllEntries();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //Setup Options Menu
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_addEntry: //add Entry
                Intent in = new Intent(this, AddEntry.class);
                startActivityForResult(in,1);
                return true;
        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1){
            setupListView();}
    }
}
