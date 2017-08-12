package chhatrachhorm.androidapp.onenterpise.lapidchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private TextInputEditText EmailField;
    private TextInputEditText PasswordField;
    private Button LoginBtn;

    private ProgressDialog mLoginProgressDialog;

    private android.support.v7.widget.Toolbar mToolbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EmailField = findViewById(R.id.login_email);
        PasswordField = findViewById(R.id.login_pass);
        LoginBtn = findViewById(R.id.login_btn);

        mLoginProgressDialog = new ProgressDialog(this);

        mToolbar = findViewById(R.id.login_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Log in");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = EmailField.getEditableText().toString();
                String password = PasswordField.getEditableText().toString();

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    mLoginProgressDialog.setTitle("Checking your credential");
                    mLoginProgressDialog.setMessage("This may take a little while");
                    mLoginProgressDialog.setCanceledOnTouchOutside(false);
                    mLoginProgressDialog.show();
                    logInWithEmailPassword(email, password);
                }else{
                    Toast.makeText(LoginActivity.this, "Please Correctly Input the Data", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void logInWithEmailPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mLoginProgressDialog.dismiss();
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        }else{
                            mLoginProgressDialog.hide();
                            Toast.makeText(LoginActivity.this, "Log in Fail", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
}
