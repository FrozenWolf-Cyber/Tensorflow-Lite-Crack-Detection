package com.example.bharathfly_park;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class GridAdapter extends ArrayAdapter<Info> {
    public GridAdapter(@NonNull Context context, ArrayList<Info> imagesList) {
        super(context, 0, imagesList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.image_card, parent, false);
        }


        Info bmp = getItem(position);
        ImageView image = listitemView.findViewById(R.id.cardImage);

        image.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (dashboard.index!=0) {
                    dashboard.index = position - 1;
                }
                else{
                    dashboard.index = getCount()-1;
                }
                getContext().startActivity(new Intent(getContext(),dashboard.class));
            }
        });
        String encoded = getItem(position).img;
        byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
        image.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        return listitemView;
    }
}