package com.example.study;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainPage extends AppCompatActivity{

    private FirebaseAuth firebaseAuth;
    private BottomNavigationView bottomNavigationView; // 바텀 네비게이션 뷰
    private DrawerLayout drawerLayout;  // 사이드바 네비게이션 뷰
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Frag_FindStudy findStudy;
    private Frag_Community community;;
    private Frag_Notification notification;
    private Frag_MakeStudy makeStudy;
    private Frag_MyPage MyPage;
    private Frag_MyPage_Edit MyPage_Edit;
    private Toolbar toolbar;
    private Context context = this;
    private String myStudy;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.
        actionBar.setDisplayHomeAsUpEnabled(true);  //뒤로가기 버튼 생성
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_view_headline_24); //뒤로가기 버튼 이미지 지정 -> 추후 이벤트 처리를 통해 사이드바 나타나게 한다.


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myStudy = snapshot.child("myStudy").getValue().toString();
                Log.v("asdfg",myStudy);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //사이드바 버튼 클릭 이벤트
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();

                int itemID = item.getItemId();
                switch (itemID) {
                    case R.id.mypage:
                        MyPage = new Frag_MyPage();
                        getSupportFragmentManager().beginTransaction().replace(R.id.drawer_layout, MyPage).commit();
                        Toast.makeText(context, "회원정보 창으로 전환", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.mystudy:
                        Intent intent = new Intent(MainPage.this, MyStudyList.class);
                        startActivity(intent);
                        Toast.makeText(context, "내스터디 창으로 전환", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getApplicationContext(), "로그아웃합니다.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainPage.this, LoginActivity.class));
                        finish();
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        //바텀 네비게이션의 이벤트리스너
        bottomNavigationView = findViewById(R.id.bottom_navi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.navi_study:
                        setFrag(0);
                        break;
                    case R.id.navi_communnity:
                        setFrag(1);
                        break;
                    case R.id.navi_notifications:
                        setFrag(2);
                        //startActivity(new Intent(MainPage.this, testActivity.class));
                        break;
                    case R.id.navi_makestudy:
                        setFrag(3);
                        break;
                }
                return true;
            }
        });

        findStudy = new Frag_FindStudy();
        community = new Frag_Community();
        notification = new Frag_Notification();
        makeStudy = new Frag_MakeStudy();
        setFrag(0); // 로그인 후 메인 페이지로 들어가면 첫화면이 study목록을 띄워주는 fragment

    }

    // 이전버튼 클릭시 Drawer를 닫는다
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 프레그먼트 교체
    public void setFrag(int n)
    {
        fm = getSupportFragmentManager();
        ft= fm  .beginTransaction();
        switch (n)  //n의 값은 프래그먼트의 인덱스들
        {
            case 0:
                ft.replace(R.id.Main_Frame,findStudy);
                ft.commit();
                break;

            case 1:
                ft.replace(R.id.Main_Frame,community);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.Main_Frame,notification);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.Main_Frame,makeStudy);
                ft.commit();
                break;
        }

    }

    //메뉴가 커스텀 액션바 메뉴가 출력된다.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.drawer_layout, fragment).commit();      // Fragment로 사용할 MainActivity내의 layout공간을 선택합니다.
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);   // mainpage_content
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

}

