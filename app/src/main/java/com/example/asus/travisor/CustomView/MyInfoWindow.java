package com.example.asus.travisor.CustomView;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.asus.travisor.Model.Firebase.Place;
import com.example.asus.travisor.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * Created by ASUS on 5/15/2018.
 */

public class MyInfoWindow implements GoogleMap.InfoWindowAdapter {
    Context context;

    public MyInfoWindow(Context ctx){
        context=ctx;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.infowindow, null);

        TextView name = view.findViewById(R.id.tv_info_name);
        TextView address = view.findViewById(R.id.tv_info_address);
        ImageView imgv = view.findViewById(R.id.img_map_place);

        TextView cmt = view.findViewById(R.id.tv_info_cmt);
        TextView vote = view.findViewById(R.id.tv_info_voted);

        Place model = (Place) marker.getTag();

        if (!model.getFeatureImage().isEmpty()) {
            String url=model.getFeatureImage();
            if (url.startsWith("http"))
                Picasso.with(context).load(url).into(imgv);
            else //url is fbstorage ref
            {
                StorageReference ref= FirebaseStorage.getInstance().getReference(url);
                Glide.with(context)
                        .using(new FirebaseImageLoader())
                        .load(ref)
                        .into(imgv);
            }
        }

        name.setText(model.getName());
        address.setText(model.getAddress());
        cmt.setText(String.valueOf(model.getComment()));
        vote.setText(String.valueOf(model.getLuotVote()));

        return view;
    }
}
