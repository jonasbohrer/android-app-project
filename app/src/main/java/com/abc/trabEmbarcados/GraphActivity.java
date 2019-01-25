package com.abc.trabEmbarcados;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class GraphActivity extends AppCompatActivity {

    private ArrayList<Registro> alimentos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_graphs);
        alimentos = (ArrayList<Registro>) getIntent().getSerializableExtra("alimentos");
        if (alimentos != null && alimentos.size() > 0) {
            Collections.reverse(alimentos);

            Graph1d();
            Graph7d();
            Graph30d();
        } else {
            Toast.makeText(this, "Nenhum registro adicionado!", Toast.LENGTH_SHORT).show();
        }
    }

    private void Graph7d() {
        GraphView graph = (GraphView) findViewById(R.id.graph1);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(), thresholdseries = new LineGraphSeries<>();
        ArrayList<DataPoint> plotseries= new ArrayList<DataPoint>();
        String name; Date date = null; double calories, i=0, threshold=450, totalCalories = 0; Integer quantity; Date lastDate = null;

        for(Registro entry : alimentos) {
            name = entry.name;
            date = new Date(entry.date);
            calories = new Double(entry.calories);
            quantity = entry.quantity;

            if (lastDate == null) {
                totalCalories = quantity*calories + totalCalories;
                Date finalDate = new Date(new SimpleDateFormat("MM/dd/yyyy 12:00:00").format(date.getTime()));
                plotseries.add(new DataPoint(finalDate, totalCalories));
                lastDate = date;
                i++;
            } else if (lastDate.getDay() == date.getDay()) {
                totalCalories = quantity*calories + totalCalories;
                plotseries.remove(i);
                Date finalDate = new Date(new SimpleDateFormat("MM/dd/yyyy 12:00:00").format(lastDate.getTime()));
                plotseries.add(new DataPoint(finalDate, totalCalories));
            } else {
                Date finalDate = new Date(new SimpleDateFormat("MM/dd/yyyy 12:00:00").format(date.getTime()));
                plotseries.add(new DataPoint(finalDate, quantity*calories));
                totalCalories = quantity*calories;
                i++;
                if (i >= 7) {break;}
                lastDate = date;
            }
        }

        Collections.reverse(plotseries);
        for (DataPoint entry: plotseries) {
            series.appendData( entry, true, 99, true);
            thresholdseries.appendData( new DataPoint(entry.getX(), threshold), true, 99, true);
        }

        series.setDrawDataPoints(true);
        series.setDataPointsRadius(5);
        series.setDrawBackground(true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
        series.setCustomPaint(paint);

        thresholdseries.setColor(Color.RED);

        graph.addSeries(thresholdseries);
        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this.getBaseContext(), new SimpleDateFormat("d")));
        graph.getGridLabelRenderer().setNumHorizontalLabels(7); // only 4 because of the space

        graph.getViewport().setXAxisBoundsManual(true);
        Date minx = new Date(new SimpleDateFormat("MM/dd/yyyy 00:00:00").format(plotseries.get(0).getX()));
        graph.getViewport().setMinX(minx.getTime());
        Date maxx = new Date(new SimpleDateFormat("MM/dd/yyyy 23:59:59").format(plotseries.get(plotseries.size()-1).getX()));
        graph.getViewport().setMaxX(maxx.getTime());

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);

        graph.getGridLabelRenderer().setHumanRounding(true);

        graph.getGridLabelRenderer().setHorizontalAxisTitle("Dia");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Kcals");

        graph.getViewport().setScrollable(true);
    }

    private void Graph1d() {
        GraphView graph = (GraphView) findViewById(R.id.graph0);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(), thresholdseries = new LineGraphSeries<>();
        ArrayList<DataPoint> plotseries= new ArrayList<DataPoint>();
        String name; Date date = null; double calories, i=0, threshold = 200; Integer quantity; Date lastDate = null;

        for(Registro entry : alimentos) {
            name = entry.name;
            date = new Date(entry.date);
            calories = new Double(entry.calories);
            quantity = entry.quantity;

            if (lastDate == null) {
                plotseries.add(new DataPoint(date, quantity*calories));
                lastDate = date;
            } else if (lastDate.getDay() == date.getDay()) {
                plotseries.add(new DataPoint(date, quantity*calories));
            } else {
                plotseries.add(new DataPoint(date, quantity*calories));
                break;
            }
        }

        Collections.reverse(plotseries);
        for (DataPoint entry: plotseries) {
            series.appendData( entry, true, 99, true);
            thresholdseries.appendData( new DataPoint(entry.getX(), threshold), true, 99, true);
            System.out.println(entry.toString());
        }

        series.setDrawDataPoints(true);
        series.setDataPointsRadius(5);
        series.setDrawBackground(true);

        thresholdseries.setColor(Color.RED);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
        series.setCustomPaint(paint);

        graph.addSeries(thresholdseries);
        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this.getBaseContext(), new SimpleDateFormat("H")));
        graph.getGridLabelRenderer().setNumHorizontalLabels(6); // only 4 because of the space

        Date minx = null;
        if (plotseries.size() > 1) {
            minx = new Date(new SimpleDateFormat("MM/dd/yyyy 00:00:00").format(plotseries.get(1).getX()));
        } else {
            minx = new Date(new SimpleDateFormat("MM/dd/yyyy 00:00:00").format(plotseries.get(0).getX()));
        }

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(minx.getTime());
        Date maxx = new Date(new SimpleDateFormat("MM/dd/yyyy 23:59:59").format(plotseries.get(plotseries.size()-1).getX()));
        graph.getViewport().setMaxX(maxx.getTime());

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);

        graph.getGridLabelRenderer().setHumanRounding(true);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Hora");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Kcals");



        //graph.setTitle("Calorias no último dia");
    }

    private void Graph30d() {
        GraphView graph = (GraphView) findViewById(R.id.graph2);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(), thresholdseries = new LineGraphSeries<>();
        ArrayList<DataPoint> plotseries= new ArrayList<DataPoint>();
        String name; Date date = null; double calories, i=0, threshold = 450, totalCalories = 0; Integer quantity; Date lastDate = null;

        for(Registro entry : alimentos) {
            name = entry.name;
            date = new Date(entry.date);
            calories = new Double(entry.calories);
            quantity = entry.quantity;

            if (lastDate == null) {
                totalCalories = quantity*calories + totalCalories;
                Date finalDate = new Date(new SimpleDateFormat("MM/dd/yyyy 12:00:00").format(date.getTime()));
                plotseries.add(new DataPoint(finalDate, totalCalories));
                lastDate = date;
                i++;
            } else if (lastDate.getDay() == date.getDay()) {
                totalCalories = quantity*calories + totalCalories;
                plotseries.remove(i);
                Date finalDate = new Date(new SimpleDateFormat("MM/dd/yyyy 12:00:00").format(lastDate.getTime()));
                plotseries.add(new DataPoint(finalDate, totalCalories));
            } else {
                Date finalDate = new Date(new SimpleDateFormat("MM/dd/yyyy 12:00:00").format(date.getTime()));
                plotseries.add(new DataPoint(finalDate, quantity*calories));
                totalCalories = quantity*calories;
                i++;
                if (i >= 30) {break;}
                lastDate = date;
            }
        }

        Collections.reverse(plotseries);
        for (DataPoint entry: plotseries) {
            series.appendData( entry, true, 99, true);
            thresholdseries.appendData( new DataPoint(entry.getX(), threshold), true, 99, true);
        }

        //series.setSpacing(5);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(5);
        series.setDrawBackground(true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
        series.setCustomPaint(paint);

        thresholdseries.setColor(Color.RED);

        graph.addSeries(thresholdseries);
        graph.addSeries(series);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this.getBaseContext(), new SimpleDateFormat("d")));
        //graph.getGridLabelRenderer().setNumHorizontalLabels(7); // only 4 because of the space

        graph.getViewport().setXAxisBoundsManual(true);
        Date minx = new Date(new SimpleDateFormat("MM/dd/yyyy 00:00:00").format(plotseries.get(0).getX()));
        graph.getViewport().setMinX(minx.getTime());
        Date maxx = new Date(new SimpleDateFormat("MM/dd/yyyy 23:59:59").format(plotseries.get(plotseries.size()-1).getX()));
        graph.getViewport().setMaxX(maxx.getTime());

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);

        graph.getGridLabelRenderer().setHumanRounding(true);

        graph.getGridLabelRenderer().setHorizontalAxisTitle("Dia");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Kcals");

        graph.getViewport().setScrollable(true);
    }

}