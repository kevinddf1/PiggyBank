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
/**
 * The adapter that contains the details for each month in the History.
 * @author Peter Gonzalez
 * @version 5.23
 */
public class HistoryItemAdapter extends ArrayAdapter<HistoryItem> {

    /**
     * Data structure to hold all items
     */
    private ArrayList<HistoryItem> items;

    private static final int MONTH_INDEX = 0;
    private static final int YEAR_INDEX = 1;
    private static final double DOUBLE = 100.00;
    private static final int DISTANCE_FROM_MILLIONS_COMMA = 9;
    private static final int DISTANCE_FROM_THOUSANDS_COMMA = 6;
    private static final int LENGTH_LESS_THAN_THOUSANDS = 6;
    private static final int LENGTH_LESS_THAN_MILLIONS = 9;
    private static final int BEGIN_INDEX = 0;
    private static final int DISTANCE_FROM_MILLIONS_COMMA_NO_DECIMAL = 6;
    private static final int DISTANCE_FROM_THOUSANDS_COMMA_NO_DECIMAL = 3;
    private static final int LENGTH_LESS_THAN_THOUSANDS_NO_DECIMAL = 3;
    private static final int LENGTH_LESS_THAN_MILLIONS_NO_DECIMALS = 6;
    private static final int JANUARY = 0;
    private static final int FEBRUARY = 1;
    private static final int MARCH = 2;
    private static final int APRIL = 3;
    private static final int MAY = 4;
    private static final int JUNE = 5;
    private static final int JULY = 6;
    private static final int AUGUST = 7;
    private static final int SEPTEMBER = 8;
    private static final int OCTOBER = 9;
    private static final int NOVEMBER = 10;
    private static final int DECEMBER = 11;
    private static final int CORRECT_DECIMAL = 2;
    private static final int TOO_SHORT_DECIMAL = 1;
    private static final int MISSING_DECIMAL = -1;

    /**
     * Only constructor bridges front-end ListView w/ backend HistoryItem
     * @param context The class this is instantiated in
     * @param items The items to be represented in the list
     */
    public HistoryItemAdapter(Context context, ArrayList<HistoryItem> items){
        super(context, BEGIN_INDEX, items);
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

        // Identify the current month being rendered
        final HistoryItem item = getHistoryItem(position);

        //Render all the information belonging to this current month
        renderStaticInfo(convertView, item);

        //Return manipulated view
        return convertView;

    }

    /**
     * Identify the current month we are displaying
     * @param position The index of the month in the list
     * @return A HistoryItem w/ all the information pertaining to the associated month
     */
    private HistoryItem getHistoryItem(int position) {
        // Get the data item for this position
        final HistoryItem item = getItem(position);
        assert item != null; //error checking
        return item;
    }

    /**
     * Render the name of the month, budget and expenses
     * @param convertView The newly formatted view to render information
     * @param item The History containing all the correct information
     */
    private void renderStaticInfo(View convertView, HistoryItem item) {
        //Render name of the month and year
        //Declare our TextViews to edit
        TextView name = convertView.findViewById(R.id.month_year_separate_display);
        String[] parseMonthYear = item.getMonthYear().split(" ");
        String monthRendering = getMonth(Integer.parseInt(parseMonthYear[MONTH_INDEX])) + " " + parseMonthYear[YEAR_INDEX];
        name.setText(monthRendering);

        //Render budget of the month
        TextView budget = convertView.findViewById(R.id.total_budget_display);
        String renderBudget = "Budget: $" + formatIntMoneyString(item.getTotalBudget());
        budget.setText(renderBudget);

        //Render total expenses
        TextView totalExpenses = convertView.findViewById(R.id.total_expense_display);
        String expensesRendering = "Total Expenses: -$" + formatMoneyString(formatDecimal(Double.toString(Double.parseDouble(item.getTotalExpenses())/ DOUBLE)));
        totalExpenses.setText(expensesRendering);
    }

