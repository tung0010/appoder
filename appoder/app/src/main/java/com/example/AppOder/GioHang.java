package com.example.AppOder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

import com.example.AppOder.Model.PreferenceUtil;
import com.example.AppOder.Model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GioHang#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GioHang extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private LinearLayout hoSo, caiDat;
    private Button singOut;
    private ImageView imgAvatar;
    private TextView tvName;
    private Uri mUri;
    private User userProfile;

    public GioHang() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GioHang.
     */
    // TODO: Rename and change types and number of parameters
    public static GioHang newInstance(String param1, String param2) {
        GioHang fragment = new GioHang();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_khac, container, false);
        hoSo = view.findViewById(R.id.llHoSo);
        caiDat = view.findViewById(R.id.llCaiDat);
        singOut = view.findViewById(R.id.btnDangXuat);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvName = view.findViewById(R.id.tvName);



        hoSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HoSo.class));
            }
        });

        caiDat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangePassword.class));
            }
        });

        singOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), DangNhap.class));
            }
        });

        String loginType = getActivity().getIntent().getStringExtra("login_type");
        Log.d("LoginType", "Value: " + loginType);
        // Dựa vào loại đăng nhập, gọi phương thức hiển thị thông tin người dùng tương ứng
        if ("email".equals(loginType)) {
            showUser();
            PreferenceUtil.saveLoginType(getActivity(), "email");
        } else if ("google".equals(loginType)) {
            showUserGG();
            PreferenceUtil.saveLoginType(getActivity(), "google");
        }

        userProfile = PreferenceUtil.getUserProfile(getActivity());
        // Hiển thị dữ liệu hồ sơ
        showUserUpdate();
        PreferenceUtil.saveUserProfile(getActivity(),userProfile);
        return view;
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
                        Uri photoUrl = currentUser.getPhotoUrl();

                        // Hiển thị thông tin người dùng lên giao diện
                        tvName.setText(name);
                        // Kiểm tra xem Fragment vẫn được gắn vào Activity trước khi sử dụng Glide
                        if (isAdded()) {
                            Uri uriHT = currentUser.getPhotoUrl();
                            if (uriHT != null) {
                                // Nếu URI tồn tại, tải và hiển thị ảnh từ URI
                                Glide.with(getActivity()).load(uriHT).circleCrop().into(imgAvatar);
                            } else {
                                // Nếu URI không tồn tại, sử dụng ảnh mặc định
                                imgAvatar.setImageResource(R.drawable.avatar_foreground);
                            }                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý khi có lỗi
                    Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        String name = user.getDisplayName();
        Uri photoUrl = user.getPhotoUrl();

        if(name ==null){
            tvName.setVisibility(View.GONE);
        }
        else {
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(name);
        }

        Glide.with(getActivity()).load(photoUrl).circleCrop().error(R.drawable.avatar_foreground).into(imgAvatar);
    }
    private void showUserGG(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        tvName.setText(account.getDisplayName());
        Glide.with(this).load(account.getPhotoUrl()).error(R.drawable.avatar_foreground).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(imgAvatar);

    }


    @Override
    public void onResume() {
        super.onResume();

        // Kiểm tra login_type từ SharedPreferences
        String loginType = PreferenceUtil.getLoginType(getActivity());

        // Hiển thị dữ liệu người dùng tùy thuộc vào login_type
        if ("google".equals(loginType)) {
            showUserGG();
        } else if ("email".equals(loginType)) {
            showUser();
        }
    }

}