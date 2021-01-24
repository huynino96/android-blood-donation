package com.example.blood_donation.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.blood_donation.R;
import com.example.blood_donation.model.User;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserStat extends Fragment implements OnChartValueSelectedListener {
    private ProgressDialog pd;
    DatabaseReference db_ref;
    private Calendar calendar;
    private int cur_month, cur_year , cur_day;
    private LinkedHashMap<String, Integer> dateMap ;
    private ArrayList<String>dateStrList ;
    private com.github.mikephil.charting.charts.CombinedChart mChart;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bar_chart_fragment, container, false);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        calendar = Calendar.getInstance();
        cur_month = calendar.get(Calendar.MONTH) + 1;
        cur_year = calendar.get(Calendar.YEAR);

        db_ref = FirebaseDatabase.getInstance().getReference();

        mChart = (com.github.mikephil.charting.charts.CombinedChart) view.findViewById(R.id.combinedChart);
        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setHighlightFullBarEnabled(false);
        mChart.setOnChartValueSelectedListener(this);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);

        onDrawOnChart();

        return view;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Toast.makeText(getContext(), "Value: "
                + e.getY()
                + ", index: "
                + h.getX()
                + ", DataSet index: "
                + h.getDataSetIndex(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }

    private void onDrawOnChart(){
        dateStrList = new ArrayList<>();
        dateMap = new LinkedHashMap<>();

        Query allposts = db_ref.child("users");

        allposts.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    for (DataSnapshot singlepost : dataSnapshot.getChildren()) {
                        User customUserData = singlepost.getValue(User.class);
                        dateStrList.add(customUserData.getDate());
                    }
                    getDateMap();
                    try {
                        viewOneMonth();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Database is empty now!",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.d("User", databaseError.getMessage());

            }
        });
    }

    private void getDateMap(){
        Collections.sort(dateStrList, new Comparator<String>() {
            @SuppressLint("SimpleDateFormat")
            final DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
            @Override
            public int compare(String o1, String o2) {
                try {
                    return f.parse(o1).compareTo(f.parse(o2));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });


        for(String dateStr:dateStrList){
            if (dateMap.containsKey(dateStr)){
                dateMap.put(dateStr,dateMap.get(dateStr) + 1);
            }
            else {
                dateMap.put(dateStr,1);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void viewOneMonth() throws ParseException {
        LinkedHashMap<String, Integer> dateOfMonth = new LinkedHashMap<>();
        @SuppressLint("SimpleDateFormat") final DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        // Getting an iterator
        for (Map.Entry<String, Integer> stringIntegerEntry : dateMap.entrySet()) {

            Date date = f.parse((String) stringIntegerEntry.getKey());
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int month = localDate.getMonthValue();
            int year = localDate.getYear();
            String day = Integer.toString(localDate.getDayOfMonth());

            //CURRENT YEAR
            if (year == Calendar.getInstance().get(Calendar.YEAR) && month == cur_month){
                if (dateOfMonth.containsKey(day)){
                    dateOfMonth.put(day, dateOfMonth.get(day) + stringIntegerEntry.getValue());
                }
                else {
                    dateOfMonth.put(day, stringIntegerEntry.getValue());
                }
            }
        }
        appendDataOnChart(dateOfMonth);
    }

    private void appendDataOnChart(LinkedHashMap<String, Integer> dateOfMonth){
        final List<String> xLabel = new ArrayList<>();
        for (Map.Entry<String, Integer> stringIntegerEntry : dateOfMonth.entrySet()){
            xLabel.add(stringIntegerEntry.getKey());
        }

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel.get((int) value % xLabel.size());
            }
        });

        CombinedData data = new CombinedData();
        LineData lineDatas = new LineData();
        lineDatas.addDataSet((ILineDataSet) dataChart(dateOfMonth));

        data.setData(lineDatas);

        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        mChart.setData(data);
        mChart.invalidate();
    }


    private static DataSet dataChart(LinkedHashMap<String, Integer> dateOfMonth) {

        LineData d = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        int index = 0;
        for (Map.Entry<String, Integer> stringIntegerEntry : dateOfMonth.entrySet()){
            entries.add(new Entry(index, stringIntegerEntry.getValue()));
            index++;
        }


        LineDataSet set = new LineDataSet(entries, "This month");
        set.setColor(Color.GREEN);
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.GREEN);
        set.setCircleRadius(5f);
        set.setFillColor(Color.GREEN);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.GREEN);

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return set;
    }
}
