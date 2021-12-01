package com.example.projecte;

import java.text.DecimalFormat;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class PopUpWindow extends MainActivity {

    protected TextView popUp;
    protected EditText editA, editB,editC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_window);
        popUp = findViewById(R.id.popUp);
        editA = findViewById(R.id.editA);
        editB = findViewById(R.id.editC);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        Intent i = getIntent();
        String result = i.getStringExtra("result");


        getWindow().setLayout((int) (width*.7), (int) (height*.5));


        popUp.setText("X1, X2 = " + "\n" + result + "\n" + "is your answer!");


        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);
    }
}