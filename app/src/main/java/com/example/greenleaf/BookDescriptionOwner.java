package com.example.greenleaf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greenleaf.Model.Demande;
import com.example.greenleaf.Model.Livre;
import com.example.greenleaf.Model.User;
import com.example.greenleaf.global.Global;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookDescriptionOwner extends AppCompatActivity {

    public TextView bookTitle, bookCategory , bookAuthor, etat;
    public ImageView bookImage;
    public Livre livre;
    public List<String> list;
    public String id;
    public String replacedEmail;
    private Button btnSubmit;

    private HashMap<String,String> mapDemandes;

    DatabaseReference demandeRef, valueRef;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_description_owner);

        bookTitle = findViewById(R.id.titleOwner);
        bookCategory = findViewById(R.id.categoryOwner);
        bookImage = findViewById(R.id.imagebookOwner);
        bookAuthor = findViewById(R.id.authorOwner);

        mapDemandes = new HashMap<>();

        livre = Global.currentBook;
        bookTitle.setText(livre.getTitre());
        bookAuthor.setText(livre.getAuteur());
        bookCategory.setText(livre.getCategorie());
        Picasso.get().load(livre.getImage()).into(bookImage);

        etat = findViewById(R.id.etat);

        addItemsOnSpinner();
        addListenerOnButton();
    }

    public void addItemsOnSpinner() {

        spinner = (Spinner) findViewById(R.id.liste_request);
        list = new ArrayList<>();
        list.add("");

        id = livre.getBookId();
        demandeRef = FirebaseDatabase.getInstance().getReference().child("Demandes").child(id);
        demandeRef.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                      valueRef = FirebaseDatabase.getInstance().getReference().child("Demandes").child(id).child(postSnapshot.getKey());
                      valueRef.addValueEventListener(new ValueEventListener() {
                          @Override
                          public void onDataChange(DataSnapshot dataSnapshot) {
                              for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                replacedEmail = (String) snapshot.getValue();
                                //list.add((String) snapshot.getValue());
                                FirebaseDatabase.getInstance().getReference().child("Users").child(replacedEmail)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        list.add(user.getFirstName()+" "+user.getLastName());
                                        mapDemandes.put(user.getFirstName()+" "+user.getLastName(),replacedEmail);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                              }
                          }

                          @Override
                          public void onCancelled(DatabaseError databaseError) {
                              System.out.println("The read failed: " + databaseError.getCode());
                          }
                      });
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
        });

        ArrayAdapter < String > dataAdapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    String name;

    public void addListenerOnButton(){
        btnSubmit = (Button) findViewById(R.id.Validate);

        if(livre.getAcheteurId()!=null && !livre.getAcheteurId().equals("")) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(livre.getAcheteurId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            name = user.getFirstName()+" "+user.getLastName();
                            Toast.makeText(BookDescriptionOwner.this, "Livre vendu à : \n"+name, Toast.LENGTH_SHORT).show();
                            btnSubmit.setEnabled(false);
                            etat.setText("Livre vendu à : "+name);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
            });



            btnSubmit.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            TextView description_label = findViewById(R.id.description_label3);
            description_label.setVisibility(View.GONE);
        }else{
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = mapDemandes.get(String.valueOf(spinner.getSelectedItem()));
                    if (id != null && !id.equals("")) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Livre");

                        HashMap<String, Object> bookMap = new HashMap<>();
                        bookMap. put("acheteurId", id);
                        ref.child(livre.getBookId()).updateChildren(bookMap);
                    }
                    Toast.makeText(BookDescriptionOwner.this, "Livre vendu avec succes à "+String.valueOf(spinner.getSelectedItem()), Toast.LENGTH_SHORT).show();
                    btnSubmit.setEnabled(false);
                    etat.setText("Livre vendu");
                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
