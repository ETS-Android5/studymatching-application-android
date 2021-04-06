package com.example.study;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Frag_MyPage_Edit extends Fragment {
    private View view;
    private TextView ETname = null; // (TextView)findViewById(R.id.myName);
    private TextView ETgender = null; // (TextView)findViewById(R.id.myGender);
    private TextView ETaddress = null; // (TextView)findViewById(R.id.myAddress);
    private TextView ETphone = null; // (TextView)findViewById(R.id.myNumber);

    private String gender;
    private String userId;
    private TextView name;
    private DatabaseReference mDatabase;


    public static Frag_MyPage_Edit newInstance() {
        return new Frag_MyPage_Edit();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.frag4_mypage_edit,container,false);
        getUserInfo();

        Button button_ok = (Button)view.findViewById(R.id.save);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ETname = (EditText)view.findViewById(R.id.myName);
                ETaddress = (EditText)view.findViewById(R.id.myAddress);
                ETphone = (EditText)view.findViewById(R.id.myNumber);

                mDatabase.child("Users").child(userId).child("userName").setValue(ETname.getText().toString());
                mDatabase.child("Users").child(userId).child("gender").setValue(gender);
                mDatabase.child("Users").child(userId).child("address").setValue(ETaddress.getText().toString());
                mDatabase.child("Users").child(userId).child("phone").setValue(ETphone.getText().toString());
                ((MainPage)getActivity()).replaceFragment(Frag_MyPage.newInstance());
            }
        });

        Button male = (Button)view.findViewById(R.id.male);
        male.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                gender = "male";
            }
        });
        Button female = (Button)view.findViewById(R.id.female);
        female.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                gender = "female";
            }
        });
        return view;
    };

    private void getUserInfo() {
        name = view.findViewById(R.id.userName);
        ETname = view.findViewById(R.id.myName);
        //ETgender = view.findViewById(R.id.myGender);
        ETaddress = view.findViewById(R.id.myAddress);
        ETphone = view.findViewById(R.id.myNumber);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("userName").getValue().toString() + " 님");
                ETname.setText(snapshot.child("userName").getValue().toString());
                //ETgender.setText(snapshot.child("gender").getValue().toString());
                ETaddress.setText(snapshot.child("address").getValue().toString());
                ETphone.setText(snapshot.child("phone").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    /*
    private void goToMain(){
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStack();
    }
     */
}
