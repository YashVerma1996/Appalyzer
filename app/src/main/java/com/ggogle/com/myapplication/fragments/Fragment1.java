
package com.ggogle.com.myapplication.fragments;


import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import com.ggogle.com.myapplication.R;

import android.graphics.Color;
import android.util.Log;

import com.ggogle.com.myapplication.Utility;
import com.ggogle.com.myapplication.database.AppContract;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Highlight;
import com.github.mikephil.charting.utils.ValueFormatter;



import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Fragment1 extends Fragment implements OnChartValueSelectedListener {

    public PieChart mChart;
    Typeface mTf;

    ArrayList<String> xVals;
    ArrayList<Entry> yVals1;
     public static long TUT;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = inflater.inflate(R.layout.activity_pie, container, false);
        TUT = Utility.getTotalUsageTime(getActivity());
        setChartProps(r);
        getGraphPoints();
        return r;
    }

    public void getGraphPoints() {
        xVals = new ArrayList<>();
        yVals1 = new ArrayList<>();
        long AUT;
        float percentage;
        float percentOfOthers = 0;
        int i = 0;
        Cursor cursor = getActivity().getContentResolver().query(AppContract.AppEntry.contentUri,
                new String[]{AppContract.AppEntry.APP_NAME, AppContract.AppEntry.DURATION},
                AppContract.AppEntry.DATE+"=?",
                new String[]{Utility.getSelectedDate()}, null);

        while (cursor.moveToNext()) {
Log.e("LOOP","DD");
            AUT = cursor.getLong(1);
            percentage = (float) ((AUT * 10000) / TUT);                                         //AUT=APP USAGE TIME
            percentage /= 100;

            Log.e("Percentage", " " + cursor.getLong(1) + " " + percentage + " " + cursor.getString(0));                                                               //TUT=TOTAL USAGE TIME
            if (percentage > 4.9) {
                xVals.add(cursor.getString(0));
                yVals1.add(new BarEntry(percentage, i));
                i++;
            } else {
                percentOfOthers += percentage;
            }
        }
        cursor.close();
        if (percentOfOthers > 3.0) {
            xVals.add("Others");
            yVals1.add(new BarEntry(percentOfOthers, i));
        }


        PieDataSet setR = new PieDataSet(yVals1, "");
        setR.setColors(colors);
        ArrayList<PieDataSet> dataSetsPie = new ArrayList<>();
        dataSetsPie.add(setR);


        PieData data = new PieData(xVals, setR);
        data.setValueTextSize(9f);
        data.setValueTextColor(Color.parseColor("#000000"));
        data.setValueTypeface(mTf);
        data.setValueFormatter(new MyValueFormatter());


        mChart.setData(data);

    }

    ArrayList<Integer> colors = new ArrayList<>();

    public void setChartProps(View r) {
        mChart = (PieChart) r.findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDescription("");
        mChart.animateXY(3000, 3000);
        mChart.setCenterText("App Stats");
        mTf = Typeface.create("sans-serif-thin", 1);
        mChart.setCenterTextTypeface(mTf);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
    }


    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (e == null)
            return;

    }

    public void onNothingSelected() {
    }


    public class MyValueFormatter implements ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,###,##0.0");
        }

        @Override
        public String getFormattedValue(float value) {
            return mFormat.format(value) + " %";
        }

    }


}
