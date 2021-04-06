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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Frag_Notification extends Fragment {
    private View view;
    RecyclerView rc;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    ArrayList<Notiinfo> myDataset;
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
        mAdapter = new Noti_ListAdapter(myDataset);
        rc.setAdapter(mAdapter);


        //db에서 값을 받아온게 출력이 안되서 일단은 수기로 테스트
        myDataset.add(new Notiinfo("저희 어플리케이션을 이용해주셔서 감사합니다.", "아직 개발중에 있지만 많은 피드백을 통해 다양한 사용자 편의 기능을 구현하여 학습에 도움이 되는 스터디 매칭 어플리케이션이 되겠습니다. \n감사합니다."));
        myDataset.add(new Notiinfo("코로나 19 대비 공지사항", "코로나로 인한 사회적 거리두기 2단계가 시행되었으니 사용자분들은 되도록 비대면으로 zoom을 통해 스터디를 진행하고 진행상황을  공유해주시면 감사하겠습니다."));



        FirebaseDatabase.getInstance().getReference().child("notis").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Log.d("ㅎㅎ왜 안되니", String.valueOf(snapshot));
//                    title = snapshot.child("noti_title").getValue().toString();
//                    Log.d("글제목", title);
//                    body = snapshot.child("noti_body").getValue().toString();
//                    Log.d("내용", body);
//                    //디버깅해도 값은 잘 받아오는데 add가 안되는 부분 ㅎㅎ
//                    myDataset.add(new Notiinfo(title, body));
//                }
//            }

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("ㅎㅎ왜 안되니", String.valueOf(snapshot));
                title = snapshot.child("noti_title").getValue().toString();
                Log.d("글제목", title);
                body = snapshot.child("noti_body").getValue().toString();
                Log.d("내용", body);
                //디버깅해도 값은 잘 받아오는데 add가 안되는 부분 ㅎㅎ
                myDataset.add(new Notiinfo(title, body));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }


}
class Noti_ListAdapter extends RecyclerView.Adapter<Noti_ListAdapter.MyViewHolder> {
    Context noti_context;
    private ArrayList<Notiinfo> mDataset;


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
    public Noti_ListAdapter(ArrayList<Notiinfo> myDataset){
        mDataset = myDataset;
    }

    //새로운 view를 생성한다.
    @NonNull
    @Override
    public Noti_ListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {   //현재 만들어준 아이템을 휴대폰에 출력하기 위한 메소드

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new MyViewHolder(view);
    }

    //layoutmanager가 view의 내용을 바꾼다.
    @Override
    public void onBindViewHolder(@NonNull Noti_ListAdapter.MyViewHolder holder, int position) {  //전달 받은 값을 설정한다

        holder.TVtitle.setText(mDataset.get(position).title);
        holder.TVbody.setText(mDataset.get(position).body);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
class Notiinfo{
    public String title;
    public String body;
    public Notiinfo(String t, String b){
        this.title = t;
        this.body = b;
    }
}