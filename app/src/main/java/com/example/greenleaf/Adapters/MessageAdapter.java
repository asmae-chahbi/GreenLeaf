package com.example.greenleaf.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.greenleaf.Model.Message;
import com.example.greenleaf.R;
import com.example.greenleaf.ViewHolder.DayMessageViewHolder;
import com.example.greenleaf.ViewHolder.RecieveMessageViewHolder;
import com.example.greenleaf.ViewHolder.SendMessageViewHolder;
import com.example.greenleaf.global.Global;

import java.util.List;

public class MessageAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<Message> listMessage;

    public MessageAdapter(List<Message> listMessage) {
        super();
        this.listMessage= listMessage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == Global.TYPE_SEND) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_message, parent, false);
            return new SendMessageViewHolder(view);

        } else if(viewType == Global.TYPE_RECIEVE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recieve_message, parent, false);
            return new RecieveMessageViewHolder(view);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_message, parent, false);
            return new DayMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position) == Global.TYPE_SEND) {
            ((SendMessageViewHolder) holder).display(listMessage.get(position));
        }
         else if(getItemViewType(position) == Global.TYPE_RECIEVE){
            ((RecieveMessageViewHolder) holder).display(listMessage.get(position));
        }else{
            ((DayMessageViewHolder) holder).display(listMessage.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return listMessage.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(listMessage.get(position).getMessage().equals(""))
            return Global.TYPE_DAY;
        if (listMessage.get(position).getDe().equals(Global.value_1_2)) {
            return Global.TYPE_SEND;
        } else {
            return Global.TYPE_RECIEVE;
        }
    }
}
