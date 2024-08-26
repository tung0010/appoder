package com.example.AppOder.Adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.AppOder.Model.SanPhamModel;
import com.example.AppOder.Model.VoucherModel;
import com.example.AppOder.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.DecimalFormat;
public class VoucherAdapter extends FirebaseRecyclerAdapter<VoucherModel,VoucherAdapter.viewHolder> {

    public interface onClickListener{
        void onClick(String id);
    }
    private onClickListener mListener;

    public VoucherAdapter(@NonNull FirebaseRecyclerOptions<VoucherModel> options,onClickListener listener){
        super(options);
        this.mListener= listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull VoucherAdapter.viewHolder viewHolder, int i, @NonNull VoucherModel voucherModel) {
        viewHolder.voucherName.setText(voucherModel.getMaGG());
        viewHolder.voucherDiscount.setText(String.valueOf(voucherModel.getGiamgia()));
        viewHolder.voucherExpiry.setText(voucherModel.getNgayKetThuc());
        String idVoucher= getRef(i).getKey();
        viewHolder.itemView.setOnClickListener(v -> {mListener.onClick(idVoucher);});
    }

    @NonNull
    @Override
    public VoucherAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_voucher,parent,false);
        return new viewHolder(view);
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView voucherName, voucherDiscount, voucherExpiry;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            voucherName = itemView.findViewById(R.id.tvVoucherName);
            voucherDiscount=itemView.findViewById(R.id.tvVoucherDiscount);
            voucherExpiry= itemView.findViewById(R.id.tvVoucherExpiry);
        }
    }
}
