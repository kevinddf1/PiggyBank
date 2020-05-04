package com.example.cse110;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

class ExpenseListAdapter extends ArrayAdapter<Expense> {

    private static final String LOG_TAG = ExpenseListAdapter.class.getSimpleName();

    //Declare core elements that cause changes
    Button btnDelete, btnEdit;
    final ArrayList<Expense> itemsList;

    private Category category;

    // Constructor
    public ExpenseListAdapter(Context context, ArrayList<Expense> items, Category category) {
        super(context, 0, items);

        this.category = category;
        // Allow for class wide scope
        itemsList = items;
    }


    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_expense, parent, false);
        }

        final Expense item = getItem(position);
        TextView expenseName = listItemView.findViewById(R.id.expense_name);
        expenseName.setText(item.getName());
        TextView expenseCost = listItemView.findViewById(R.id.expense_cost);
        expenseCost.setText("$" + formatMoneyString(item.getCostAsString()));

        btnDelete = listItemView.findViewById(R.id.delete);
        btnDelete.setTag(position);
        btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (v.getTag() != null) {
                    // Remove item from MonthlyData and update adapter
                    category.deleteExpense(item.getId());
                    notifyDataSetChanged();
                }
            }
        });

        // Return the completed view to render on screen
        return listItemView;
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
