package com.example.greenleaf.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenleaf.Model.Livre;
import com.example.greenleaf.Model.User;
import com.example.greenleaf.R;
import com.example.greenleaf.ViewHolder.BookViewHolder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {
    private List<Livre> listBook;

    public BookAdapter(List<Livre> listBook) {
        super();
        this.listBook = listBook;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent,false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder bookViewHolder, int position) {
        Livre livre =listBook.get(position);
        bookViewHolder.setLivre(livre);
        bookViewHolder.bookTitle.setText(livre.getTitre());
        bookViewHolder.bookPrice.setText(livre.getPrix()+"DH");
        bookViewHolder.bookAuthor.setText(livre.getAuteur());
        Picasso.get().load(livre.getImage()).into(bookViewHolder.bookImage);
    }

    @Override
    public int getItemCount() {
        return listBook.size();
    }
}
