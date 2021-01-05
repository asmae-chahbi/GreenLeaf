package com.example.greenleaf.ViewHolder;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenleaf.ChatActivity;
import com.example.greenleaf.Model.User;
import com.example.greenleaf.R;
import com.example.greenleaf.global.Global;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView imageView;
    private TextView name;
    private User user;
    public TextView dateMessage, user_last_message;

    public UserViewHolder(@NonNull final View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.user_item_Name);
        imageView  = itemView.findViewById(R.id.user_item_image);
        dateMessage = itemView.findViewById(R.id.dateMessage);
        user_last_message = itemView.findViewById(R.id.user_last_message);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String r1 = Global.currentUser.getEmail().replace(".","_DOT_");
                String r2 = user.getEmail().replace(".","_DOT_");

                if(r1.compareTo(r2) >0) {
                    Global.value_1_2 = "1";
                    Global.composedKey = r1 + "*" + r2;
                }else{
                    Global.value_1_2 = "2";
                    Global.composedKey = r2 + "*" + r1;
                }
                Global.otherUser = user;

                Intent intent = new Intent(itemView.getContext(), ChatActivity.class);
                itemView.getContext().startActivity(intent);
            }
        });

    }

    public void display(User user){
        this.user = user;
        name.setText(user.getFirstName()+" "+user.getLastName());
        Picasso.get().load(user.getImage()).into(imageView);
    }
}
