package com.example.study;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");
    // 이메일, 비밀번호 로그인 모듈 변수
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    // 현재 로그인 된 유저 정보를 담을 변수
    private FirebaseUser currentUser;
    // 로그인 모듈 리스터
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    Intent intent = null;
    EditText useremail = null;
    EditText userpassword = null;
    String myStudy = "";
    String email = "";
    String password = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mDatabase.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    myStudy = snapshot.child("myStudy").getValue().toString();

                    firebaseAuth = FirebaseAuth.getInstance();
                    // 로그인유무와 스터디 가입 유무에 따른 액티비티 차별점 두기

                    if (firebaseAuth.getCurrentUser() != null && myStudy.equals("")) {
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainPage.class));
                    } else if (firebaseAuth.getCurrentUser() != null && myStudy != "") {
                        finish();
                        startActivity(new Intent(getApplicationContext(), MyStudyList.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        useremail = (EditText) findViewById(R.id.ETemail);
        userpassword = (EditText) findViewById(R.id.ETpassword);
        /*
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) { //화면 전환등을 해도 로그인이 풀리지 않는다.
                    Toast.makeText(MainActivity.this, "로그인 되셨습니다.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(MainActivity.this, MainPage.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "로그아웃 되셨습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        };
         */
    }

    public void onClick(View view){
        int getId = view.getId();

        switch(getId){
            case R.id.signin :  //로그인 버튼을 눌렀을시 회원정보를 확인하고 메인 페이지로 전환한다.

                email = useremail.getText().toString();
                password = userpassword.getText().toString();

                if(isValidEmail() && isValidPasswd()) {
                    loginUser(email, password);
                }else{
                    Toast.makeText(LoginActivity.this, "빈칸입니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.register :  //가입하기 버튼을 눌렀을 때 가입창으로 전환환
                intent = new  Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.srchIdPw : //아이디 비밀번호 찾기를 눌렀을 때
                FindPassword();
               break;
        }

    }

    // 이메일 유효성 검사
    private boolean isValidEmail() {
        if (email.isEmpty()) {
            // 이메일 공백
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // 이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 비밀번호 유효성 검사
    private boolean isValidPasswd() {
        if (password.isEmpty()) {
            // 비밀번호 공백
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    // 로그인
    private void loginUser(String email, String password)
    {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공
                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            intent = new  Intent(LoginActivity.this, MainPage.class);
                            startActivity(intent);
                        } else {
                            // 로그인 실패
                            Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //비밀번호 찾기
    private void FindPassword(){
        final AutoCompleteTextView email = new AutoCompleteTextView(this);
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(email)
                .setTitle("이메일을 입력해주세요.")
                .setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            String emailAddress =  email.getText().toString();

                            if(!emailAddress.equals("")){
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                auth.sendPasswordResetEmail(emailAddress)
                                        .addOnCompleteListener(new OnCompleteListener<Void>(){
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task){
                                                if(task.isSuccessful()){
                                                    Toast.makeText(LoginActivity.this, "비밀번호 재설정 메일을 보냈습니다.", Toast.LENGTH_LONG).show();
                                                }else{
                                                    Toast.makeText(LoginActivity.this, "해당 이메일이 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                        }else{
                                Toast.makeText(LoginActivity.this, "메일을 다시 입력해주세요.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    ).show();
    }
}