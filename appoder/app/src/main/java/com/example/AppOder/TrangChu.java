package com.example.AppOder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.AppOder.Adapter.BannerAdapter;
import com.example.AppOder.Adapter.LoaiAdapter;
import com.example.AppOder.Adapter.SanPhamBestSeller;
import com.example.AppOder.Model.LoaiModel;
import com.example.AppOder.Model.SanPhamModel;
import com.example.AppOder.Model.User;
import com.example.AppOder.Model.VoucherModel;
import com.example.AppOder.Utils.DsSanPhamMuaUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class TrangChu extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String idLoai;
    private ImageView icTimKiemTC, icHoSoTC;
    TextView txtCuaHang;
    LinearLayout btnCH;
    BottomNavigationView bottomNavigationView;
    TextView tvsoLuongSPM;
    RecyclerView recyclerView, rvSPBestSeller;
    LoaiAdapter loaiAdapter;
    ImageView gioHang;
    SanPhamBestSeller sanPhamBestSeller;
    ConstraintLayout csGioHang;
    private ViewPager viewPager;
    private List<String> mImageUrls;
    private int currentPage = 0;
    private Timer timer;
    private final long DELAY_MS = 0; // Thời gian trễ trước khi bắt đầu chạy auto scroll
    private final long PERIOD_MS = 2000;
    private float dX, dY;
    private int lastAction;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private TextView tvVoucherName, tvVoucherDescription;
    private Button btnSaveVoucher;
    private User currentUser; // Giả sử bạn đã có đối tượng User cho người dùng hiện tại
    private Handler handler = new Handler();
    private Runnable voucherRunnable;
    private String userId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public TrangChu() {
        // Required empty public constructor
    }

    public static TrangChu newInstance(String param1, String param2) {
        TrangChu fragment = new TrangChu();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trang_chu, container, false);

        // Khởi tạo các view cho voucher
        tvVoucherName = view.findViewById(R.id.tvVoucherName);
        tvVoucherDescription = view.findViewById(R.id.tvVoucherDescription);
        btnSaveVoucher = view.findViewById(R.id.btnSaveVoucher);

        // Khởi tạo user data (giả sử bạn có phương thức để lấy người dùng hiện tại)
        currentUser = getCurrentUser();

        // Định kỳ tải voucher
        scheduleVoucherLoading();

        // Thiết lập sự kiện click cho nút lưu voucher
        btnSaveVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String voucherCode = tvVoucherName.getText().toString();
                if (!currentUserHasVoucher(voucherCode)) {
                    saveVoucherToUser(voucherCode);
                    btnSaveVoucher.setText("Đã lưu");
                    btnSaveVoucher.setBackgroundColor(Color.GRAY);
                    btnSaveVoucher.setEnabled(false);
                }
            }
        });

        //chay banner
        viewPager = view.findViewById(R.id.viewBanner);
        mImageUrls = new ArrayList<>();

        BannerAdapter adapter = new BannerAdapter(getActivity(), mImageUrls);
        viewPager.setAdapter(adapter);
        fetchBannerImages();
        loadImagesFromFirebaseStorage();

        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            public void run() {
                if (currentPage == mImageUrls.size()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, DELAY_MS, PERIOD_MS);

        //Hiện Horizontal của các loại sp
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView = view.findViewById(R.id.rvLoai);
        recyclerView.setLayoutManager(layoutManager);

        //khi click vào item LoaiSP thì sẽ chuyển nameLoai sang SanPhamTrangChu.class
        LoaiAdapter.OnItemClickListener listener = new LoaiAdapter.OnItemClickListener() {
            @Override
            public void onLoaiItemClick(String idLoai) {
                Intent intent = new Intent(getActivity(), SanPhamTrangChu.class);
                intent.putExtra("idLoai", idLoai);
                startActivity(intent);
            }
        };

        FirebaseRecyclerOptions<LoaiModel> options =
                new FirebaseRecyclerOptions.Builder<LoaiModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("loai"), LoaiModel.class)
                        .build();

        loaiAdapter = new LoaiAdapter(options, listener);
        recyclerView.setAdapter(loaiAdapter);

        //hiện List best seller
        LinearLayoutManager layoutManagerSP = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvSPBestSeller = view.findViewById(R.id.rvSPBest);
        rvSPBestSeller.setLayoutManager(layoutManagerSP);

        SanPhamBestSeller.OnItemSPBestClickListener spBestClickListener = new SanPhamBestSeller.OnItemSPBestClickListener() {
            @Override
            public void onItemClick(String idSP) {
                Intent intent = new Intent(getActivity(), ChiTietSP.class);
                intent.putExtra("idSP", idSP);
                startActivity(intent);
            }
        };

        FirebaseRecyclerOptions<SanPhamModel> optionSP =
                new FirebaseRecyclerOptions.Builder<SanPhamModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("sanpham").orderByChild("isBanChay").equalTo(true), SanPhamModel.class)
                        .build();

        sanPhamBestSeller = new SanPhamBestSeller(optionSP, spBestClickListener);
        rvSPBestSeller.setAdapter(sanPhamBestSeller);
        sanPhamBestSeller.startListening();

        //sang trang giỏ hàng
        //onTouch giohang để nó có thể di chuyển tuỳ ý trong layout
        gioHang = view.findViewById(R.id.imageViewGioHang);
        csGioHang = view.findViewById(R.id.csGioHang);
        csGioHang.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        lastAction = MotionEvent.ACTION_DOWN;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        view.setX(event.getRawX() + dX);
                        view.setY(event.getRawY() + dY);
                        lastAction = MotionEvent.ACTION_MOVE;
                        break;

                    case MotionEvent.ACTION_UP:
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            Intent intent = new Intent(getActivity(), ActivityGioHang.class);
                            startActivity(intent);
                        }
                        break;

                    default:
                        return false;
                }
                return true;
            }
        });

        //txtSoluongSPM được thay đổi
        tvsoLuongSPM = view.findViewById(R.id.tvSoLuongMua);
        onResume();

        return view;
    }
    private User getCurrentUser() {
        // Thay thế đoạn mã này bằng mã thực tế để lấy User từ cơ sở dữ liệu của bạn
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
                if (currentUser == null) {
                    currentUser = new User(); // Khởi tạo nếu người dùng không tồn tại
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseDatabase", "Error loading current user", error.toException());
            }
        });
        return currentUser;
    }

    private boolean currentUserHasVoucher(String voucherCode) {
        if (currentUser != null && currentUser.getVoucherList() != null) {
            return currentUser.getVoucherList().contains(voucherCode);
        }
        return false;
    }

    private void saveVoucherToUser(String voucherCode) {
        String userID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(userID)
                    .child("voucherList");


            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<String> vouchers = new ArrayList<>();
                    if (snapshot.exists()) {
                        for (DataSnapshot voucherSnapshot : snapshot.getChildren()) {
                            String existingVoucher = voucherSnapshot.getValue(String.class);
                            if (existingVoucher != null) {
                                vouchers.add(existingVoucher);
                            }
                        }
                    }
                    if (!vouchers.contains(voucherCode)) {
                        vouchers.add(voucherCode);
                    }
                    userRef.setValue(vouchers);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseDatabase", "Error updating voucher list", error.toException());
                }
            });
        } else {
            Log.e("saveVoucherToUser", "currentUser or currentUser.getPhoneNumber() is null");
        }
    }
    private void loadNewVoucher() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference vouchersRef = database.getReference("vouchers");

        vouchersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<DataSnapshot> voucherList = new ArrayList<>();
                    for (DataSnapshot voucherSnapshot : dataSnapshot.getChildren()) {
                        voucherList.add(voucherSnapshot);
                    }

                    if (!voucherList.isEmpty()) {
                        int randomIndex = new Random().nextInt(voucherList.size());
                        DataSnapshot randomVoucherSnapshot = voucherList.get(randomIndex);
                        VoucherModel randomVoucher = randomVoucherSnapshot.getValue(VoucherModel.class);

                        if (randomVoucher != null) {
                            // Hiển thị thông tin voucher ngẫu nhiên
                            tvVoucherName.setText(randomVoucher.getMaGG());
                            tvVoucherDescription.setText(String.valueOf(randomVoucher.getMota()));

                            if (currentUserHasVoucher(randomVoucher.getMaGG())) {
                                btnSaveVoucher.setText("Đã lưu");
                                btnSaveVoucher.setBackgroundColor(Color.GRAY);
                                btnSaveVoucher.setEnabled(false);
                            } else {
                                btnSaveVoucher.setText("Lưu");
                                btnSaveVoucher.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                btnSaveVoucher.setEnabled(true);
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.e("Firebase", "Lỗi khi tải voucher: " + databaseError.getMessage());
            }
        });
    }
