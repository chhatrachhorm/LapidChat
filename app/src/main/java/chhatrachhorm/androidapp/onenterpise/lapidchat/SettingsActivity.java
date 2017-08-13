package chhatrachhorm.androidapp.onenterpise.lapidchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference userDatabaseRef;
    private FirebaseUser mFireBaseUser;

    private TextView mUsername;
    private TextView mStatus;
    private CircleImageView mCircleImageView;

    private Button mChangeStatusBtn;
    private Button mChangeImageProfileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mUsername = findViewById(R.id.settings_username);
        mStatus = findViewById(R.id.settings_status);
        mCircleImageView = findViewById(R.id.setting_image_profile);

        mChangeStatusBtn = findViewById(R.id.settings_change_status_btn);
        mChangeImageProfileBtn = findViewById(R.id.settings_change_image_btn);

        mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(mFireBaseUser.getUid());

        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mUsername.setText(username);
                mStatus.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SettingsActivity.this, databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mChangeStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, StatusChangesActivity.class)
                    .putExtra("status_value", mStatus.getText().toString()));

            }
        });
    }
}
