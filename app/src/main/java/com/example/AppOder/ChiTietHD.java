package com.example.AppOder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AppOder.Adapter.HD_SanPhamMuaAdapter;
import com.example.AppOder.Model.HoaDonModel;
import com.example.AppOder.Model.SanPhamMuaModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ChiTietHD extends AppCompatActivity {
    TextView maHD, thoiGianL, tenCH, diaChiCH1, diaChiCH2, TinhTam, GiaTongT, GiaTTT,tvTrangThaiD;
    ImageView btnTroLai;
    RecyclerView rcvChiTietHD;
    HD_SanPhamMuaAdapter adapter;
    Button btnHuyDonHang;

    ArrayList<SanPhamMuaModel> arrSanPhamM = new ArrayList<>();
    private DatabaseReference mDatabase;
    private int trangThaiHD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_hd);
        getView();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        String idHD = intent.getStringExtra("idHD");
        if (intent != null) {
            loadChiTietHD(idHD);
        }

        btnTroLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnHuyDonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"0".equals(trangThaiHD)) {
                    huyDonHang(idHD);
                }
            }
        });
    }

    private void huyDonHang(String idHD) {
        mDatabase.child("hoadon").child(idHD).child("trangThaiD").setValue(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Cập nhật giao diện
                            btnHuyDonHang.setText("Đã hủy");
                            btnHuyDonHang.setEnabled(false);
                            // Thông báo cho người dùng
                            Toast.makeText(ChiTietHD.this, "Đơn hàng đã được hủy", Toast.LENGTH_SHORT).show();
                            // Quay về màn hình trước đó
                            finish();
                        } else {
                            Toast.makeText(ChiTietHD.this, "Không thể hủy đơn hàng", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadChiTietHD(String idHD) {
        DatabaseReference SPRef = mDatabase.child("hoadon").child(idHD);
        SPRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HoaDonModel hoaDonModel = dataSnapshot.getValue(HoaDonModel.class);
                    if (hoaDonModel != null) {
                        maHD.setText(hoaDonModel.getMaHd());
                        thoiGianL.setText(String.format("%s %s", hoaDonModel.getNgayL(), hoaDonModel.getGioL()));
                        tenCH.setText(hoaDonModel.getNameCH());
                        DecimalFormat decimalFormat = new DecimalFormat("#,###");
                        float giaSP = hoaDonModel.getTongTien();
                        String formattedGiaSP = decimalFormat.format(giaSP);
                        TinhTam.setText(String.format("%s đ", formattedGiaSP));
                        GiaTongT.setText(String.format("%s đ", formattedGiaSP));
                        GiaTTT.setText(String.format("%s đ", formattedGiaSP));
                        arrSanPhamM = (ArrayList<SanPhamMuaModel>) hoaDonModel.getSanPhamMua();
                        rcvChiTietHD.setLayoutManager(new LinearLayoutManager(ChiTietHD.this));
                        adapter = new HD_SanPhamMuaAdapter(getLayoutInflater(), arrSanPhamM, new HD_SanPhamMuaAdapter.OnItemSanPhamMClickListener() {
                            @Override
                            public void onItemClick(String idHD, Integer stt) {
                                // Handle item click if necessary
                            }
                        });
                        rcvChiTietHD.setAdapter(adapter);

                        // Kiểm tra trạng thái đơn hàng
                        trangThaiHD = hoaDonModel.isTrangThaiD();
                        if (trangThaiHD == 0) {
                            btnHuyDonHang.setText("Đã hủy");
                            btnHuyDonHang.setEnabled(false);
                            tvTrangThaiD.setText("Đã huỷ");
                        }
                        if(trangThaiHD == 1)
                        {
                            tvTrangThaiD.setText("Đang chuẩn bị");
                        }
                        if (trangThaiHD == 2)
                        { tvTrangThaiD.setText("Đang giao");
                            btnHuyDonHang.setEnabled(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChiTietHD.this, "Lỗi khi tải chi tiết hóa đơn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    private void getView() {
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
