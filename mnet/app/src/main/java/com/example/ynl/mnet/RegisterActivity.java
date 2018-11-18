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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    //defining view objects
    private EditText TextUsername;
    private EditText TextEmail;
    private EditText TextPassword;
    private EditText TextPassword2;
    private ProgressDialog progressDialog;

    private FirebaseAuth auth;
    //sescion
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //inicializamos el objeto firebaseAuth
        mAuth = FirebaseAuth.getInstance();

        TextUsername = (EditText) findViewById(R.id.username);
        TextEmail = (EditText) findViewById(R.id.email);
        TextPassword = (EditText) findViewById(R.id.password);
        TextPassword2 = (EditText) findViewById(R.id.password2);
        progressDialog = new ProgressDialog(this);

        //
        getSupportActionBar().hide();
    }



    //Registro de usuario con firebase
    public void registrarUsuario(View view){
        final String username = TextUsername.getText().toString().trim();
        String email = TextEmail.getText().toString().trim();
        String password  = TextPassword.getText().toString().trim();
        String password2  = TextPassword2.getText().toString().trim();

        //verificacion de entrada
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Se debe ingresar un email",Toast.LENGTH_LONG).show();
            return;
        }
        if(password.length()<6){
            Toast.makeText(this,"Escribir contraseña con almenos 6 caracteres",Toast.LENGTH_LONG).show();
            return;
        }
        if(!password.equals(password2)){
            Toast.makeText(this,"Error: Las contraseñas no coinciden.",Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Registrando en linea...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){//exito

                            Toast.makeText(RegisterActivity.this,"Se ha registrado el usuario con el email: "+ TextEmail.getText(),Toast.LENGTH_LONG).show();
                            //add username
                            addUserNameToUser(task.getResult().getUser(),username);
                            //redirigir al login
                            goto_LoginActivity();

                        }else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {//si se presenta una colisión
                                Toast.makeText(RegisterActivity.this, "EL usuario ya existe ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario ", Toast.LENGTH_LONG).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    //actualiza el displayname y hace una copia de urusario firabase a users
    public void addUserNameToUser(FirebaseUser user, String username) {
        insert_to_users_db(user, username); //insert copia a tabla users
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.e("ECHO:", "Usuario actualizado.");
                        }else{
                            Log.e("ERROR:", "No se puedo actualizar nombre de usuario.");
                        }
                    }
                });
    }

    public void goto_LoginActivity(){
        Intent intent  = new  Intent(getApplication(), MainActivity.class);
        startActivity(intent);
    }


    public void insert_to_users_db(FirebaseUser user_firebase, String username){
        String db_name_users = getString(R.string.db_name_users);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(db_name_users);

        User u = new User();
        u.set_id(user_firebase.getUid());
        u.set_name(username);
        u.set_email(user_firebase.getEmail());
        //TODO: falta foto
        ref.child(u.get_id()).setValue(u);//save for especific user
    }
}



