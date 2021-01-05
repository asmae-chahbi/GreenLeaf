package com.example.greenleaf.Adapters;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.*;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;


import com.example.greenleaf.Model.Livre;
import com.example.greenleaf.R;
import com.example.greenleaf.ViewHolder.MyViewHolder;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> implements Filterable {
    private List<Livre> listBook;
    private List<Livre> listBookComplete;


    public MyAdapter(List<Livre> books ){
        this.listBook=books;
        listBookComplete = new ArrayList<>(listBook);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        Livre livre =listBook.get(position);
        myViewHolder.display(livre);
    }

    @Override
    public int getItemCount() {
        return listBook.size();
    }


    @Override
    public Filter getFilter() {
        return livreFilter;
    }

    private Filter livreFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Livre> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(listBookComplete);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Livre livre : listBookComplete){
                    if(livre.getTitre().toLowerCase().contains(filterPattern) || livre.getAuteur().toLowerCase().contains(filterPattern) )
                        filteredList.add(livre);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listBook.clear();
            listBook.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}