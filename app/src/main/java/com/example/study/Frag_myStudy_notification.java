package com.example.study;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Frag_myStudy_notification extends Fragment {
    private View view;
    RecyclerView rc;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    ArrayList<MyNotiinfo> myDataset;
    String title;
    String body;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag3_notification, container, false); //프래그먼트 레이아웃 등록
        rc = view.findViewById(R.id.noti_recycle); //frag3_notification.xml에 존재하는 리사이클 뷰 등록

        //LinearLayoutmanager를 사용하여 리사이클뷰의 정렬형태 설정
        mLayoutManager = new LinearLayoutManager(getActivity());    //fragment에서 context는 getActivity로 대체된다. 액티비티위에 프래그먼트가 존재하는 것이므로
        rc.setLayoutManager(mLayoutManager);

        myDataset = new ArrayList<>();
        mAdapter = new MyNoti_ListAdapter(myDataset);
        rc.setAdapter(mAdapter);


        //db에서 값을 받아온게 출력이 안되서 일단은 수기로 테스트
        myDataset.add(new MyNotiinfo("첫 번째 공지사항입니다.", "날씨가 쌀쌀하고 사회적 분위기가 좋지 않지만 우리 모두 힘을내서 각자의 위치에서 화이팅해요!"));
        myDataset.add(new MyNotiinfo("다음 미팅 일정", "시간 : 12월 25일\n장소 : 천안 신세계 백화점 근처 파스구찌\n시간 : 18시까지"));
        myDataset.add(new MyNotiinfo("다음 미팅 과제", "학습 계획서 및 교재, 직전 수강 과목 성적"));



        //onresume이 끝나고 이벤트가 발생하고 객체가 추가됨,,, 수정이 필요함
//        FirebaseDatabase.getInstance().getReference().child("StudyList").child("").child("Study_noti").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Log.d("ㅎㅎ왜 안되니", String.valueOf(snapshot));
//                    title = snapshot.child("noti_title").getValue().toString();
//                    Log.d("글제목", title);
//                    body = snapshot.child("noti_body").getValue().toString();
//                    Log.d("내용", body);
//                    //디버깅해도 값은 잘 받아오는데 add가 안되는 부분 ㅎㅎ
//                    myDataset.add(new MyNotiinfo(title, body));
//                    Log.d("내용", "추가 안되는건가");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

       return view;
    }


}
class MyNoti_ListAdapter extends RecyclerView.Adapter<MyNoti_ListAdapter.MyViewHolder> {
    Context noti_context;
    private ArrayList<MyNotiinfo> mDataset;


    // 각 데이터 항목의 뷰에 대한 참조 제공
    // 복잡한 데이터 항목은 항목당 하나 이상의 view가 필요함
    // view holder의 데이터 항목은 모든 view의 대한 엑세스를 제공
    public class MyViewHolder extends RecyclerView.ViewHolder{      //리사이클 뷰에 속한 아이템을 그리기 위한 레이아웃의 값을 설정해준다.
        TextView TVtitle, TVbody;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            TVtitle = itemView.findViewById(R.id.noti_title);
            TVbody = itemView.findViewById(R.id.noti_body);
        }
    }

    // 제네릭을 사용해 적합한 생서자 생성
    public MyNoti_ListAdapter(ArrayList<MyNotiinfo> myDataset){
        mDataset = myDataset;
    }

    //새로운 view를 생성한다.
    @NonNull
    @Override
    public MyNoti_ListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {   //현재 만들어준 아이템을 휴대폰에 출력하기 위한 메소드
//        LayoutInflater inflater= LayoutInflater.from(noti_context);
        //LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new MyViewHolder(view);
    }

    //layoutmanager가 view의 내용을 바꾼다.
    @Override
    public void onBindViewHolder(@NonNull MyNoti_ListAdapter.MyViewHolder holder, int position) {  //전달 받은 값을 설정한다
//        holder.title.setText(noti_title);
//        holder.body.setText(noti_body);
        holder.TVtitle.setText(mDataset.get(position).title);
        holder.TVbody.setText(mDataset.get(position).body);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}

class MyNotiinfo{
    public String title;
    public String body;
    public MyNotiinfo(String t, String b){
        this.title = t;
        this.body = b;
    }
}