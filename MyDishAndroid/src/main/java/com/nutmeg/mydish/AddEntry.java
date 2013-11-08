package com.nutmeg.mydish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by chris on 11/7/13.
 */
public class AddEntry extends Activity {
    Button save, textSave, cancel, inputCategories, inputTakePicture;
    ImageView inputPicture;
    EditText title, recipe, notes;

    ArrayList<String> urls;
    String picturePath;

    ArrayList<CharSequence> selectedCategories = new ArrayList<CharSequence>();
    ArrayList<CharSequence> categories = new ArrayList<CharSequence>();

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
                newEntry.setCategories(selectedCategories);

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
    protected void showCategoriesDialog() {
        getCategories();
        boolean[] checked = new boolean[categories.size()];

        for(int i = 0; i < categories.size(); i++)
            checked[i] = selectedCategories.contains(categories.get(i));

        DialogInterface.OnMultiChoiceClickListener categoryDialogListener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which, boolean isChecked) {
                if(isChecked){
                    if (which == categories.size() - 1){
                        final EditText addCategory = new EditText(AddEntry.this);

                        new AlertDialog.Builder(AddEntry.this)
                                .setTitle("Add A new Category")
                                .setView(addCategory)
                                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String newCategory = addCategory.getText().toString();
                                        if (newCategory.length() < 1) {
                                            Toast.makeText(AddEntry.this,"Give the category a name!",Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }

                                        if (!AddEntry.this.categories.contains(newCategory)){
                                            AddEntry.this.categories.add(newCategory);
                                            selectedCategories.add(categories.get(which - 1));
                                        }
                                        else
                                        {
                                            Toast.makeText(AddEntry.this, "This category already exists",Toast.LENGTH_SHORT).show();
                                            selectedCategories.add(newCategory);
                                        }


                                        //Save to preference
                                        getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                                                .edit()
                                                .putString("categories", getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("categories", "") + newCategory + "#")
                                                .commit();

                                        onChangeCategories();
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();
                    }}
                else
                    selectedCategories.remove(categories.get(which));;

                onChangeCategories();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Categories");
        CharSequence[] newSeq = categories.toArray(new CharSequence[categories.size()]);
        builder.setMultiChoiceItems(newSeq, checked, categoryDialogListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Get categories from shared preferences
    void getCategories(){
        String raw = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("categories", "");
        if (raw.equals("")){
            raw = "Other#";
        }
        categories = new ArrayList<CharSequence>(Arrays.asList(raw.split("#")));
        categories.add("Other");
    }

    //When Categories changed do this to refresh the button text
    protected void onChangeCategories() {
        StringBuilder stringBuilder = new StringBuilder();

        for(CharSequence category : selectedCategories)
            stringBuilder.append(category);
            stringBuilder.append(",");
        if (stringBuilder.length() > 30)
            inputCategories.setText(new String(stringBuilder).substring(0,27) + "...");
        else
            inputCategories.setText(stringBuilder.toString());
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


