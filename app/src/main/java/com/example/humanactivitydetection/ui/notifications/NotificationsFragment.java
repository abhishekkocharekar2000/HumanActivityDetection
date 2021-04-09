package com.example.humanactivitydetection.ui.notifications;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Build;
import android.os.Bundle;
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

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;

    DatePicker datePicker;
    Button btnClick;
    TextView walked;
    TextView ran;
    public Date date2;
    Button logoutButton;
    private GoogleSignInClient mGoogleSignInClient;

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
        ran=root.findViewById(R.id.textView5);
        logoutButton = root.findViewById(R.id.account_logout);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);


        btnClick.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(),"HERE",Toast.LENGTH_LONG).show();
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                month++;
                int year = datePicker.getYear();
                String date = day+"-"+month+"-"+year;
                Date myDate = parseDate(date);
                String asdf = myDate.toString();
                try {
                    date2 = new SimpleDateFormat("dd-MM-yyyy").parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String date3 = date2.toString();
                Toast.makeText(getContext(),date3,Toast.LENGTH_LONG).show();
                mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String userid = mFirebaseUser.getUid();
                ArrayList<String> array2 = new ArrayList<>();
                mDatabaseReference = FirebaseDatabase.getInstance().getReference("Progress/"+userid).child(date);
                mDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        array2.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            array2.add(postSnapshot.getValue().toString());
                        }
                        try {
                            String total_walk = array2.get(0);
                            Toast.makeText(getContext(),total_walk,Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(),"ERROR",Toast.LENGTH_LONG).show();
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
                                signOut();
                            }
                        })
                        .setNegativeButton("Cancel",null);

                AlertDialog logoutAlert = logoutBuild.create();
                logoutAlert.show();
            }
        });

        return root;
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Signed out", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                });
    }

    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
}