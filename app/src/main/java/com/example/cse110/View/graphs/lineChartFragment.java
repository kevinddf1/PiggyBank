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
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class lineChartFragment extends Fragment {


    AnyChartView anyChartView;
    List<String> monthArrayList =new ArrayList<>();
    List<Double> monthExpenseArrayList = new ArrayList<>();
    List<Double> totalExpenseArrayList = new ArrayList<>();
    double  currentMonthExpense;

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
        GraphsActivity activity=(GraphsActivity) getActivity();
        totalExpenseArrayList=activity.getTotalExpenseArrayList();
        currentMonthExpense=getTotalMonthExpense();

        //need to build monthArrayList and monthExpenseArraylsit let's say 5 months
        monthArrayList.add("Jan");
        monthArrayList.add("Feb");
        monthArrayList.add("Mar");
        monthArrayList.add("Apr");
        monthArrayList.add("May");

        monthExpenseArrayList.add(310.00);
        monthExpenseArrayList.add(214.00);
        monthExpenseArrayList.add(389.40);
        monthExpenseArrayList.add(150.87);
        monthExpenseArrayList.add(currentMonthExpense);


        setuplineChart();
    }


    public double  getTotalMonthExpense(){
        double ret=0;
        for(int i=0; i<totalExpenseArrayList.size(); i++){
            ret+=totalExpenseArrayList.get(i);
        }
        return ret;
    }


    public void setuplineChart(){

        Cartesian cartesian = AnyChart.line();
        cartesian.title("Trend of Total Expenses per Month");
        cartesian.yAxis(0).title("expenses of each month");
        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");
        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i=0; i<monthArrayList.size(); i++){
            dataEntries.add(new ValueDataEntry(monthArrayList.get(i), monthExpenseArrayList.get(i)));
        }

        Line line = cartesian.line(dataEntries);

        anyChartView.setChart(cartesian);
    }
}
