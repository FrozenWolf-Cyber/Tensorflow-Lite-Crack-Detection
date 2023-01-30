package com.example.bharathfly_park;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageButton;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class dashboard extends AppCompatActivity {
    private ImageView img_view;
    private ImageButton btnLeft;
    private ImageButton btnRight;
    private Button btnGallery;
    private Handler handler;
    public TextView date, loc, pred;
    public ArrayList<Info> imageList;
    public static Integer index = 0;
    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        img_view = findViewById(R.id.viewPager);
        btnLeft = findViewById(R.id.leftBtn);
        btnRight = findViewById(R.id.rightBtn);
        btnGallery = findViewById(R.id.btnGallery);
        pred = findViewById(R.id.pred_dash);
        date = findViewById(R.id.DATE);
        loc = findViewById(R.id.loc);
        imageList = getAllSavedMyPhotos();

//        imageList.add(R.drawable.ic_android_black_24dp);
//        imageList.add(R.drawable.ic_launcher_background);
//        imageList.add(R.drawable.left_arrow);
//        imageList.add(R.drawable.right_arrow);

        if (imageList.size()>0) {
            if (index>=0) {
                index = index % imageList.size();
            }
            else{
                index+=imageList.size();
            }
            String encoded = imageList.get(index).img;
            byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
            img_view.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            settext();
        }



        btnRight.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (imageList.size()!=0) {
                    index++;
                    index = index % imageList.size();
                    String encoded = imageList.get(index).img;
                    byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
                    img_view.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                    settext();
//                img_view.setCurrentItem((viewPager2.getCurrentItem() + 1)%imageList.size());
                }
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (imageList.size()!=0) {
                    if (index==0){
                        index=imageList.size()-1;
                    }
                    else {
                        index--;
                    }
                    String encoded = imageList.get(index).img;
                    byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
                    img_view.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                    settext();
                }
            }
        });


        btnGallery.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this,gallery.class));
            }
        });
    }
    public double round_(float a){
        return Math.round(a * 100.0) / 100.0;
    }

    void settext(){
        date.setText("DATE: "+df.format(imageList.get(index).currentTime));
        double loc_[] = imageList.get(index).loc;
        loc.setText("LATITUDE: "+ Double.toString(loc_[0])+"      LONGITUDE: "+ Double.toString(loc_[1]));
        float preds[] = imageList.get(index).pred;
        if (preds[0]>preds[1]) {
            pred.setText("NEGATIVE: "+Double.toString(round_(preds[0]*100)));
        }
        else {
            pred.setText("POSITIVE: "+Double.toString(round_(preds[1]*100)));
        }
    }


    public void saveMyPhotos(ArrayList<Info> imagelist ) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            Gson gson = new Gson();
            String json = gson.toJson(imagelist);
            editor.putString("MyPhotos", json);
            editor.commit();     // This line is IMPORTANT !!!
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ArrayList<Info> getAllSavedMyPhotos() {
        SharedPreferences  prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString("MyPhotos", null);
        Type type = new TypeToken<ArrayList<Info>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    }


