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
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.cse110.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing the graphs for PiggyBank.
 * When user presses: See PieChart Graph, this page will appear.
 * @author Fan Ding
 * @version May 28
 *
 */


/**
 * A simple {@link Fragment} subclass.
 */
public class pieChartFragment extends Fragment {

    /**
     * graph object
     */
    private AnyChartView anyChartView;

    /**
     * String List of category names of current month
     */
    private List<String> cateArrayList =new ArrayList<>();

    /**
     * Each category cost of current month
     */
    private List<Double> cateCostArrayList = new ArrayList<>();


    /**
     * constructor
     */
    public pieChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pie_chart, container, false);
    }

    public void onViewCreated(View view,  Bundle savedInstanceState) {
        anyChartView = (AnyChartView) getView().findViewById(R.id.pie_chart_view);

        /**
         * create an graphsActivity object to archive the arrayList we want to build the graph
         */
        GraphsActivity activity=(GraphsActivity) getActivity();
        cateArrayList=activity.getCateArrayList();
        cateCostArrayList =activity.getCateCostArrayList();

        setupPieChart();
    }


    /**
     * set up pieChart with 2 arrayLists.
     * cateArrayList, which contains String List of category names of current month
     * cateCostArrayList, which contains each category cost of current month
     */
    public void setupPieChart(){

        Pie pie= AnyChart.pie();

        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i=0; i<cateArrayList.size(); i++){
            dataEntries.add(new ValueDataEntry(cateArrayList.get(i), cateCostArrayList.get(i)));
        }

        pie.data(dataEntries);

        pie.title("weight of different categories");

        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("categories")
                .padding(0d, 0d, 10d, 0d);

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        anyChartView.setChart(pie);
    }

}
