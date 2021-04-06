package com.example.study;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Frag_myStudy_chat extends Fragment {
    private View view;

    private String CHAT_NAME;
    private String USER_NAME;
    private String Study_UID;

    private ListView chat_view;
    private EditText chat_edit;
    private Button chat_send;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.frag6_mystudy_chat,container,false);

        // 위젯 ID 참조
        chat_view = (ListView)view.findViewById(R.id.chat_view);
        chat_edit = (EditText)view.findViewById(R.id.chat_edit);
        chat_send = (Button)view.findViewById(R.id.chat_sent);

        // 로그인 화면에서 받아온 채팅방 이름, 유저 이름 저장
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CHAT_NAME = "Study";         // 스터디 방장 아이디 들어갈 부분
        databaseReference.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                USER_NAME = snapshot.child("userName").getValue().toString();
                Study_UID = snapshot.child("myStudy").getValue().toString();    //
                Log.d("유저 이름", USER_NAME);
                Log.d("스터디 uid", Study_UID);
                Log.d("오픈챗 시작", "그렇대");
                openChat(CHAT_NAME);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 채팅 방 입장
//        Log.d("유저 이름", USER_NAME);
//        Log.d("스터디 uid", Study_UID);
        // 메시지 전송 버튼에 대한 클릭 리스너 지정
        chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chat_edit.getText().toString().equals(""))
                    return;

                ChatDTO chat = new ChatDTO(USER_NAME, chat_edit.getText().toString()); //ChatDTO를 이용하여 데이터를 묶는다.
                databaseReference.child("StudyList").child(Study_UID).child("myChat").child("chat").child(CHAT_NAME).push().setValue(chat); // 데이터 푸쉬
                chat_edit.setText(""); //입력창 초기화

            }
        });
        return view;
    }


    private void addMessage(String userName, String message, ArrayAdapter<String> adapter) {
        ChatDTO chatDTO = new ChatDTO(userName, message);
        adapter.add(chatDTO.getUserName() + " : " + chatDTO.getMessage());
    }

    private void removeMessage(DataSnapshot dataSnapshot, ArrayAdapter<String> adapter) {
        ChatDTO chatDTO = dataSnapshot.getValue(ChatDTO.class);
        adapter.remove(chatDTO.getUserName() + " : " + chatDTO.getMessage());
    }

    private void openChat(String chatName) {
        Log.d("오픈챗 내부", "그렇대");
        // 리스트 어댑터 생성 및 세팅
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        chat_view.setAdapter(adapter);

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("StudyList").child(Study_UID).child("myChat").child("chat").child(chatName).addChildEventListener(new ChildEventListener() {
        //databaseReference.child("chat").child(chatName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                String message = dataSnapshot.child("message").getValue().toString();
                String userName = dataSnapshot.child("userName").getValue().toString();
                addMessage(userName, message, adapter);
                Log.e("LOG", "s:"+s);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeMessage(dataSnapshot, adapter);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}