package com.example.cse110;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class Adapter extends ArrayAdapter<ExpenseItem> {

    //Declare core elements that cause changes
    Button btnDelete, btnEdit;
    final ArrayList<ExpenseItem> itemsList;

    // Constructor
    public Adapter(Context context, ArrayList<ExpenseItem> items) {
        super(context, 0, items);

        //Allow for class wide scope
        itemsList = items;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_expense, parent, false);
        }

        // Get the data item for this position
        final ExpenseItem item = getItem(position);
        assert item != null;
        item.setPosition(position);

        // Lookup view for data population
        TextView expenseName = convertView.findViewById(R.id.ExpenseName);
        TextView expenseCost = convertView.findViewById(R.id.ExpenseCost);
        final EditText category = convertView.findViewById(R.id.Category);

        // Create buttons to delete row or edit category
        btnDelete = convertView.findViewById(R.id.delete);
        btnEdit = convertView.findViewById(R.id.update_category);

        // Set Event Handler for editing category
        btnEdit.setTag(position);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getTag() != null){
                    if (!category.getText().toString().isEmpty()) {
                        //Update Expense Item object
                        item.setCategory(category.getText().toString());

                        //Provide Confirmation
                        Toast confirmation = Toast.makeText(getContext(), "Category has been updated.", Toast.LENGTH_SHORT);
                        confirmation.show();
                    }else{

                        //Warning of no new category
                        Toast warning = Toast.makeText(getContext(), "No category was specified", Toast.LENGTH_SHORT);
                        warning.show();

                        //Set item's category to default
                        item.setCategory("other");
                    }

                }
            }
        });

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
