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
 * A simple {@link Fragment} subclass.
 */
public class columnChartFragment extends Fragment {


    AnyChartView anyChartView;
    List<String> cateArrayList =new ArrayList<>();
    List<Double> totalExpenseArrayList = new ArrayList<>();

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
        GraphsActivity activity=(GraphsActivity) getActivity();
        cateArrayList=activity.getCateArrayList();
        totalExpenseArrayList=activity.getTotalExpenseArrayList();
        setupcolumnChart();
    }


    public void setupcolumnChart(){

        Cartesian cartesian = AnyChart.column();
        cartesian.title("Expenses for Each Category of This Month");
        cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");
        cartesian.xAxis(0).title("Category");
        cartesian.yAxis(0).title("Expenses");

        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i=0; i<cateArrayList.size(); i++){
            dataEntries.add(new ValueDataEntry(cateArrayList.get(i), totalExpenseArrayList.get(i)));
        }

        Column column = cartesian.column(dataEntries);

        anyChartView.setChart(cartesian);
    }
}
