package com.example.greenleaf.ViewHolder;

import android.content.Intent;
import android.util.Log;
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

public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView bookTitle, bookAuthor , bookPrice;
    public ImageView bookImage;
    public Livre livre;
    private View itemView;


    public BookViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        bookTitle = (TextView) itemView.findViewById(R.id.item_book_title);
        bookAuthor= (TextView) itemView.findViewById(R.id.item_book_author);
        bookPrice = (TextView) itemView.findViewById(R.id.item_book_price);
        bookImage = (ImageView) itemView.findViewById(R.id.item_book_image);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String replacedEmail  = Global.currentUser.getEmail().replace(".","_DOT_");
        Global.currentBook = livre;
        if( livre.getUserId().equals(replacedEmail)){
            Intent intent = new Intent(v.getContext(), BookDescriptionOwner.class);
            v.getContext().startActivity(intent);
        }else{
            Intent intent = new Intent(v.getContext(), BookDescription.class);
            v.getContext().startActivity(intent);
        }

    }

    public void setLivre(Livre l){ livre = l;}
}
