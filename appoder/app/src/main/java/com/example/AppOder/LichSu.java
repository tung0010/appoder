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

import com.example.AppOder.Adapter.Item_HoaDonAdapter;
import com.example.AppOder.Model.HoaDonModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LichSu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LichSu extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView icHoSoLS, icTimKiemLS;

    RecyclerView rcvi_hoadon;
    private String userId;
    Item_HoaDonAdapter itemHoaDonAdapter;
    public LichSu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LichSu.
     */
    // TODO: Rename and change types and number of parameters
    public static LichSu newInstance(String param1, String param2) {
        LichSu fragment = new LichSu();
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

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();

        rcvi_hoadon.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<HoaDonModel> options =
                new FirebaseRecyclerOptions.Builder<HoaDonModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("hoadon").orderByChild("maKh").equalTo(userId), HoaDonModel.class)
                        .build();

        Item_HoaDonAdapter.OnItemHDClickListener listener= new Item_HoaDonAdapter.OnItemHDClickListener() {
            @Override
            public void onItemClick(String idHD) {
                Intent intent = new Intent(getActivity(), ChiTietHD.class);
                intent.putExtra("idHD", idHD);
                startActivity(intent);
            }
        };
        itemHoaDonAdapter= new Item_HoaDonAdapter(options,listener);
        rcvi_hoadon.setAdapter(itemHoaDonAdapter);

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