package com.example.humanactivitydetection.ui.notifications;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.humanactivitydetection.MainActivity;
import com.example.humanactivitydetection.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    DatePicker datePicker;
    Button btnClick;
    TextView walked;
    TextView ran;
    public Date date2;
    Button logoutButton;
    public String total_walk;
    public String total_run;
    public String date;

    FirebaseUser mFirebaseUser;
    public DatabaseReference mDatabaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        datePicker=root.findViewById(R.id.datePicaker);
        btnClick=root.findViewById(R.id.b3);
        walked=root.findViewById(R.id.textView4);
        ran=root.findViewById(R.id.ran);
        logoutButton = root.findViewById(R.id.account_logout);


        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walked.setText("No Record Available");
                total_walk = "";
                ran.setText("No Record Available");
                total_run = "";
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                month++;
                int year = datePicker.getYear();
                if(month<=9){
                    date = day+"-0"+month+"-"+year;
                }
                else{
                    date = day+"-"+month+"-"+year;
                }

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
                            int seconds = Integer.parseInt(total_walk)/1000;
                            int mins = seconds/60;
                            seconds = seconds%60;
                            walked.setText(mins+" Minutes "+seconds+" Seconds");
                        } catch (Exception e) {
                            Toast.makeText(getContext(),"Invalid date.",Toast.LENGTH_SHORT);
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
                            int seconds = Integer.parseInt(total_run)/1000;
                            int mins = seconds/60;
                            seconds = seconds%60;
                            ran.setText(mins+" Minutes "+seconds+" Seconds");
                        } catch (Exception e) {
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                    }

                });

            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder logoutBuild = new AlertDialog.Builder(getActivity());

                logoutBuild.setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AuthUI.getInstance().signOut(getContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Intent i = new Intent(getContext(),MainActivity.class);
                                            startActivity(i);
                                        }
                                        else{
                                            Log.e(TAG,"onComplete : ",task.getException());
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel",null);

                AlertDialog logoutAlert = logoutBuild.create();
                logoutAlert.show();
            }
        });

        return root;
    }



}