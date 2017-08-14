package chhatrachhorm.androidapp.onenterpise.lapidchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;


import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference userDatabaseRef;
    private FirebaseUser mFireBaseUser;

    private TextView mUsername;
    private TextView mStatus;
    private CircleImageView mCircleImageView;

    private Button mChangeStatusBtn;
    private Button mChangeImageProfileBtn;

    private StorageReference mStorageRef;

    private ProgressDialog mProgressDialog;

    private static int GALLERY_PICK = 1;

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


        mStorageRef = FirebaseStorage.getInstance().getReference();
        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mUsername.setText(username);
                mStatus.setText(status);
                Picasso.with(SettingsActivity.this).load(image).into(mCircleImageView);
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

        mChangeImageProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_PICK);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgressDialog = new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Uploading Image");
                mProgressDialog.setMessage("This may take a couple times...");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                Uri resultUri = result.getUri();
                StorageReference firebaseFilePath = mStorageRef.child("profile_images").child(mFireBaseUser.getUid() + ".jpg");
                firebaseFilePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            userDatabaseRef.child("image").setValue(task.getResult().getDownloadUrl().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mProgressDialog.dismiss();
                                        Toast.makeText(SettingsActivity.this, "Uploaded Successfully", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });


                        }else{
                            mProgressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Uploaded Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SettingsActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }

    }
}
