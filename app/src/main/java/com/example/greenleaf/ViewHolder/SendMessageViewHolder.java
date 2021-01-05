package com.example.greenleaf.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenleaf.Model.Message;
import com.example.greenleaf.R;

public class SendMessageViewHolder extends RecyclerView.ViewHolder {

    private Message message;
    private TextView message_sent, message_sent_hour;

    public SendMessageViewHolder(@NonNull View itemView) {
        super(itemView);

        message_sent = itemView.findViewById(R.id.message_sent);
        message_sent_hour = itemView.findViewById(R.id.message_sent_hour);
    }

    public void display( Message message){
        this.message= message;
        message_sent.setText(message.getMessage());
        message_sent_hour.setText(message.getCurrentTime().substring(0,5));
    }

}
