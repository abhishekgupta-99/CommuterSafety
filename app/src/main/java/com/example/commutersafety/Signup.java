package com.example.commutersafety;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    public EditText emailId, passwd, spname,phone,repasswd;
    Button btnSignUp;
    TextView signIn,login;
    FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        mDatabase =  FirebaseDatabase.getInstance().getReference("user_details");

        //getSupportActionBar().hide(); // hide the title bar

        firebaseAuth = FirebaseAuth.getInstance();
        spname = findViewById(R.id.sign_name);
        phone = findViewById(R.id.ETemail);
        emailId = findViewById(R.id.ETemail2);
        passwd = findViewById(R.id.ETpassword2);

        //repasswd = findViewById(R.id.ETpassword2);
        btnSignUp = findViewById(R.id.login);
        signIn = findViewById(R.id.textView7);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailID = emailId.getText().toString();
                String paswd = passwd.getText().toString();
                final String sppname = spname.getText().toString();
                final String spphone = phone.getText().toString();

                if (emailID.isEmpty()) {
                    emailId.setError("Provide your Email first!");
                    emailId.requestFocus();
                } else if (paswd.isEmpty()) {
                    passwd.setError("Set your password");
                    passwd.requestFocus();
                } else if (emailID.isEmpty() && paswd.isEmpty() && spphone.isEmpty() && spphone.isEmpty()) {
                    Toast.makeText(Signup.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(emailID.isEmpty() && paswd.isEmpty())) {
                    firebaseAuth.createUserWithEmailAndPassword(emailID, paswd).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(Signup.this,"Try Different Credentials ",Toast.LENGTH_SHORT).show();
                            } else {

                                User user = new User(firebaseAuth.getUid(),sppname,emailID,spphone);
                                mDatabase.child(firebaseAuth.getUid()).setValue(user);

                                Intent UserActiv = new Intent(Signup.this, MapsActivity.class);
                                startActivity(UserActiv);
                                Toast.makeText(Signup.this,"SignUp successful: ",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(Signup.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(Signup.this, Login.class);
                startActivity(I);
            }
        });
    }
}