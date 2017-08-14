package chhatrachhorm.androidapp.onenterpise.lapidchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    private TextInputEditText UsernameField;
    private TextInputEditText EmailField;
    private TextInputEditText PassField;
    private Button RegisterBtn;
    private Toolbar mToolbar;
    private ProgressDialog mRegProgressDialog;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mToolbar = findViewById(R.id.register_page_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.welcome_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegProgressDialog = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();

        UsernameField = findViewById(R.id.reg_username);
        EmailField = findViewById(R.id.reg_email);
        PassField = findViewById(R.id.reg_pass);
        RegisterBtn =findViewById(R.id.reg_signup_btn);


        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = UsernameField.getEditableText().toString();
                String email = EmailField.getEditableText().toString();
                String password = PassField.getEditableText().toString();

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                    mRegProgressDialog.setTitle("Assigning User ID");
                    mRegProgressDialog.setMessage("This will take a couple seconds...");
                    mRegProgressDialog.setCanceledOnTouchOutside(false);
                    mRegProgressDialog.show();

                    createUserWithEmailAndPassword(email, password, username);

                }else Toast.makeText(RegisterActivity.this, "Please Input All the Fields", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void createUserWithEmailAndPassword(String email, String password, final String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", username);
                            userMap.put("status", "Hi there, I am using LapidChat for secure talk!");
                            userMap.put("image", "https://firebasestorage.googleapis.com/v0/b/lapidchat-a1b4b.appspot.com/o/profile_images%2FPhotoAvatar.jpg?alt=media&token=c6e5d54f-97a8-49a3-b1cd-78ffd3a22a28");
                            userMap.put("thumb_image", "default");

                            mDatabaseRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mRegProgressDialog.dismiss();

                                        Intent startMain = new Intent(RegisterActivity.this, MainActivity.class);
                                        startMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(startMain);
                                        finish();
                                    }
                                }
                            });
                        }else{
                            mRegProgressDialog.hide();
                            Toast.makeText(RegisterActivity.this, "Registration Fail", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
