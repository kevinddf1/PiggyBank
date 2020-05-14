package com.example.cse110.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cse110.Controller.HistoryDetailedItem;
import com.example.cse110.R;

import java.util.ArrayList;


/**
 * The adapter that contains the details for each expense in the History item.
 * @author Peter Gonzalez
 * @version 4.27
 */
public class HistoryDetailAdapter extends ArrayAdapter<HistoryDetailedItem> {

    final ArrayList<HistoryDetailedItem> itemsList;

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

        // Set the name of the expenses
        final HistoryDetailedItem currentItem = getItem(position);
        TextView expenseName = convertView.findViewById(R.id.expense_name);
        expenseName.setText(currentItem.getName());

        // Set the cost of the expense
        TextView expenseCost = convertView.findViewById(R.id.expense_cost);
        expenseCost.setText("$ " + formatMoneyString(currentItem.getCost()));
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
}
