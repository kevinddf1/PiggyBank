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
import com.example.cse110.R;

import java.util.ArrayList;


/**
 * The adapter that contains the details for each expense in the History item.
 * @author Peter Gonzalez
 * @version 4.27
 */
public class HistoryDetailAdapter extends ArrayAdapter<HistoryDetailedItem> {

    private static final int DISTANCE_FROM_MILLIONS_COMMA = 9;
    private static final int DISTANCE_FROM_THOUSANDS_COMMA = 6;
    private static final int LENGTH_LESS_THAN_THOUSANDS = 6;
    private static final int LENGTH_LESS_THAN_MILLIONS = 9;
    private static final int BEGIN_INDEX = 0;
    private static final int CORRECT_DECIMAL = 2;
    private static final int TOO_SHORT_DECIMAL = 1;
    private static final int MISSING_DECIMAL = -1;
    private final ArrayList<HistoryDetailedItem> itemsList;

    /**
     * The only constructor to instantiate a HistoryDetailAdapter. Requires an ArrayList of HistoryDetailedItems.
     * @param context The ListView context into which to represent the information.
     * @param items The ArrayList of HistoryDetailedItem to hold all the information necessary.
     */
    public HistoryDetailAdapter(Context context, ArrayList<HistoryDetailedItem> items){
        super(context, 0, items);
        itemsList = items;
    }

    /**
     * Converts the ListView items to the customized HistoryDetailedItem specified in the layout resource file.
     * @param position The position of the item in the list.
     * @param convertView The customized view to represent the detailed item.
     * @param parent The parent ListView.
     * @return The custom view with all the instantiated information.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.history_detail_item, parent, false);
        }
        //Render category, total budget, expenses
        renderStaticInfo(position, convertView);

        return convertView;


    }

    /**
     * Render all the information of the current categories expenses
     * @param position The position of the item in the listView
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
        String expenseRendering = "$ " + formatMoneyString(formatDecimal(currentItem.getCost()));
        expenseCost.setText(expenseRendering);
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
     * Helper method to format the rendering in regards to decimal places
     * @param valueToFormat The string to add decimals too
     * @return The formatted string w/ correct decmal places
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

}
