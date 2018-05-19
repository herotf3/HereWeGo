package com.example.asus.travisor.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.travisor.Model.Common;
import com.example.asus.travisor.Model.RestaurantAdmin;
import com.example.asus.travisor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {
    EditText edtPhone, edtPass;
    Button btnSignin;

    FirebaseDatabase db;
    DatabaseReference tableUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPhone=findViewById(R.id.edtPhone);
        edtPass=findViewById(R.id.edtPass);
        btnSignin=findViewById(R.id.btnSignIn);

        initDatabase();
        setListener();
    }

    private void setListener() {
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog=new ProgressDialog(SignIn.this);
                progressDialog.setMessage("Đợi chút nhá...");
                progressDialog.show();

                tableUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //check valid user
                        if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                            progressDialog.dismiss();
                            //get the user with specific phone
                            RestaurantAdmin user = dataSnapshot.child(edtPhone.getText().toString()).getValue(RestaurantAdmin.class);
                            if (true){
                                assert user != null;
                                if (user.getPassword().equals(edtPass.getText().toString())) {
                                    Toast.makeText(SignIn.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                    Common.currentUser=user;
                                    Common.userKey=dataSnapshot.child(edtPhone.getText().toString()).getKey();
                                    Intent home=new Intent(SignIn.this, Home.class);
                                    startActivity(home);
                                    finish();
                                } else {
                                    Toast.makeText(SignIn.this, "Mật khẩu không chính xác!", Toast.LENGTH_LONG).show();
                                }
                            }else
                                Toast.makeText(SignIn.this, "Vui lòng đăng nhập bằng tài khoản quản lý", Toast.LENGTH_LONG).show();

                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Tài khoản không tồn tại!",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void initDatabase() {
        db= FirebaseDatabase.getInstance();
        tableUser=db.getReference("RestaurantAdmin");
    }
}
