

package com.example.study;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.study.databinding.Frag2CommunityPostEditBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Community_PostEditActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PostEditActivity";
    private DatabaseReference mPostReference;
    private String mPostKey;
    private Frag2CommunityPostEditBinding binding;
    private ValueEventListener mPostListener;

    String index;
    public static String test;
    int idx;
    String DBtitle = "";
    String DBbody = "";

    EditText ETtitle = null;
    EditText ETbody = null;
    String title;
    String body;

    public Community_PostEditActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = Frag2CommunityPostEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent in = getIntent();
        index = in.getStringExtra("index");
        idx = Integer.parseInt(index);
        // Get post key from intent
        mPostKey = getIntent().getStringExtra("index");
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        FirebaseDatabase.getInstance().getReference().child("posts").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                int cnt = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(cnt == idx){

                        test = snapshot.getKey();
                        DBtitle = snapshot.child("title").getValue().toString();
                        DBbody = snapshot.child("body").getValue().toString();
                        Log.i("수정중인 글의 key", test);
                        binding.EditPostTitle.setText(DBtitle);
                        binding.EditPostBody.setText(DBbody);
                        break;
                    }
                    else cnt++;
                }

                Log.i(TAG, "onstart - datasnapshot");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(Community_PostEditActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });
        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference().child("posts");

        FloatingActionButton fab = findViewById(R.id.fabEditPost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ETtitle = findViewById(R.id.EditPostTitle);
                ETbody = findViewById(R.id.EditPostBody);

                title = ETtitle.getText().toString();

                mPostReference.child(test).child("title").setValue(ETtitle.getText().toString());
                mPostReference.child(test).child("body").setValue(ETbody.getText().toString());
                finish();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener);
        }
    }

    @Override
    public void onClick(View v) {

    }

}
