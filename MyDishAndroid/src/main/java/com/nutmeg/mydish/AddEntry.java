package com.nutmeg.mydish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by chris on 11/7/13.
 */

public class AddEntry extends Activity {
    Button save, textSave, cancel, inputCategories, inputTakePicture;
    ImageView inputPicture;
    EditText title, recipe, notes;

    CategoryDialog catsDialog;

    ArrayList<String> urls;
    String picturePath;

    ArrayList<Category> categories = new ArrayList<Category>();
    DBHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addentry);
        urls =  new ArrayList<String>(Arrays.asList(getString(R.string.cuteAnimals).split(",")));
        getCategories();
        //Initializing the DBHandler
        db = new DBHandler(this);
        db.open();

        //Grabbing UI Elements
        save = (Button) findViewById(R.id.save);
        textSave = (Button) findViewById(R.id.textSave);
        cancel = (Button) findViewById(R.id.cancel);
        inputCategories = (Button) findViewById(R.id.inputCategories);
        inputTakePicture = (Button) findViewById(R.id.inputTakePicture);

        inputTakePicture.setText("Tap to take a picture!");
        inputPicture = (ImageView) findViewById(R.id.inputPicture);
        if (picturePath != null){
        new AsyncTask<Void, Void, Drawable>(){
            protected Drawable doInBackground(Void... voids){
                return LoadImageFromWebOperations(urls.get(new Random().nextInt(urls.size())));}
            protected void onPostExecute(Drawable draw){
                Toast.makeText(AddEntry.this,"Hooray! You got a kitty!",Toast.LENGTH_LONG).show();
                inputPicture.setImageDrawable(draw);
                inputPicture.invalidate();
            }
        }.execute();}

        title = (EditText) findViewById(R.id.inputTitle);
        recipe = (EditText) findViewById(R.id.inputRecipe);
        notes = (EditText) findViewById(R.id.inputNotes);

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
                Toast.makeText(AddEntry.this,"Recipe Options Coming Soon!",Toast.LENGTH_SHORT).show();
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
                                inputTakePicture.setText("Tap to retake picture!");
                                grabPicture();
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
                SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
                final String curDate = sdf.format(new Date(System.currentTimeMillis()));
                String input = notes.getText().toString();
                if (!input.equals("")){
                    input = input  + "   --" + curDate;
                }
                Entry newEntry = new Entry(
                                    title.getText().toString(),
                                    input,
                                    recipe.getText().toString(),
                                    String.valueOf(System.currentTimeMillis()),
                                    "",
                                    "");
                newEntry.setPicture(AddEntry.this.picturePath);
                List<CharSequence> selected = new ArrayList<CharSequence>();
                for (Category cat: categories){
                    if (cat.checked){
                        selected.add(cat.name);
                    }
                }
                newEntry.setCategories(selected);
                saveCategories();
                db.addEntry(newEntry);
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
        catsDialog = new CategoryDialog(AddEntry.this, categories);
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
            categories.add(new Category("Entree"));
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
        AddEntry.this.picturePath = urls.get(new Random().nextInt(urls.size()));
        new AsyncTask<Void, Void, Drawable>(){
            protected Drawable doInBackground(Void... voids){
                return LoadImageFromWebOperations(AddEntry.this.picturePath);}
            protected void onPostExecute(Drawable draw){
                Toast.makeText(AddEntry.this,"Hooray! You got a kitty!",Toast.LENGTH_LONG).show();
                inputPicture.setImageDrawable(draw);
                inputPicture.invalidate();
            }
        }.execute();
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(AddEntry.this);
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


