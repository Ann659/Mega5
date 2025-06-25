package com.example.mega.supabase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mega.R;
import com.example.mega.models.Category1;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private List<Category1> categories;

    public CategoryAdapter(Context context, List<Category1> categories) {
        this.context = context;
        this.categories = categories;
    }

    @Override
    public int getCount() { return categories.size(); }

    @Override
    public Object getItem(int position) { return categories.get(position); }

    @Override
    public long getItemId(int position) { return categories.get(position).getId(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        }
        Category1 category = categories.get(position);

        TextView name = convertView.findViewById(R.id.categoryName);
        name.setText(category.getName());
        return convertView;
    }
}
