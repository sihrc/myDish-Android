package com.nutmeg.mydish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

    ArrayList<Category> categories;
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
    }

    //Camera Activity
    private View.OnClickListener takePicture(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Are you sure?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                Entry newEntry = new Entry(
                                    title.getText().toString(),
                                    notes.getText().toString(),
                                    recipe.getText().toString(),
                                    String.valueOf(Calendar.getInstance().get(Calendar.MONTH)) +
                                    "/" + String.valueOf(Calendar.getInstance().get(Calendar.DATE)) + "/" +
                                    String.valueOf(Calendar.getInstance().get(Calendar.YEAR)),
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
        getCategories();
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
                stringBuilder.append(cat.name);
                stringBuilder.append(", ");
            }
        }
        if (stringBuilder.length() > 30)
            inputCategories.setText(new String(stringBuilder).substring(0,27) + "...");
        else
            inputCategories.setText(stringBuilder.toString());
    }
    void getCategories(){
        String raw = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("categories", "");
        categories = new ArrayList<Category>();
        if (raw.equals("")){
            categories.add(new Category("Lunch"));
        }
        else{
            for (String cat: Arrays.asList(raw.split("#"))){
                categories.add(new Category(cat));
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
            protected void onPreExecute(){
                Toast.makeText(AddEntry.this,"Loading image ... ", Toast.LENGTH_SHORT).show();
            }
            protected Drawable doInBackground(Void... voids){
                return LoadImageFromWebOperations(AddEntry.this.picturePath);}
            protected void onPostExecute(Drawable draw){
                Toast.makeText(AddEntry.this,"Hooray! You got a kitty!",Toast.LENGTH_LONG).show();
                inputPicture.setImageDrawable(draw);
                inputPicture.invalidate();
            }
        }.execute();
    }


}


