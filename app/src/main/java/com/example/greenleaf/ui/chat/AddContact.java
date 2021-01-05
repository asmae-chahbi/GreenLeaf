package com.example.greenleaf.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.greenleaf.Adapters.UserAdapter;
import com.example.greenleaf.Model.Message;
import com.example.greenleaf.Model.User;
import com.example.greenleaf.R;
import com.example.greenleaf.global.Global;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class AddContact extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerView;
    private TextView text;
    private List<User> list, listExiste, finalList;
    private UserAdapter userAdapter;
    //private HashMap<String, Integer> mapUsers;

    private String mParam1;
    private String mParam2;
    final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();

    private View root;

    public AddContact() {
        // Required empty public constructor
    }
    public static AddContact newInstance(String param1, String param2) {
        AddContact fragment = new AddContact();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =inflater.inflate(R.layout.fragment_add_contact, container, false);

        recyclerView = root.findViewById(R.id.listNewContact);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return root;

    }

    @Override
    public void onResume() {
        super.onResume();
        list = new ArrayList<>();
        listExiste = new ArrayList<>();
        finalList = new ArrayList<>();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot npsnapshot : dataSnapshot.child("Users").getChildren()) {
                    User user=npsnapshot.getValue(User.class);
                    if(!Global.currentUser.getEmail().equals(user.getEmail())){
                        list.add(user);

                    }
                }

                for (DataSnapshot npsnapshot : dataSnapshot.child("Messages").getChildren()){
                    String composedId = npsnapshot.getKey();
                    String []IdTab = composedId.split("\\*");
                    for(int i =0; i<2; i++){
                        if(IdTab[i].equals(Global.currentUserId)){
                            User user= dataSnapshot.child("Users").child(IdTab[(i+1)%2]).getValue(User.class);
                            listExiste.add(user);
                        }
                    }
                }

                Boolean existe;
                for(User user : list){
                    existe = false;
                    for(User u : listExiste){
                        if( u.getEmail().equals(user.getEmail())) {
                            existe = true;
                            break;
                        }
                    }
                    if(!existe)
                        finalList.add(user);

                }

                Collections.sort(finalList, new Comparator<User>() {
                    @Override
                    public int compare(User o1, User o2) {
                        int n;
                        if((n = o1.getFirstName().compareTo(o2.getFirstName())) == 0)
                            return o1.getLastName().compareTo(o2.getLastName());
                        else
                            return n;
                    }
                });




                userAdapter =new UserAdapter(finalList);
                recyclerView.setAdapter(userAdapter);
                recyclerView.setItemAnimator(new DefaultItemAnimator());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
