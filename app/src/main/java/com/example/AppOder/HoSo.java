package com.example.AppOder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.AppOder.Adapter.VoucherAdapter;
import com.example.AppOder.Adapter.Voucher_UersAdapter;
import com.example.AppOder.Model.PreferenceUtil;
import com.example.AppOder.Model.User;
import com.example.AppOder.Model.VoucherModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HoSo extends AppCompatActivity  {
    private ImageView icBackHS, imgAvatar;
    private TextView tvName;
    private EditText edEmail, edHoten, edSinhNhat, edSDT, edGioiTinh;
    private Button luuProfile,btnQuanlynhanvien;
    private Uri mUri;
    private ProgressDialog dialog;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private User userProfile;

    private ArrayList<VoucherModel> vouchers;
    private Voucher_UersAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ho_so);

     getview();
        edSinhNhat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(HoSo.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                edSinhNhat.setText(selectedDate);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        edGioiTinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HoSo.this);
                builder.setTitle("Chọn Giới Tính");
                builder.setItems(new CharSequence[]{"Nam", "Nữ"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedGender = "";
                        if (which == 0) {
                            selectedGender = "Nam";
                        } else if (which == 1) {
                            selectedGender = "Nữ";
                        }
                        edGioiTinh.setText(selectedGender);
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        edSDT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNumber = s.toString().trim();
                String mobile = "0[2-9]";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobile);
                mobileMatcher = mobilePattern.matcher(phoneNumber);

                if (phoneNumber.length() == 10) {
                    // Nếu nhập đủ 10 số, không làm gì
                } else if (phoneNumber.length() > 10) {
                    // Nếu nhập quá 10 số, cắt bớt số điện thoại
                    edSDT.setText(phoneNumber.substring(0, 10));
                    edSDT.setError("Số điện thoại phải có đúng 10 số");
                    edSDT.setSelection(10); // Di chuyển con trỏ đến cuối
                } else if (!mobileMatcher.find()) {
                    edSDT.setError("Số điện thoại k đúng định dạng");
                } else {
                    edSDT.setError("Số điện thoại chưa đủ 10 số");
                }
            }
        });
    btnQuanlynhanvien= findViewById(R.id.btnQuanlyNhanvien);
    btnQuanlynhanvien.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(HoSo.this,AdminQuanLyNhanVien.class);
            startActivity(intent);
        }
    });
        icBackHS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(HoSo.this, MainActivity.class);
