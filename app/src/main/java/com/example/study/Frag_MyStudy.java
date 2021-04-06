package com.example.study;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Frag_MyStudy extends Fragment {
    TextView studyName = null;
    TextView studyType  = null;
    TextView numOfMembers  = null;
    TextView studyContent  = null;
    Button apply  = null;

    View view;

    public static Frag_MyStudy newInstance() {
        return new Frag_MyStudy();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.study_explanation, container, false);

        apply = (Button) view.findViewById(R.id.study_explanation_apply);
        apply.setText("확인");
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMain();
            }
        });

        getUserInfo();



        return view;
    }
    private void getUserInfo() {
        studyName = (TextView) view.findViewById(R.id.ExplanationStudyName);
        studyType = (TextView) view.findViewById(R.id.ExplanationStudyKind);
        numOfMembers = (TextView) view.findViewById(R.id.ExplanationNumOfMembers);
        studyContent = (TextView) view.findViewById(R.id.ExplanationStudyContent);


        FirebaseDatabase.getInstance().getReference().child("StudyList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                //String test = datasnapshot.getValue().toString();
                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    studyName.setText(snapshot.child("contentTitle").getValue().toString());
                    studyType.setText(snapshot.child("studyKind").getValue().toString());
                    numOfMembers.setText(snapshot.child("currMembers").getValue().toString() +  " / " + snapshot.child("maxMembers").getValue().toString() );
                    studyContent.setText(snapshot.child("content").getValue().toString());
                }

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



