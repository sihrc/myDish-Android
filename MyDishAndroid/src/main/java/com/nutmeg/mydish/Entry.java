package com.nutmeg.mydish;

import java.util.List;

/**
 * Created by chris on 11/7/13
 */
public class Entry {
    String title,notes,date,recipe, categories, picture, id;

    public Entry (String title, String notes, String recipe, String date, String categories, String picture){
        this.title = title;
        this.notes = notes;
        this.date = date;
        this.recipe = recipe;
        this.categories = categories;
        this.picture = picture;
    }

    public void setDate(String value){
        this.date = value;
    }

    public void setPicture(String value){
        this.picture = value;
    }

    public void setCategories(List<CharSequence> values){
        StringBuilder sb = new StringBuilder();
        for (CharSequence value : values){
            sb.append(value);
            sb.append("#");
        }
        this.categories = sb.toString();
    }

    public void setId(String value){
        this.id = value;
    }
}
