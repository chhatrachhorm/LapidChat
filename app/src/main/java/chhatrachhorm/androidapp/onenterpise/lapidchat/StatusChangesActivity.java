package chhatrachhorm.androidapp.onenterpise.lapidchat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusChangesActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputEditText mNewStatus;
    private Button mSaveChangesBtn;
    private ProgressDialog mProgressDialog;

    private DatabaseReference mDatabaseUserRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_changes);

        mToolbar = findViewById(R.id.status_changes_ToolBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Update Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mNewStatus = findViewById(R.id.status_changes_newStatus);
        mNewStatus.setText(getIntent().getStringExtra("status_value"));
        mSaveChangesBtn = findViewById(R.id.status_changes_saveChanges);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());

        mSaveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = mNewStatus.getEditableText().toString();
                if(!TextUtils.isEmpty(status)){
                    mProgressDialog = new ProgressDialog(StatusChangesActivity.this);
                    mProgressDialog.setTitle("Updating Status");
                    mProgressDialog.setMessage("This may take a couple times");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    mDatabaseUserRef.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mProgressDialog.dismiss();
                                finish();
                            }else Toast.makeText(StatusChangesActivity.this, "Fail to Update your Status", Toast.LENGTH_LONG).show();
                        }
                    });

                }else Toast.makeText(StatusChangesActivity.this, "Please Input all the data", Toast.LENGTH_LONG).show();
            }
        });

    }
}
