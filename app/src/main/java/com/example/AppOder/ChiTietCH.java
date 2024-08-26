package com.example.AppOder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.AppOder.Model.CuaHangModel;
import com.example.AppOder.Model.SelectedStore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChiTietCH extends AppCompatActivity {
    ImageView imgAnhCH, btnHuyCH;
    TextView txtNameCH, txtVtri, txtLocation, txtSDT;
    LinearLayout btnCuaHang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_ch);
        getView();

        Intent intent = getIntent();
        if (intent != null) {
            String idCH = intent.getStringExtra("idCH");
            updateChiTietCH(idCH);
        }

        btnCuaHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CuaHangModel ch=new CuaHangModel();
                String tenCuaHang = txtNameCH.getText().toString();
                ch.setNameCH(txtNameCH.getText().toString());
                ch.setDiachi(txtVtri.getText().toString());
                ch.setLocation(txtLocation.getText().toString());
                // Gửi tên cửa hàng được chọn tới SelectedStoreModel
                SelectedStore.getInstance().setStoreName(ch);

                Intent intent = new Intent(ChiTietCH.this, MainActivity.class);
                intent.putExtra("fragment", "dathang");
                startActivity(intent);

            }
        });
    }
    private void updateChiTietCH(String idCH) {
        if (idCH != null && !idCH.isEmpty()) {
            DatabaseReference SPRef = FirebaseDatabase.getInstance().getReference().child("cuahang").child(idCH);
            SPRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        CuaHangModel cuaHangModel = dataSnapshot.getValue(CuaHangModel.class);
                        if (cuaHangModel != null) {
                            String nameCH = cuaHangModel.getNameCH();
                            String vtri = cuaHangModel.getDiachi();
                            String location = cuaHangModel.getLocation();
                            String imgAnh = cuaHangModel.getImgUrl();
                            String sdt = cuaHangModel.getSdt();
                            txtNameCH.setText(nameCH);
                            txtVtri.setText(vtri);
                            txtLocation.setText(location);
                            txtSDT.setText(sdt);

                            Glide.with(getApplicationContext())
                                    .load(imgAnh)
                                    .into(imgAnhCH);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            // Xử lý trường hợp idCH null hoặc trống nếu cần
        }

        btnHuyCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // If there are fragments, pop the back stack to go back to the previous fragment
            getSupportFragmentManager().popBackStack();
        } else {
            // If there are no fragments in the back stack, finish the activity to go back to the previous activity
            finish();
        }
    }

    private void getView(){
        imgAnhCH =findViewById(R.id.imgAnhCH);
        txtNameCH = findViewById(R.id.txtNameCH);
        txtVtri = findViewById(R.id.txtVTri);
        txtLocation = findViewById(R.id.txtLocation);
        txtSDT = findViewById(R.id.txtSDT);
        btnHuyCH = findViewById(R.id.btnHuyCH);
        btnCuaHang = findViewById(R.id.btnCuaHang);
    }
}