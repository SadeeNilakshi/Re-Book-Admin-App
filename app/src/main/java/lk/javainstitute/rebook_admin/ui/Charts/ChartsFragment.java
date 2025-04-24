package lk.javainstitute.rebook_admin.ui.Charts;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lk.javainstitute.rebook_admin.R;

public class ChartsFragment extends Fragment {

    private PieChart pieChart;
    private BarChart barChart;
    private FirebaseFirestore db;
    private Button buttonCatCount, buttonWishlisted, buttonMostBuying;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);

        buttonCatCount = view.findViewById(R.id.buttonCatCount);
        buttonWishlisted = view.findViewById(R.id.buttonWish);
        buttonMostBuying = view.findViewById(R.id.button2);
        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);


        db = FirebaseFirestore.getInstance();


        buttonWishlisted.setOnClickListener(v -> {
            loadWishlistedChartData();
        });

        buttonCatCount.setOnClickListener(v -> {
            loadCategoryChartData();
        });

        buttonMostBuying.setOnClickListener(v -> {
            loadMostBoughtCategoryData();
        });


        return view;
    }

    private void loadCategoryChartData() {
        db.collection("product").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Integer> categoryCountMap = new HashMap<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String category = document.getString("category");
                    if (category != null) {
                        categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
                    }
                }

                displayPieChart(categoryCountMap, "Category Distribution");
            }
        });
    }

    private void loadWishlistedChartData() {
        db.collection("wishlist").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Integer> wishlistCountMap = new HashMap<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String category = document.getString("category");
                    if (category != null) {
                        wishlistCountMap.put(category, wishlistCountMap.getOrDefault(category, 0) + 1);
                    }
                }

                displayPieChart(wishlistCountMap, "Most Wishlisted Categories");
            }
        });
    }

    private void loadMostBoughtCategoryData() {
        db.collection("product").whereEqualTo("status", "sold").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Integer> soldCategoryMap = new HashMap<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String category = document.getString("category");
                    if (category != null) {
                        soldCategoryMap.put(category, soldCategoryMap.getOrDefault(category, 0) + 1);
                    }
                }

                displayBarChart(soldCategoryMap);
            }
        });
    }

    private void displayPieChart(Map<String, Integer> dataMap, String centerText) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
            pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, centerText);
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(16f);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setCenterText(centerText);
        pieChart.setCenterTextColor(Color.WHITE);
        pieChart.setCenterTextSize(18f);
        pieChart.animateY(2000);
        pieChart.invalidate();
    }

    private void displayBarChart(Map<String, Integer> dataMap) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
            barEntries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Most Bought Categories");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextSize(16f);
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.getDescription().setText("Most Bought Categories");
        barChart.getDescription().setTextColor(Color.WHITE);
        barChart.animateY(2000);
        barChart.invalidate();
    }
}
