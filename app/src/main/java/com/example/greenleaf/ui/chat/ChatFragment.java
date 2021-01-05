package com.example.greenleaf.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.greenleaf.Adapters.TabAdapter;
import com.example.greenleaf.Adapters.UserAdapter;
import com.example.greenleaf.AddContactChat;
import com.example.greenleaf.Model.User;
import com.example.greenleaf.R;
import com.example.greenleaf.global.Global;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatFragment extends Fragment {

    private ChatViewModel chatViewModel;

    private RecyclerView recyclerView;
    private TextView text;
    private List<User> list;
    private UserAdapter userAdapter;
    BottomNavigationView bottomNavigation;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chatViewModel =
                ViewModelProviders.of(this).get(ChatViewModel.class);
        root = inflater.inflate(R.layout.fragment_chat_tab, container, false);




        bottomNavigation = root.findViewById(R.id.bottomnav);
        bottomNavigation.setSelectedItemId(R.id.navigation_home);

        BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_add_contact:
                                openFragment(AddContact.newInstance("", ""));
                                return true;
                            case R.id.navigation_home:
                                openFragment(MyContacts.newInstance("", ""));
                                return true;
                        }
                        return false;
                    }
                };

        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(MyContacts.newInstance("", ""));
        return root;
    }


    public void openFragment(Fragment fragment) {
        ;
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}