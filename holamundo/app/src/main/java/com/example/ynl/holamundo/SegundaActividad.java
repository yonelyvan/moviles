package com.example.ynl.holamundo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SegundaActividad extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        //
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Log.e(MainActivity.TAG,message);
        //
        TextView textView = findViewById(R.id.text_view);
        textView.setText(message);

    }







    //extra
    public void goToAnActivity(View view){
        Intent intent = new Intent(this,LastActivity.class);
        startActivity(intent);
    }

}
