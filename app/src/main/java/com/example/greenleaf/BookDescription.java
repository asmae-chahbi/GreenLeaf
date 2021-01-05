package com.example.greenleaf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greenleaf.Model.Demande;
import com.example.greenleaf.Model.Livre;
import com.example.greenleaf.Model.User;
import com.example.greenleaf.global.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

public class BookDescription extends AppCompatActivity {

    public TextView bookTitle, bookPrice, bookCategory , bookAuthor, demandeText;
    public ImageView bookImage, contact;
    public Livre livre;

    public ImageButton contactBoutton;

    private ProgressDialog loadingBar;
    private DatabaseReference demandeRef;
    private User user;

    String pathSave ="" ;
    MediaPlayer mediaPlayer ;
    Context context;

    private Handler handler = new Handler();

    AppCompatSeekBar playerSeek;

    TextView textTotalTime, textCurrentTime;
    ImageButton start_stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_description);



        bookTitle = findViewById(R.id.title);
        bookCategory = findViewById(R.id.category);
        bookImage = findViewById(R.id.imagebook);
        bookAuthor = findViewById(R.id.author);
        bookPrice = findViewById(R.id.price);
        demandeText = findViewById(R.id.demande);

        loadingBar = new ProgressDialog(this);

        livre = Global.currentBook;
        bookTitle.setText(livre.getTitre());
        bookAuthor.setText(livre.getAuteur());
        bookCategory.setText(livre.getCategorie());
        bookPrice.setText("prix : "+livre.getPrix()+"DH");
        Picasso.get().load(livre.getImage()).into(bookImage);

        initialiseRequestValue();

        start_stop = findViewById(R.id.start_stop);
        textTotalTime = findViewById(R.id.textTotalTime);
        textCurrentTime = findViewById(R.id.textCurrentTime);
        playerSeek = findViewById(R.id.playerSeek);
        context = getApplicationContext();
        playerSeek.setMax(100);

        prepareMediaPlayer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null){
                    if(mediaPlayer.isPlaying()){
                        handler.removeCallbacks(updater);
                        mediaPlayer.pause();
                        start_stop.setImageResource(R.drawable.play);
                    }else{
                        mediaPlayer.start();
                        start_stop.setImageResource(R.drawable.pause);
                        updateSeekBar();
                    }
                }
            }
        });

        contactBoutton = findViewById(R.id.contactImage);

        FirebaseDatabase.getInstance().getReference().child("Users").child(Global.currentBook.getUserId()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                    } @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }});


        contactBoutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactClicked();
            }
        });

        demandeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBook();
            }
        });
    }

    public void prepareMediaPlayer(){
        if(livre.getAudio()!=null && livre.getAudio()!=""){
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(livre.getAudio());
                mediaPlayer.prepare();
                textTotalTime.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            playerSeek.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(mediaPlayer!=null && mediaPlayer.isPlaying()){
                        AppCompatSeekBar seekBar = (AppCompatSeekBar)v;
                        int playPosition = (mediaPlayer.getDuration()/100)*seekBar.getProgress();
                        mediaPlayer.seekTo(playPosition);
                        textCurrentTime.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                    }
                    return false;
                }
            });
        }
    }

    public void initialiseRequestValue(){
        demandeRef = FirebaseDatabase.getInstance().getReference().child("Demandes");
        if(livre.getAcheteurId()!=null && !livre.getAcheteurId().equals("")) {
            if(!livre.getAcheteurId().equals(Global.currentUserId))
                demandeText.setText("Livre vendu");
            else
                demandeText.setText("Vous avez acheter ce livre");
        }else{
        demandeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String id = livre.getBookId();
                String replacedEmail = Global.currentUser.getEmail().replace(".","_DOT_");
                if ((dataSnapshot.child(id).exists()) && (dataSnapshot.child(id).child(replacedEmail).exists()))
                {
                    demandeText.setText("Demande envoyé, cliquez pour annuler");
                }
                else
                {
                    demandeText.setText("Cliquez pour demander");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        }
    }

    public void requestBook(){
        if(livre.getAcheteurId()!=null && !livre.getAcheteurId().equals("")) {
            //demandeText.setText("Livre vendu");
        }else{
        loadingBar.setTitle("Traitement de demande");
        loadingBar.setMessage("Veuillez patienter");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        demandeRef = FirebaseDatabase.getInstance().getReference().child("Demandes");
        demandeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                String id = livre.getBookId();
                String replacedEmail = Global.currentUser.getEmail().replace(".","_DOT_");
                if (!(dataSnapshot.child(id).exists()) || !(dataSnapshot.child(id).child(replacedEmail).exists()))
                {
                    HashMap<String, Object> requestdataMap = new HashMap<>();
                    requestdataMap .put("userEmail", replacedEmail);

                    demandeRef.child(id).child(replacedEmail).updateChildren(requestdataMap )
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(BookDescription.this, "Demande envoyé veuillez communiquer avec le vendeur", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        demandeText.setText("Demande envoyé, cliquez pour annuler");
                                        //Intent intent = new Intent(Registering.this, Login.class);
                                        //startActivity(intent);
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(BookDescription.this, "Probléme de connexion réseau: veuillez réessayer après un certain temps", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("Demandes").child(id).child(replacedEmail).removeValue();
                    demandeText.setText("Cliquez pour demander");
                    Toast.makeText(BookDescription.this, "Demande annulé", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        }
    }


    public void contactClicked(){

        String r1 = Global.currentUser.getEmail().replace(".","_DOT_");
        String r2 = Global.currentBook.getUserId();

        if(r1.compareTo(r2) >0) {
            Global.value_1_2 = "1";
            Global.composedKey = r1 + "*" + r2;
        }else{
            Global.value_1_2 = "2";
            Global.composedKey = r2 + "*" + r1;
        }


        Global.otherUser = user;


        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);

    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            textCurrentTime.setText(milliSecondsToTimer(currentDuration));
        }
    };

    private void updateSeekBar(){
        if(mediaPlayer.isPlaying()){
            playerSeek.setProgress((int)(((float) mediaPlayer.getCurrentPosition()/ mediaPlayer.getDuration())*100));
            handler.postDelayed(updater,1000);
            if(mediaPlayer.getCurrentPosition()+1000 >= mediaPlayer.getDuration()){
                start_stop.setImageResource(R.drawable.play);
            }
        }else{
            mediaPlayer.pause();
        }
    }

    private String milliSecondsToTimer(long milliSeconds) {
        String timerString = "";

        int hours = (int)(milliSeconds/(1000 * 60 *60));
        int minutes = (int)((milliSeconds%(1000 * 60 *60))/(1000 * 60 ));
        int seconds= (int)((milliSeconds%(1000 * 60 *60))%(1000*60)/1000);

        if(hours>0) timerString = hours + ":";

        if(minutes<10) timerString += "0";

        timerString += minutes + ":";

        if(seconds<10) timerString += "0";

        timerString += seconds;
        return timerString;
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
