package com.nutmeg.mydish;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Activity {
    ListView entryList;
    ArrayList<Entry> entries;

    ArrayList<Entry> filtered = new ArrayList<Entry>();
    DBHandler db;
    RelativeLayout searchBar;
    EntryAdapter entryAdapter;
    ImageView myDishGuy;
    TextView welcome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchBar = (RelativeLayout) findViewById(R.id.searchBox);
        welcome = (TextView) findViewById(R.id.welcome_message);
        myDishGuy = (ImageView) findViewById(R.id.myDishIcon);
        //Getting Database
        db = new DBHandler(this);
        db.open();

        setupListView();
        setupAndroidHelp();
        setupSearchBar();
    }

    //Set up List View for Entries
    public void setupListView(){
        updateEntries();
        filtered.clear();
        filtered.addAll(entries);
        entryList = (ListView) findViewById(R.id.entries);
        entryAdapter = new EntryAdapter(this,filtered);
        entryList.setAdapter(entryAdapter);
        entryAdapter.notifyDataSetChanged();

        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Entry curEntry = ((Entry)adapterView.getItemAtPosition(i));
                Intent in = new Intent(MainActivity.this, EditEntry.class);
                in.putExtra("id",curEntry.id);
                startActivityForResult(in, 1);
            }
        });

        entryList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete Entry?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Entry curEntry = ((Entry)adapterView.getItemAtPosition(i));
                                db.deleteEntryById(curEntry.id);
                                updateEntries();
                                filtered.clear();
                                filtered.addAll(entries);
                                entryAdapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
                return false;
            }
        });
    }

    public void setupSearchBar(){
        Button go = (Button) findViewById(R.id.search_go);
        Button cancel = (Button) findViewById(R.id.search_cancel);
        String raw = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("categories", "");
        ArrayList<String> categories;
        if (raw.equals("")){
             categories= new ArrayList<String>();
        }
        else{
            categories = new ArrayList<String>(Arrays.asList(raw.split("#")));
        }
        final AutoCompleteTextView search = (AutoCompleteTextView) findViewById(R.id.search_input);
        search.setThreshold(0);
        search.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_dropdown_item_1line, categories));
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.showDropDown();
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cat = search.getText().toString();
                filtered = db.getEntriesByCat(cat);
                refreshView();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtered.clear();
                filtered.addAll(db.getAllEntries());
                search.setText("");
                refreshView();
                searchBar.setVisibility(View.INVISIBLE);
                welcome.setVisibility(View.VISIBLE);
                myDishGuy.setVisibility(View.VISIBLE);
            }
        });
    }

    public void refreshView(){
        entryAdapter.clear();
        entryAdapter.addAll(filtered);
        entryAdapter.notifyDataSetChanged();
    }
    //Update Entries from database
    public void updateEntries(){
        this.entries = db.getAllEntries();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    //Setup Options Menu
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_addEntry: //add Entry
                Intent in = new Intent(this, AddEntry.class);
                startActivityForResult(in,1);
                return true;

            case R.id.action_search:
                if (searchBar.getVisibility() == View.VISIBLE){
                    searchBar.setVisibility(View.INVISIBLE);
                    welcome.setVisibility(View.VISIBLE);
                }
                else{
                    showSearchBar();
                }
        }

        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1){
            setupListView();}
    }

    public void showSearchBar(){
        searchBar.setVisibility(View.VISIBLE);
        welcome.setVisibility(View.INVISIBLE);
        myDishGuy.setVisibility(View.INVISIBLE);
        searchBar.setFocusable(true);

    }

    public void setupAndroidHelp(){
        ImageView android = (ImageView) findViewById(R.id.myDishIcon);
        TextView message = (TextView) findViewById(R.id.welcome_message);

        android.setOnClickListener(openHelp());
        message.setOnClickListener(openHelp());
    }

    //IMPLEMENT
    public View.OnClickListener openHelp(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperDialog(MainActivity.this).show();
            }
        };
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(MainActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
