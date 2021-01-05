package com.example.greenleaf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.greenleaf.ui.chat.ChatFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AddContactChat extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact_chat);

        bottomNavigation = findViewById(R.id.bottomnav);
        bottomNavigation.setSelectedItemId(R.id.navigation_home);

        BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_add_contact:
                                //startActivity(new Intent(root.getContext(),AddContact.class));
                                //openFragment(HomeFragment.newInstance("", ""));
                                return true;
                            case R.id.navigation_home:
                                startActivity(new Intent(getApplicationContext(), ChatFragment.class));
                                //openFragment(SmsFragment.newInstance("", ""));
                                return true;
                        }
                        return false;
                    }
                };

        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
