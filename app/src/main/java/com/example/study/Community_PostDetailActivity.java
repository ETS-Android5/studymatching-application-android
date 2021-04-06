package com.example.study;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.study.databinding.Frag2CommunityPostDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Community_PostDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PostDetailActivity";

    public static final String EXTRA_POST_KEY = "post_key";

    DatabaseReference mPostReference;
    DatabaseReference mPostReference2;
    DatabaseReference mCommentsReference;
    private ValueEventListener mPostListener;
    private String mPostKey;
    private CommentAdapter mAdapter;
    private Frag2CommunityPostDetailBinding binding;
    private Toolbar toolbar;
    private TextView toolbarText;

    String index;
    int idx;
    String DBauthor = "";
    String DBtitle = "";
    String DBbody = "";
    String postUid = "";

    String Useruid ="";
    String Posteruid = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = Frag2CommunityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent in = getIntent();
        index = in.getStringExtra("index");
        idx = Integer.parseInt(index);

        //커스텀 액션바를 띄운다.
        toolbar = findViewById(R.id.PostDetailToolbar); //툴바(커스텀 액션바 선언)
        setSupportActionBar(toolbar);                   //액션바 등록
        toolbarText = toolbar.findViewById(R.id.toolbarPostTitle);  //툴바의 제목은 글의 제목으로 한다.

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목을 없애줍니다.

        // Get post key from intent
        mPostKey = getIntent().getStringExtra("index");
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }
        //글을 삭제할 때 할때 게시글 작성한 사람과 매칭하여 삭제하기 위해 현재 접속된 유저의 uid를 받아온다.
        Useruid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //글을 작성한 사람의  uid

        mPostReference2 = FirebaseDatabase.getInstance().getReference().child("posts");
        mPostReference = FirebaseDatabase.getInstance().getReference().child("posts").child(postUid);
        mCommentsReference = FirebaseDatabase.getInstance().getReference().child("post-comments");

        // community에서 액티비티 전환되며 선택한 포지션(mPostkey)를 받아와 해당 데이터를 읽는다.
        binding.buttonPostComment.setOnClickListener(this);
        binding.recyclerPostComments.setLayoutManager(new LinearLayoutManager(this));

    }


    @Override
    public void onStart() {
        super.onStart();
        // Add value event listener to the post
        // [START post_value_event_listener]
        //ValueEventListener postListener = new ValueEventListener() {
        FirebaseDatabase.getInstance().getReference().child("posts").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // position과 같다면 데이터베이스의 값을 가져온다.
                int cnt = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(cnt == idx){
                        DBauthor = snapshot.child("author").getValue().toString();
                        DBtitle = snapshot.child("title").getValue().toString();
                        DBbody = snapshot.child("body").getValue().toString();
                        Posteruid = snapshot.child("uid").getValue().toString();
                        postUid= snapshot.getKey();
                        Log.i("postUid 체크", postUid);
                        Log.d(TAG,"접속한 사람 uid: " +Useruid);
                        Log.d(TAG, "글쓴사람 uid:" + Posteruid);
                        binding.postAuthorLayout.postAuthor.setText(DBauthor);
                        binding.postTextLayout.postTitle.setText(DBtitle);
                        binding.postTextLayout.postBody.setText(DBbody);
                        toolbarText.setText(DBtitle);

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
                Toast.makeText(Community_PostDetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });
        Log.d("제발 좀 되면 안될까 ㅠ",  mCommentsReference.toString());
        // Listen for comments
        mAdapter = new CommentAdapter(this, mCommentsReference);
        binding.recyclerPostComments.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener);
        }

        // Clean up comments listener
        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonPostComment) {
            postComment();
        }
    }

    private void postComment() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.getUserName();

                        // Create new comment object
                        String commentText = binding.fieldCommentText.getText().toString();
                        Comment comment = new Comment(uid, authorName, commentText);

                        // Push the comment, it will appear in the list
                        mCommentsReference.child(postUid).push().setValue(comment);

                        // Clear the field
                        binding.fieldCommentText.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView authorView;
        public TextView bodyView;

        public CommentViewHolder(View itemView) {
            super(itemView);

            authorView = itemView.findViewById(R.id.commentAuthor);
            bodyView = itemView.findViewById(R.id.commentBody);
        }
    }

    private class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private Context mContext;
        final DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mCommentIds = new ArrayList<>();
        private List<Comment> mComments = new ArrayList<>();

        public CommentAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("post-comments").child(postUid);

            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "outsideSnapShot:" + dataSnapshot.getKey());
                    String snapshotKey = dataSnapshot.getKey();
                    Log.d(TAG, "key:" + snapshotKey);
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Log.d(TAG, "snapshot:" +snapshot.getKey());
                        Log.d(TAG, "postuid:" + postUid);
                        if(postUid.equals(snapshotKey)){
                            Log.d(TAG, "innerSnapshot:" + snapshot.getKey());
                            // A new comment has been added, add it to the displayed list
                            Comment comment = snapshot.getValue(Comment.class);

                            // [START_EXCLUDE]
                            // Update RecyclerView
                            mCommentIds.add(snapshot.getKey());
                            mComments.add(comment);
                            notifyItemInserted(mComments.size() - 1);
                            Log.d("코멘츠 사이즈", String.valueOf((mComments.size() - 1)));
                            // [END_EXCLUDE]
                        }
                    }
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                    /*
                    System.out.println(mDatabaseReference);
                    // A new comment has been added, add it to the displayed list
                    Comment comment = dataSnapshot.getValue(Comment.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mCommentIds.add(dataSnapshot.getKey());
                    mComments.add(comment);
                    notifyItemInserted(mComments.size() - 1);
                    // [END_EXCLUDE]

                     */
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Comment newComment = snapshot.getValue(Comment.class);
                        String commentKey = snapshot.getKey();
                        Log.d(TAG, "체인지 스냅샷:" +snapshot.getKey());
                        // [START_EXCLUDE]
                        int commentIndex = mCommentIds.indexOf(commentKey);
                        Log.d(TAG, "commentIndex:" + commentIndex);
                        if (commentIndex > -1) {
                            // Replace with the new data
                            mComments.set(commentIndex, newComment);

                            // Update the RecyclerView
                            notifyItemChanged(commentIndex);
                        } else {
                            Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                        }
                    }
                    /*
                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    Comment newComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();
                    Log.d(TAG, "체인지 스냅샷:" +dataSnapshot.getKey());
                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    Log.d(TAG, "commentIndex:" + commentIndex);
                    if (commentIndex > -1) {
                        // Replace with the new data
                        mComments.set(commentIndex, newComment);

                        // Update the RecyclerView
                        notifyItemChanged(commentIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                    }
                    */
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Remove data from the list
                        mCommentIds.remove(commentIndex);
                        mComments.remove(commentIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(commentIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Comment comment = mComments.get(position);
            holder.authorView.setText(comment.author);
            holder.bodyView.setText(comment.text);
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }
    //메뉴가 커스텀 액션바 메뉴가 출력된다.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.postmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DatabaseReference nowPost = FirebaseDatabase.getInstance().getReference().child("posts").child(postUid);
        DatabaseReference nowComment = FirebaseDatabase.getInstance().getReference().child("post-comments").child(postUid);
        switch (item.getItemId()){
            case R.id.postresive:{ // 왼쪽 상단 버튼 눌렀을 때 - 현재 uid와 매칭해서 맞으면 수정 아니면 안내 매세지
                if(Useruid.equals(Posteruid)) {  //로그인한 유저와 작성한 유저의 uid가 같으면 게시글을 삭제한다.
                    Intent intent = new Intent(this, Community_PostEditActivity.class);
                    intent.putExtra("index", mPostKey);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "회원님은 해당글의 작성자가 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            case R.id.postdelete :{ //수정 글쓴사람이랑 매칭 안됨
                if(Useruid.equals(Posteruid)){  //로그인한 유저와 작성한 유저의 uid가 같으면 게시글을 삭제한다.
                    Toast.makeText(getApplicationContext(), "게시글을 삭제합니다.", Toast.LENGTH_SHORT).show();

                    nowPost.setValue(null); //해당 키의 하위 값을 null로 설정하면 내용이 삭제된다.
                    nowComment.setValue(null); //해당 키의 하위 값을 null로 설정하면 해당 게시글의 댓글이 삭제된다.
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "회원님은 해당글의 작성자가 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
