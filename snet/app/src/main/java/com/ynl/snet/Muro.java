package com.ynl.snet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Muro extends AppCompatActivity {
    public static final String user="names";
    TextView txtUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muro);

        txtUser =(TextView)findViewById(R.id.txt_user);
        String user = getIntent().getStringExtra("names");
        txtUser.setText("¡Bienvenido "+ user +"!");
    }
}
