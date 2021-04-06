package com.example.study;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Frag_MyPage extends Fragment{
    private View view;
    private TextView ETname = null; // (TextView)findViewById(R.id.myName);
    private TextView ETgender = null; // (TextView)findViewById(R.id.myGender);
    private TextView ETaddress = null; // (TextView)findViewById(R.id.myAddress);
    private TextView ETphone = null; // (TextView)findViewById(R.id.myNumber);

    private String userName;
    private TextView name;

    public static Frag_MyPage newInstance() {
        return new Frag_MyPage();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.frag4_mypage,container,false);

        Button button_ok = (Button)view.findViewById(R.id.ok);
        Button button_resive = (Button)view.findViewById(R.id.resive);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMain();
            }
        });
        button_resive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainPage)getActivity()).replaceFragment(Frag_MyPage_Edit.newInstance());
            }
        });
        getUserInfo();

        return view;
    };

    private void getUserInfo() {
        name = view.findViewById(R.id.userName);
        ETname = view.findViewById(R.id.myName);
        ETgender = view.findViewById(R.id.myGender);
        ETaddress = view.findViewById(R.id.myAddress);
        ETphone = view.findViewById(R.id.myNumber);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("userName").getValue().toString() + " 님");
                ETname.setText(snapshot.child("userName").getValue().toString());
                ETgender.setText(snapshot.child("gender").getValue().toString());
                ETaddress.setText(snapshot.child("address").getValue().toString());
                ETphone.setText(snapshot.child("phone").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    //프래그먼트 종료
    private void goToMain(){
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        fragmentManager.popBackStack();
    }

}
