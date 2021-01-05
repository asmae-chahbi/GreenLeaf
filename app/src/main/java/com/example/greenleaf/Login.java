package com.example.greenleaf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greenleaf.Model.User;
import com.example.greenleaf.global.Global;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class Login extends AppCompatActivity {
    /*
        Page de connexion
     */
    private TextView passme;
    private Button LoginButton;
    private EditText InputPassword, InputEmail;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InputPassword = (EditText) findViewById(R.id.password);
        InputEmail = (EditText) findViewById(R.id.profil_email);
        LoginButton = (Button) findViewById(R.id.login);
        loadingBar = new ProgressDialog(this);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });


        Paper.init(this);

        this.passme = findViewById(R.id.passme);
        passme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Registering.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void LoginUser()
    {
        String email = InputEmail.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Veuillez écrire votre email", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Veuillez écrire votre mot de passe", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Connexion au compte");
            loadingBar.setMessage("Veuillez patienter pendant que nous vérifions les informations d'identification.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            AllowAccessToAccount(email, password);
        }
    }

    private void AllowAccessToAccount(final String email, final String password)
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
                                Paper.book().write(Global.UserEmailKey, email);
                                Paper.book().write(Global.UserPasswordKey, password);

                                Toast.makeText(Login.this, "Connecté avec succès", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Global.currentUser = user;
                                Global.currentUserId = replacedEmail;

                                Intent intent = new Intent(Login.this, NavigationBar.class);
                                startActivity(intent);
                                finish();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(Login.this, "Le mot de passe est incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(Login.this, "Un compte avec cet email " + email + " n'existe pas.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
