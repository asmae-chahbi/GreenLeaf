package com.example.greenleaf.ui.chat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.greenleaf.Adapters.UserAdapter;
import com.example.greenleaf.Model.Message;
import com.example.greenleaf.Model.User;
import com.example.greenleaf.R;
import com.example.greenleaf.global.Global;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MyContacts extends Fragment {

    private RecyclerView recyclerView;
    private TextView text;
    private List<User> list;
    private UserAdapter userAdapter;
    private HashMap<String, Message> mapLastMessage;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
    public MyContacts() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    public static MyContacts newInstance(String param1, String param2) {
        MyContacts fragment = new MyContacts();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_my_contacts, container, false);

        recyclerView = root.findViewById(R.id.List);
        text = root.findViewById(R.id.noUser);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = new ArrayList<>();
        mapLastMessage = new HashMap<>();

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        list = new ArrayList<>();
        mapLastMessage = new HashMap<>();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot npsnapshot : dataSnapshot.child("Messages").getChildren()){
                    String composedId = npsnapshot.getKey();
                    String []IdTab = composedId.split("\\*");
                    for(int i =0; i<2; i++){
                        if(IdTab[i].equals(Global.currentUserId)){
                            //Log.i("contact :"+i,IdTab[(i+1)%2]);
                            User user= dataSnapshot.child("Users").child(IdTab[(i+1)%2]).getValue(User.class);
                            list.add(user);
                            Message lastMessage = null;
                            for(DataSnapshot mDataSnapshot : dataSnapshot.child("Messages").child(composedId).getChildren()){
                                Message m = mDataSnapshot.getValue(Message.class);
                                if(lastMessage == null)
                                    lastMessage = m;
                                else{
                                    if(m.compareTo(lastMessage)<0) {
                                        lastMessage = m;
                                    }
                                }
                            }
                            mapLastMessage.put(user.getEmail(),lastMessage);
                        }
                    }

                    Collections.sort(list, new Comparator<User>() {
                        @Override
                        public int compare(User u1, User u2) {
                            Message m1 = mapLastMessage.get(u1.getEmail());
                            Message m2 = mapLastMessage.get(u2.getEmail());
                            return m1.compareTo(m2);
                        }
                    });

                    if(list.size() ==0){
                        text.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    else{
                        text.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        userAdapter =new UserAdapter(list, mapLastMessage);
                        recyclerView.setAdapter(userAdapter);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
