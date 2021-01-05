package com.example.greenleaf;

import android.os.Bundle;

import com.example.greenleaf.global.Global;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationBar extends AppCompatActivity {
    /*
        L’interface du menu principale va servir de guide à l’utilisateur, et ce en lui présentant les services offerts par l’application, ce menu comporte huit options :

        livres à vendre : les livres ajoutés par l'utilisateur courant de l'application
        Mes Livres : livres achetés par l'utilisateur courant
        Chat : contacter un vendeur o un acheteur
        Ajouter un livre : permet d’accéder à l'interface d’ajout de livre à la bibliothèque
        Profil : Consulter ou modifier le profil
        help : Consulter une démonstration de l'utilisation de l'application
        Déconnexion : supprimer les données de l'utilisateur courant
     */

    private AppBarConfiguration mAppBarConfiguration;
    private TextView name;
    private CircleImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_home);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_mesachats, R.id.nav_mesventes,
                R.id.nav_chat, R.id.nav_add_book, R.id.nav_profil, R.id.nav_help, R.id.nav_deconnexion)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        View headerView = navigationView.getHeaderView(0);
        name = headerView.findViewById(R.id.Name);
        profileImageView = headerView.findViewById(R.id.profile_image);

        if (Global.currentUser.getImage()!=null && !Global.currentUser.getImage().equals("")){
            Picasso.get().load(Global.currentUser.getImage()).into(profileImageView);
        }

        name.setText(Global.currentUser.getFirstName() + " "+ Global.currentUser.getLastName());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar_home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
