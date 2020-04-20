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

class ExpenseListAdapter extends ArrayAdapter<ExpenseItem> {

    private static final String LOG_TAG = ExpenseListAdapter.class.getSimpleName();

    //Declare core elements that cause changes
    Button btnDelete, btnEdit;
    final ArrayList<ExpenseItem> itemsList;

    // Constructor
    public ExpenseListAdapter(Context context, ArrayList<ExpenseItem> items) {
        super(context, 0, items);

        //Allow for class wide scope
        itemsList = items;
    }


    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView==  null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_expense, parent, false);
        }

        ExpenseItem currentCategory = getItem(position);
        TextView categoryName = listItemView.findViewById(R.id.category_name);
        categoryName.setText(currentCategory.getCategory());

        // Return the completed view to render on screen
        return listItemView;
    }


}
