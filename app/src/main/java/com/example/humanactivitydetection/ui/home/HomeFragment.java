package com.example.humanactivitydetection.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.humanactivitydetection.BackgroundDetectedActivitiesService;
import com.example.humanactivitydetection.Constants;
import com.example.humanactivitydetection.R;
import com.google.android.gms.location.DetectedActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public Chronometer chronometer;
    public Chronometer chronometer2;
    public Chronometer chronometer3;
    public Chronometer chronometer4;
    public boolean running;
    public boolean running2;
    public boolean running3;
    public boolean running4;
    public boolean completeWalk = false;
    public boolean completeRun = false;
    public long pauseOffset;
    public long pauseOffset2;
    public long pauseOffset3;
    public long pauseOffset4;
    FirebaseUser mFirebaseUser;
    public DatabaseReference mDatabaseReference;

    private HomeViewModel homeViewModel;
    private String TAG = HomeFragment.class.getSimpleName();
    BroadcastReceiver broadcastReceiver;

    private TextView txtActivity, txtConfidence;
    private ImageView imgActivity;
    private Button btnStartTrcking, btnStopTracking;
    public int w;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        txtActivity = root.findViewById(R.id.txt_activity);
        txtConfidence = root.findViewById(R.id.txt_confidence);
        imgActivity = root.findViewById(R.id.img_activity);
        btnStartTrcking = root.findViewById(R.id.btn_start_tracking);
        btnStopTracking = root.findViewById(R.id.btn_stop_tracking);
        chronometer = root.findViewById(R.id.chronometer);
        chronometer2 = root.findViewById(R.id.chronometer2);
        chronometer3 = root.findViewById(R.id.chronometer3);
        chronometer4 = root.findViewById(R.id.chronometer4);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userid = mFirebaseUser.getUid();
        ArrayList<String> array = new ArrayList<>();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Goals/"+userid);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                array.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    array.add(postSnapshot.getValue().toString());

                }
                String walk_goal = array.get(1);
                w = Integer.parseInt(walk_goal) * 60000;
                String run_goal = array.get(0);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }

        });

        btnStartTrcking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTracking();
            }
        });

        btnStopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTracking();
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    handleUserActivity(type, confidence);
                }
            }
        };

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if((SystemClock.elapsedRealtime() - chronometer.getBase()) >= w){
                    Toast.makeText(getContext(),"done",Toast.LENGTH_SHORT).show();
                    pauseChronometer(getView());
                    resetChronometer(getView());
                    completeWalk = false;
                }
            }
        });

        return root;
    }

    public void startChronometer(View v){
        if(!running){
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }
    public void pauseChronometer(View v){
        if(running){
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }
    public void resetChronometer(View v){
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }





    public void startChronometer2(View v){
        if(!running2){
            chronometer2.setBase(SystemClock.elapsedRealtime() - pauseOffset2);
            chronometer2.start();
            running2 = true;
        }
    }
    public void pauseChronometer2(View v){
        if(running2){
            chronometer2.stop();
            pauseOffset2 = SystemClock.elapsedRealtime() - chronometer2.getBase();
            running2 = false;
        }
    }
    public void resetChronometer2(View v){
        chronometer2.setBase(SystemClock.elapsedRealtime());
        pauseOffset2 = 0;
    }




    public void startChronometer3(View v){
        if(!running3){
            chronometer3.setBase(SystemClock.elapsedRealtime() - pauseOffset3);
            chronometer3.start();
            running3 = true;
        }
    }
    public void pauseChronometer3(View v){
        if(running3){
            chronometer3.stop();
            pauseOffset3 = SystemClock.elapsedRealtime() - chronometer3.getBase();
            running3 = false;
        }
    }
    public void resetChronometer3(View v){
        chronometer3.setBase(SystemClock.elapsedRealtime());
        pauseOffset3 = 0;
    }





    public void startChronometer4(View v){
        if(!running4){
            chronometer4.setBase(SystemClock.elapsedRealtime() - pauseOffset4);
            chronometer4.start();
            running4 = true;
        }
    }
    public void pauseChronometer4(View v){
        if(running4){
            chronometer4.stop();
            pauseOffset4 = SystemClock.elapsedRealtime() - chronometer4.getBase();
            running4 = false;
        }
    }
    public void resetChronometer4(View v){
        chronometer4.setBase(SystemClock.elapsedRealtime());
        pauseOffset4 = 0;
    }

    private void handleUserActivity(int type, int confidence) {
        String label = getString(R.string.activity_unknown);
        int icon = R.drawable.ic_still;

        switch (type) {
            case DetectedActivity.RUNNING: {
                label = getString(R.string.activity_running);
                icon = R.drawable.ic_running;
                if(!completeWalk){
                    startChronometer(getView());
                }
                startChronometer2(getView());
                pauseChronometer3(getView());
                pauseChronometer4(getView());
                break;
            }
            case DetectedActivity.STILL: {
                label = getString(R.string.activity_still);
                if(!completeWalk){
                    startChronometer(getView());
                }
                startChronometer2(getView());
                pauseChronometer3(getView());
                pauseChronometer4(getView());
                break;
            }
            case DetectedActivity.WALKING: {
                label = getString(R.string.activity_walking);
                icon = R.drawable.ic_walking;

                break;
            }
            case DetectedActivity.UNKNOWN: {
                label = getString(R.string.activity_unknown);
                break;
            }
        }

        Log.e(TAG, "User activity: " + label + ", Confidence: " + confidence);

        if (confidence > Constants.CONFIDENCE) {
            txtActivity.setText(label);
            txtConfidence.setText("Confidence: " + confidence);
            imgActivity.setImageResource(icon);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
    }

    @Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    private void startTracking() {
        Intent intent = new Intent(getContext(), BackgroundDetectedActivitiesService.class);
        getActivity().startService(intent);
    }

    private void stopTracking() {
        Intent intent = new Intent(getContext(), BackgroundDetectedActivitiesService.class);
        getActivity().stopService(intent);
    }
}