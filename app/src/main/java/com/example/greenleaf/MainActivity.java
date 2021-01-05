package com.example.greenleaf;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.greenleaf.Model.User;
import com.example.greenleaf.global.Global;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;


public class MainActivity extends AppCompatActivity {

    /*
        La page d’accueil comporte deux boutons "Inscription" et "Connexion",  en cliquant la dessus, va nous rediriger vers l’interface correspandate
     */

    private Button login;
    private Button register;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);

        loadingBar = new ProgressDialog(this);
        this.login = (Button) findViewById(R.id.login);
        this.register = (Button) findViewById(R.id.register);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Registering.class);
                startActivity(intent);
                finish();
            }
        });

        String UserPhoneKey = Paper.book().read(Global.UserEmailKey);
        String UserPasswordKey = Paper.book().read(Global.UserPasswordKey);

        if (UserPhoneKey != "" && UserPasswordKey != "")
        {
            if (!TextUtils.isEmpty(UserPhoneKey)  &&  !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAccess(UserPhoneKey, UserPasswordKey);

                loadingBar.setTitle("Déjà connecté");
                loadingBar.setMessage("Attendez s'il vous plaît.....");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }

    }

    private void AllowAccess(final String email, final String password)
    {


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String replacedEmail = email.replace(".","_DOT_");
                if (dataSnapshot.child("Users").child(replacedEmail).exists())
                {
                    User user = dataSnapshot.child("Users").child(replacedEmail).getValue(User.class);

                    if (user.getEmail().equals(email))
                    {
                        if (user.getPassword().equals(password))
                        {

                            Toast.makeText(MainActivity.this, "Connecté avec succès", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                             Global.currentUser = user;
                            Global.currentUserId = replacedEmail;

                            Intent intent = new Intent(MainActivity.this, NavigationBar.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Le mot de passe est incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Un compte avec cet email " + email + " n'existe pas.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
