package com.example.asus.travisor.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.asus.travisor.Model.RestaurantAdmin;
import com.example.asus.travisor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {
    EditText edtPhone, edtPass,edtRePass,edtName;
    Button btnSignup;

    FirebaseDatabase db;
    DatabaseReference tableUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        edtPhone=findViewById(R.id.edtPhone);
        edtPass=findViewById(R.id.edtPass);
        edtRePass=findViewById(R.id.edtRePass);
        edtName=findViewById(R.id.edt_singup_Name);
        btnSignup=findViewById(R.id.btnSignUp);

        db= FirebaseDatabase.getInstance();
        tableUser=db.getReference("RestaurantAdmin");
        setListener();
    }

    private void setListener() {
        btnSignup.setOnClickListener(new View.OnClickListener() {
            String phone,pass,repass,name;
            @Override
            public void onClick(View view) {

                phone=edtPhone.getText().toString();
                pass=edtPass.getText().toString();
                repass=edtRePass.getText().toString();
                name=edtName.getText().toString();
                if (!checkInputOK())
                    return;
                //
                tableUser.child(phone).setValue(new RestaurantAdmin(name,pass));
                Toast.makeText(getBaseContext(),"Đăng ký thành công",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getBaseContext(),SignIn.class));
            }

            private boolean checkInputOK() {
                if (phone.isEmpty() || pass.isEmpty() || repass.isEmpty() || name.isEmpty() ){
                    Toast.makeText(getBaseContext(),"Hãy nhập đầy đủ thông tin!",Toast.LENGTH_LONG).show();
                    return false;
                }

                if (!pass.equals(repass)) {
                    Toast.makeText(getBaseContext(),"Mật khẩu nhập lại không khớp",Toast.LENGTH_LONG).show();
                    return false;
                }

                tableUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                            if (snapshot.hasChild(edtPhone.getText().toString())) {
                                Toast.makeText(getBaseContext(), "Tên người dùng đã tồn tại", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return true;
            }
        });
    }
}
