package com.example.cse110.Model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.cse110.Controller.HistoryCategoryItem;
import com.example.cse110.R;

import java.util.ArrayList;

/**
 * The adapter to connect the HistoryItems to the list display of the History page displays.
 * Uses the history_item.xml to determine the look of the information display as well as the data population of it.
 * @see ArrayAdapter
 */
public class HistoryCategoryAdapter extends ArrayAdapter<HistoryCategoryItem> {
    //Declare our arrayList
    ArrayList<HistoryCategoryItem> items;

    //Declare our TextViews to edit
    private TextView name, budget, totalExpenses;

    /**
     * The only constructor for the adapter, connects the adapter to HistoryItem ArrayList.
     * @param context The context in which the list is created.
     * @param items The data structure that holds the HistoryItem objects.
     */
    public HistoryCategoryAdapter(@NonNull Context context, ArrayList<HistoryCategoryItem> items) {
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_category_item, parent, false);
        }


        // Get the data item for this position
        final HistoryCategoryItem item = getItem(position);
        assert item != null;

        //Look up view for data population
        name = convertView.findViewById(R.id.category_name);
        name.setText(item.getName());
        budget = convertView.findViewById(R.id.history_budget);
        budget.setText("Budget: $" + formatIntMoneyString(Integer.toString(item.getBudget())));
        totalExpenses = convertView.findViewById(R.id.history_expenses);
        totalExpenses.setText("Total Expenses: -$" + formatMoneyString(item.getFormattedTotalExpenses()));

        return convertView;

    }

    /**
     * Helper method to format a display of money value, only integers
     * @param valueToFormat The String to manipulate
     * @return The new string to display
     */
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

    /**
     * Helper method to format a display of money value, including cents
     * @param valueToFormat The string to manipulate
     * @return The new string to display
     */
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
