package com.example.cse110.Model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.cse110.Controller.Category;
import com.example.cse110.Controller.MonthlyData;
import com.example.cse110.R;
import com.example.cse110.View.CategoriesListActivity;

import java.util.ArrayList;

/**
 * The adapter handles interactions between frontend and backend when displaying/modifying a user's categories.
 *
 * @author Peter Thuycam Minxuan
 */
public class CategoriesListAdapter extends ArrayAdapter<Category> {
    //Declare core elements that cause changes
    private Button btnDelete;
    /**
     * Allows us to pull most up to date info from the database.
     */
    private final Database base = Database.getInstance(); // create a Database object

    /**
     * Static info to display
     */
    private TextView categoryName;
    private final MonthlyData monthlyData;

    /**
     * Primary data structure to check for duplicate naming
     */
    private final ArrayList<Category> itemsList;

    //Allow for global scope
    private final Context context;

    /**
     * Formatting tool to avoid redundancies.
     */
    private final FormattingTool formattingTool = new FormattingTool();

    /**
     * Custom adapter allows for specific renderings for categories and handles frontend <-> backend interactions.
     *
     * @param context     The context in which this adapter is being used.
     * @param items       The data structure to hold all the categories to display.
     * @param monthlyData The current month to which these categories belong.
     */
    public CategoriesListAdapter(Context context, ArrayList<Category> items, MonthlyData monthlyData) {
        super(context, 0, items);

        //Set global context
        this.itemsList = items;
        this.monthlyData = monthlyData;
        this.context = context;
    }


    /**
     * Converts views for individual Category items to render all pertinent information.
     *
     * @param position    The position of the category in the list.
     * @param convertView The view to modify and return.
     * @param parent      The parent file from which the adapter will attach to.
     * @return listItemView The modified view to render all the Category item's components.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_category, parent, false);
        }

        // Get the data item for this position
        final Category item = getItem(position);
        assert item != null;

        //Render static info like name and budget of the category
        renderStaticInfo(convertView, item);

        //Set up delete button and handle user clicks
        initializeDeleteButtonAndHandler(position, convertView, item);

        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * Initialize the delete button and handle the user deleting a Category.
     *
     * @param position    The position of the expense in the list.
     * @param convertView The front-end rendering to display.
     * @param item        The current Expense object.
     */
    private void initializeDeleteButtonAndHandler(int position, View convertView, final Category item) {
        // Create buttons to delete row or edit category
        btnDelete = convertView.findViewById(R.id.delete_category);
        //Set event handler for DELETE item
        btnDelete.setTag(position);

        //Set up a onclick listener -- MINXUAN
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() != null) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                    alertDialog.setTitle("Confirm Deletion");
                    alertDialog.setMessage("Delete category \"" + item.getName() + "\"?");
                    alertDialog.setIcon(R.drawable.delete);

                    alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //your deleting code
                            // Remove item from MonthlyData and update adapter
                            String toDelete = item.getName();
                            monthlyData.deleteCategory(item.getName());
                            itemsList.remove(item);
                            notifyDataSetChanged();
                            ((CategoriesListActivity) context).confirmDeletion(toDelete);
                            dialog.dismiss();


                        }
                    });
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.create().show();
                }
            }
        });
    }

    /**
     * Render static info such as category name and budget.
     *
     * @param convertView The view to modify.
     * @param item        The current category the user is dealing with.
     */
    private void renderStaticInfo(View convertView, Category item) {
        // Lookup view for data population
        categoryName = convertView.findViewById(R.id.category_name);
        categoryName.setText(item.getName());

        //Budget rendering
        TextView categoryBudget = convertView.findViewById(R.id.category_budget);
        String budgetRendering = "Budget: " + "$" + formattingTool.formatIntMoneyString(item.getBudgetAsString());
        categoryBudget.setText(budgetRendering);
    }


}

