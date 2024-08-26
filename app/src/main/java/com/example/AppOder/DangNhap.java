package com.example.AppOder;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class DangNhap extends AppCompatActivity {

    private EditText mail, pass;
    private ImageView iconPass;
    private Button taoTK, dangNhap, quenMK;
    private FirebaseAuth auth;
    private AlertDialog.Builder dialog;
    private LayoutInflater inflater;
    private LinearLayout signInGG;
    private FirebaseDatabase database;
    private GoogleSignInClient mGoogleSignInClient;
    //private GoogleSignInAccount gsa;
    private DatabaseReference mDatabase;

    private boolean passwordshowing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        auth = FirebaseAuth.getInstance();
        mail = findViewById(R.id.ed_email);
        pass = findViewById(R.id.ed_pass);
        iconPass = findViewById(R.id.icon_pass);
        taoTK = findViewById(R.id.btn_taoTK);
        dangNhap = findViewById(R.id.btn_DN);
        quenMK = findViewById(R.id.btnQuenMK);

        dialog = new AlertDialog.Builder(DangNhap.this);
        inflater = this.getLayoutInflater();
        signInGG = findViewById(R.id.dk_signInGG);
        database = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        processrequest();

        signInGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processLogin();
            }
        });

        iconPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordshowing){
                    passwordshowing = false;

                    pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    iconPass.setImageResource(R.drawable.show_pass_foreground);
                }
                else{
                    passwordshowing = true;

                    pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    iconPass.setImageResource(R.drawable.hide_pass_foreground);
                }

                pass.setSelection(pass.length());
            }
        });

        quenMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = inflater.inflate(R.layout.reset_pass,null);
                dialog.setTitle("Reset Forgot Password?")
                        .setMessage("Enter your email to get password reset link.")
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText email = view.findViewById(R.id.reset_email);
                                if(email.getText().toString().isEmpty()){
                                    email.setError("Please enter your email");
                                    email.requestFocus();
                                }

                                auth.sendPasswordResetEmail(email.getText().toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(DangNhap.this,"Reset Email Sent", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(DangNhap.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }).setNegativeButton("Cancel", null)
                        .setView(view)
                        .create().show();
            }
        });

        taoTK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DangNhap.this, DangKy.class));
            }
        });

        dangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = mail.getText().toString().trim();
                String strPassword = pass.getText().toString().trim();

                if (strEmail.isEmpty()) {
                    mail.setError("Please enter your email");
                    mail.requestFocus();
                }else if (strPassword.isEmpty()) {
                    pass.setError("Please enter your password");
                    pass.requestFocus();
                } else {
                    auth.signInWithEmailAndPassword(strEmail, strPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        if (strEmail.toString().equals("admin@gmail.com")){
                                            Intent adminIntent = new Intent(DangNhap.this, AdminActivity.class);
                                            startActivity(adminIntent);
                                            finish();
                                        }
                                        else{
                                        Intent intent = new Intent(DangNhap.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        }
                                    } else {
                                        Toast.makeText(DangNhap.this, "Đăng nhập không thành công. Hãy kiểm tra lại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(DangNhap.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private void processrequest() {
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
    }

    private void processLogin() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Sau khi đăng xuất hoàn tất, yêu cầu đăng nhập mới
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("login_type", "google"); // Gửi dữ liệu đánh dấu loại đăng nhập
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(DangNhap.this, "Problem found in firebase login", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }catch (ApiException e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error in getting Google's information", Toast.LENGTH_LONG).show();
            }
        }
    }


}