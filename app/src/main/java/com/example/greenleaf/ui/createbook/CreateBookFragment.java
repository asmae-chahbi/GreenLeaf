package com.example.greenleaf.ui.createbook;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greenleaf.R;
import com.example.greenleaf.global.Global;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class CreateBookFragment extends Fragment {

    private CreateBookViewModel mViewModel;
    private static final int GalleryImage = 1;
    private Uri imageUri;
    private ProgressDialog loadingBar;
    private boolean fileDestroyed = false;
    String id;

    private Button addBook;
    private ImageView inputImage;
    private ImageButton importImage;
    private EditText inputTitle, inputPrice, inputAuthor;
    private String title, price, category, image, author;
    private String saveCurrentDate, saveCurrentTime, randomKey, downloadImageUrl, audioPath;

    private Spinner inputCategorieSpinner;
    private StorageReference imageRef, audioRef;
    private DatabaseReference livreRef;
    private Context context;

    String pathSave ="" ;
    MediaRecorder mediaRecorder ;
    MediaPlayer mediaPlayer ;
    final  int REQUEST_PERMISSION_CODE=1000 ;

    AppCompatSeekBar playerSeek;

    TextView textTotalTime, textCurrentTime;
    ImageButton start_stopRecording, start_stop;
    boolean startRecording;

    private Handler handler = new Handler();

    public static CreateBookFragment newInstance() {
        return new CreateBookFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_createbook, container, false);

        inputImage = view.findViewById(R.id.inputImage);
        importImage = view.findViewById(R.id.importImage);
        inputTitle = view.findViewById(R.id.inputTitle);
        inputPrice = view.findViewById(R.id.inputPrice);
        inputCategorieSpinner = view.findViewById(R.id.inputCategorySpinner);
        inputAuthor = view.findViewById(R.id.inputAuthor);
        addBook = view.findViewById(R.id.addBook);

        livreRef = FirebaseDatabase.getInstance().getReference().child("Livre");
        imageRef = FirebaseStorage.getInstance().getReference().child("Book Images");
        audioRef = FirebaseStorage.getInstance().getReference().child("Book Audio");
        context  = getContext();

        importImage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OpenGallery();
                    }
                }
        );

        addBook.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        verifyDataNewBook();
                    }
                }
        );


        start_stopRecording = view.findViewById(R.id.start_stopRecording);
        start_stop = view.findViewById(R.id.start_stop);
        textTotalTime = view.findViewById(R.id.textTotalTime);
        textCurrentTime = view.findViewById(R.id.textCurrentTime);
        playerSeek = view.findViewById(R.id.playerSeek);


        playerSeek.setMax(100);

        if ( checkPermissionForDevice()){

            start_stopRecording.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(startRecording){
                        //to stop recording
                        mediaRecorder.stop();
                        start_stopRecording.setImageResource(R.drawable.ic_mic);
                        startRecording = false;
                    }else{
                        if(pathSave  == ""){
                            pathSave = context.getExternalFilesDir(null).getAbsolutePath()+"/"+ UUID.randomUUID().toString()+"_audio_recorder.3gp";
                        }
                        setupMediaRecorder();
                        try {
                            mediaRecorder.prepare();

                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        mediaRecorder.start();
                        start_stopRecording.setImageResource(R.drawable.stop);
                        Toast.makeText(context,"recording", Toast.LENGTH_SHORT).show();
                        startRecording = true;
                    }
                }
            });


            start_stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pathSave != ""){
                        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
                            handler.removeCallbacks(updater);
                            mediaPlayer.pause();
                            start_stop.setImageResource(R.drawable.play);
                        }else{
                            mediaPlayer =new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(pathSave);
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                                textTotalTime.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            start_stop.setImageResource(R.drawable.pause);
                            updateSeekBar();
                        }
                    }
                }
            });


            playerSeek.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(mediaPlayer!=null && mediaPlayer.isPlaying()){
                        AppCompatSeekBar seekBar = (AppCompatSeekBar)v;
                        int playPosition = (mediaPlayer.getDuration()/100)*seekBar.getProgress();
                        mediaPlayer.seekTo(playPosition);
                        textCurrentTime.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                    }
                    return false;
                }
            });


        }
        else {
            requestPermission();

        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CreateBookViewModel.class);


    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryImage);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryImage  &&  resultCode==-1  &&  data!=null)
        {
            imageUri = data.getData();
            inputImage.setImageURI(imageUri);
        }
    }

    private void verifyDataNewBook(){
        title = inputTitle.getText().toString();
        price = inputPrice.getText().toString();
        category = String.valueOf(inputCategorieSpinner.getSelectedItem());//inputCategory.getText().toString();
        author = inputAuthor.getText().toString();

        if(imageUri == null){
            Toast.makeText(context, "veuillez spécifier l'image du livre", Toast.LENGTH_SHORT).show();
        }else if(title.equals("")){
            Toast.makeText(context, "veuillez spécifier le titre du livre", Toast.LENGTH_SHORT).show();
        }else if(author.equals("")){
            Toast.makeText(context, "veuillez spécifier l'auteur du livre", Toast.LENGTH_SHORT).show();
        }else if(price.equals("")){
            Toast.makeText(context, "veuillez spécifier le prix du livre", Toast.LENGTH_SHORT).show();
        }else if(category.equals("")){
            Toast.makeText(context, "veuillez spécifier la categorie du livre", Toast.LENGTH_SHORT).show();
        }else if(pathSave  == ""){
            Toast.makeText(context, "Veuillez enregistrer la description auditive du livre", Toast.LENGTH_SHORT).show();
        } else{
            addNewBook();
        }
    }

    private void addNewBook(){
        loadingBar = new ProgressDialog(context);
        loadingBar.setTitle("Ajout du livre "+title);
        loadingBar.setMessage("Attendez s'il vous plait");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        randomKey = saveCurrentDate + saveCurrentTime;


        randomKey = currentDate.format(calendar.getTime()) + currentTime.format(calendar.getTime());

        livreRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    id = String.valueOf((int)dataSnapshot.getChildrenCount() + 1);
                }else
                    id = "1";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final StorageReference filePath = imageRef.child(imageUri.getLastPathSegment() + randomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(context, "Erreur: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(context, "Image récupérée avec succès...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(context, "URL de l'mage récupérée avec succès...", Toast.LENGTH_SHORT).show();

                            //SavebookToDatabase();
                            saveAudioToDatabase();
                        }
                    }
                });
            }
        });

    }

    private void saveAudioToDatabase(){

        final StorageReference audioFilePath = audioRef.child(id);
        Uri uri= Uri.fromFile(new File(pathSave));
/*
        audioFilePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                audioPath = audioFilePath.getDownloadUrl().toString();
                SavebookToDatabase();
            }
        });*/

        final UploadTask uploadTask = audioFilePath.putFile(uri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(context, "Erreur: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(context, "Audio récupérée avec succès...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        audioPath = audioFilePath.getDownloadUrl().toString();
                        return audioFilePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            File file = new File(pathSave);
                            file.delete();
                            fileDestroyed = true;
                            audioPath = task.getResult().toString();
                            Toast.makeText(context, "URL de l'audio récupérée avec succès...", Toast.LENGTH_SHORT).show();
                            SavebookToDatabase();
                        }
                    }
                });
            }
        });

    }

    private void SavebookToDatabase(){
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("bookId", id);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("categorie", category);
        productMap.put("image", downloadImageUrl);
        productMap.put("prix", price);
        productMap.put("titre", title);
        productMap.put("auteur",author);
        productMap.put("audio",audioPath);
        productMap.put("userId", Global.currentUser.getEmail().replace(".","_DOT_"));

        livreRef.child(id).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {

                            loadingBar.dismiss();
                            Toast.makeText(context, "Le livre a bien été ajouté.", Toast.LENGTH_SHORT).show();

                            //inputImage.
                            inputTitle.setText("");
                            inputPrice.setText("");
                            inputAuthor.setText("");
                            inputImage.setImageURI(null);
                            //Intent intent = new Intent(context, SalesFragment.class);
                            //startActivity(intent);
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(context, "Erreur: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private  void  setupMediaRecorder(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(  getActivity() ,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO} ,REQUEST_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {   Toast.makeText( context ,"granted permis ",Toast.LENGTH_SHORT).show();}
                else{
                    Toast.makeText( context , "permis ",Toast.LENGTH_SHORT).show();
                }
            }break;

        }
    }


    private boolean checkPermissionForDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission( context , Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(context , Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED ;
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            textCurrentTime.setText(milliSecondsToTimer(currentDuration));
        }
    };

    private void updateSeekBar(){
        if(mediaPlayer.isPlaying()){
            playerSeek.setProgress((int)(((float) mediaPlayer.getCurrentPosition()/ mediaPlayer.getDuration())*100));
            handler.postDelayed(updater,1000);
            if(mediaPlayer.getCurrentPosition()+1000 >= mediaPlayer.getDuration()){
                start_stop.setImageResource(R.drawable.play);
                textCurrentTime.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
                playerSeek.setProgress(100);
            }
        }else{
            mediaPlayer.pause();
        }
    }

    private String milliSecondsToTimer(long milliSeconds) {
        String timerString = "";

        int hours = (int)(milliSeconds/(1000 * 60 *60));
        int minutes = (int)((milliSeconds%(1000 * 60 *60))/(1000 * 60 ));
        int seconds= (int)((milliSeconds%(1000 * 60 *60))%(1000*60)/1000);

        if(hours>0) timerString = hours + ":";

        if(minutes<10) timerString += "0";

        timerString += minutes + ":";

        if(seconds<10) timerString += "0";

        timerString += seconds;
        return timerString;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!fileDestroyed ){
            File file = new File(pathSave);
            file.delete();
        }
    }

}







