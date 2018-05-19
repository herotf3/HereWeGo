package com.example.asus.travisor.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.asus.travisor.Model.Firebase.Place;
import com.example.asus.travisor.R;
import com.example.asus.travisor.Utils.UploadManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.UUID;

public class AddPlaceActivity extends AppCompatActivity {

    EditText edtName,edtAddress,edtLat,edtLng;
    ImageView imvUpload;
    ImageButton btnCapture;
    Button btnAdd;
    String categotyId;
    private static final int REQUEST_IMAGE_CAPTURE=100;
    private static final int REQUEST_PICK_IMAGE=101 ;

    Uri filePath=null;
    private boolean hasPickedImg=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        initView();
        categotyId=getIntent().getStringExtra("CategoryId");
        setListener();
    }

    private void setListener() {
        imvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name,address,lat,lng;
                name=edtName.getText().toString();
                address=edtAddress.getText().toString();
                lat=edtLat.getText().toString();
                lng=edtLng.getText().toString();
                if (!checkFillAllField(name,address,lat,lng)){
                    Toast.makeText(getApplicationContext(),"Hãy nhập đầy đủ thông tin!",Toast.LENGTH_LONG).show();
                    return;
                }
                //upload img to firebase storage
                UploadManager uploader=new UploadManager(AddPlaceActivity.this);
                String fbPath="imgPlaces/"+ UUID.randomUUID().toString()+".jpeg";
                uploader.UpLoadImage(imvUpload,fbPath);

                Place newPlace=new Place(name,address,fbPath,Double.parseDouble(lat),Double.parseDouble(lng));
                newPlace.setCategoryId(categotyId);
                DatabaseReference placesTable= FirebaseDatabase.getInstance().getReference("Places");
                //insert new place to database
                placesTable.push().setValue(newPlace);
            }
        });
    }

    private boolean checkFillAllField(String name, String address, String lat, String lng) {
        if (name.isEmpty()||address.isEmpty()||lat.isEmpty()||lng.isEmpty())
            return false;
        if (hasPickedImg)
            return true;
        else
            return false;
    }

    private void initView() {
        edtAddress=findViewById(R.id.edt_add_address);
        edtName=findViewById(R.id.edt_add_name);
        edtLat=findViewById(R.id.edt_add_lat);
        edtLng=findViewById(R.id.edt_add_lng);
        imvUpload=findViewById(R.id.imv_upload);
        btnCapture=findViewById(R.id.btn_capture);
        btnAdd=findViewById(R.id.btnAddPlace);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            Bundle extras=data.getExtras();
            Bitmap bitmap= (Bitmap) extras.get ("data");
            try {
                imvUpload.setImageBitmap(bitmap);
                hasPickedImg=true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //ket qua tu viec chon anh tu bo nho
        if(requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imvUpload.setImageBitmap(bitmap);
                hasPickedImg=true;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
