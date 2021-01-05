package com.example.greenleaf.ViewHolder;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenleaf.Model.Message;
import com.example.greenleaf.R;
import com.example.greenleaf.global.Global;

import java.util.Locale;

public class RecieveMessageViewHolder extends RecyclerView.ViewHolder {

    private Message message;
    public TextView message_recieved, message_recieved_hour;
    public TextToSpeech mTTS;

    public RecieveMessageViewHolder(@NonNull View itemView) {
        super(itemView);

        message_recieved = itemView.findViewById(R.id.message_recieved);
        message_recieved_hour = itemView.findViewById(R.id.message_recieved_hour);

        mTTS=new TextToSpeech(itemView.getContext(), new TextToSpeech.OnInitListener() {
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
        message_recieved.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                speakup(message_recieved);
                return  true;
            }
        });


    }

    public void display( Message message){
        this.message= message;
        message_recieved.setText(message.getMessage());
        message_recieved_hour.setText(message.getCurrentTime().substring(0,5));
    }

    public  void speakup(TextView hi){

        String text = hi.getText().toString();
        float pitch = (float) 1.0;
        float speed = (float) 1.0 ;


        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH,null);

    }

}
