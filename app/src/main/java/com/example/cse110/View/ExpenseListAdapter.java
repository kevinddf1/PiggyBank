package com.example.cse110.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.cse110.Controller.Category;
import com.example.cse110.Controller.Expense;
import com.example.cse110.Model.FormattingTool;
import com.example.cse110.R;

import java.util.ArrayList;

/**
 * The adapter handles interactions between the frontend and backend.
 *
 * @author Peter Thuycam
 */
class ExpenseListAdapter extends ArrayAdapter<Expense> {

    private static final String LOG_TAG = ExpenseListAdapter.class.getSimpleName();
    private static final double DOUBLE = 100.00;

    //Declare core elements that cause changes
    private Button btnDelete;
    private final ArrayList<Expense> itemsList;

    /**
     * Formatting tool to avoid redundancies
     */
    private final FormattingTool formattingTool = new FormattingTool();
    private final Category category;
    private final Context context;


    /**
     * Custom adapter allows for specific renderings for expenses and handles frontend <-> backend interactions.
     *
     * @param context  The context in which this adapter is being used.
     * @param items    The data structure to hold all the expenses to display
     * @param category The current category to which these expenses belong
     */
    public ExpenseListAdapter(Context context, ArrayList<Expense> items, Category category) {
        super(context, 0, items);

        // Allow for class wide scope
        this.category = category;
        itemsList = items;
        this.context = context;
    }


    /**
     * Converts views for individual Expense items to render all pertinent information.
     *
     * @param position    The position of the expense in the list.
     * @param convertView The view to modify and return.
     * @param parent      The parent file from which the adapter will attach to
     * @return listItemView The modified view to render all the expense item's components.
     */
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        //Initialize xml files and front end rendering
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_expense, parent, false);
        }

        //Identify the expense the user is currently being shown
        final Expense item = getItem(position);

        //Render static info such as expense name and cost
        assert item != null;
        renderStaticInfo(listItemView, item);

        //Initialize the delete button and user clicks on deleting an expense
        deleteButtonAndHandler(position, listItemView, item);

        // Return the completed view to render on screen
        return listItemView;
    }

    /**
     * Initialize the delete button and handle the user deleting an expense.
     *
     * @param position     The position of the expense in the list.
     * @param listItemView The front-end rendering to display.
     * @param item         The current Expense object.
     */
    private void deleteButtonAndHandler(int position, View listItemView, final Expense item) {

        //Attach to xml file
        btnDelete = listItemView.findViewById(R.id.delete);
        btnDelete.setTag(position);

        //Handle user presses
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() != null) {
                    // Remove item from MonthlyData and update adapter
                    category.deleteExpense(item.getId());

                    //Allow the updating of the expense list activity
                    double calculatedRemainder = (double) category.getTotalExpenses() / DOUBLE;
                    String totalExpenseString = Double.valueOf(calculatedRemainder).toString();

                    ((ExpensesListActivity) context).updateTotalExpenseDisplay("$" + formattingTool.formatMoneyString(formattingTool.formatDecimal(totalExpenseString)));

                    //Add fine tuning on expense Display
                    notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * Render non-changing info such as name and cost.
     *
     * @param listItemView The front-end rendering to display.
     * @param item         The current Expense object we are modifying.
     */
    private void renderStaticInfo(View listItemView, Expense item) {
        TextView expenseName = listItemView.findViewById(R.id.expense_name);
        expenseName.setText(item.getName());
        TextView expenseCost = listItemView.findViewById(R.id.expense_cost);
        expenseCost.setText("$" + formattingTool.formatMoneyString(formattingTool.formatDecimal(item.getCostAsString())));
    }


}
