package com.ynl.sn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Muro extends AppCompatActivity {

    public static final String user="names";
    public TextView txtUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muro);
        //

        txtUser =(TextView)findViewById(R.id.txt_user);
        String user = getIntent().getStringExtra("names");
        txtUser.setText("¡Bienvenido "+ user +"!");
        //verificacion de session
        mAuth = FirebaseAuth.getInstance();
    }
}
