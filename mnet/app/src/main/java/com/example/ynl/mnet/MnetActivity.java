package com.example.ynl.mnet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class MnetActivity extends AppCompatActivity {
    FragmentNotificaciones fragmentNotificaciones;
    FragmentMuro fragmentMuro;
    FragmentDashboard fragmentDashboard;
    FragmentChat fragmentChat;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_notifications:
                    Log.e("TAG:","Notificaciones");
                    setFragment(fragmentNotificaciones);
                    return true;
                case R.id.navigation_dashboard:
                    Log.e("TAG:","dashboard");
                    setFragment(fragmentDashboard);
                    return true;
                case R.id.navigation_muro:
                    setFragment(fragmentMuro);
                    Log.e("TAG:","MURO");
                    return true;
                case R.id.navigation_chat:
                    Log.e("TAG:","CHAT");
                    setFragment(fragmentChat);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mnet);
        //
        getSupportActionBar().hide();

        //
        fragmentNotificaciones = new FragmentNotificaciones();
        fragmentMuro = new FragmentMuro();
        fragmentDashboard = new FragmentDashboard();
        fragmentChat = new FragmentChat();

        //
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    public void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

}