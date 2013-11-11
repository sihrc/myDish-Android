package com.nutmeg.mydish;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by chris on 11/9/13.
 */
public class CategoryDialog extends AlertDialog {
    Context context;
    EditText search;
    ListView categoryList;
    ArrayList<Category> categories = new ArrayList<Category>();
    CategoryAdapter categoryAdapter;

    public CategoryDialog (Context context, ArrayList<Category> categories) {
        //Constructing the Dialog
        super(context);
        setContentView(R.layout.category_dialog);
        this.context = context;
        this.categories = categories;
    }

    public void onCreate(Bundle savedInstanceState){
        categoryList = (ListView) findViewById (R.id.category_list);
        categoryAdapter  = new CategoryAdapter(context,categories);
        categoryList.setAdapter(categoryAdapter);
        categoryList.setTextFilterEnabled(true);
        categoryList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        categoryList.setClickable(true);
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Category curCat = categories.get(i);
                if (curCat.checked){
                    curCat.uncheck();
                }
                else{
                    curCat.check();
                }
                updateAdapter();
            }
        });

        search = (EditText) findViewById(R.id.search_category);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //categoryAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        Button add = (Button) findViewById(R.id.add_category);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newCat = search.getText().toString();
                if (newCat.length() < 1){
                    Toast.makeText(context,"Give the category a name!",Toast.LENGTH_SHORT).show();
                }
                else {
                    Boolean exists = false;
                    for (Category cat: categories){
                        if (cat.name.equals(newCat)){
                            cat.check();
                            exists = true;
                            break;
                        }
                    }
                    if (!exists){
                        newCat = Character.toUpperCase(newCat.charAt(0)) + newCat.substring(1);
                        Category addCat = new Category(newCat);
                        addCat.check();
                        categories.add(addCat);
                    }
                    updateAdapter();
                }
            }
        });
    }

    public void updateAdapter(){
        categoryAdapter.notifyDataSetChanged();
    }

    public List<Category> getSelected(){
        return this.categories;
    }

    public void cancelCats(){
        for (int i = 0; i < categories.size(); i++){
            categories.get(i).uncheck();
        }
        updateAdapter();
    }

}