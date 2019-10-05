package com.example.commutersafety;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class WallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        RecyclerView wall =(RecyclerView) findViewById(R.id.wall_recycler_view);
        wall.setHasFixedSize(true);

        String [] loc1 = getResources().getStringArray(R.array.loc1);
        String [] loc2 = getResources().getStringArray(R.array.area);
        String [] time = getResources().getStringArray(R.array.time);

        WallAdapter wallAdapter = new WallAdapter(loc1,loc2,time);
        wall.setAdapter(wallAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        wall.setLayoutManager(linearLayoutManager);
    }
}
