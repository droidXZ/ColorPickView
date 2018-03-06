package com.yhz.study.colorpicker;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ColorPickView colorPickView = findViewById(R.id.colorPickView);
        final TextView textView = findViewById(R.id.showTv);
        textView.setBackgroundColor(Color.RED);
        colorPickView.setBarListener(new ColorPickView.OnColorBarListener() {
            @Override
            public void moveBar(int color) {
                textView.setBackgroundColor(color);
            }
        });
    }
}
