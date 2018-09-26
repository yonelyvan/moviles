package com.example.ynl.rf;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView textView;

    private int level = 1; //[0-5]
    int delay = 600;


    private boolean status = true;
    private static Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        textView = findViewById(R.id.train);
        run_train();
    }

    public void run_train(){
        Thread t = new Thread() {
            @Override
            public void run() {
                //for (int i = 0; i < 100; i++) { //(!isInterupted){
                while(!isInterrupted() && status ){
                    try {
                        Thread.sleep(delay);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                set_test();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }



    private void set_test(){
        String word = get_word();
        textView.setText(word);
        //Log.e("MESSAGE:: ",word); //e warming, r, ...
    }

    private String get_word(){
        char[] numbers = { '0', '1', '2', '3','4','5','6','7','8','9'};
        char[] abc = {'b','c','d','f','g','h','j','k','l','m','n','ñ','p','q','r','s','t','v','w','x','y','z'};
        char[] vocales = {'a','e','i','o','u'};

        String space = " ";
        int d = 12;
        for (int i =0; i<d*level;i++){
            space+=" ";
        }
        String r="";
        Random rand = new Random();
        int s = rand.nextInt(10);
        if(s<=2){
            //numeros
            int  a = rand.nextInt(numbers.length);
            int  b = rand.nextInt(numbers.length);
            r = r + numbers[a] +space+ numbers[b];
        }else{
            //letras
            int v_or_l = rand.nextInt(5);
            if(v_or_l == 0){//primero vocal
                int  a = rand.nextInt(vocales.length);
                int  b = rand.nextInt(abc.length);
                r = r + vocales[a] +space+ abc[b];
            }else{
                int  a = rand.nextInt(abc.length);
                int  b = rand.nextInt(vocales.length);
                r = r + abc[a] +space+ vocales[b];
            }
        }
        return r;
    }


    public void next_level(View v){
        if(level == 5){
            level=5;
        }else{
            ++level;
        }
        Toast.makeText( this, "Nivel " + Integer.toString(level), Toast.LENGTH_SHORT).show();
    }

    public void stop_play(View v){
        if(status==true){
            status = false;
            Toast.makeText( this, "Pausa", Toast.LENGTH_SHORT).show();
        }else{
            status = true;
            Toast.makeText( this, "Play", Toast.LENGTH_SHORT).show();
            run_train();
        }
    }

    public void previous_level(View v){
        if(level==0){
            level = 0;
        }else{
            --level;
        }
        Toast.makeText( this, "Nivel " + Integer.toString(level), Toast.LENGTH_LONG).show();
    }

}
