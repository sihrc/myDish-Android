package com.nutmeg.mydish;

/**
 * Created by chris on 11/9/13.
 */
public class Category {
    String name;
    Boolean checked;
    public Category(String name){
        this.name = name;
        this.checked = false;
    }

    public void check(){
        this.checked = true;
    }

    public void uncheck(){
        this.checked = false;
    }
}
