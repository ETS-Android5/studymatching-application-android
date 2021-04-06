package com.example.study;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

//현재 커뮤니티 글을 작성하면 리스트가 그대로 다시 출력되는 오류가 존재
public class Frag_Community extends Fragment {
    private View view;
    private static final String TAG = "Community Fragment";

    private ListView m_ListView;            // 리스트뷰
    private ArrayAdapter<String> m_Adapter; // 리스트뷰 어댑터
    ArrayList<String> value;

    String PUID;
    String title;
    String author;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        view = inflater.inflate(R.layout.frag2_community,container,false);
        // 데이터베이스 읽기 #1. ValueEventListener
        value = new ArrayList<>();
        m_Adapter = new ArrayAdapter<String>(getActivity(), R.layout.memolist_type, value);
        // layout xml 파일에 정의된 ListView의 객체

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fabnewpost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Community_NewPostActivity.class));
            }
        });

        m_ListView = view.findViewById(R.id.communityList);
        // ListView에 어댑터 연결
        m_ListView.setAdapter(m_Adapter);
        // ListView 아이템 터치 시 이벤트를 처리할 리스너 설정
        m_ListView.setOnItemClickListener(onClickListItem);
        registerForContextMenu(m_ListView);

        //addchildEventListener를 이용하여 추가되거나 변동되는 하위 값만 갱신하게 된다.
        FirebaseDatabase.getInstance().getReference().child("posts").addChildEventListener(new ChildEventListener() {

            //하위 값이 추가되었을 때때
           @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                PUID = snapshot.getKey();
                title = snapshot.child("title").getValue().toString();
                author = snapshot.child("author").getValue().toString();
                String str = title + "\n 작성자 : " + author;
                m_Adapter.add(str);     // 리스트뷰에 추가
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                m_Adapter.remove(snapshot.getKey());
                Log.d("게시글 추가 작성 cancelled 실행",snapshot.getKey());
//                PUID = snapshot.getKey();
//                title = snapshot.child("title").getValue().toString();
//                author = snapshot.child("author").getValue().toString();
//                String str = title + "\n 작성자 : " + author;
//                m_Adapter.add(str);     // 리스트뷰에 추가

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                m_Adapter.clear();
                PUID = snapshot.getKey();
                title = snapshot.child("title").getValue().toString();
                author = snapshot.child("author").getValue().toString();
                String str = title + "\n 작성자 : " + author;
                m_Adapter.add(str);     // 리스트뷰에 추가
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("게시글 추가 작성 childmoved 실행","moved" + previousChildName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("게시글 추가 작성 cancelled 실행","취소 되어 에러매세지");
            }
        });



        return view;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.detach(this).attach(this).commit();
//    }

    // 아이템 터치 이벤트 리스너 구현
    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //DatabaseReference postRef = getRef();
            //final String postKey = postRef.getKey();
            Intent in = new Intent(getActivity(), Community_PostDetailActivity.class);
            in.putExtra("index",Integer.toString(position));    // 해당 리스트뷰의 인덱스를 다음 액티비티에 넘겨줌
            startActivity(in);
        }

    };
}