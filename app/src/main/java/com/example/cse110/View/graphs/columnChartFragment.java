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
import com.anychart.core.cartesian.series.Column;
import com.example.cse110.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing the graphs for PiggyBank.
 * When user presses: See columnChart Graph, this page will appear.
 * @author Fan Ding
 * @version May 28
 *
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class columnChartFragment extends Fragment {


    /**
     * graph object
     */
    AnyChartView anyChartView;

    /**
     * String List of category names of current month
     */
    List<String> cateArrayList =new ArrayList<>();

    /**
     * List each category cost of current month
     */
    List<Double> cateCostArrayList = new ArrayList<>();

    /**
     * constructor
     */
    public columnChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_column_chart, container, false);
    }


    public void onViewCreated(View view,  Bundle savedInstanceState) {
        anyChartView = (AnyChartView) getView().findViewById(R.id.column_chart_view);

        /**
         * create an graphsActivity object to archive the arrayList we want to build the graph
         */
        GraphsActivity activity=(GraphsActivity) getActivity();
        cateArrayList=activity.getCateArrayList();
        cateCostArrayList =activity.getCateCostArrayList();

        setupColumnChart();
    }


    /**
     * set up ColumnChart with 2 arrayLists.
     * cateArrayList, which contains String List of category names of current month
     * cateCostArrayList, which contains each category cost of current month
     */
    public void setupColumnChart(){

        Cartesian cartesian = AnyChart.column();
        cartesian.title("Expenses for Each Category of This Month");
        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");
        cartesian.xAxis(0).title("Category");
        cartesian.yAxis(0).title("Expenses");

        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i=0; i<cateArrayList.size(); i++){
            dataEntries.add(new ValueDataEntry(cateArrayList.get(i), cateCostArrayList.get(i)));
        }

        Column column = cartesian.column(dataEntries);

        cartesian.getSeriesAt(0).name("$");

        anyChartView.setChart(cartesian);
    }
}
