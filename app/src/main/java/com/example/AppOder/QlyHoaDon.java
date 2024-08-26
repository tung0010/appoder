package com.example.AppOder;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.AppOder.Adapter.Item_Admin_HoadonAdapter;
import com.example.AppOder.Model.HoaDonModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LichSu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QlyHoaDon extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private ImageView icHoSoLS, icTimKiemLS;

    RecyclerView rcvi_hoadon;
    Item_Admin_HoadonAdapter itemHoaDonAdapter;

    public QlyHoaDon() {
        // Required empty public constructor
    }

    public static QlyHoaDon newInstance(String param1, String param2) {
        QlyHoaDon fragment = new QlyHoaDon();
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
        View view = inflater.inflate(R.layout.fragment_lich_su, container, false);
        icHoSoLS = view.findViewById(R.id.icHoSoLS);
        icTimKiemLS = view.findViewById(R.id.icTimKiemLS);
        rcvi_hoadon = view.findViewById(R.id.rcvItemHD);

        // Set up RecyclerView
        rcvi_hoadon.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Query all invoices without filtering by userId
        FirebaseRecyclerOptions<HoaDonModel> options =
                new FirebaseRecyclerOptions.Builder<HoaDonModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("hoadon"), HoaDonModel.class)
                        .build();

        // Define item click listener
        Item_Admin_HoadonAdapter.OnItemHDClickListener listener = new Item_Admin_HoadonAdapter.OnItemHDClickListener(){
            @Override
            public void onItemClick(String idHD) {
                Intent intent = new Intent(getActivity(), chiTietHdAdmin.class);
                intent.putExtra("idHD", idHD);
                startActivity(intent);
            }
        };

        // Initialize adapter with the options and click listener
        itemHoaDonAdapter = new Item_Admin_HoadonAdapter(options, listener);
        rcvi_hoadon.setAdapter(itemHoaDonAdapter);

        // Set up onClick listeners for icons
        icTimKiemLS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TimKiem.class);
                startActivity(intent);
            }
        });

        icHoSoLS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HoSo.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        itemHoaDonAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        itemHoaDonAdapter.stopListening();
    }
}
