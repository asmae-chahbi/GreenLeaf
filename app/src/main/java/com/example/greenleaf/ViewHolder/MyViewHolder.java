package com.example.greenleaf.ViewHolder;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenleaf.BookDescription;
import com.example.greenleaf.BookDescriptionOwner;
import com.example.greenleaf.Model.Livre;
import com.example.greenleaf.R;
import com.example.greenleaf.global.Global;
import com.squareup.picasso.Picasso;

public class MyViewHolder extends RecyclerView.ViewHolder{


    private final TextView titre;
    private final TextView auteur;
    private final ImageView img;
    private Livre currentBook;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        titre = itemView.findViewById(R.id.titre);
        auteur = itemView.findViewById(R.id.auteur);
        img=  itemView.findViewById(R.id.logo);



        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Global.currentBook = currentBook;
                Intent intent = new Intent(view.getContext(), BookDescription.class);
                view.getContext().startActivity(intent);
            }
        });
    }

    public void display(Livre book) {
        currentBook = book;
        auteur.setText(book.getAuteur());
        titre.setText(book.getTitre());
        Picasso.get().load(book.getImage()).into(img);
    }
}
