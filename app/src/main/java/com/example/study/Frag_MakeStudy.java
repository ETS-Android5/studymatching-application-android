package com.example.study;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class Frag_MakeStudy extends Fragment {
    private View view;
    private String email;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private DrawerLayout drawerLayout;
    EditText studyName = null;
    EditText studyKind = null;
    EditText maxMember = null;
    EditText content = null;
    Content c = new Content();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.study_add,container,false);
        studyName = view.findViewById(R.id.studyName);
        studyKind = view.findViewById(R.id.studyKind);
        maxMember = view.findViewById(R.id.maxMembers);
        content = view.findViewById(R.id.studycontent);
        Button addButton = view.findViewById(R.id.studyadd_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                DatabaseReference mDatabase;

                mDatabase = FirebaseDatabase.getInstance().getReference();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final DatabaseReference mdb = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                String mystudy = mDatabase.child("StudyList").push().getKey();

                switch(v.getId()){
                    case R.id.studyadd_add:
                        c.setContentTitle(studyName.getText().toString());
                        c.setStudyKind(studyKind.getText().toString());
                        setEmail();
                        c.setHead(email);
                        c.setHeadUid(userId);
                        c.setMaxMembers(maxMember.getText().toString());
                        c.setCurrMembers("1");
                        c.setContent(content.getText().toString());
                        c.setMyChat("");
                        c.setMyNoti("");    //이거 둑 content 클래스에 추가하기
                        mDatabase.child("StudyList").child(mystudy).setValue(c);
                        Log.d("아버지 알려주세요", mystudy);
                        break;
                }
                mdb.child("myStudy").setValue(mystudy);
                goToMain();
            }
        });
        return view;

    }

    private void setEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
        }
    }
    private void goToMain(){
        fm = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        ft= fm.beginTransaction();
        ft.replace(R.id.Main_Frame,new Frag_FindStudy());
        ft.commit();
    }

}