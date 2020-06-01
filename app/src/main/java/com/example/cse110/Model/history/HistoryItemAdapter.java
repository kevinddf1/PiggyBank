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
import com.example.cse110.Model.FormattingTool;

import java.util.ArrayList;
import java.util.Comparator;

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

    private static final int BEGIN_INDEX = 0;

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


    /**
     * Formatting tool to avoid redundant code.
     */
    private FormattingTool formattingTool = new FormattingTool();


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
        String renderBudget = "Budget: $" + formattingTool.formatIntMoneyString(item.getTotalBudget());
        budget.setText(renderBudget);

        //Render total expenses
        TextView totalExpenses = convertView.findViewById(R.id.total_expense_display);
        String expensesRendering = "Total Expenses: -$" + formattingTool.formatMoneyString(formattingTool.formatDecimal(Double.toString(Double.parseDouble(item.getTotalExpenses())/ DOUBLE)));
        totalExpenses.setText(expensesRendering);
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



