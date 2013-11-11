package com.nutmeg.mydish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * Created by chris on 11/7/13.
 */

public class EditEntry extends Activity {
    Button save, textSave, cancel, inputCategories, inputTakePicture;
    ImageView inputPicture;
    EditText title, recipe, notes;

    CategoryDialog catsDialog;
    Entry curEntry;

    ArrayList<String> urls;
    String picturePath;

    ArrayList<Category> categories = new ArrayList<Category>();
    DBHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addentry);
        Intent in = getIntent();
        String id = in.getStringExtra("id");

        urls =  new ArrayList<String>(Arrays.asList(getString(R.string.cuteAnimals).split(",")));
        //Initializing the DBHandler
        db = new DBHandler(this);
        db.open();
        curEntry = db.getEntryById(id);
        getCategories();
        this.picturePath = curEntry.picture;

        //Grabbing UI Elements
        save = (Button) findViewById(R.id.save);
        textSave = (Button) findViewById(R.id.textSave);
        cancel = (Button) findViewById(R.id.cancel);
        inputCategories = (Button) findViewById(R.id.inputCategories);
        inputTakePicture = (Button) findViewById(R.id.inputTakePicture);

        inputPicture = (ImageView) findViewById(R.id.inputPicture);
        if (picturePath != null){
            new AsyncTask<Void, Void, Drawable>(){
                protected Drawable doInBackground(Void... voids){
                    return LoadImageFromWebOperations(curEntry.picture);}
                protected void onPostExecute(Drawable draw){
                    Toast.makeText(EditEntry.this,"Just a sec! Kitty loading...",Toast.LENGTH_SHORT).show();
                    inputPicture.setImageDrawable(draw);
                    inputPicture.invalidate();
                }
            }.execute();}

        title = (EditText) findViewById(R.id.inputTitle);
        recipe = (EditText) findViewById(R.id.inputRecipe);
        notes = (EditText) findViewById(R.id.inputNotes);

        setUpInputCategory();

        title.setText(curEntry.title);
        recipe.setText(curEntry.recipe);
        notes.setText(curEntry.notes);

        setupButtons();
    }
    private void setupButtons(){
        save.setOnClickListener(save());
        textSave.setOnClickListener(save());

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();            }
        });
        inputPicture.setOnClickListener(takePicture());
        inputTakePicture.setOnClickListener(takePicture());
        inputCategories.setOnClickListener(selectCategories());
        recipe.setOnClickListener(insertRecipe());
    }
    public View.OnClickListener insertRecipe(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditEntry.this,"Recipe Options Coming Soon!",Toast.LENGTH_SHORT).show();
            }
        };
    }

    //Camera Activity
    private View.OnClickListener takePicture(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Are you sure?")
                        .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                /*ContentValues values = new ContentValues();
                                String filename = String.valueOf(System.currentTimeMillis()/1000L);
                                values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + filename + ".png");
                                imageUri = getContentResolver().insert(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(intent, 1);*/

                                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivity(intent);*/

                                grabPicture();
                                Toast.makeText(EditEntry.this,"Hooray! You're getting a kitty!",Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do nothing.
                    }
                }).show();
            }
        };

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1){
            grabPicture();}
    }

    //Save
    private View.OnClickListener save(){
        return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Long curTime = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
                    String curDate = sdf.format(new Date(curTime));
                    curEntry.title = title.getText().toString();
                    if (!curEntry.notes.equals(notes.getText().toString())){
                        curEntry.notes = notes.getText().toString() + "   --" + curDate;
                    } else {
                        curEntry.notes = notes.getText().toString();
                    }
                    Log.i("Date",curDate);
                    curEntry.recipe = recipe.getText().toString();

                    db.updateEntry(curEntry);
                    finish();
                }
            };
    }

    //Select Categories
    private View.OnClickListener selectCategories(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategoriesDialog();
            }
        };
    }

    //Categories Dialog with List of Categories
    protected void showCategoriesDialog(){
        catsDialog = new CategoryDialog(EditEntry.this, categories);
        Button saveCats = (Button) catsDialog.findViewById(R.id.saveCats);
        Button textSaveCats = (Button) catsDialog.findViewById(R.id.textSaveCats);
        Button cancelCats = (Button)  catsDialog.findViewById(R.id.cancelCats);
        Button textCancelCats = (Button)  catsDialog.findViewById(R.id.textCancelCats);

        saveCats.setOnClickListener(saveCats());
        textSaveCats.setOnClickListener(saveCats());
        cancelCats.setOnClickListener(resetCats());
        textCancelCats.setOnClickListener(resetCats());

        catsDialog.show();
    }
    protected View.OnClickListener saveCats(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeCategories();
                saveCategories();
                catsDialog.dismiss();
            }
        };
    }
    protected View.OnClickListener resetCats(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                catsDialog.cancelCats();
            }
        };
    }
    protected void onChangeCategories() {
        StringBuilder stringBuilder = new StringBuilder();

        for(Category cat : catsDialog.getSelected()){
            if (cat.checked){
                stringBuilder.append(Character.toUpperCase(cat.name.charAt(0)));
                stringBuilder.append(cat.name.substring(1));
                stringBuilder.append(", ");
            }
        }
        if (stringBuilder.length() > 30)
            inputCategories.setText(new String(stringBuilder).substring(0,27) + "...");
        else if (stringBuilder.length() > 1){
            String text = stringBuilder.toString();
            inputCategories.setText(text.substring(0, text.length() - 2));}
        else {
            inputCategories.setText("Tap to select categories!");
        }
    }
    void getCategories(){
        String raw = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("categories", "");
        if (raw.equals("")){
            categories.add(new Category("Lunch"));
        }
        else{
            for (String cat: Arrays.asList(raw.split("#"))){
                Boolean existing = false;
                for (Category cat2 :categories){
                    if (cat2.name.equals(cat)){
                        existing = true;
                    }
                }
                if (!existing){
                    categories.add(new Category(cat));
                }
            }
        }
    }
    void saveCategories(){
        StringBuilder sb = new StringBuilder();
        for (Category cat: categories){
            sb.append(cat.name);
            sb.append("#");
        }
        getSharedPreferences("PREFERENCE",MODE_PRIVATE).edit().putString("categories",sb.toString()).commit();
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public void grabPicture(){
        EditEntry.this.picturePath = urls.get(new Random().nextInt(urls.size()));
        new AsyncTask<Void, Void, Drawable>(){
            protected Drawable doInBackground(Void... voids){
                return LoadImageFromWebOperations(EditEntry.this.picturePath);}
            protected void onPostExecute(Drawable draw){
                inputPicture.setImageDrawable(draw);
                inputPicture.invalidate();
            }
        }.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    //Setup Options Menu
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_share: //add Entry
                showShareDialog();
                return true;
        }
        return true;
    }

    public void setUpInputCategory(){
        String raw = curEntry.categories.replace("#",",");
        Log.i("RawString",raw);
        if (raw.length() > 30)
            inputCategories.setText(raw.substring(0,27) + "...");
        else if (raw.length() > 1)
            inputCategories.setText(raw.substring(0,raw.length() - 1));
        else
            inputCategories.setText("Tap to add categories!");
        for (String cat: raw.split(",")){
            for (Category cat2: categories){
                if (cat2.name.equals(cat)){
                    cat2.check();
                }
            }
        }
    }
    public void showShareDialog(){
        new ShareDialog(EditEntry.this).show();
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            Log.i("HI I'M VIEW", view.toString());
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(EditEntry.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
                Log.i("I DID THIS ONCE", "I DID THIS ONCE");
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}


