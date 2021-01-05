package com.example.greenleaf.ui.help;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.greenleaf.R;


public class HelpFragment extends Fragment {

    private HelpViewModel helpViewModel;
    private VideoView videoView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        helpViewModel =
                ViewModelProviders.of(this).get(HelpViewModel.class);
        View root = inflater.inflate(R.layout.fragment_help, container, false);


        videoView = root.findViewById(R.id.videoView);

        Uri uri= Uri.parse("https://firebasestorage.googleapis.com/v0/b/green-leaf-43ece.appspot.com/o/demo.mp4?alt=media&token=db67bb4f-6826-446c-ac55-3ceabbbdd106");
        videoView.setVideoURI(uri);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.resume();
    }
}

