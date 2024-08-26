package com.example.AppOder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.AppOder.Adapter.LoaiAdapter;
import com.example.AppOder.Adapter.SanPhamAdapter;
import com.example.AppOder.Model.LoaiModel;
import com.example.AppOder.Model.SanPhamModel;
import com.example.AppOder.Model.SelectedStore;
import com.example.AppOder.Utils.DsSanPhamMuaUtil;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DanhMUc extends Fragment {
    private float dX, dY;
    private int lastAction;
    private static final String ARG_ID_LOAI = "idLoai";
    private String idLoai;
    private ImageView icTimKiemDH, icHoSoDH;
    RecyclerView recyclerView, recyclerViewSP;
    LoaiAdapter loaiAdapter;
    SanPhamAdapter sanPhamAdapter;
    LinearLayout btnCH;
    TextView txtSoLuongM;
    ConstraintLayout icGioHang;
    TextView txtNameDH, txtNameCH;
    public DanhMUc() {
    }

    public static DanhMUc newInstance(String idLoai) {
        DanhMUc fragment = new DanhMUc();
        Bundle args = new Bundle();
        args.putString("idLoai", idLoai);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idLoai = getArguments().getString("idLoai");
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_danh_muc, container, false);
        ConstraintLayout csGioHang = view.findViewById(R.id.ivDatHang_GioHang);
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
//        icGioHang.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ActivityGioHang.class);
//                startActivity(intent);
//            }
//        });

        txtSoLuongM = view.findViewById(R.id.tvSoLuongSP_DatH);
        onResume();
        String slMua = String.valueOf(DsSanPhamMuaUtil.getInstance().getSoLuongSP());
        txtSoLuongM.setText(slMua);

        txtNameCH = view.findViewById(R.id.txtNameCH);
        String tenCuaHang = SelectedStore.getInstance().getStoreName();

        // Kiểm tra xem tên cửa hàng có tồn tại không
        if (tenCuaHang != null && !tenCuaHang.isEmpty()) {
            // Thiết lập tên cửa hàng cho TextView txtCuaHang
            txtNameCH.setText(tenCuaHang);
        } else {
            // Xử lý trường hợp không có tên cửa hàng nếu cần
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView = view.findViewById(R.id.rvLoaiDH);
        recyclerView.setLayoutManager(layoutManager);

        txtNameDH = view.findViewById(R.id.txtNameDH);

//        Xử lý click vào item Loai
        LoaiAdapter.OnItemClickListener listener = new LoaiAdapter.OnItemClickListener() {
            @Override
            public void onLoaiItemClick(String idLoai) {
                updateRecyclerView(idLoai);
            }
        };

        FirebaseRecyclerOptions<LoaiModel> options =
                new FirebaseRecyclerOptions.Builder<LoaiModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("loai"), LoaiModel.class)
                        .build();

        loaiAdapter = new LoaiAdapter(options,listener);
        recyclerView.setAdapter(loaiAdapter);

        LinearLayoutManager layoutManagerSP = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewSP = view.findViewById(R.id.rvSanPham);
        recyclerViewSP.setLayoutManager(layoutManagerSP);

        SanPhamAdapter.OnItemSPClickListener spClickListener = new SanPhamAdapter.OnItemSPClickListener() {
            @Override
            public void onItemClick(String idSP) {
                Intent intent = new Intent(getActivity(), ChiTietSP.class);
                intent.putExtra("idSP", idSP);
                Log.d("DatHangFragment", "idSP: " + idSP);
                startActivity(intent);
            }
        };

        FirebaseRecyclerOptions<SanPhamModel> optionSP =
                new FirebaseRecyclerOptions.Builder<SanPhamModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("sanpham").orderByChild("idLoai").equalTo("id1"), SanPhamModel.class)
                        .build();
        sanPhamAdapter = new SanPhamAdapter(optionSP, spClickListener);
        recyclerViewSP.setAdapter(sanPhamAdapter);
        sanPhamAdapter.startListening();

        icTimKiemDH = view.findViewById(R.id.icTimKiemDH);
        icHoSoDH = view.findViewById(R.id.icHoSoDH);
        icTimKiemDH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TimKiem.class);
                startActivity(intent);
            }
        });
        icHoSoDH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HoSo.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String slMua = String.valueOf(DsSanPhamMuaUtil.getInstance().getSoLuongSP());
        txtSoLuongM.setText(slMua);
    }

    @Override
    public void onStart() {
        super.onStart();
        loaiAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
//        loaiAdapter.stopListening();


    }

    private void updateRecyclerView(String idLoai) {
        Query query = FirebaseDatabase.getInstance().getReference().child("sanpham").orderByChild("idLoai").equalTo(idLoai);
        FirebaseRecyclerOptions<SanPhamModel> spOptions =
                new FirebaseRecyclerOptions.Builder<SanPhamModel>()
                        .setQuery(query, SanPhamModel.class)
                        .build();
        SanPhamAdapter.OnItemSPClickListener spClickListener = new SanPhamAdapter.OnItemSPClickListener() {
            @Override
            public void onItemClick(String idSP) {
                Intent intent = new Intent(getActivity(), ChiTietSP.class);
                intent.putExtra("idSP", idSP);
                startActivity(intent);
            }
        };
        SanPhamAdapter sanPhamAdapterSP = new SanPhamAdapter(spOptions, spClickListener);
        sanPhamAdapterSP.startListening();
        LinearLayoutManager layoutManagerSP = new LinearLayoutManager(getActivity());
        recyclerViewSP.setLayoutManager(layoutManagerSP);
        recyclerViewSP.setAdapter(sanPhamAdapterSP);

        DatabaseReference loaiRef = FirebaseDatabase.getInstance().getReference().child("loai").child(idLoai);
        loaiRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    LoaiModel loaiModel = dataSnapshot.getValue(LoaiModel.class);
                    if (loaiModel != null) {
                        String nameLoai = loaiModel.getNameLoai();
                        txtNameDH.setText(nameLoai);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




}