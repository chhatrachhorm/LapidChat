package chhatrachhorm.androidapp.onenterpise.lapidchat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mImageView;
    private TextView mUsername, mStatus, mTotalFriends;
    private Button mFriendReqBtn;
    private ProgressDialog mProgressDialog;

    private DatabaseReference profileDatabaseRef;
    private DatabaseReference friendReqDatabaseRef;
    private DatabaseReference friendDatabaseRef;
    private DatabaseReference notificationDBRef;

    private FirebaseUser mCurrentUser;

    private String friendStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id = getIntent().getStringExtra("user_id");

        mImageView = findViewById(R.id.profile_imageview);
        mUsername = findViewById(R.id.profile_username);
        mStatus = findViewById(R.id.profile_status);
        mTotalFriends = findViewById(R.id.profile_total_friends);
        mFriendReqBtn = findViewById(R.id.profile_friendRequest_btn);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User's Profile");
        mProgressDialog.setMessage("This may take a couple time");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        profileDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        friendReqDatabaseRef = FirebaseDatabase.getInstance().getReference().child("friends");
        friendDatabaseRef = FirebaseDatabase.getInstance().getReference().child("lapid_friends");
        notificationDBRef = FirebaseDatabase.getInstance().getReference().child("notifications");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        friendStatus = "not_friend";

        profileDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                mUsername.setText(username);
                mStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_avatar_male).into(mImageView);

                friendReqDatabaseRef.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type.equals("sent")){
                                friendStatus = "req_sent";
                                mFriendReqBtn.setText(R.string.cancel_friend_request);
                            }else if(req_type.equals("received")){
                                friendStatus = "req_received";
                                mFriendReqBtn.setText(R.string.confirm_friends);
                            }
                        }else{
                            friendDatabaseRef.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_id)){
                                        friendStatus = "friend";
                                        mFriendReqBtn.setText(R.string.unfriend);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mProgressDialog.dismiss();
                                }
                            });
                        }

                        mProgressDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mProgressDialog.dismiss();
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mFriendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFriendReqBtn.setEnabled(false);
                if(friendStatus.equals("not_friend")){
                    friendReqDatabaseRef.child(mCurrentUser.getUid()).child(user_id).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                HashMap<String, String> noti = new HashMap<>();
                                noti.put("from", mCurrentUser.getUid());
                                noti.put("type", "request");

                                notificationDBRef.child(user_id).push().setValue(noti).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        friendReqDatabaseRef.child(user_id).child(mCurrentUser.getUid()).child("request_type")
                                                .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                friendStatus = "req_sent";
                                                mFriendReqBtn.setEnabled(true);
                                                mFriendReqBtn.setText(R.string.cancel_friend_request);
                                                Toast.makeText(ProfileActivity.this, "Friend Request Sent", Toast.LENGTH_LONG).show();


                                            }
                                        });

                                    }
                                });


                            }else{
                                Toast.makeText(ProfileActivity.this, "Friend Request Failed", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
                if(friendStatus.equals("req_sent")){
                    friendReqDatabaseRef.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendReqDatabaseRef.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendStatus = "not_friend";
                                    mFriendReqBtn.setEnabled(true);
                                    mFriendReqBtn.setText(R.string.send_friend_request);
                                    Toast.makeText(ProfileActivity.this, "Canceled Successfully", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
                if(friendStatus.equals("req_received")){
                    final String date = DateFormat.getDateTimeInstance().format(new Date()).toString();
                    friendDatabaseRef.child(mCurrentUser.getUid()).child(user_id).setValue(date)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendDatabaseRef.child(user_id).child(mCurrentUser.getUid()).setValue(date)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    friendReqDatabaseRef.child(mCurrentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            friendReqDatabaseRef.child(user_id).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    friendStatus = "friend";
                                                                    mFriendReqBtn.setEnabled(true);
                                                                    mFriendReqBtn.setText(R.string.unfriend);
                                                                }
                                                            });
                                                        }
                                                    });

                                                }
                                            });

                                }
                            });
                }
            }
        });



    }
}
