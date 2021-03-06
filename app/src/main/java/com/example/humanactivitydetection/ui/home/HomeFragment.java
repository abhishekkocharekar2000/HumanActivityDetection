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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.humanactivitydetection.BackgroundDetectedActivitiesService;
import com.example.humanactivitydetection.Constants;
import com.example.humanactivitydetection.R;
import com.example.humanactivitydetection.getProgress;
import com.google.android.gms.location.DetectedActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class HomeFragment extends Fragment {

    public Chronometer chronometer;
    public Chronometer chronometer2;
    public boolean running;
    public boolean running2;
    public long pauseOffset;
    public long pauseOffset2;
    FirebaseUser mFirebaseUser;
    public DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseRef;
    public String date;

    private HomeViewModel homeViewModel;
    private String TAG = HomeFragment.class.getSimpleName();
    BroadcastReceiver broadcastReceiver;

    private TextView txtActivity, txtConfidence,congo,message;
    private ImageView imgActivity;
    private Button btnStartTrcking, btnStopTracking;
    public int total_walk_int = 1;
    public int total_run_int = 1;
    public int input1;
    public int input2;
    public String total_walk;
    public String total_run;
    public String userid;
    public String walk_goal;
    public int walk_goal_int = Integer.MAX_VALUE;
    public String run_goal;
    public int run_goal_int = Integer.MAX_VALUE;
    public int type2;
    public long walkTotal = 0;
    public long runTotal = 0;
    public boolean wasWalking = false;
    public boolean wasRunning = false;


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
        congo = root.findViewById(R.id.congo);
        message = root.findViewById(R.id.message);

        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userid = mFirebaseUser.getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Walk_Progress/"+userid).child(date);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    total_walk = snapshot.child("walkTotal").getValue(String.class);
                }
                try {
                    total_walk_int = Integer.parseInt(total_walk);
                } catch(Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }

        });

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Run_Progress/"+userid).child(date);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    total_run = snapshot.child("runTotal").getValue(String.class);
                }
                try {
                    total_run_int = Integer.parseInt(total_run);
                } catch(Exception e) {

                }
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
        if(goalCompleted()){
            congo.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
        }
        else{
            congo.setVisibility(View.INVISIBLE);
            message.setVisibility(View.INVISIBLE);
        }
        if(goalCompleted()){
            congo.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
        }
        else{
            congo.setVisibility(View.INVISIBLE);
            message.setVisibility(View.INVISIBLE);
        }
        return root;
    }

    private boolean goalCompleted() {

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userid = mFirebaseUser.getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Walk_Progress/"+userid).child(date);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    total_walk = snapshot.child("walkTotal").getValue(String.class);
                }
                try {
                    total_walk_int = Integer.parseInt(total_walk);
                } catch(Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }

        });

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Run_Progress/"+userid).child(date);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    total_run = snapshot.child("runTotal").getValue(String.class);
                }
                try {
                    total_run_int = Integer.parseInt(total_run);
                } catch(Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }

        });


        mDatabaseRef = FirebaseDatabase.getInstance().getReference("WalkingGoals").child(userid);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    walk_goal = snapshot.child("walk").getValue(String.class);
                }
                try {
                    walk_goal_int = Integer.parseInt(walk_goal);
                    walk_goal_int = walk_goal_int*60000;
                } catch(Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }

        });

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("RunningGoals").child(userid);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    run_goal = snapshot.child("run").getValue(String.class);
                }
                try {
                    run_goal_int = Integer.parseInt(run_goal);
                    run_goal_int = run_goal_int*60000;
                } catch(Exception e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }

        });
        if(total_walk_int>=walk_goal_int){
            if(total_run_int>=run_goal_int) {
                return true;
            }
            else {
                return false;
            }
        }
        else{
            return false;
        }

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




    private void handleUserActivity(int type, int confidence) {
        String label = getString(R.string.activity_unknown);
        int icon = R.drawable.ic_still;

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = getString(R.string.activity_in_vehicle);
                icon = R.drawable.ic_driving;
                if(confidence>70){
                    pauseChronometer(getView());
                    pauseChronometer2(getView());
                    wasWalking = false;
                    wasRunning = false;
                }
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = getString(R.string.activity_on_bicycle);
                icon = R.drawable.ic_on_bicycle;
                if(confidence>70){
                    pauseChronometer(getView());
                    pauseChronometer2(getView());
                    wasWalking = false;
                    wasRunning = false;
                }
                break;
            }
            case DetectedActivity.ON_FOOT:
            case DetectedActivity.WALKING: {
                label = getString(R.string.activity_walking);
                icon = R.drawable.ic_walking;
                if(confidence>70){
                    startChronometer(getView());
                    pauseChronometer2(getView());
                    wasWalking = true;
                    wasRunning = false;
                }
                break;
            }
            case DetectedActivity.RUNNING: {
                label = getString(R.string.activity_running);
                icon = R.drawable.ic_running;
                if(confidence>70){
                    startChronometer2(getView());
                    pauseChronometer(getView());
                    wasWalking = false;
                    wasRunning = true;
                }
                break;
            }
            case DetectedActivity.STILL: {
                label = "STILL";
                icon = R.drawable.ic_still;
                if(confidence>70){
                    pauseChronometer(getView());
                    pauseChronometer2(getView());
                    wasWalking = false;
                    wasRunning = false;
                }
                break;
            }
            case DetectedActivity.TILTING:
            case DetectedActivity.UNKNOWN: {
                label = getString(R.string.activity_unknown);
                if(confidence>70){
                    pauseChronometer(getView());
                    pauseChronometer2(getView());
                    wasWalking = false;
                    wasRunning = false;
                }
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
        stopTrackingBYFORCE();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    private void stopTrackingBYFORCE() {
        Intent intent = new Intent(getContext(), BackgroundDetectedActivitiesService.class);
        getActivity().stopService(intent);
    }

    private void startTracking() {
        Intent intent = new Intent(getContext(), BackgroundDetectedActivitiesService.class);
        getActivity().startService(intent);
    }

    private void stopTracking() {

        if(wasWalking){
            walkTotal =SystemClock.elapsedRealtime() - chronometer.getBase();
            pauseChronometer(getView());
            resetChronometer(getView());
        }

        if(wasRunning){
            runTotal =SystemClock.elapsedRealtime() - chronometer2.getBase();
            pauseChronometer2(getView());
            resetChronometer2(getView());
        }


        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userid = mFirebaseUser.getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Walk_Progress/"+userid).child(date);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    total_walk = snapshot.child("walkTotal").getValue(String.class);
                }
                try {
                    total_walk_int = Integer.parseInt(total_walk);
                } catch(Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }

        });

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Run_Progress/"+userid).child(date);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    total_run = snapshot.child("runTotal").getValue(String.class);
                }
                try {
                    total_run_int = Integer.parseInt(total_run);
                } catch(Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }

        });

        txtActivity.setText("");
        txtConfidence.setText("");
        input1 = (int) (walkTotal + total_walk_int);
        input2 = (int) (runTotal + total_run_int);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Walk_Progress/"+userid+"/"+date+"/completed");
        HashMap<String, String> hashMap2 = new HashMap<>();
        hashMap2.put("walkTotal", String.valueOf(input1));
        mDatabaseReference.setValue(hashMap2);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Run_Progress/"+userid+"/"+date+"/completed");
        HashMap<String, String> hashMap3 = new HashMap<>();
        hashMap3.put("runTotal", String.valueOf(input2));
        mDatabaseReference.setValue(hashMap3);
        if(goalCompleted()){
            congo.setVisibility(View.VISIBLE);
            message.setVisibility(View.VISIBLE);
        }
        else{
            congo.setVisibility(View.INVISIBLE);
            message.setVisibility(View.INVISIBLE);
        }
        Intent intent = new Intent(getContext(), BackgroundDetectedActivitiesService.class);
        getActivity().stopService(intent);
    }
}