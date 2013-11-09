package com.nutmeg.mydish;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import android.widget.Filter;

/**
 * Created by chris on 11/9/13.
 */
public class CategoryAdapter extends ArrayAdapter<Category> {
    Context context;
    private ArrayList<Category> originalItems = new ArrayList<Category>();
    ArrayList<Category> categories;

    Filter filter;
    private final Object mLock = new Object();

    public CategoryAdapter (Context context, ArrayList<Category>categories){
        super(context, R.layout.category_list_item, categories);
        this.context = context;
        this.categories = categories;
        cloneItems(categories);
    }
    private class CategoryHolder{
        TextView name;
        CheckBox checkBox;
    }

    @Override
    public int getCount() {
        synchronized(mLock) {
            return categories!=null ? categories.size() : 0;
        }
    }

    protected void cloneItems(ArrayList<Category> items) {
        for (Iterator iterator = items.iterator(); iterator
                .hasNext();) {
            Category cat = (Category) iterator.next();
            originalItems.add(cat);
        }
    }

    @Override
    public Category getItem(int item) {
        Category cat = null;
        synchronized(mLock) {
            cat = categories!=null ? categories.get(item) : null;
        }
        return cat;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        final CategoryHolder holder;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.category_list_item, parent, false);
            holder = new CategoryHolder();

            holder.name = (TextView) convertView.findViewById(R.id.catName);
            holder.checkBox = (CheckBox)convertView.findViewById(R.id.checkBox);

            convertView.setTag(holder);
        } else holder = (CategoryHolder) convertView.getTag();

        holder.name.setText(categories.get(position).name);
        holder.checkBox.setChecked(categories.get(position).checked);
        return convertView;
    }

    public Filter getFilter(){
        if (filter == null){
            filter = new CategoryFilter();
        }
        return filter;
    }

    private class CategoryFilter extends Filter{
        @Override
            protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (prefix == null || prefix.length() == 0){
                synchronized (mLock) {
                    results.values = originalItems;
                    results.count = originalItems.size();
                }
            } else {
                synchronized (mLock){
                    String prefixString = prefix.toString().toLowerCase();
                    final ArrayList<Category> filteredItems = new ArrayList<Category>();
                    final ArrayList<Category> localItems = new ArrayList<Category>();

                    localItems.addAll(originalItems);
                    final int count = localItems.size();

                    for (int i = 0; i < count; i++){
                        final Category item = localItems.get(i);
                        final String itemName = item.name.toLowerCase();

                        if (itemName.startsWith(prefixString)){
                            filteredItems.add(item);
                        } else {}
                    }
                    results.values = filteredItems;
                    results.count = filteredItems.size();
                }
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence prefix, FilterResults results) {
            synchronized (mLock){
                final ArrayList<Category> localItems = (ArrayList<Category>) results.values;
                notifyDataSetChanged();
                clear();

                for (Iterator iterator = localItems.iterator(); iterator.hasNext();){
                    Category cat = (Category) iterator.next();
                    add(cat);
                }
            }
        }
    }
}
