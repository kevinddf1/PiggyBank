package com.example.cse110;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * The adapter to connect the HistoryItems to the list display of the History page displays.
 * Uses the history_item.xml to determine the look of the information display as well as the data population of it.
 * @see ArrayAdapter
 */
public class HistoryItemAdapter extends ArrayAdapter<HistoryItem> {
    //Declare our arrayList
    ArrayList<HistoryItem> items;

    //Declare our TextViews to edit
    private TextView name, budget, totalExpenses;

    /**
     * The only constructor for the adapter, connects the adapter to HistoryItem ArrayList.
     * @param context The context in which the list is created.
     * @param items The data structure that holds the HistoryItem objects.
     */
    public HistoryItemAdapter(@NonNull Context context, ArrayList<HistoryItem> items) {
        super(context,0,  items);
        this.items = items;

    }


    /**
     * The View converter to create the display for the History item.
     * @param position The position of the view in the adapter.
     * @param convertView The view that will be converted to show all necessary information.
     * @param parent The ViewGroup of the parent view.
     * @return The new modified view containing all pertinent information.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_item, parent, false);
        }


        // Get the data item for this position
        final HistoryItem item = getItem(position);
        assert item != null;

        //Look up view for data population
        name = convertView.findViewById(R.id.category_name);
        name.setText(item.getName());
        budget = convertView.findViewById(R.id.budget);
        budget.setText("Budget: $" + formatIntMoneyString(Integer.toString(item.getBudget())));
        totalExpenses = convertView.findViewById(R.id.Categories);
        totalExpenses.setText("Total Expenses: -$" + formatMoneyString(item.getFormattedTotalExpenses()));

        return convertView;

    }

    private String formatMoneyString(String valueToFormat){
        int hundredthComma = valueToFormat.length() - 6;
        int thousandthComma = valueToFormat.length() - 9;
        if(valueToFormat.length() <= 6){
            return valueToFormat;
        }else if(valueToFormat.length() <= 9){
            return valueToFormat.substring(0, hundredthComma) + "," + valueToFormat.substring(hundredthComma);
        }

        return valueToFormat.substring(0, thousandthComma) + "," + valueToFormat.substring(thousandthComma , hundredthComma) + "," + valueToFormat.substring(hundredthComma );
    }

    private String formatIntMoneyString(String valueToFormat){
        int hundredthComma = valueToFormat.length() - 3;
        int thousandthComma = valueToFormat.length() - 6;

        if (valueToFormat.length() <= 3){
            return  valueToFormat;
        }else if (valueToFormat.length() <= 6){
            return valueToFormat.substring(0, hundredthComma) + "," + valueToFormat.substring(hundredthComma);
        }
        return valueToFormat.substring(0, thousandthComma) + "," + valueToFormat.substring(thousandthComma , hundredthComma) + "," + valueToFormat.substring(hundredthComma );
    }

    }
