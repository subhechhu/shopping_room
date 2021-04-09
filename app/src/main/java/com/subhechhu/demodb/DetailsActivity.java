package com.subhechhu.demodb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Log.e("TAGGED", "item name : " + getIntent().getStringExtra("item"));
        Log.e("TAGGED", "item id: " + getIntent().getIntExtra("id", 0));
        Log.e("TAGGED", "item quantity: " + getIntent().getFloatExtra("q", 0f));
        Log.e("TAGGED", "item type: " + getIntent().getIntExtra("type", 0));
    }
}