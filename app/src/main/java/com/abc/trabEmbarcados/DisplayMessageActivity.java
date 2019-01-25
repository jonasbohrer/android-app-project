package com.abc.trabEmbarcados;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(this, "Comeu arroz", Toast.LENGTH_SHORT).show();
        //Intent intent = getIntent();
        //String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        //setContentView(R.layout.activity_display_message);

        TextView textView = new TextView(this);
        textView.setTextSize(14);

        textView.setText(getIntent().getStringExtra("infos"));
        setContentView(textView);
    }
}
