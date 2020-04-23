package com.example.cse110;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class CategoriesListAdapter extends ArrayAdapter<Category> {
    //Declare core elements that cause changes
    Button btnDelete, btnEdit;
    final ArrayList<Category> itemsList;

    private MonthlyData monthlyData;

    // Constructor
    public CategoriesListAdapter(Context context, ArrayList<Category> items, MonthlyData monthlyData) {
        super(context, 0, items);

        this.monthlyData = monthlyData;
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
        final Category item = getItem(position);
        assert item != null;

        // Lookup view for data population
        TextView categoryName = convertView.findViewById(R.id.category_name);
        TextView categoryBudget = convertView.findViewById(R.id.category_budget);
        categoryName.setText(item.getName());
        categoryBudget.setText(item.getBudgetAsString());

        // Create buttons to delete row or edit category
        btnDelete = convertView.findViewById(R.id.delete);

        //Set event handler for delete item
        btnDelete.setTag(position);
        btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (v.getTag() != null) {
                    // Remove item from MonthlyData and update adapter
                    monthlyData.deleteCategory(item.getName());
                    itemsList.remove(item);
                    notifyDataSetChanged();
                }
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

}
