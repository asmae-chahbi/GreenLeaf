package com.example.greenleaf.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenleaf.Model.Message;
import com.example.greenleaf.R;

public class DayMessageViewHolder extends RecyclerView.ViewHolder {

    private Message message;
    private TextView message_day;

    public DayMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        message_day = itemView.findViewById(R.id.message_day);

    }

    public void display( Message message){
        this.message= message;
        message_day.setText(message.getCurrentDate());
    }

}
