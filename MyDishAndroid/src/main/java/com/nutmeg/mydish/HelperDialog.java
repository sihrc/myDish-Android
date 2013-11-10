package com.nutmeg.mydish;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by chris on 11/9/13.
 */
public class HelperDialog extends AlertDialog {
    Context context;

    public HelperDialog(Context context) {
        //Constructing the Dialog
        super(context);
        setContentView(R.layout.helper_dialog);
        this.context = context;
    }

    public void onCreate(Bundle savedInstanceState){
        TextView add = (TextView) findViewById(R.id.helper_add);
        TextView share = (TextView) findViewById(R.id.helper_share);
        TextView edit = (TextView) findViewById(R.id.helper_edit);
        TextView search = (TextView) findViewById(R.id.helper_search);
        TextView ret = (TextView) findViewById(R.id.helper_return);

        add.setOnClickListener(getAdd());
        share.setOnClickListener(getShare());
        edit.setOnClickListener(getEdit());
        search.setOnClickListener(getSearch());
        ret.setOnClickListener(getReturn());

    }

    public View.OnClickListener getAdd(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Add an entry? Check out the Add button above me!", Toast.LENGTH_LONG).show();
                dismiss();
            }
        };
    }

    public View.OnClickListener getEdit(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Edit an entry? Tap one of the entries below me!", Toast.LENGTH_LONG).show();
                dismiss();
            }
        };
    }

    public View.OnClickListener getShare(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Share an entry? Tap one of the entries below me!", Toast.LENGTH_LONG).show();
                dismiss();
            }
        };
    }
    public View.OnClickListener getSearch(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Searching for entries? Check out the Search button above me!", Toast.LENGTH_LONG).show();
                dismiss();
            }
        };
    }

    public View.OnClickListener getReturn(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Oh okay. I'll always be here if you need me later!", Toast.LENGTH_LONG).show();
                dismiss();
            }
        };
    }
}