package com.example.study;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");
    // 이메일, 비밀번호 로그인 모듈 변수
    private FirebaseAuth firebaseAuth;
    // 현재 로그인 된 유저 정보를 담을 변수
    private FirebaseUser currentUser;
    // ?
    private FirebaseDatabase mDatabase;

    private String gender = "";
    private EditText ETemail = null;// (EditText)findViewById(R.id.ETemail);
    private EditText ETpassword = null;// (EditText)findViewById(R.id.ETpassword);
    private EditText ETname = null;// (EditText)findViewById(R.id.ETname);
    private EditText ETaddress = null;// (EditText)findViewById(R.id.ETaddress);
    private EditText ETnumber = null;// (EditText)findViewById(R.id.ETphone);
    Button btnSignUp = null;

    private String email ="";
    private String password ="";
    private String name ="";
    private String address ="";
    private String number ="";

    User user = new User();



   // mDatabase = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 파이어베이스 인증 객체 선언
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        //회원 정보
        ETemail = (EditText)findViewById(R.id.ETemail);
        ETpassword = (EditText)findViewById(R.id.ETpassword);
        ETname = (EditText)findViewById(R.id.ETname);
        ETaddress = (EditText)findViewById(R.id.ETaddress);
        ETnumber =  (EditText)findViewById(R.id.ETphone);
    }

    public void onClick(View v){
        int Id = v.getId();

        switch(Id) {
            case R.id.male :
                gender = "male";
                break;
            case R.id.female :
                gender = "female";
                break;
            case R.id.register :
                //editText뷰에 입력한 값을 String 객체로 변환하여 저장
                email = ETemail.getText().toString();
                password = ETpassword.getText().toString();
                name = ETname.getText().toString();
                address = ETaddress.getText().toString();
                number =ETnumber.getText().toString();

                if(isValidEmail() && isValidPasswd() && isValidName() && isValidGender()) {
                    createUser(email, password, name, gender, address, number);
                }
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
    // 이름 유효성 검사
    private boolean isValidName() {
        if (name.isEmpty()) {
            // 비밀번호 공백
            return false;
        }  else {
            return true;
        }
    }
    // 성별 유효성 검사
    private boolean isValidGender() {
        if (gender.isEmpty()) {
            // 성별 미선택
            return false;
        }  else  {
            return true;
        }
    }
    // 이메일 인증을 우선으로함
    // 휴대폰번호나 주소는 추후에도 입력할 수 있도록 하자.


    //회원가입
    private void createUser(final String e, final String pw, final String n, final String g, final String a, final String num) {
        firebaseAuth.createUserWithEmailAndPassword(e, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공시
                            currentUser = firebaseAuth.getCurrentUser();
                           final String uid = task.getResult().getUser().getUid();//회원생성할 때 마다 고유의 식별자 unique Identify 생성성

                          //String객체로 저장한 값을 User클래스 객체 user에 set
                            user.setEmail(e);
                            user.setPassword(pw);
                            user.setUserName(n);
                            user.setGender(g);
                            user.setAddress(a);
                            user.setPhone(num); //나중에 변수명 맞추기
                            user.setMyStudy("");

                            //set한 유저의 정보를 파이어베이스에 저장한다.
                            //분류 목록은 다음과 같다 대분류 : "Users", 중분류 : "사용자 이메일 - 기본키로 설정"
                            mDatabase.getReference().child("Users").child(uid).setValue(user);

                            Toast.makeText(RegisterActivity.this, "가입 성공" ,Toast.LENGTH_SHORT).show();
                            finish(); //가입에 성공하여 해당 화면을 빠져나가 메인 액티비티로 전환한다.
                        }else{
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) { //8자리 이상으로 해야함
                                Toast.makeText(RegisterActivity.this,"비밀번호가 간단해요.." ,Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(RegisterActivity.this,"email 형식에 맞지 않습니다." ,Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthUserCollisionException e) {
                                Toast.makeText(RegisterActivity.this,"이미존재하는 email 입니다." ,Toast.LENGTH_SHORT).show();
                            } catch(Exception e) {
                                Toast.makeText(RegisterActivity.this,"다시 확인해주세요.." ,Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
