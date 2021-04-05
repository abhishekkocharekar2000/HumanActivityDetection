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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    Button save;
    EditText walk,run;

    private DatabaseReference mDatabaseRef;
    FirebaseUser mFirebaseUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        walk = root.findViewById(R.id.editTextTextPersonName);
        run = root.findViewById(R.id.editTextTextPersonName2);
        save = root.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String userid = mFirebaseUser.getUid();
                mDatabaseRef = FirebaseDatabase.getInstance().getReference("Goals/").child(userid);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("walk", walk.getText().toString());
                hashMap.put("run", run.getText().toString());
                mDatabaseRef.setValue(hashMap);
                Toast.makeText(getContext(),"Goals Saved!",Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }
}