//                intent.putExtra("fragmentHS", "giohang");
//                startActivity(intent);
                onBackPressed();
            }
        });

        String loginType = getIntent().getStringExtra("login_type");
        Log.d("LoginType", "Value: " + loginType);
        // Dựa vào loại đăng nhập, gọi phương thức hiển thị thông tin người dùng tương ứng
        if ("email".equals(loginType)) {
            showUser();
            PreferenceUtil.saveLoginType(this, "email");
        } else if ("google".equals(loginType)) {
            showUserGG();
            PreferenceUtil.saveLoginType(this, "google");
        }

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();

            }
        });

        luuProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onclickLuuProfile();
                onclickLuuProfile();
            }
        });
        String userid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        vouchers = new ArrayList<>();
        adapter = new Voucher_UersAdapter(this, vouchers, new Voucher_UersAdapter.onClickListener() {
            @Override
            public void onClick(VoucherModel voucher) {

            }
        });

        Button showDialogButton = findViewById(R.id.showDialogButton);
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVoucherDialog();
            }
        });

        fetchUserVouchers(userid);
    }


    private void fetchUserVouchers(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        DatabaseReference vouchersRef = FirebaseDatabase.getInstance().getReference("vouchers");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null && user.getVoucherList() != null) {
                    List<String> userVoucherCodes = user.getVoucherList();
                    for (String voucherCode : userVoucherCodes) {
                        vouchersRef.orderByChild("maGG").equalTo(voucherCode)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot voucherSnapshot : dataSnapshot.getChildren()) {
                                            VoucherModel voucher = voucherSnapshot.getValue(VoucherModel.class);
                                            if (voucher != null) {
                                                vouchers.add(voucher);
                                                adapter.notifyDataSetChanged();
                                            } else {
                                                Log.e("Voucher Fetch", "Voucher with code " + voucherCode + " is null");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.e("Voucher Fetch", "Error: " + databaseError.getMessage());
                                    }
                                });
                    }
                } else {
                    Log.e("User Fetch", "User or voucher list is null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("User Fetch", "Error: " + databaseError.getMessage());
            }
        });
    }



    private void showVoucherDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Loại bỏ tiêu đề (optional)
        dialog.setContentView(R.layout.dialog_voucher_list);

        RecyclerView voucherRecyclerView = dialog.findViewById(R.id.voucherRecyclerView);
        Button closeButton = dialog.findViewById(R.id.closeButton);

        voucherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        voucherRecyclerView.setAdapter(adapter);

        closeButton.setOnClickListener(v -> dialog.dismiss());

        // Thiết lập chiều rộng của dialog thành MATCH_PARENT
        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT; // Hoặc MATCH_PARENT nếu bạn muốn chiều cao cũng đầy đủ
            dialog.getWindow().setAttributes(params);
        }

        dialog.show();
    }

    private void showUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        String name = user.getDisplayName();
        String hoten = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        if(name ==null){
            tvName.setVisibility(View.GONE);
        }
        else {
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(name);
        }

        if(hoten ==null){
            edHoten.setText("");
        }
        else {
            edHoten.setText(name);
        }
        edEmail.setText(email);
        Glide.with(HoSo.this).load(photoUrl).error(R.drawable.avatar_foreground).into(imgAvatar);
    }

    private void showUserGG(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //if (account != null) {

        edEmail.setText(account.getEmail());
        edHoten.setText(account.getDisplayName());
        tvName.setText(account.getDisplayName());
        Glide.with(this).load(account.getPhotoUrl()).error(R.drawable.avatar_foreground).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(imgAvatar);

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null){
            mUri = data.getData();
            imgAvatar.setImageURI(mUri);
           // loadImageToCircleImageView(mUri);
           // uploadPic();
        }
    }
    private void loadImageToCircleImageView(Uri uri) {
        Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(imgAvatar);;
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // If there are fragments, pop the back stack to go back to the previous fragment
            getSupportFragmentManager().popBackStack();
        } else {
            // If there are no fragments in the back stack, finish the activity to go back to the previous activity
            finish();
        }
    }



    private void uploadPic(){
        if(mUri != null){
            //Save the image with uid of the currently logged user
            StorageReference fileReference = storageReference.child(mAuth.getCurrentUser().getUid() +"."+ getFiExtension(mUri));

            //Upload image to Storage
            fileReference.putFile(mUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            firebaseUser = mAuth.getCurrentUser();

                            //Finally set the display image of the user after upload
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileUpdates);
                        }
                    });
                    Toast.makeText(HoSo.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HoSo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(HoSo.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFiExtension(Uri mUri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(mUri));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Kiểm tra login_type từ SharedPreferences
        String loginType = PreferenceUtil.getLoginType(this);

        // Hiển thị dữ liệu người dùng tùy thuộc vào login_type
        if ("google".equals(loginType)) {
            showUserGG();
        } else if ("email".equals(loginType)) {
            showUser();
        }
        // Khôi phục dữ liệu hồ sơ từ SharedPreferences
        userProfile = PreferenceUtil.getUserProfile(this);
        showUserUpdate();
    }

    private void onclickLuuProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        dialog.show();
        String name = edHoten.getText().toString().trim();
        String gioiTinh = edGioiTinh.getText().toString().trim();
        String dienThoai = edSDT.getText().toString().trim();
        String sinhNhat = edSinhNhat.getText().toString().trim();

        String mobile = "0[2-9]";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobile);
        mobileMatcher = mobilePattern.matcher(dienThoai);

        if (dienThoai.length() == 10) {
            // Nếu nhập đủ 10 số, không làm gì
        } else if (dienThoai.length() > 10) {
            // Nếu nhập quá 10 số, cắt bớt số điện thoại
            edSDT.setText(dienThoai.substring(0, 10));
            edSDT.setError("Số điện thoại phải có đúng 10 số");
            edSDT.setSelection(10); // Di chuyển con trỏ đến cuối
        } else if (!mobileMatcher.find()) {
            edSDT.setError("Số điện thoại k đúng định dạng");
        } else {
            edSDT.setError("Số điện thoại chưa đủ 10 số");
        }

        // Lấy URI của ảnh hiện tại
        Uri currentPhotoUri = user.getPhotoUrl();

        // Cập nhật thông tin người dùng trên Firebase Authentication
        UserProfileChangeRequest.Builder profileUpdatesBuilder = new UserProfileChangeRequest.Builder()
                .setDisplayName(name);

        if (currentPhotoUri != null) {
            // Nếu URI của ảnh hiện tại có sẵn, sử dụng nó để cập nhật ảnh người dùng
            profileUpdatesBuilder.setPhotoUri(currentPhotoUri);
        }

        UserProfileChangeRequest profileUpdates = profileUpdatesBuilder.build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Lưu thông tin vào Realtime Database
                            saveUserDataToDatabase(sinhNhat,gioiTinh,dienThoai);
                            showUserUpdate();
                            PreferenceUtil.saveUserProfile(HoSo.this, userProfile);
                        } else {
                            Toast.makeText(HoSo.this, "Lỗi khi cập nhật thông tin người dùng", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        dialog.dismiss();
    }

    private void saveUserDataToDatabase(String sinhNhat, String gioiTinh, String dienThoai) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        User u = new User(sinhNhat, gioiTinh, dienThoai);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        userRef.child(user.getUid()).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(HoSo.this, "Save sucessfully", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(HoSo.this, "Save failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void showUserUpdate() {
        //Kiểm tra trạng thái đăng nhập của người dùng
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Người dùng đã đăng nhập, lấy ID của họ và truy vấn thông tin người dùng từ Firebase Realtime Database
            String userId = currentUser.getUid();
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        // Lấy thông tin người dùng từ snapshot
                        String name = currentUser.getDisplayName();
                        String email = currentUser.getEmail();
                        Uri photoUrl = currentUser.getPhotoUrl();
                        String phoneNumber = user.getPhoneNumber();
                        String gender = user.getGender();
                        String birthday = user.getBirthday();

                        // Hiển thị thông tin người dùng lên giao diện
                        tvName.setText(name);
                        edHoten.setText(name);
                        edGioiTinh.setText(gender);
                        edSDT.setText(phoneNumber);
                        edSinhNhat.setText(birthday);
                        edEmail.setText(email);
                        Uri uriHT = currentUser.getPhotoUrl();
                        if (uriHT != null) {
                            // Nếu URI tồn tại, tải và hiển thị ảnh từ URI
                            Glide.with(HoSo.this).load(uriHT).circleCrop().into(imgAvatar);
                        } else {
                            // Nếu URI không tồn tại, sử dụng ảnh mặc định
                            imgAvatar.setImageResource(R.drawable.avatar_foreground);
                        }                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý khi có lỗi
                    Toast.makeText(HoSo.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }private void getview() {
        icBackHS = findViewById(R.id.icBackChangePass);
        imgAvatar = findViewById(R.id.img_avatar);
        tvName = findViewById(R.id.tv_Name);
        edEmail = findViewById(R.id.edEmail);
        edHoten = findViewById(R.id.edHoten);
        edSinhNhat = findViewById(R.id.edSinhNhat);
        edGioiTinh = findViewById(R.id.edGioiTinh);
        edSDT = findViewById(R.id.edSDT);
        luuProfile = findViewById(R.id.btn_LuuProfile);
        dialog = new ProgressDialog(HoSo.this);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("DisplayAvatar");

    }
    private void showFullscreenDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_voucher_user4);

        // Set chiều rộng của dialog thành MATCH_PARENT
        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(params);
        }

        dialog.show();
    }
}