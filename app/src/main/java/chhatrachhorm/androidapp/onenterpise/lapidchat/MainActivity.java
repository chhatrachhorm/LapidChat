package chhatrachhorm.androidapp.onenterpise.lapidchat;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

@TargetApi(Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;

    private ViewPager mViewPager;
    private SectionsPagersAdapter mSectionsPagersAdapter;
    private TabLayout mTabLayout;

    private DatabaseReference mUserDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);


        mAuth = FirebaseAuth.getInstance();
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());

        mViewPager = findViewById(R.id.main_TapPager);

        mSectionsPagersAdapter = new SectionsPagersAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagersAdapter);

        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startWelcomeActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            mUserDatabaseRef.child("online").setValue(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
            mUserDatabaseRef.child("online").setValue(ServerValue.TIMESTAMP);
    }

    private void startWelcomeActivity() {
        Intent welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(welcomeIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.main_logout:
                FirebaseAuth.getInstance().signOut();
                startWelcomeActivity();
                break;
            case R.id.main_account_setting:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.main_users:
                startActivity(new Intent(MainActivity.this, UsersActivity.class));
                break;
        }
        return true;
    }
}
