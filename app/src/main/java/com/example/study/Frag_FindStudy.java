package com.example.study;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Frag_FindStudy extends Fragment {
    private View view;
    private ListView m_ListView;            // 리스트뷰
    private ArrayAdapter<String> m_Adapter; // 리스트뷰 어댑터
    ArrayList<String> value;

    String studyName;
    String studyKind;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.frag1_study,container,false);
        // 데이터베이스 읽기 #1. ValueEventListener
        value = new ArrayList<>();
        m_Adapter = new ArrayAdapter<String>(getActivity(), R.layout.memolist_type, value);
        // layout xml 파일에 정의된 ListView의 객체\

        m_ListView = view.findViewById(R.id.studyList);
        // ListView에 어댑터 연결
        m_ListView.setAdapter(m_Adapter);
        // ListView 아이템 터치 시 이벤트를 처리할 리스너 설정
        m_ListView.setOnItemClickListener(onClickListItem);
        m_ListView.setOnItemClickListener(onClickListItem);
        registerForContextMenu(m_ListView);
        FirebaseDatabase.getInstance().getReference().child("StudyList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    studyName = snapshot.child("contentTitle").getValue().toString();
                    studyKind = snapshot.child("studyKind").getValue().toString();
                    String str = "스터디: " + studyName + "\n종류: " + studyKind;
                    m_Adapter.add(str);     // 리스트뷰에 추가
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }


    // 아이템 터치 이벤트 리스너 구현
    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent in = new Intent(getActivity(), StudyExplanation.class);
            in.putExtra("index",Integer.toString(position));    // 해당 리스트뷰의 인덱스를 다음 액티비티에 넘겨줌
            startActivity(in);
        }

    };
}