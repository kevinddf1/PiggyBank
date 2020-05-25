package com.example.cse110.Model.history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.cse110.Model.FormattingTool;
import androidx.annotation.NonNull;

import com.example.cse110.Controller.history.HistoryCategoryItem;
import com.example.cse110.R;

import java.text.Normalizer;
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
     * Helper method to avoid redundancies.
     */
    private FormattingTool formattingTool = new FormattingTool();

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

        //Render all displays
        renderStaticInfo(convertView, item);

        return convertView;

    }

    /**
     * Displays all attributes for a category
     * @param convertView
     * @param item
     */
    private void renderStaticInfo(View convertView, HistoryCategoryItem item) {
        //Look up view for data population
        //Render name
        name = convertView.findViewById(R.id.category_name);
        name.setText(item.getName());

        //Render budget
        budget = convertView.findViewById(R.id.history_budget);
        String budgetRender ="Budget: $" + formattingTool.formatIntMoneyString(Integer.toString(item.getBudget()));
        budget.setText(budgetRender);

        //Render total expenses
        totalExpenses = convertView.findViewById(R.id.history_expenses);
        String expenseRender = "Total Expenses: -$" + formattingTool.formatMoneyString(item.getFormattedTotalExpenses());
        totalExpenses.setText(expenseRender);
    }



}
