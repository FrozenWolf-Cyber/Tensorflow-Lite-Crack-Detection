package com.example.bharathfly_park;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class camer_modes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camer_modes);
    }

    public void go_to_live(View view){
        Intent myIntent = new Intent(camer_modes.this, camera_fast.class);
//        myIntent.putExtra("key", value); //Optional parameters
        camer_modes.this.startActivity(myIntent);
    }

    public void go_to_focus(View view){
        Intent myIntent = new Intent(camer_modes.this, camera_focused.class);
//        myIntent.putExtra("key", value); //Optional parameters
        camer_modes.this.startActivity(myIntent);
    }

    public void go_to_upload(View view){
        Intent myIntent = new Intent(camer_modes.this, upload_pred.class);
//        myIntent.putExtra("key", value); //Optional parameters
        camer_modes.this.startActivity(myIntent);
    }
}