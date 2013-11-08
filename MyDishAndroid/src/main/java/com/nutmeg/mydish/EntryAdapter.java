package com.nutmeg.mydish;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by chris on 11/7/13.
 */
public class EntryAdapter extends ArrayAdapter {
    Context context;
    List<Entry> entries;
    ArrayList<String> urls = new ArrayList<String>(Arrays.asList(getContext().getString(R.string.cuteAnimals).split(",")));
    public EntryAdapter (Context context, List<Entry> entries){
        super(context, R.layout.entry_list_item, entries);
        this.context = context;
        this.entries =  entries;
    }

    private class PostHolder{
        TextView title, notes, date;
        ImageView picture;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        final PostHolder holder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.entry_list_item, parent, false);
            holder = new PostHolder();

            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.notes = (TextView) convertView.findViewById(R.id.notes);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.picture = (ImageView) convertView.findViewById(R.id.picture);

            convertView.setTag(holder);
        } else holder = (PostHolder) convertView.getTag();

        final Entry entry = this.entries.get(position);

        holder.title.setText(entry.title);
        holder.notes.setText(entry.notes);
        holder.date.setText(entry.date);


        new AsyncTask<Void, Void, Drawable>(){
            protected void onPreExecute(){
                Toast.makeText(getContext(), "Loading image ... ", Toast.LENGTH_SHORT).show();
            }
            protected Drawable doInBackground(Void... voids){
                return LoadImageFromWebOperations(entry.picture);}
            protected void onPostExecute(Drawable draw){
                holder.picture.setImageDrawable(draw);
            }
        }.execute();
        return convertView;
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

}

