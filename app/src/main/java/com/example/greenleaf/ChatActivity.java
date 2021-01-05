package com.example.greenleaf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.greenleaf.Adapters.MessageAdapter;
import com.example.greenleaf.Model.Message;
import com.example.greenleaf.ViewHolder.RecieveMessageViewHolder;
import com.example.greenleaf.global.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    /*LinearLayout layout;
    ScrollView scrollView;
    Firebase reference1,*/


    private ImageView sendButton;
    private EditText messageT;
    private TextView name;
    private RecyclerView recyclerView;
    private DatabaseReference messageReference;
    private List<Message> list;
    private MessageAdapter adapter;
    private CircleImageView image;
    private ImageView mic;
    private static  final int RECOGNIZER_RESULT = 1 ;
    public TextToSpeech mTTS;
    private Message lastMessage;
    boolean lastExiste = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageReference = FirebaseDatabase.getInstance().getReference("Messages");

        sendButton = findViewById(R.id.chat_envoyer);
        messageT = findViewById(R.id.message);
        name = findViewById(R.id.name);
        mic = findViewById(R.id.mic);

        //if(Global.otherUser != null) {
        name.setText(Global.otherUser.getFirstName() + " " + Global.otherUser.getLastName());
        image = findViewById(R.id.chat_pic);
        Picasso.get().load(Global.otherUser.getImage()).into(image);

        recyclerView = findViewById(R.id.messages_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, final int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if ( bottom < oldBottom) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(bottom);
                        }
                    }, 100);
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mTTS=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.getDefault());
                    if ( result == TextToSpeech.LANG_MISSING_DATA ||result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS","Langue not supportée");
                    }

                }else {
                    Log.e("TTS","Initialisation impossible");
                }
            }

        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageT.getText().toString();

                if(!messageText.equals("")){
                    messageT.setText("");
                    Calendar calendar = Calendar.getInstance();

                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");

                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");

                    String date = currentDate.format(calendar.getTime()).replace('.','_') + currentTime.format(calendar.getTime()).replace('.','_');

                    Map<String, Object> map = new HashMap<>();
                    map.put("message", messageText);
                    map.put("currentDate", currentDate.format(calendar.getTime()) );
                    map.put("currentTime", currentTime.format(calendar.getTime()));
                    map.put("de", Global.value_1_2);
                    map.put("vu",false);

                    messageReference.child(Global.composedKey).child(date).updateChildren(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {

                                }
                            });


                }
            }
        });

        list = new ArrayList<>();


        messageReference.child(Global.composedKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Message message = dataSnapshot.getValue(Message.class);
                if(message != null){
                    if(!lastExiste){
                        lastMessage = new Message(message.getCurrentDate(),message.getCurrentTime());
                        list.add(lastMessage);
                        lastExiste = true;
                    }else{
                        if(!lastMessage.getCurrentDate().equals(message.getCurrentDate())){
                            lastMessage = new Message(message.getCurrentDate(),message.getCurrentTime());
                            list.add(lastMessage);
                        }
                    }
                    if(!message.getDe().equals(Global.value_1_2) && message.getVu() == false) {
                        Message m = new Message(message.getMessage(),message.getCurrentDate(),message.getCurrentTime(),message.getDe(),message.getVu());
                        message.setVu(true);
                        messageReference.child(Global.composedKey).child(dataSnapshot.getKey()).setValue(message);
                        list.add(m);
                    }else{
                        list.add(message);
                    }
                    adapter= new MessageAdapter(list);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.scrollToPosition(list.size() - 1);

                    readLastMessages();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speech to text");
                startActivityForResult(intent,RECOGNIZER_RESULT);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == RECOGNIZER_RESULT && resultCode ==RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            messageT.setText(matches.get(0).toString());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }



    public void readLastMessages(){


        int i;
        for(i=list.size()-1; i>=0 ;i--){
            if(!list.get(i).getMessage().equals(""))
                if(list.get(i).getDe().equals(Global.value_1_2) || (!list.get(i).getDe().equals(Global.value_1_2) && list.get(i).getVu()==true))
                    break;
        }
        this.j = i+1;
        TTSSpeaking();
    }

    private int j;
    public void TTSSpeaking(){

        final Handler h =new Handler();
        Runnable r = new Runnable() {

            public void run() {

                if(j < list.size()){
                    if (!mTTS.isSpeaking()) {
                        while(j < list.size() && list.get(j).getMessage().equals(""))
                            j++;
                        if(j < list.size()){
                            list.get(j).setVu(true);
                            float pitch = (float) 1.0;
                            float speed = (float) 1.0 ;
                            mTTS.setPitch(pitch);
                            mTTS.setSpeechRate(speed);
                            mTTS.speak(list.get(j).getMessage(), TextToSpeech.QUEUE_FLUSH,null);
                            j++;
                            h.postDelayed(this, 1000);
                        }
                    }else{
                        h.postDelayed(this, 1000);
                    }
                }

            }
        };

        h.postDelayed(r, 1000);
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
