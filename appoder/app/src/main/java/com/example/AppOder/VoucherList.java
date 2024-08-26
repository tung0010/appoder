package com.example.AppOder;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.AppOder.Adapter.VoucherAdapter;
import com.example.AppOder.Model.VoucherModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VoucherList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoucherList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    RecyclerView rcVoucher;
    ImageView imgThemVoucher;
   private VoucherAdapter voucherAdapter;
    private String mParam1;
    private String mParam2;

    public VoucherList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddVoucher.
     */
    // TODO: Rename and change types and number of parameters
    public static VoucherList newInstance(String param1, String param2) {
        VoucherList fragment = new VoucherList();
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
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_voucher,container,false);

        //hiện danh sách voucher
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rcVoucher=view.findViewById(R.id.rvVoucherList);
        rcVoucher.setLayoutManager(linearLayoutManager);

        VoucherAdapter.onClickListener onClickListener = new VoucherAdapter.onClickListener() {
            @Override
            public void onClick(String id) {
                Intent intent= new Intent(getActivity(),ChiTietVoucher.class);
                intent.putExtra("idVoucher",id);
                Log.d("Voucher","idVoucher: "+id);
                startActivity(intent);
            }
        };
        FirebaseRecyclerOptions options=
                new FirebaseRecyclerOptions.Builder<VoucherModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("vouchers"),VoucherModel.class)
                        .build();

        voucherAdapter= new VoucherAdapter(options,onClickListener);
        rcVoucher.setAdapter(voucherAdapter);
        voucherAdapter.startListening();

        imgThemVoucher = view.findViewById(R.id.imgAddVoucher);
        imgThemVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(),AddVoucher.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void themVoucher(){
        DatabaseReference vcRef = FirebaseDatabase.getInstance().getReference("vouchers");
        String vcId=vcRef.push().getKey();

    }
}