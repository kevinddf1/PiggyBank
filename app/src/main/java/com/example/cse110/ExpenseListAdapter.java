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
    Context context;

    // Constructor
    public ExpenseListAdapter(Context context, ArrayList<Expense> items, Category category) {
        super(context, 0, items);

        this.category = category;
        // Allow for class wide scope
        itemsList = items;
        this.context = context;


    }


    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

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



                    //Allow the updating of the expense list activity
                    String totalExpenseString = Double.toString(category.getTotalExpenses()/100);
                    ((ExpensesListActivity)context).updateTotalExpenseDisplay("$" + formatMoneyString(totalExpenseString));

                    //Add fine tuning on expense Display
                    notifyDataSetChanged();
                }
            }
        });

        // Return the completed view to render on screen
        return listItemView;
    }

    private String formatMoneyString(String valueToFormat){

        // Add formatting for whole numbers
        if(valueToFormat.indexOf('.') == -1){
            valueToFormat = valueToFormat.concat(".00");
        }else{
            //Ensure only valid input
            int costLength = valueToFormat.length();
            int decimalPlace = valueToFormat.indexOf(".");

            // If the user inputs a number formatted as "<num>.", appends a 00 after the decimal
            if (costLength - decimalPlace == 1) {
                valueToFormat = valueToFormat.substring(0, decimalPlace + 1) +  "00";
            }
            // If the user inputs a number formatted as "<num>.1", where 1 could be any number,
            // appends a 0 to the end
            else if (costLength - decimalPlace == 2) {
                valueToFormat = valueToFormat.substring(0, decimalPlace + 1 + 1) + "0";
            }
            // If the user inputs a number with >= 2 decimal places, only displays up to 2
            else {
                valueToFormat = valueToFormat.substring(0, valueToFormat.indexOf(".") + 2 + 1);
            }
        }
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
