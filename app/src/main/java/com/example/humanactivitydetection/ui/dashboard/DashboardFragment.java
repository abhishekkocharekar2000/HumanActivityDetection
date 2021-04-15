package com.example.humanactivitydetection.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.humanactivitydetection.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    Button save;
    EditText walk,run;
    TextView walked,ran;

    private DatabaseReference mDatabaseRef;
    FirebaseUser mFirebaseUser;

    public DatabaseReference mDatabaseReference;
    public String date;
    public int total_walk_int;
    public String total_walk;
    public String total_run;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        walk = root.findViewById(R.id.editTextTextPersonName);
        run = root.findViewById(R.id.editTextTextPersonName2);
        walked = root.findViewById(R.id.walked);
        ran = root.findViewById(R.id.ran);
        save = root.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String userid = mFirebaseUser.getUid();

                mDatabaseRef = FirebaseDatabase.getInstance().getReference("WalkingGoals/"+userid+"/1");
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("walk", walk.getText().toString());
                mDatabaseRef.setValue(hashMap);

                mDatabaseRef = FirebaseDatabase.getInstance().getReference("RunningGoals/"+userid+"/1");
                HashMap<String, String> hashMap2 = new HashMap<>();
                hashMap2.put("run", run.getText().toString());
                mDatabaseRef.setValue(hashMap2);
                Toast.makeText(getContext(),"Goals Saved!",Toast.LENGTH_SHORT).show();

            }
        });

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userid = mFirebaseUser.getUid();
        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Walk_Progress/"+userid).child(date);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    total_walk = snapshot.child("walkTotal").getValue(String.class);
                }
                try{
                    int seconds = Integer.parseInt(total_walk)/1000;
                    int mins = seconds/60;
                    seconds = seconds%60;
                    walked.setText(mins+" Minutes "+seconds+" Seconds");
                }
                catch(Exception e){

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
                try{
                    int seconds = Integer.parseInt(total_run)/1000;
                    int mins = seconds/60;
                    seconds = seconds%60;
                    ran.setText(mins+" Minutes "+seconds+" Seconds");
                }
                catch(Exception e){

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }

        });




        return root;
    }
}