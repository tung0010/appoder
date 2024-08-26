package com.example.AppOder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.AppOder.Adapter.SanPhamAdapter;
import com.example.AppOder.Model.LoaiModel;
import com.example.AppOder.Model.SanPhamModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SanPhamTrangChu extends AppCompatActivity implements SanPhamAdapter.OnItemSPClickListener {
    ImageView btnHuyTC, btnTimKiem;
    TextView txtNameTC;
    RecyclerView recyclerSP;
    SanPhamAdapter sanPhamAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_san_pham_trang_chu);
        getView();


        Intent intent = getIntent();
        if (intent != null) {
            String idLoai = intent.getStringExtra("idLoai");
            updateChiTietSP(idLoai);
        }

    }

    private void updateChiTietSP(String idLoai){
        Query query = FirebaseDatabase.getInstance().getReference().child("sanpham").orderByChild("idLoai").equalTo(idLoai);
        FirebaseRecyclerOptions<SanPhamModel> spOptions =
                new FirebaseRecyclerOptions.Builder<SanPhamModel>()
                        .setQuery(query, SanPhamModel.class)
                        .build();

        SanPhamAdapter.OnItemSPClickListener spClickListener = new SanPhamAdapter.OnItemSPClickListener() {
            @Override
            public void onItemClick(String idSP) {
                Intent intent = new Intent(SanPhamTrangChu.this, ChiTietSP.class);
                intent.putExtra("idSP", idSP);
                startActivity(intent);
            }
        };

        sanPhamAdapter = new SanPhamAdapter(spOptions, spClickListener);
        sanPhamAdapter.startListening();
        LinearLayoutManager layoutManagerSP = new LinearLayoutManager(this);
        recyclerSP.setLayoutManager(layoutManagerSP);
        recyclerSP.setAdapter(sanPhamAdapter);

        DatabaseReference loaiRef = FirebaseDatabase.getInstance().getReference().child("loai").child(idLoai);
        loaiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    LoaiModel loaiModel = dataSnapshot.getValue(LoaiModel.class);
                    if (loaiModel != null) {
                        String nameLoai = loaiModel.getNameLoai();
                        txtNameTC.setText(nameLoai);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btnTimKiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SanPhamTrangChu.this, TimKiem.class);
                startActivity(intent);
            }
        });

        btnHuyTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


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
    private void getView(){
        btnHuyTC = findViewById(R.id.btnHuyTC);
        btnTimKiem = findViewById(R.id.btnTimKiemTC);
        txtNameTC = findViewById(R.id.txtNameTC);
        recyclerSP = findViewById(R.id.rvSanPhamTChu);
    }

    @Override
    public void onItemClick(String idSP) {
        Intent intent = new Intent(this, ChiTietSP.class);
        intent.putExtra("idSP", idSP);
        startActivity(intent);
    }
}