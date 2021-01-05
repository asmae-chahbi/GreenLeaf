package com.example.greenleaf.ui.profil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.greenleaf.ModifyProfil;
import com.example.greenleaf.R;
import com.example.greenleaf.global.Global;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilFragment extends Fragment {

    private ProfilViewModel profilViewModel;
    private LinearLayout settings;
    private AppCompatTextView InputName, InputPhoneNumber, InputEmail;
    private CircleImageView profileImageView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profilViewModel =
                ViewModelProviders.of(this).get(ProfilViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profil, container, false);


        InputName= root.findViewById(R.id.ProfileName);
        InputEmail = root.findViewById(R.id.ProfileEmail);
        InputPhoneNumber = root.findViewById(R.id.ProfileTel);

        InputName.setText(Global.currentUser.getFirstName() + " "+ Global.currentUser.getLastName());
        InputEmail.setText(Global.currentUser.getEmail());
        InputPhoneNumber.setText(Global.currentUser.getPhone());

        profileImageView = root.findViewById(R.id.profile_image);

        settings = root.findViewById(R.id.Settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intente =  new Intent(v.getContext(), ModifyProfil.class);
                startActivity(intente);
            }
        });


        if (Global.currentUser.getImage()!=null && !Global.currentUser.getImage().equals("")){
            Picasso.get().load(Global.currentUser.getImage()).into(profileImageView);
        }

        return root;
    }
}