//    private VoucherModel getRandomVoucher(List<VoucherModel> allVouchers, List<String> userVouchers) {
//        List<VoucherModel> availableVouchers = new ArrayList<>();
//
//        for (VoucherModel voucher : allVouchers) {
//            if (!userVouchers.contains(voucher.getMaGG())) {
//                availableVouchers.add(voucher);
//            }
//        }
//
//        if (!availableVouchers.isEmpty()) {
//            Random random = new Random();
//            int randomIndex = random.nextInt(availableVouchers.size());
//            return availableVouchers.get(randomIndex);
//        }
//
//        return null; // Không có voucher mới để hiển thị
//    }

    private void scheduleVoucherLoading() {
        voucherRunnable = new Runnable() {
            @Override
            public void run() {
                loadNewVoucher();
                handler.postDelayed(this,3000); // Định kỳ tải mỗi 5 giây
            }
        };
        handler.post(voucherRunnable);
    }


    private void fetchBannerImages() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("banner");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot bannerSnapshot : dataSnapshot.getChildren()) {
                    String downloadUrl = bannerSnapshot.child("downloadUrl").getValue(String.class);
                    mImageUrls.add(downloadUrl);
                    viewPager.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseDatabase", "Error reading banner data", databaseError.toException());
            }
        });
    }

    private void loadImagesFromFirebaseStorage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("banner");

        storageReference.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    mImageUrls.add(imageUrl);
                    viewPager.getAdapter().notifyDataSetChanged();
                }).addOnFailureListener(e -> {
                    Log.e("FirebaseStorage", "Error getting download URL", e);
                });
            }
        }).addOnFailureListener(e -> {
            Log.e("FirebaseStorage", "Error listing file", e);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loaiAdapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        String slMua = String.valueOf(DsSanPhamMuaUtil.getInstance().getSoLuongSP());
        tvsoLuongSPM.setText(slMua);
    }

    @Override
    public void onStop() {
        super.onStop();
        loaiAdapter.stopListening();
        handler.removeCallbacks(voucherRunnable);
    }
}

