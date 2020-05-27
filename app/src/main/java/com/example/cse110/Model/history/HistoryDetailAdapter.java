package com.example.cse110.Model.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cse110.Controller.history.HistoryDetailedItem;
import com.example.cse110.Model.FormattingTool;
import com.example.cse110.R;

import java.util.ArrayList;


/**
 * The adapter that contains the details for each expense in the History item.
 *
 * @author Peter Gonzalez
 * @version 4.27
 */
public class HistoryDetailAdapter extends ArrayAdapter<HistoryDetailedItem> {

    private final ArrayList<HistoryDetailedItem> itemsList;

    /**
     * Formatting tool to avoid redundancies.
     */
    FormattingTool formattingTool = new FormattingTool();

    /**
     * The only constructor to instantiate a HistoryDetailAdapter. Requires an ArrayList of HistoryDetailedItems.
     *
     * @param context The ListView context into which to represent the information.
     * @param items   The ArrayList of HistoryDetailedItem to hold all the information necessary.
     */
    public HistoryDetailAdapter(Context context, ArrayList<HistoryDetailedItem> items) {
        super(context, 0, items);
        itemsList = items;
    }

    /**
     * Converts the ListView items to the customized HistoryDetailedItem specified in the layout resource file.
     *
     * @param position    The position of the item in the list.
     * @param convertView The customized view to represent the detailed item.
     * @param parent      The parent ListView.
     * @return The custom view with all the instantiated information.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_detail_item, parent, false);
        }
        //Render category, total budget, expenses
        renderStaticInfo(position, convertView);

        return convertView;


    }

    /**
     * Render all the information of the current categories expenses
     *
     * @param position    The position of the item in the listView
     * @param convertView The modified view
     */
    private void renderStaticInfo(int position, @Nullable View convertView) {
        // Set the name of the expenses
        final HistoryDetailedItem currentItem = getItem(position);
        TextView expenseName = convertView.findViewById(R.id.expense_name);
        assert currentItem != null;
        expenseName.setText(currentItem.getName());

        // Set the cost of the expense
        TextView expenseCost = convertView.findViewById(R.id.expense_cost);
        String expenseRendering = "$ " + formattingTool.formatMoneyString(formattingTool.formatDecimal(currentItem.getCost()));
        expenseCost.setText(expenseRendering);
    }


}
