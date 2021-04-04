package com.example.humanactivitydetection.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.humanactivitydetection.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    Button track;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        track = root.findViewById(R.id.track);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String test = track.getText().toString();
                if(test.equals("Start Tracking")){
                    Toast.makeText(getContext(),"Started Tracking",Toast.LENGTH_SHORT).show();
                    track.setText("Stop Tracking");
                }
                else if(test.equals("Stop Tracking")){
                    Toast.makeText(getContext(),"Stopped Tracking",Toast.LENGTH_SHORT).show();
                    track.setText("Start Tracking");
                }

            }
        });
        return root;
    }
}