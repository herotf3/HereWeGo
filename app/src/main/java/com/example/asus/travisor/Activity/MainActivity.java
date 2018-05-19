package com.example.asus.travisor.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.asus.travisor.R;

public class MainActivity extends AppCompatActivity {
    Button btnSignup,btnSignin;
    TextView sologan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*if (savedInstanceState==null){
            FragmentAddStore fragment=new FragmentAddStore();
            setFragment(fragment);
        }*/
        btnSignup=findViewById(R.id.btnSignUp);
        btnSignin=findViewById(R.id.btnSignIn);
        sologan=findViewById(R.id.sologan);
        Typeface face=Typeface.createFromAsset(getAssets(),"font/Harabara.ttf");
        sologan.setTypeface(face);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signinIntent= new Intent(MainActivity.this,SignUp.class);
                startActivity(signinIntent);
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signinIntent= new Intent(MainActivity.this,SignIn.class);
                startActivity(signinIntent);
            }
        });
    }


}
