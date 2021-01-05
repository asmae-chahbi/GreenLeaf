package com.example.greenleaf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greenleaf.global.Global;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ModifyProfil extends AppCompatActivity {

    private CircleImageView profileImageView;
    private String email, name, tel;
    private TextView Name, Email , Tel;
    private TextView change_picture;
    private ImageView editname, edittel;
    private Button Validate;
    private Uri imageUri;
    private boolean clicked;
    private static final int ProfilImage = 1;
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String replacedEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profil);
        Intent intent = getIntent();

        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        Name = findViewById(R.id.EditProfileName);
        Email = findViewById(R.id.EditProfileEmail);
        Tel = findViewById(R.id.EditProfileTel);
        profileImageView = (CircleImageView) findViewById(R.id.modify_profile_image);

        edittel = findViewById(R.id.edittel);
        Validate = findViewById(R.id.Validate);
        change_picture = findViewById(R.id.changeImage);

        Name.setText(Global.currentUser.getFirstName() + " "+ Global.currentUser.getLastName());
        Email.setText(Global.currentUser.getEmail());
        Tel.setText(Global.currentUser.getPhone());
        if (Global.currentUser.getImage()!=null && !Global.currentUser.getImage().equals("")){
            Picasso.get().load(Global.currentUser.getImage()).into(profileImageView);
        }


        edittel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tel.setText(" ");
            }
        });
        Validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        change_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, ProfilImage );
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ProfilImage && resultCode == -1 && data != null) {
            imageUri = data.getData();
            clicked = true;
            profileImageView.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Erreur", Toast.LENGTH_SHORT).show();

        }
    }

    private void uploadImage()
    {

        Toast.makeText(this, "Veuillez patienter pendant que nous mettons à jour les informations de votre compte", Toast.LENGTH_SHORT).show();

        if (imageUri != null)
        {
            replacedEmail = Global.currentUser.getEmail().replace(".","_DOT_");
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(replacedEmail + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            if (task.isSuccessful())
                            {
                                Uri downloadUrl = task.getResult();
                                String myUrl = downloadUrl.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap. put("phone", Tel.getText().toString());
                                userMap. put("image", myUrl);
                                ref.child(replacedEmail).updateChildren(userMap);


                                Global.currentUser.setImage(myUrl);
                                Toast.makeText(getApplication(), "Profile Info update successfully.", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ModifyProfil.this, NavigationBar.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Erreur.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "image is not selected.", Toast.LENGTH_SHORT).show();
        }
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
