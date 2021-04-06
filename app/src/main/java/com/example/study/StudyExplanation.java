package com.example.study;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudyExplanation extends AppCompatActivity {
    private Toolbar toolbar;
    TextView studyName;
    TextView studyType;
    TextView numOfMembers;
    TextView studyContent;
    Button apply;
    TextView toolbarText;
    String index;
    int idx;
    String name;
    String type;
    String curr_num;
    String max_num;
    String content;
    String member;

    DataSnapshot sdx;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_explanation);

        //커스텀 액션바를 띄운다.
        toolbar = findViewById(R.id.PostDetailToolbar); //툴바(커스텀 액션바 선언)
        setSupportActionBar(toolbar);                   //액션바 등록
        toolbarText = toolbar.findViewById(R.id.toolbarPostTitle);  //툴바의 제목은 글의 제목으로 한다.

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.

        Intent in = getIntent();
        index = in.getStringExtra("index");
        idx = Integer.parseInt(index);

        studyName = (TextView)findViewById(R.id.ExplanationStudyName);
        studyType = (TextView)findViewById(R.id.ExplanationStudyKind);
        numOfMembers = (TextView)findViewById(R.id.ExplanationNumOfMembers);
        studyContent = (TextView)findViewById(R.id.ExplanationStudyContent);
        apply = (Button)findViewById(R.id.study_explanation_apply);

        FirebaseDatabase.getInstance().getReference().child("StudyList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int cnt = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(cnt == idx){
                        name = snapshot.child("contentTitle").getValue().toString();
                        type = snapshot.child("studyKind").getValue().toString();
                        curr_num = snapshot.child("currMembers").getValue().toString();
                        max_num = snapshot.child("maxMembers").getValue().toString();
                        member = curr_num + " / " + max_num;
                        content = snapshot.child("content").getValue().toString();
                        toolbarText.setText(name);
                        studyName.setText(name);
                        studyType.setText(type);
                        numOfMembers.setText(member);
                        studyContent.setText(content);
                        sdx = snapshot;
                        break;

                    }
                    else cnt++;
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        apply.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(v.getId() == R.id.study_explanation_apply) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                    String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String key = sdx.getKey().toString();
                    mDatabase.child("Users").child(auth).child("myStudy").setValue(key);
                    /*
                    String key = sdx.getKey();
                    Log.d("sdx? ", key);

                    // frag5_mystudy_notification 으로 전환(선택한 스터디의 고유 키 값 전달하면서), frag6_mystudy_chat 에도 전달해야함
                    Bundle bundle = new Bundle();

                    bundle.putString("key", key);
                    // 이건 테스트하려고 이렇게 작성한거고 이론적으로 보면 참가 버튼 눌렀을 때
                    // 내 스터디_공지로 가는 것이 맞음. 근데 공지, 채팅방 모두에게 key 값을 전달해줘야 함... 어떻게하지?
                    Frag_myStudy_chat frag_chat = new Frag_myStudy_chat();
                    frag_chat.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, frag_chat).commit();
                    Toast.makeText(StudyExplanation.this, member, Toast.LENGTH_SHORT).show();
                    */
                }
                finish();
            }
        });


    }
}
