package chhatrachhorm.androidapp.onenterpise.lapidchat;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerViewLists;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUserDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = findViewById(R.id.users_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All LapidChat Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerViewLists = findViewById(R.id.users_recyclerView);
        mRecyclerViewLists.setHasFixedSize(true);
        mRecyclerViewLists.setLayoutManager(new LinearLayoutManager(this));

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users");
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserDatabaseRef.child("online").setValue(true);

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>
                (
                        Users.class,
                        R.layout.single_user_layout,
                        UsersViewHolder.class,
                        mDatabaseRef
                ) {
            // Users class is the model for database (get and set data)
            // Layout file to show the data
            // View to hold the view's data (set data into view)
            // DatabaseRef to store reference of the database
            // Adapter acts like a controller
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users model, final int position) {
                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setThumbImage(model.getThumb_image(), getApplicationContext());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String user_id = getRef(position).getKey();
                        Intent startProfile = new Intent(UsersActivity.this, ProfileActivity.class);
                        startProfile.putExtra("user_id", user_id);
                        startActivity(startProfile);
                    }
                });

            }
        };
        mRecyclerViewLists.setAdapter(firebaseRecyclerAdapter);
    }


    // User view holder is used to get data from FirebaseRecyclerAdapter and put into View (R.layout.single_user_layout)
    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String name){
            TextView singleUserTextView = mView.findViewById(R.id.single_user_username);
            singleUserTextView.setText(name);
        }
        public void setStatus(String status){
            TextView singleUserStatusView = mView.findViewById(R.id.single_user_status);
            singleUserStatusView.setText(status);
        }
        public void setThumbImage(String thumbImageUrl, Context ctx){
            CircleImageView imageView = mView.findViewById(R.id.single_user_image);
            Picasso.with(ctx).load(thumbImageUrl).placeholder(R.drawable.default_avatar_male).into(imageView);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mUserDatabaseRef.child("online").setValue(ServerValue.TIMESTAMP);

    }
}
