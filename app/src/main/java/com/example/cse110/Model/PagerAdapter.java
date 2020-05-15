package com.example.cse110.Model;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.cse110.View.columnChartFragment;
import com.example.cse110.View.lineChartFragment;
import com.example.cse110.View.pieChartFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    private int numOfTabs;


    public PagerAdapter( FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs=numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0:
                return new pieChartFragment();
            case 1:
                return new columnChartFragment();
            case 2:
                return new lineChartFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
