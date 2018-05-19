package com.example.asus.travisor.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.asus.travisor.Model.Firebase.Place;
import com.example.asus.travisor.Model.Firebase.PlaceDetail;
import com.example.asus.travisor.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class PlaceDetailActivity extends AppCompatActivity {

    ImageView imvSlide;
    FloatingActionButton fabLeft,fabRight;
    TextView tvName,tvAdrress,tvDescription,tvCmtNum,tvVoteNum;
    Button btnBookmark;

    Place place;
    PlaceDetail placeDetail;
    LinkedList<Bitmap> imgList=new LinkedList<Bitmap>();
    int count,curr=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        initView();
        bindData();
        setListener();
    }

    private void setListener() {
        fabLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curr==0){
                    curr=count;
                    imvSlide.setImageBitmap(imgList.getLast());
                }else
                {
                    imvSlide.setImageBitmap(imgList.get(--curr));
                }
            }
        });
        fabRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curr==count-1){
                    curr=0;
                    imvSlide.setImageBitmap(imgList.getFirst());
                }else
                {
                    imvSlide.setImageBitmap(imgList.get(++curr));
                }
            }
        });
    }

    private void setDataView(PlaceDetail detail) {
        tvName.setText(place.getName());
        tvAdrress.setText(place.getAddress());
        tvDescription.setText(detail.getDescription());
        tvCmtNum.setText(String.valueOf(place.getComment()));
        tvVoteNum.setText(String.valueOf(place.getLuotVote()));
        imgList.clear();
        count=0;

        for (String link:detail.getImages().values()) {
            downloadBitmap(link);
        }
        String url=place.getFeatureImage();
        if (url.startsWith("http"))
            Picasso.with(this).load(place.getFeatureImage()).into(imvSlide);
        else //url is fbstorage ref
        {
            StorageReference ref= FirebaseStorage.getInstance().getReference(url);
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(ref)
                    .into(imvSlide);
        }
    }

    private void bindData() {
        place=(Place)getIntent().getSerializableExtra("Place");
        DatabaseReference detailRef= FirebaseDatabase.getInstance()
                .getReference("PlaceDetails");

        detailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if (snapshot.getKey().equals(place.getDetailId())){
                        placeDetail=snapshot.getValue(PlaceDetail.class);
                    }
                }
                setDataView(placeDetail);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void downloadBitmap(String imageUrl) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(imageUrl);
            imgList.add(BitmapFactory.decodeStream((InputStream)url.getContent()) );
            count++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        imvSlide=findViewById(R.id.imv_detail);
        fabLeft=findViewById(R.id.slide_left);
        fabRight=findViewById(R.id.slide_right);
        tvName=findViewById(R.id.tv_detail_name);
        tvAdrress=findViewById(R.id.tv_detail_address);
        tvDescription=findViewById(R.id.tv_detail_content);
        tvCmtNum=findViewById(R.id.tv_cmt_num);
        tvVoteNum=findViewById(R.id.tv_voted_num);
        btnBookmark=findViewById(R.id.btn_detail_bookmark);
    }
}
