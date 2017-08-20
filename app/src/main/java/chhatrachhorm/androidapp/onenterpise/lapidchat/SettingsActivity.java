package chhatrachhorm.androidapp.onenterpise.lapidchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

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
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                mUsername.setText(username);
                mStatus.setText(status);
                Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar_male).into(mCircleImageView);
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
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);
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
                File imagePath = new File(resultUri.getPath());
                Bitmap thumb_image = null;
                try {
                    thumb_image = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(50)
                            .compressToBitmap(imagePath);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumbImageByte = baos.toByteArray();

                StorageReference imageFirebaseFilePath = mStorageRef.child("profile_images").child(mFireBaseUser.getUid() + ".jpg");
                final StorageReference thumbFirebaseFilePath = mStorageRef.child("thumb_images").child(mFireBaseUser.getUid() + ".jpg");
                imageFirebaseFilePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            UploadTask uploadTask = thumbFirebaseFilePath.putBytes(thumbImageByte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> UploadTask) {
                                    if(UploadTask.isSuccessful()){
                                        String download_Url = task.getResult().getDownloadUrl().toString();
                                        String thumb_download_url = UploadTask.getResult().getDownloadUrl().toString();
                                        Map images = new HashMap();
                                        images.put("image", download_Url);
                                        images.put("thumb_image", thumb_download_url);
                                        userDatabaseRef.updateChildren(images).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "Uploaded Successfully", Toast.LENGTH_LONG).show();
                                                }else{
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "Uploaded Failed", Toast.LENGTH_LONG).show();

                                                }

                                            }
                                        });
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
