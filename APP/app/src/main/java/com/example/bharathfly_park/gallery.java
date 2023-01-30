package com.example.bharathfly_park;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class gallery extends AppCompatActivity {
    private GridView gridView;
    public ArrayList<Info> imageList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        gridView = findViewById(R.id.galleryGrid);

        imageList = getAllSavedMyPhotos();
        GridAdapter gridAdapter = new GridAdapter(this, imageList);
        gridView.setAdapter(gridAdapter);
    }

    private ArrayList<Info> getAllSavedMyPhotos() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString("MyPhotos", null);
        Type type = new TypeToken<ArrayList<Info>>() {}.getType();
        return gson.fromJson(json, type);
    }
}