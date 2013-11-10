package com.nutmeg.mydish;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
public class ShareDialog extends AlertDialog {
    Context context;

    public ShareDialog(Context context) {
        //Constructing the Dialog
        super(context);
        setContentView(R.layout.share_dialog);
        this.context = context;
    }

    public void onCreate(Bundle savedInstanceState){
        Button cancel = (Button) findViewById(R.id.share_cancel);
        Button email = (Button) findViewById(R.id.email);
        Button facebook = (Button) findViewById(R.id.facebook);
        Button twitter = (Button) findViewById(R.id.twitter);
        Button instagram = (Button) findViewById(R.id.instagram);

        cancel.setOnClickListener(click("not to share!"));
        email.setOnClickListener(click("Email"));
        facebook.setOnClickListener(click("Facebook"));
        twitter.setOnClickListener(click("Twitter"));
        instagram.setOnClickListener(click("Instagram"));
    }

    View.OnClickListener click(final String choice){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"You've chosen " + choice + "! Nice choice.",Toast.LENGTH_SHORT).show();
                dismiss();
            }
        };
    }

}