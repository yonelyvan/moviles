package com.example.ynl.mnet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    //defining view objects
    private EditText TextEmail;
    private EditText TextPassword;
    private ProgressDialog progressDialog;

    //sesion
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inicializamos el objeto firebaseAuth
        mAuth = FirebaseAuth.getInstance();

        TextEmail = (EditText) findViewById(R.id.email);
        TextPassword = (EditText) findViewById(R.id.password);
        progressDialog = new ProgressDialog(this);

        //
        getSupportActionBar().hide();

        ver_estado();
    }

    public  void loguearUsuario(View view) {
        //Obtenemos el email y la contraseña desde las cajas de texto
        final String email = TextEmail.getText().toString().trim();
        String password = TextPassword.getText().toString().trim();
        //Verificamos que las cajas de texto no esten vacías
        if (TextUtils.isEmpty(email)) {//(precio.equals(""))
            Toast.makeText(this, "Se debe ingresar un email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Falta ingresar la contraseña", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.setMessage("Realizando consulta en linea...");
        progressDialog.show();

        //loguear usuario
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            //Toast.makeText(MainActivity.this, "Bienvenido al MURO: " + TextEmail.getText(), Toast.LENGTH_LONG).show();
                            goto_muro_activity();//
                        } else {
                                Toast.makeText(MainActivity.this, "Error de autenticación", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    public void goto_register_activity(View v){
        Intent intent  = new  Intent(getApplication(), RegisterActivity.class);
        startActivity(intent);
    }

    public void goto_muro_activity(){
        ver_estado();
        Intent intent  = new  Intent(getApplication(), MnetActivity.class);
        startActivity(intent);
    }

    private void ver_estado(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            try {
                Log.e("EMAIL", user.getEmail());
                Log.e("USERNAME", user.getDisplayName());
                welcome();
            } catch (Exception e) {
                Log.e("MNET", "exception", e);
            }
        } else {
            // No user is signed in
            Log.e("NO USER", "NULL");
        }
    }


    public void welcome(){
        Intent i=new Intent(MainActivity.this, MnetActivity.class);
        startActivity( i );
        Toast.makeText(MainActivity.this,"Bienvenido!",Toast.LENGTH_LONG).show();
    }
}
