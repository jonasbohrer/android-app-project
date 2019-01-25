package com.abc.trabEmbarcados;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LogActivity extends AppCompatActivity {

    private TableLayout tableLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_logs);

        tableLayout = (TableLayout) findViewById(R.id.table);

        ArrayList<Registro> alimentos = (ArrayList<Registro>) getIntent().getSerializableExtra("alimentos");

        if(alimentos != null && alimentos.size() > 0) {
            Collections.reverse(alimentos);
            for (Registro entry : alimentos) {
                String name = entry.name;
                String date = entry.date;
                String calories = entry.calories;
                Integer quantity = entry.quantity;

                View tableRow = LayoutInflater.from(this).inflate(R.layout.table_item, null, false);
                TextView history_display_no = (TextView) tableRow.findViewById(R.id.history_display_no);
                TextView history_display_date = (TextView) tableRow.findViewById(R.id.history_display_date);
                TextView history_display_orderid = (TextView) tableRow.findViewById(R.id.history_display_orderid);
                TextView history_display_quantity = (TextView) tableRow.findViewById(R.id.history_display_quantity);

                history_display_no.setText(quantity.toString());
                history_display_date.setText(date);
                history_display_orderid.setText(name);
                history_display_quantity.setText(calories);
                tableLayout.addView(tableRow);
            }
        }
        else{
            Toast.makeText(this, "Nenhum registro adicionado!", Toast.LENGTH_SHORT).show();
        }
    }
}