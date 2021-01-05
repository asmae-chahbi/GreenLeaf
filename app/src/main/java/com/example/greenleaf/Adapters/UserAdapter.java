package com.example.greenleaf.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenleaf.Model.Livre;
import com.example.greenleaf.Model.Message;
import com.example.greenleaf.Model.User;
import com.example.greenleaf.R;
import com.example.greenleaf.ViewHolder.BookViewHolder;
import com.example.greenleaf.ViewHolder.MyViewHolder;
import com.example.greenleaf.ViewHolder.UserViewHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private List<User> listUsers;
    private HashMap<String, Message> mapLastMessage;
    Calendar calendar = Calendar.getInstance();

    public UserAdapter(List<User> users ){
        this.listUsers = users;
    }

    public UserAdapter(List<User> users , HashMap<String, Message> mapLastMessage){
        this.listUsers = users;
        this.mapLastMessage = mapLastMessage;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int position) {
        User user =listUsers.get(position);
        if(mapLastMessage !=null){
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            String date = currentDate.format(calendar.getTime());
            if(mapLastMessage.get(user.getEmail()).isToday(date))
                userViewHolder.dateMessage.setText(mapLastMessage.get(user.getEmail()).getCurrentTime());
            else
                userViewHolder.dateMessage.setText(mapLastMessage.get(user.getEmail()).getCurrentDate());
            String m = mapLastMessage.get(user.getEmail()).getMessage();
            if(m.length()<25)
                userViewHolder.user_last_message.setText(m);
            else
                userViewHolder.user_last_message.setText(m.substring(0,25)+"...");
        }
        userViewHolder.display(user);
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

}
