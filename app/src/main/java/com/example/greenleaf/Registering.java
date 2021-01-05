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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Registering extends AppCompatActivity {

    /*
        Page d'inscription
     */
    private TextView redirectLogin;
    private Button CreateAccountButton;
    private EditText InputFirstName, InputLastName, InputPhoneNumber, InputPassword, InputEmail;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registering);

        CreateAccountButton = (Button) findViewById(R.id.login);
        InputFirstName = (EditText) findViewById(R.id.profil_firstname);
        InputLastName = (EditText) findViewById(R.id.profil_lastname);
        InputPassword = (EditText) findViewById(R.id.password);
        InputEmail = (EditText) findViewById(R.id.profil_email);
        InputPhoneNumber = (EditText) findViewById(R.id.profil_mobile);
        loadingBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateAccount();
            }
        });

        redirectLogin = (TextView)findViewById(R.id.redirectLogin);
        redirectLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void CreateAccount()
    {
        String firstName = InputFirstName.getText().toString();
        String lastName = InputLastName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
        String email = InputEmail.getText().toString();

        if (TextUtils.isEmpty(firstName))
        {
            Toast.makeText(this, "Veuillez écrire votre prénom", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(lastName))
        {
            Toast.makeText(this, "Veuillez écrire votre nom", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Veuillez écrire votre email", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Veuillez écrire votre mot de passe", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Veuillez écrire votre numéro de téléphone", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creation de compte");
            loadingBar.setMessage("Veuillez patienter pendant que nous vérifions les informations d'identification.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatephoneNumber(firstName, lastName, email, phone, password);
        }
    }



    private void ValidatephoneNumber(final String firstName, final String lastName, final String email, final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String replacedEmail = email.replace(".","_DOT_");
                if (!(dataSnapshot.child("Users").child(replacedEmail).exists()))
                {
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("email", email);
                    userdataMap.put("firstName", firstName);
                    userdataMap.put("lastName", lastName);
                    userdataMap.put("password", password);
                    userdataMap.put("phone", phone);



                    RootRef.child("Users").child(replacedEmail).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(Registering.this, "Félicitations, votre compte a été créé", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(Registering.this, Login.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(Registering.this, "Probléme de connexion réseau: veuillez réessayer après un certain temps", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    Toast.makeText(Registering.this, "cet email  " + email + " est déjà utilisé", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(Registering.this, "Veuillez réessayer en utilisant un autre email", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Registering.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
