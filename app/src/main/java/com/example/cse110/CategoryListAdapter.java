package com.example.cse110;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoryListAdapter extends ArrayAdapter<CategoryItem> {

    //Declare core elements that cause changes
    Button btnDelete, btnEdit;
    final ArrayList<CategoryItem> itemsList;

    // Constructor
    public CategoryListAdapter(Context context, ArrayList<CategoryItem> items) {
        super(context, 0, items);

        //Allow for class wide scope
        itemsList = items;
    }


    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_category, parent, false);
        }

        // Get the data item for this position
        final CategoryItem item = getItem(position);
        assert item != null;
        item.setPosition(position);

        // Lookup view for data population
        TextView expenseName = convertView.findViewById(R.id.ExpenseName);
        TextView expenseCost = convertView.findViewById(R.id.ExpenseCost);
        TextView category = convertView.findViewById(R.id.Category);
        category.setText(item.getCategory());

        // Create buttons to delete row or edit category
        btnDelete = convertView.findViewById(R.id.delete);


        //Set event handler for delete item
        btnDelete.setTag(position);
        btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(v.getTag() != null){

                    //Remove Expense Item and update adapter
                    itemsList.remove((int)v.getTag());
                    notifyDataSetChanged();
                }
            }
        });

        // Populate the data into the template view using the data object
        expenseName.setText(item.getExpenseName());
        expenseCost.setText(item.getExpenseCost());

        // Return the completed view to render on screen
        return convertView;
    }

}