    /**
     * Helper method to format a display of money value, only integers
     * @param valueToFormat The String to manipulate
     * @return The new string to display
     */
    private String formatMoneyString(String valueToFormat){
        int thousandsComma = valueToFormat.length() - DISTANCE_FROM_THOUSANDS_COMMA;
        int millionsComma = valueToFormat.length() - DISTANCE_FROM_MILLIONS_COMMA;
        if(valueToFormat.length() <= LENGTH_LESS_THAN_THOUSANDS){
            return valueToFormat;
        }else if(valueToFormat.length() <= LENGTH_LESS_THAN_MILLIONS){
            return valueToFormat.substring(BEGIN_INDEX, thousandsComma) + "," + valueToFormat.substring(thousandsComma);
        }

        return valueToFormat.substring(BEGIN_INDEX, millionsComma) + "," + valueToFormat.substring(millionsComma , thousandsComma) + "," + valueToFormat.substring(thousandsComma );
    }

    /**
     * Helper method to format a display of money value, including cents
     * @param valueToFormat The string to manipulate
     * @return The new string to display
     */
    private String formatIntMoneyString(String valueToFormat){
        int hundredthComma = valueToFormat.length() - DISTANCE_FROM_THOUSANDS_COMMA_NO_DECIMAL;
        int millionsComma = valueToFormat.length() - DISTANCE_FROM_MILLIONS_COMMA_NO_DECIMAL;

        if (valueToFormat.length() <= LENGTH_LESS_THAN_THOUSANDS_NO_DECIMAL){
            return  valueToFormat;
        }else if (valueToFormat.length() <= LENGTH_LESS_THAN_MILLIONS_NO_DECIMALS){
            return valueToFormat.substring(BEGIN_INDEX, hundredthComma) + "," + valueToFormat.substring(hundredthComma);
        }
        return valueToFormat.substring(BEGIN_INDEX, millionsComma) + "," + valueToFormat.substring(millionsComma , hundredthComma) + "," + valueToFormat.substring(hundredthComma );
    }

    /**
     * Helper method to format the rendering in regards to decimal places
     * @param valueToFormat The string to fix decimal placement
     * @return The formatted string
     */
    private String formatDecimal(String valueToFormat){
        String costString = valueToFormat;

        // Add formatting for whole numbers
        if(costString.indexOf('.') == MISSING_DECIMAL){
            costString = costString.concat(".00");
        }else{
            //Ensure only valid input
            int costLength = costString.length();
            int decimalPlace = costString.indexOf(".");

            // If the user inputs a number formatted as "<num>.", appends a 00 after the decimal
            if (costLength - decimalPlace == TOO_SHORT_DECIMAL) {
                costString = costString.substring(BEGIN_INDEX, decimalPlace + 1) +  "00";
            }
            // If the user inputs a number formatted as "<num>.1", where 1 could be any number,
            // appends a 0 to the end
            else if (costLength - decimalPlace == CORRECT_DECIMAL) {
                costString = costString.substring(BEGIN_INDEX, decimalPlace + CORRECT_DECIMAL) + "0";
            }
            // If the user inputs a number with >= 2 decimal places, only displays up to 2
            else {
                costString = costString.substring(BEGIN_INDEX, costString.indexOf(".") + CORRECT_DECIMAL + 1);
            }
        }

        return costString;

    }

    /**
     * Helper method to find the correct month name to display
     * @param month The month as an int
     * @return The month as a string
     */
    private String getMonth(int month) {
        switch (month) {
            case JANUARY:
                return "January";

            case FEBRUARY:
                return "February";

            case MARCH:
                return "March";

            case APRIL:
                return "April";

            case MAY:
                return "May";

            case JUNE:
                return "June";

            case JULY:
                return "July";

            case AUGUST:
                return "August";

            case SEPTEMBER:
                return "September";

            case OCTOBER:
                return "October";

            case NOVEMBER:
                return "November";

            case DECEMBER:
                return "December";

            default:
                throw new IllegalStateException("Unexpected value: " + month);
        }
    }

}

