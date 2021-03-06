package chhatrachhorm.androidapp.onenterpise.lapidchat;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by chhormchhatra on 8/23/17.
 */

public class LapidChat extends Application {

//    To work offline with firebase
//    1. create a class extends application
//    2. enable setPersistenceEnabled
//    3. add .keepSynced(true) to database reference query
//    4. add android:name=".LapidChat" in android manifest right in application tag

    private DatabaseReference mUserDatabaseRef;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();
//        for picasso (we retrieve url from database, but image from storage, and picasso load it from storage via the url
//        to make picasso cached, we have to add:
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
//      add this to gradle : compile compile 'com.squareup.okhttp:okhttp:2.5.0' which goes along with 'com.squareup.picasso:picasso:2.5.2'
//      and add :
//        Picasso.with(SettingsActivity.this).load(image_url)
//                .networkPolicy(NetworkPolicy.OFFLINE)
//                .placeholder(R.drawable.default_avatar_male)
//                .into(mCircleImageView, new Callback() {
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                    @Override
//                    public void onError() {
//                        Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar_male)
//                                .into(mCircleImageView);
//                    }
//                });

        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        mUserDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    mUserDatabaseRef.child("online").onDisconnect().setValue(false);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
