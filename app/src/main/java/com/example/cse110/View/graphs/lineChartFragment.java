package com.example.cse110.View.graphs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.example.cse110.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A class representing the graphs for PiggyBank.
 * When user presses: See lineChart Graph, this page will appear.
 * @author Fan Ding
 * @version May 28
 *
 */


/**
 * A simple {@link Fragment} subclass.
 */
public class lineChartFragment extends Fragment {

    /**
     * chart object
     */
    AnyChartView anyChartView;


    /**
     * string contains all details of past data, Month, year, budgets, costs
     */
    private List<String> allMonths = new ArrayList<>();


    /**
     * String List of months, like [March, April, June]
     */
    private List<String> monthArrayList =new ArrayList<>();


    /**
     * List of total spent of each months
     */
    private List<Double> monthExpenseArrayList = new ArrayList<>();


    /**
     * Indices for pulling MONTH YEAR BUDGET EXPENSES from entries in allMonths
     */
    private static final int MONTH_INDEX = 0;
    private static final int YEAR_INDEX = 1;
    private static final int EXPENSES_INDEX = 3;

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
     * constructor
     */
    public lineChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_line_chart, container, false);
    }

    public void onViewCreated(View view,  Bundle savedInstanceState) {
        anyChartView = (AnyChartView) getView().findViewById(R.id.line_chart_view);

        //past allMonths from graphsActivity to This file
        GraphsActivity activity=(GraphsActivity) getActivity();
        allMonths=activity.getAllMonths();

        Collections.sort(allMonths);
        Collections.reverse(allMonths);

        setupArrayList();

        setupLineChart();
    }


    /**
     * allMonths contains string formatting like YEAR, MONTH, BUDGETS, COST.
     * need to separate them into small partitions and store month and budgets into 2 arrayList.
     */
    private void setupArrayList() {
        Collections.reverse(allMonths);
        //Go through all Strings representing (MONTH YEAR BUDGET EXPENSES(not as cents) and
        // convert to HistoryItem
        for (String currentMonth : allMonths) {
            //Break String into components: MONTH YEAR BUDGET EXPENSES
            String[] brokenDownString = currentMonth.split("-");

            //add the expenses and month to the 2 array 2 needed
            String month=getMonth(Integer.parseInt(brokenDownString[MONTH_INDEX]));
            String year= brokenDownString[YEAR_INDEX];
            monthArrayList.add(month+" "+year );
            monthExpenseArrayList.add(Double.parseDouble(brokenDownString[EXPENSES_INDEX])/100.00);
        }
    }


    /**
     * set up LineChart with 2 arrayLists.
     * monthArrayList, which contains String List of months
     * monthExpenseArrayList, which contains cost of each months.
     */
    public void setupLineChart(){

        Cartesian cartesian = AnyChart.line();
        cartesian.title("Trend of Total Expenses per Month");
        cartesian.yAxis(0).title("expenses of each month");
        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");
        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i=0; i<monthArrayList.size(); i++){
            dataEntries.add(new ValueDataEntry(monthArrayList.get(i), monthExpenseArrayList.get(i)));
        }

        Line line = cartesian.line(dataEntries);

        cartesian.getSeriesAt(0).name("$");
        anyChartView.setChart(cartesian);
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
