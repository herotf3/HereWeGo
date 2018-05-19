package com.example.asus.travisor.ViewHolder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.asus.travisor.Activity.ListPlaces;
import com.example.asus.travisor.Interface.BookmarkCallback;
import com.example.asus.travisor.Interface.ItemClickListener;
import com.example.asus.travisor.Model.Firebase.Place;
import com.example.asus.travisor.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Locale;

/**
 * Created by ASUS on 5/14/2018.
 */

public class PlaceVH extends RecyclerView.ViewHolder implements View.OnClickListener {
    private View itemView;
    private ItemClickListener itemClickListener;
    private BookmarkCallback bookmarkCallback;

    TextView point,name,address,cmt,vote,distance;
    ImageView imgPlace;
    ImageButton btnBookmark;
    private ItemClickListener itemListener;

    public void setBookmarkCallback(BookmarkCallback bookmarkCallback) {
        this.bookmarkCallback = bookmarkCallback;
    }

    private String placeId;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }



    public PlaceVH(View itemView, BookmarkCallback bookmarkCallback) {
        super(itemView);
        this.bookmarkCallback = bookmarkCallback;
        initView(itemView);
    }

    public View getItemView() {
        return itemView;
    }

    public void setItemView(View itemView) {
        this.itemView = itemView;
    }

    public ItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public PlaceVH(View view) {
        super(view);
        initView(view);
    }

    private void initView(View view) {
        itemView=view;
        name=itemView.findViewById(R.id.txt_name);
        address=itemView.findViewById(R.id.txt_address);
        point=itemView.findViewById(R.id.txt_mark);
        cmt=itemView.findViewById(R.id.tv_cmt_num);
        vote=itemView.findViewById(R.id.tv_voted_num);
        imgPlace=itemView.findViewById(R.id.img_place);
        distance=itemView.findViewById(R.id.tvDistance);
        btnBookmark=itemView.findViewById(R.id.btn_item_bookmark);

        itemView.setOnClickListener(this);

    }

    public void bindData(Place model, Context baseContext, final String placeId) {
        this.placeId=placeId;

        name.setText(model.getName());
        address.setText(model.getAddress());
        point.setText(String.valueOf(model.getPoint()));
        cmt.setText(String.valueOf(model.getComment()));
        vote.setText(String.valueOf(model.getLuotVote()));
        if (!model.getFeatureImage().isEmpty()) {
            String url=model.getFeatureImage();
            if (url.startsWith("http"))
                Picasso.with(baseContext).load(model.getFeatureImage()).into(imgPlace);
            else //url is fbstorage ref
            {
                StorageReference ref= FirebaseStorage.getInstance().getReference(url);
                Glide.with(baseContext /* context */)
                        .using(new FirebaseImageLoader())
                        .load(ref)
                        .into(imgPlace);
            }
        }
        Location current= ListPlaces.currLocation;
        if (current==null){
            distance.setText("? km");
            return;
        }

        Location dest=new Location("D");
        dest.setLatitude(model.getLat());
        dest.setLongitude(model.getLng());
        float km=current.distanceTo(dest)/1000;
        distance.setText(roundOffTo2Dec(km)+ " km");

        //bookmark listener
        btnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnBookmark.setImageResource(R.drawable.ic_star);
                if (bookmarkCallback!=null)
                    bookmarkCallback.bookmarkPlace(placeId);
            }
        });
    }

    private String roundOffTo2Dec(float val)
    {
        return String.format("%.2f", val);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,this.getAdapterPosition(),false);
    }

    public void setItemListener(ItemClickListener itemListener) {
        this.itemListener = itemListener;
    }

    public ItemClickListener getItemListener() {
        return itemListener;
    }

    public void removeBookmark(){
        btnBookmark.setVisibility(View.INVISIBLE);
    }
}
