package com.example.blood_donation.fragments;

import android.app.ProgressDialog;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BloodStat extends Fragment {
    private DatabaseReference db_ref;

    private ProgressDialog pd;
    private PieChart pieChart;

    private LinkedHashMap<String, Integer> bloodMap ;
    private String[] bloodGroup;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.blood_chart_fragment, container, false);

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);

        pd.show();

        db_ref = FirebaseDatabase.getInstance().getReference();

        pieChart = view.findViewById(R.id.piechart);

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);

        bloodGroup = getResources().getStringArray(R.array.Blood_Group);

        bloodMap = new LinkedHashMap<>();

        for (String blood : bloodGroup){
            bloodMap.put(blood, 0);
        }

        onDrawOnChart();

        return view;
    }

    private void onDrawOnChart(){

        Query allposts = db_ref.child("users");

        allposts.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    for (DataSnapshot singlepost : dataSnapshot.getChildren()) {
                        User customUserData = singlepost.getValue(User.class);
                        String blood = bloodGroup[customUserData.getBloodGroup()];
                        bloodMap.put(blood, bloodMap.get(blood)+1);
                    }
                    onConfigPieChart();
                    pd.dismiss();
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

    private void onConfigPieChart(){
        List<PieEntry> yVals1 = new ArrayList<PieEntry>();
        for (Map.Entry<String, Integer> stringIntegerEntry : bloodMap.entrySet()) {
            if (stringIntegerEntry.getValue() != 0){
                yVals1.add( new PieEntry(stringIntegerEntry.getValue(), stringIntegerEntry.getKey()));
            }
        }

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "Blood Type");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setValueTextSize(20f);
        dataSet.setValueTextColor(R.color.primaryText);

        // instantiate pie data object now
        PieData pieData = new PieData(dataSet);

        Log.d("TAG", "onConfigPieChart: "+ dataSet.toString());
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Blood Groups");
        pieChart.animate();

        // update pie chart
        pieChart.invalidate();
    }
}
