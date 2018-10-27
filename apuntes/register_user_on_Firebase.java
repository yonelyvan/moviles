package com.ynl.snet;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    //defining view objects
    private EditText TextEmail;
    private EditText TextPassword;
    private ProgressDialog progressDialog;


    //Declaramos un objeto firebaseAuth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inicializamos el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        TextEmail = (EditText) findViewById(R.id.txt_email);
        TextPassword = (EditText) findViewById(R.id.txt_password);

        progressDialog = new ProgressDialog(this);
    }

    //Registro de usuario con firebase
    public void registrarUsuario(View view){
        String email = TextEmail.getText().toString().trim();
        String password  = TextPassword.getText().toString().trim();

        //verificacion de entrada
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Se debe ingresar un email",Toast.LENGTH_LONG).show();
            return;
        }
        if(password.length()<6){
            Toast.makeText(this,"Escribir contraseña con almenos 6 caracteres",Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Registrando en linea...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){//exito
                        Toast.makeText(MainActivity.this,"Se ha registrado el usuario con el email: "+ TextEmail.getText(),Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainActivity.this,"No se pudo registrar el usuario ",Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
                }
            });
    }


}
