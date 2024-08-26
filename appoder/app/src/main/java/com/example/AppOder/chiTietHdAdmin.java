package com.example.AppOder;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AppOder.Adapter.HD_SanPhamMuaAdapter;
import com.example.AppOder.Model.HoaDonModel;
import com.example.AppOder.Model.SanPhamMuaModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Activity for viewing and updating order details by admin
 */
public class chiTietHdAdmin extends AppCompatActivity {
    TextView maHD, thoiGianL, tenCH, diaChiCH1, diaChiCH2, TinhTam, GiaTongT, GiaTTT,tvTrangThaiD;
    ImageView btnTroLai;
    RecyclerView rcvChiTietHD;
    HD_SanPhamMuaAdapter adapter;
    Button btnHuyDonHang;
    private TextView tvTinhTrang;
    private Button btnHuy, btnDangGiao, btnDaGiao;
    private int trangthaiD;
    private DatabaseReference HDref;
    private String idHd; // ID của đơn hàng hiện tại
    RecyclerView rcChiTietHD;

    ArrayList<SanPhamMuaModel> arrSanPhamM = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chi_tiet_hd_admin);
        getView();
        // Khởi tạo các view
        tvTinhTrang = findViewById(R.id.tvChiTietHD_TinhTrang);
        btnHuy = findViewById(R.id.btnHuy);
        btnDangGiao = findViewById(R.id.btnDangGiao);
        btnDaGiao = findViewById(R.id.btnDaGiao);

        // Lấy orderId từ Intent
        idHd = getIntent().getStringExtra("idHD");
        // Khởi tạo DatabaseReference
        HDref = FirebaseDatabase.getInstance().getReference("hoadon").child(idHd);
        // Lấy trạng thái đơn hàng từ Firebase
        HDref.child("trangThaiD").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    trangthaiD = dataSnapshot.getValue(Integer.class);
                    updateUIBasedOnStatus(trangthaiD);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(chiTietHdAdmin.this, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });

       HDref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HoaDonModel hoaDonModel = dataSnapshot.getValue(HoaDonModel.class);
                    if (hoaDonModel != null) {
                        maHD.setText(hoaDonModel.getMaHd());
                        thoiGianL.setText(String.format("%s %s", hoaDonModel.getNgayL(), hoaDonModel.getGioL()));
                        tenCH.setText(hoaDonModel.getNameCH());
                        DecimalFormat decimalFormat = new DecimalFormat("#,###");
                        float giaSP = hoaDonModel.getTongTien();
                        String formattedGiaSP = decimalFormat.format(giaSP);
//                        GiaTongT.setText(String.format("%s đ", formattedGiaSP));
//                        GiaTTT.setText(String.format("%s đ", formattedGiaSP));
                        arrSanPhamM = (ArrayList<SanPhamMuaModel>) hoaDonModel.getSanPhamMua();
                        rcvChiTietHD.setLayoutManager(new LinearLayoutManager(chiTietHdAdmin.this));
                        adapter = new HD_SanPhamMuaAdapter(getLayoutInflater(), arrSanPhamM, new HD_SanPhamMuaAdapter.OnItemSanPhamMClickListener() {
                            @Override
                            public void onItemClick(String idHD, Integer stt) {
                                // Handle item click if necessary
                            }
                        });
                        rcvChiTietHD.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(chiTietHdAdmin.this, "Lỗi khi tải chi tiết hóa đơn", Toast.LENGTH_SHORT).show();
            }
        });
         //Xử lý sự kiện nhấp vào nút "Đã Hủy"
        btnHuy.setOnClickListener(v -> {
            if (trangthaiD == 1) { // Nếu đơn hàng đang giao, không cho phép hủy
                Toast.makeText(chiTietHdAdmin.this, "Không thể hủy đơn hàng đang giao", Toast.LENGTH_SHORT).show();
            } else {
                updateOrderStatus(0);
            }
        });

        // Xử lý sự kiện nhấp vào nút "Đang Giao"
        btnDangGiao.setOnClickListener(v -> {
            if (trangthaiD == 0) { // Nếu đơn hàng đã hủy, không cho phép đánh dấu là đang giao
                Toast.makeText(chiTietHdAdmin.this, "Đơn hàng đã bị hủy", Toast.LENGTH_SHORT).show();
            } else if (trangthaiD == 2) { // Nếu đơn hàng đã giao, không cho phép đánh dấu lại là đang giao
                Toast.makeText(chiTietHdAdmin.this, "Đơn hàng đã giao", Toast.LENGTH_SHORT).show();
            } else {
                updateOrderStatus(1);
            }
        });

        // Xử lý sự kiện nhấp vào nút "Đã Giao"
        btnDaGiao.setOnClickListener(v -> {
            if (trangthaiD == 0) { // Nếu đơn hàng đã hủy, không cho phép đánh dấu là đã giao
                Toast.makeText(chiTietHdAdmin.this, "Đơn hàng đã bị hủy", Toast.LENGTH_SHORT).show();
            } else if (trangthaiD == 1) { // Nếu đơn hàng đang giao, cho phép đánh dấu là đã giao
                updateOrderStatus(2);
            } else {
                Toast.makeText(chiTietHdAdmin.this, "Đơn hàng đã giao", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateOrderStatus(int newStatus) {
        HDref.child("trangThaiD").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(chiTietHdAdmin.this, "Trạng thái đơn hàng đã được cập nhật", Toast.LENGTH_SHORT).show();
                    trangthaiD = newStatus;
                    updateUIBasedOnStatus(trangthaiD);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(chiTietHdAdmin.this, "Lỗi khi cập nhật trạng thái", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUIBasedOnStatus(int status) {
        switch (status) {
            case 0:
                tvTinhTrang.setText("Đã Hủy");
//                btnHuy.setEnabled(true);
//                btnDangGiao.setEnabled(false);
//                btnDaGiao.setEnabled(false);
                break;
            case 1:
                tvTinhTrang.setText("Đang Giao");
//                btnHuy.setEnabled(true);
//                btnDangGiao.setEnabled(true);
//                btnDaGiao.setEnabled(true);
                break;
            case 2:
                tvTinhTrang.setText("Đã Giao");
//                btnHuy.setEnabled(true);
//                btnDangGiao.setEnabled(true);
//                btnDaGiao.setEnabled(true);
                break;
        }
    }
    private void getView(){
        maHD = findViewById(R.id.tvChiTietHD_MaHD);
        thoiGianL = findViewById(R.id.tvChiTietHD_ThoiGianL);
        tenCH = findViewById(R.id.tvChiTietHD_TenCH);
        diaChiCH1 = findViewById(R.id.tvChiTietHD_DiaChiCH1);
        diaChiCH2 = findViewById(R.id.tvChiTietHD_DiaChiCH2);
        TinhTam = findViewById(R.id.tvChiTietHD_TamTinh);
        GiaTongT = findViewById(R.id.tvChiTietHD_GiaTongT);
        GiaTTT = findViewById(R.id.tvChiTietHD_GiaTTT);
        btnTroLai = findViewById(R.id.btnTroLai_gh);
        rcvChiTietHD = findViewById(R.id.rcvSanPhamMua);
        btnHuyDonHang = findViewById(R.id.btnHuyDonHang);
        tvTrangThaiD= findViewById(R.id.tvTrangThaiHD);
    }
}
