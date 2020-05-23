package com.example.cse110.Model.history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cse110.Controller.history.HistoryItem;
import com.example.cse110.R;


import java.util.ArrayList;

public class HistoryItemAdapter extends ArrayAdapter<HistoryItem> {

    /**
     * Primary data structure to hold the months to display
     */
    private ArrayList<HistoryItem> items;

    //Declare our TextViews to edit
    private TextView name;
    private TextView budget;
    private TextView totalExpenses;

    public HistoryItemAdapter(Context context, ArrayList<HistoryItem> items){
        super(context, 0 , items);
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
        name = convertView.findViewById(R.id.month_year_separate_display);
        String[] parseMonthYear = item.getMonthYear().split(" ");
        name.setText(getMonth(Integer.parseInt(parseMonthYear[0])) + " " + parseMonthYear[1] );
        budget = convertView.findViewById(R.id.total_budget_display);
        budget.setText("Budget: $" + formatIntMoneyString(item.getTotalBudget()));
        totalExpenses = convertView.findViewById(R.id.total_expense_display);
        totalExpenses.setText("Total Expenses: -$" + formatMoneyString(Double.toString(Double.parseDouble(item.getTotalExpenses())/100.00)));

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

    /**
     * Helper metho to find the correct month name to display
     * @param month
     * @return
     */
    private String getMonth(int month) {
        switch (month) {
            case 0:
                return "January";

            case 1:
                return "February";

            case 2:
                return "March";

            case 3:
                return "April";

            case 4:
                return "May";

            case 5:
                return "June";

            case 6:
                return "July";

            case 7:
                return "August";

            case 8:
                return "September";

            case 9:
                return "October";

            case 10:
                return "November";

            case 11:
                return "December";

            default:
                throw new IllegalStateException("Unexpected value: " + month);
        }
    }

}

