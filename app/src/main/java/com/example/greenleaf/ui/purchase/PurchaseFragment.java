package com.example.greenleaf.ui.purchase;

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
import com.example.greenleaf.ui.sales.SalesViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PurchaseFragment extends Fragment {

    private PurchaseViewModel sendViewModel;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private List<Livre> listLivre;
    private BookAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(PurchaseViewModel.class);
        View root = inflater.inflate(R.layout.fragment_purchase, container, false);
        final TextView textView = root.findViewById(R.id.text_send);
        sendViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        recyclerView = root.findViewById(R.id.recycle_purchase);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listLivre = new ArrayList<>();

        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Livre");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()){
                        Livre l=npsnapshot.getValue(Livre.class);
                        //if(l.getUserId().equals(Global.currentUser.getEmail().replace(".","_DOT_")))
                        if(l.getUserId().equals(Global.currentUserId))
                            listLivre.add(l);
                    }
                    if(listLivre.size() == 0){
                        textView.setText("vous n'avez aucun livre proposé");
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