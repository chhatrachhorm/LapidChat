package chhatrachhorm.androidapp.onenterpise.lapidchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Button LoginBtn;
    private Button RegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        LoginBtn = findViewById(R.id.welcome_login_btn);
        RegisterBtn = findViewById(R.id.welcome_register_btn);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            RegisterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent startRegister = new Intent(WelcomeActivity.this, RegisterActivity.class);
                    startActivity(startRegister);
                    finish();
                }
            });
            LoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent startLogin = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(startLogin);
                    finish();
                }
            });
        }
    }
}
