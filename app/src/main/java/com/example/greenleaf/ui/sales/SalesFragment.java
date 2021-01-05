package com.example.greenleaf.ui.sales;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenleaf.Adapters.BookAdapter;
import com.example.greenleaf.Model.Livre;
import com.example.greenleaf.R;
import com.example.greenleaf.global.Global;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SalesFragment extends Fragment {

    private SalesViewModel salesViewModel;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private List<Livre> listLivre;
    private BookAdapter adapter;
    DatabaseReference demandeRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        salesViewModel =
                ViewModelProviders.of(this).get(SalesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_sales, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        salesViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        recyclerView = root.findViewById(R.id.recycle_sales);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listLivre = new ArrayList<>();
        demandeRef = FirebaseDatabase.getInstance().getReference().child("Demandes");

        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Livre");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        final Livre l = npsnapshot.getValue(Livre.class);
                        if(!l.getUserId().equals(Global.currentUserId)) {
                            if (l.getAcheteurId()!= null && l.getAcheteurId().equals(Global.currentUserId))
                                listLivre.add(l);
                            else{
                                String id = l.getBookId();
                                demandeRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                                            String idUser = postSnapshot.getKey();
                                            if(idUser.equals(Global.currentUserId)){
                                                listLivre.add(l);
                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    }

                    adapter=new BookAdapter(listLivre);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return root;
    }
}