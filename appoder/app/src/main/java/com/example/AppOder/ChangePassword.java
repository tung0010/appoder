package com.example.AppOder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.AuthCredential;

import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private EditText edPassOld, edPassNew, edConPassNew;
    private ImageView iconPassOld, iconPassNew, iconConPassNew, icBack;
    private Button btnUpdateMK;
    private FirebaseAuth mAuth;
    private boolean passwordOldshowing = false;
    private boolean passwordNewShowing = false;
    private boolean conPasswordNewShowing = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edPassOld = findViewById(R.id.ed_MKCu);
        edPassNew = findViewById(R.id.ed_MKMoi);
        edConPassNew = findViewById(R.id.ed_MKNL);
        iconPassOld = findViewById(R.id.icon_pass_cu);
        iconPassNew = findViewById(R.id.icon_pass_moi);
        iconConPassNew = findViewById(R.id.icon_conpass_moi);
        btnUpdateMK = findViewById(R.id.btnUpdateMK);
        icBack = findViewById(R.id.icBackChangePass);

        icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(ChangePassword.this, GioHang.class));
//                finish();
                onBackPressed();
            }
        });

        iconPassOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordOldshowing){
                    passwordOldshowing = false;

                    edPassOld.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    iconPassOld.setImageResource(R.drawable.show_pass_foreground);
                }
                else{
                    passwordOldshowing = true;

                    edPassOld.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    iconPassOld.setImageResource(R.drawable.hide_pass_foreground);
                }

                edPassOld.setSelection(edPassOld.length());
            }
        });

        iconPassNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordNewShowing){
                    passwordNewShowing = false;
                    edPassNew.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    iconPassNew.setImageResource(R.drawable.show_pass_foreground);
                }
                else{
                    passwordNewShowing = true;

                    edPassNew.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    iconPassNew.setImageResource(R.drawable.hide_pass_foreground);
                }

                edPassNew.setSelection(edPassNew.length());
            }
        });

        iconConPassNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(conPasswordNewShowing){
                    conPasswordNewShowing = false;

                    edConPassNew.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    iconConPassNew.setImageResource(R.drawable.show_pass_foreground);
                }
                else{
                    conPasswordNewShowing = true;

                    edConPassNew.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    iconConPassNew.setImageResource(R.drawable.hide_pass_foreground);
                }

                edConPassNew.setSelection(edConPassNew.length());
            }
        });

        btnUpdateMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //reAuthenticate(firebaseUser);
                String passOld = edPassOld.getText().toString();
                String passNew = edPassNew.getText().toString();
                String conPassNew = edConPassNew.getText().toString();
                if(passOld.isEmpty()) {
                    edPassOld.setError("Please enter old pass");
                    edPassOld.requestFocus();
                } else if(passNew.isEmpty()){
                    edPassNew.setError("Please enter new pass");
                    edPassNew.requestFocus();
                } else if (conPassNew.isEmpty()) {
                    edConPassNew.setError("Please re-enter new pass");
                    edConPassNew.requestFocus();
                } else if (!isValidPassword(passNew)) {
                    edPassNew.setError("Password must be at least 6 characters long");
                    edPassNew.requestFocus();
                } else if (!passNew.equals(conPassNew)) {
                    edConPassNew.setError("Please re-enter same pass");
                    edConPassNew.requestFocus();
                } else if (passOld.equals(passNew)) {
                    edPassNew.setError("Please enter new pass");
                    edPassNew.requestFocus();
                } else{
                    updatePassword(passOld, passNew);
                    onBackPressed();
                }

            }
        });
    }

    private void updatePassword(String passOld, String passNew){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), passOld);
        firebaseUser.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //successful authenticated, update begin
                firebaseUser.updatePassword(passNew).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ChangePassword.this, "Password update", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChangePassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChangePassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // If there are fragments, pop the back stack to go back to the previous fragment
            getSupportFragmentManager().popBackStack();
        } else {
            // If there are no fragments in the back stack, finish the activity to go back to the previous activity
            finish();
        }
    }

}