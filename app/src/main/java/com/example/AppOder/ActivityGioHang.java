package com.example.AppOder;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AppOder.Adapter.HD_SanPhamMuaAdapter;
import com.example.AppOder.Adapter.VoucherAdapter;
import com.example.AppOder.Adapter.Voucher_UersAdapter;
import com.example.AppOder.Model.CuaHangModel;
import com.example.AppOder.Model.HoaDonModel;
import com.example.AppOder.Model.SanPhamMuaModel;
import com.example.AppOder.Model.SelectedStore;
import com.example.AppOder.Model.User;
import com.example.AppOder.Model.VoucherModel;
import com.example.AppOder.Utils.DsSanPhamMuaUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActivityGioHang extends AppCompatActivity {
    RecyclerView rcvDsSPMua;
    ImageView btnTroLai;
    private String userId;
    HD_SanPhamMuaAdapter adapter;
    Integer countTongHD=0;
    TextView txtXoaGioHang, txtThaiDoiCH,txtGioHangTenCH, txtGhDiaChiCH1,txtGhDiaChiCH2,txtGhiChu;
    TextView txtThemSP,txtGiaTT,txtGh_DatHang,txtTongCong,tvThemVoucher;
    ArrayList<SanPhamMuaModel> DssanPhamMua;
    private ArrayList<VoucherModel> vouchers;
    private Voucher_UersAdapter voucher_uersAdapter;
    int giamgia=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actyvity_gio_hang);
        getView();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();

        CuaHangModel cuaHangModel= SelectedStore.getInstance().getStore();
        if(cuaHangModel.getNameCH() != ""){
            txtGioHangTenCH.setText("Lofita && Coffee");
            txtGhDiaChiCH1.setText("36 Hào Nam");
            txtGhDiaChiCH2.setText("Đống Đa, Hà Nội");
        }


        onResume();
        UpLoadDsSanPhamMua();

        txtXoaGioHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DsSanPhamMuaUtil.getInstance().setDsSanPhamMuaUtil(new ArrayList<SanPhamMuaModel>());
                UpLoadDsSanPhamMua();
            }
        });

        txtGh_DatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DsSanPhamMuaUtil.getInstance().getDsSanPhamMuaUtil().size()>0){
                    UploadDuLieu();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    // Truyền dữ liệu (nếu cần)
                    intent.putExtra("key", "lichsu");
                    // Chuyển đến Activity mới
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "Bạn chưa chọn sản phẩm cần mua", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtThaiDoiCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityGioHang.class);
                // Truyền dữ liệu (nếu cần)
                intent.putExtra("key", "cuahang");
                // Chuyển đến Activity mới
                startActivity(intent);
            }
        });


        txtThemSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), DanhMUc.class);
                startActivity(intent);
            }
        });
        btnTroLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txtThemSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // Truyền dữ liệu (nếu cần)
                intent.putExtra("key", "DatHang");
                // Chuyển đến Activity mới
                startActivity(intent);

            }
        });
        //khởi tạo userid để lấy về voucher theo user
        String userid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        //khởi tạo voucher mới để load danh sach voucher

        tvThemVoucher = findViewById(R.id.tvThemVC);
        tvThemVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showDialogVoucher();
            }
        });
        vouchers = new ArrayList<>();
        voucher_uersAdapter= new Voucher_UersAdapter(this, vouchers, new Voucher_UersAdapter.onClickListener() {
            @Override
            public void onClick(VoucherModel voucher) {
                TextView tvMaGG= findViewById(R.id.tvMaVC);
                TextView tvGiatri= findViewById(R.id.tvGh_KhuyenMai);
                tvMaGG.setText(voucher.getMaGG());
                tvGiatri.setText(String.valueOf(voucher.getGiamgia())+"đ");
            }
        });
        fetchUserVouchers(userId);

    }
    private void showDialogVoucher(){
        Dialog dialog = new Dialog(ActivityGioHang.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Loại bỏ tiêu đề (optional)
        dialog.setContentView(R.layout.dialog_voucher_list);

        RecyclerView voucherRecyclerView = dialog.findViewById(R.id.voucherRecyclerView);
        Button closeButton = dialog.findViewById(R.id.closeButton);

        voucherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        voucherRecyclerView.setAdapter(voucher_uersAdapter);

        closeButton.setOnClickListener(v -> dialog.dismiss());
        //set chiều ngang thành match_parent
        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT; // Hoặc MATCH_PARENT nếu bạn muốn chiều cao cũng đầy đủ
            dialog.getWindow().setAttributes(params);
        }

        dialog.show();

        //xử lý khi click item

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
                                                boolean isValid = checkVoucherCondition(voucher); // Kiểm tra điều kiện voucher
                                                if (isValid) {
                                                    vouchers.add(voucher);
                                                   giamgia=  voucher.getGiamgia();
                                                   UpLoadDsSanPhamMua();

                                                }
                                            }
                                        }
                                        if (vouchers.isEmpty()) {
                                            Toast.makeText(ActivityGioHang.this, "Không có voucher khả dụng", Toast.LENGTH_SHORT).show();
                                        } else {
                                            voucher_uersAdapter.notifyDataSetChanged();
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

    private boolean checkVoucherCondition(VoucherModel voucher) {
        int minimumOrderValue = voucher.getDieuKien(); // Giá trị tối thiểu để áp dụng voucher
        int totalOrderValue = calculateTotalOrderValue(); // Tổng giá trị giỏ hàng
        return totalOrderValue >= minimumOrderValue;
    }

    private int calculateTotalOrderValue() {
        int total = 0;
        for (SanPhamMuaModel sp : DssanPhamMua) {
            total += sp.getGia();
        }
        return total;
    }


    private  void UploadDuLieu(){
        Calendar calendar = Calendar.getInstance();
        // Lấy ngày hiện tại
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // Lấy tháng hiện tại (lưu ý: tháng trong Java bắt đầu từ 0)
        int month = calendar.get(Calendar.MONTH) + 1;
        // Lấy năm hiện tại
        int year = calendar.get(Calendar.YEAR);
        //Lấy thời gian
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // Lấy giờ theo định dạng 24h
        int minute = calendar.get(Calendar.MINUTE);

        // Khởi tạo Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // Tham chiếu đến nút mà bạn muốn ghi dữ liệu vào
        DatabaseReference myRef = database.getReference("hoadon");

        // Tạo một đối tượng chứa dữ liệu bạn muốn ghi
        // Ví dụ, một đối tượng User

        String mahd="HD"+userId.charAt(1)+userId.charAt(3)+year+month+day+hour+minute;
        HoaDonModel hoadon= new HoaDonModel();
        hoadon.setGioL(hour+":"+minute);
        hoadon.setNgayL(String.format("%d/%d/%d", day, month, year));
        hoadon.setMaKh(userId);
        hoadon.setMaHd(mahd);
        hoadon.setTrangThaiD(1);
        String sTongTien = txtTongCong.getText().toString().replaceAll("[^0-9]", "");
        hoadon.setTongTien(Integer.parseInt(sTongTien));
        List<SanPhamMuaModel> listSanPhamMua = new ArrayList<>(DssanPhamMua);
        hoadon.setSanPhamMua(listSanPhamMua);
        hoadon.setNameCH(txtGioHangTenCH.getText().toString());
        hoadon.setDiachiCH(txtGhDiaChiCH1.getText().toString());
        hoadon.setLocation(txtGhDiaChiCH2.getText().toString());
        // Ghi dữ liệu lên Firebase Realtime Database
        myRef.push().setValue(hoadon)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Ghi dữ liệu thành công
                        DsSanPhamMuaUtil.getInstance().setDsSanPhamMuaUtil(new ArrayList<SanPhamMuaModel>());
                        UpLoadDsSanPhamMua();
                        Log.d(TAG, "Đặt hàng thành công.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ghi dữ liệu thất bại
                        Log.w(TAG, "Error writing data to Firebase.", e);
                    }
                });
    }
    private  void UpLoadDsSanPhamMua(){
        rcvDsSPMua.setLayoutManager(new LinearLayoutManager(this));
        DssanPhamMua= DsSanPhamMuaUtil.getInstance().getDsSanPhamMuaUtil();
        // Tạo và thiết lập adapter cho RecyclerView
        adapter = new HD_SanPhamMuaAdapter(this.getLayoutInflater(), DssanPhamMua, new HD_SanPhamMuaAdapter.OnItemSanPhamMClickListener() {
            @Override
            public void onItemClick(String idSP,Integer stt) {
                Intent intent = new Intent(getApplicationContext(), ChiTietSP.class);
                intent.putExtra("idSP", idSP);
                intent.putExtra("STT",stt);
                startActivity(intent);
            }
        });
        int TongGia=0;
        for (SanPhamMuaModel sp : DssanPhamMua){
            TongGia =TongGia + sp.getGia();
        }
        int discountValue = 0;
        int finalAmount = TongGia - giamgia;
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedGiaSP = decimalFormat.format(finalAmount);
        txtGiaTT.setText(String.format("%s đ", formattedGiaSP));
        txtTongCong.setText(String.format("%s đ", formattedGiaSP));
        txtGh_DatHang.setText(String.format("Đặt Hàng (%s đ)", formattedGiaSP));
        rcvDsSPMua.setAdapter(adapter);
    }


    private void tongSoHD(){
        DatabaseReference SPMua = FirebaseDatabase.getInstance().getReference().child("hoadon");
        Query query = SPMua.orderByChild("makh").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, String.valueOf(snapshot.getChildrenCount()));
                if (snapshot.exists()) {
                    countTongHD= Math.toIntExact(snapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
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
    private void getView(){
        txtXoaGioHang = findViewById(R.id.tvXoaGioHang);
        txtThaiDoiCH = findViewById(R.id.tvThaiDoiCH);
        txtGioHangTenCH = findViewById(R.id.tvGioHangTenCH);
        txtGhDiaChiCH1 = findViewById(R.id.tvGhDiaChiCH1);
        txtGhDiaChiCH2 = findViewById(R.id.tvGhDiaChiCH2);
        txtGhiChu = findViewById(R.id.edGhiChuHD);
        txtThemSP = findViewById(R.id.tvThemSP);
        txtGiaTT =findViewById(R.id.tvGiaTT);
        txtGh_DatHang = findViewById(R.id.tvGh_DatHang);
        txtTongCong = findViewById(R.id.tvGh_TongCong);
        rcvDsSPMua = findViewById(R.id.rcvDsSPMua);
        btnTroLai =findViewById(R.id.icBackGH);
    }
    public void onResume() {
        super.onResume();
        UpLoadDsSanPhamMua();
    }
}