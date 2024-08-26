package com.example.AppOder;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class DangKy extends AppCompatActivity {

    private EditText mail, pass, conPass, ten;
    private ImageView iconPass, iconConPass;
    private Button dangKy, dangNhap;

    private FirebaseAuth auth;
    private LinearLayout signInGG;
    private FirebaseDatabase database;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount gsa;
    private boolean passwordShowing = false;
    private boolean conPasswordShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);

        auth = FirebaseAuth.getInstance();
        mail = findViewById(R.id.ed_email);
        pass = findViewById(R.id.ed_MK);
        conPass = findViewById(R.id.ed_MKNL);
        iconPass = findViewById(R.id.icon_pass);
        iconConPass = findViewById(R.id.icon_conpass);
        ten = findViewById(R.id.ed_ten);
        dangKy = findViewById(R.id.btnDK);
        dangNhap = findViewById(R.id.btn_DangNhap);
        signInGG = findViewById(R.id.llGoogleAuth);
        database = FirebaseDatabase.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        gsa = GoogleSignIn.getLastSignedInAccount(this);

//        if(gsa !=null){
//            startActivity(new Intent(DangKy.this, HoSo.class));
//            finish();
//        }
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                handleSignTask(task);
            }
        });


        signInGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIntent = mGoogleSignInClient.getSignInIntent();
                activityResultLauncher.launch(signIntent);
            }
        });

        iconPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordShowing){
                    passwordShowing = false;

                    pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    iconPass.setImageResource(R.drawable.show_pass_foreground);
                }
                else{
                    passwordShowing = true;

                    pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    iconPass.setImageResource(R.drawable.hide_pass_foreground);
                }

                pass.setSelection(pass.length());
            }
        });

        iconConPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(conPasswordShowing){
                    conPasswordShowing = false;

                    conPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    iconConPass.setImageResource(R.drawable.show_pass_foreground);
                }
                else{
                    conPasswordShowing = true;

                    conPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    iconConPass.setImageResource(R.drawable.hide_pass_foreground);
                }

                conPass.setSelection(conPass.length());
            }
        });

        dangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTen = ten.getText().toString().trim();
                String strEmail = mail.getText().toString().trim();
                String strPassword = pass.getText().toString().trim();
                String strConfirmPassword = conPass.getText().toString().trim();

                if (strTen.isEmpty()) {
                    ten.setError("Please enter your ten");
                    ten.requestFocus();
                } else if (strEmail.isEmpty()) {
                    mail.setError("Please enter your email");
                    mail.requestFocus();
                } else if (strPassword.isEmpty()) {
                    pass.setError("Please enter your password");
                    pass.requestFocus();
                } else if (strConfirmPassword.isEmpty()) {
                    conPass.setError("Please confirm your password");
                    conPass.requestFocus();
                } else if (!isValidEmail(strEmail)) {
                    mail.setError("Invalid email address");
                    mail.requestFocus();
                } else if (!isValidPassword(strPassword)) {
                    pass.setError("Password must be at least 6 characters long");
                    pass.requestFocus();
                } else if (!strPassword.equals(strConfirmPassword)) {
                    conPass.setError("Passwords do not match");
                    conPass.requestFocus();
                } else {

                    auth.createUserWithEmailAndPassword(strEmail, strPassword)
                            .addOnCompleteListener(DangKy.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if (user != null) {
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(strTen)
                                                    .build();
                                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Sign in success, update UI with the signed-in user's information
                                                        Intent intent = new Intent(DangKy.this, DangNhap.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        // If sign in fails, display a message to the user.
                                                        Toast.makeText(DangKy.this, "Authentication lỗi. Email đã tồn tại", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(DangKy.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(DangKy.this, "Authentication lỗi. Email đã tồn tại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DangKy.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        dangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(DangKy.this, DangNhap.class));
                finish();
            }
        });
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Function to validate password
    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private void handleSignTask(Task<GoogleSignInAccount> task){
        startActivity(new Intent(DangKy.this, HoSo.class));
        finish();
    }
}