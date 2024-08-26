package com.example.AppOder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AppOder.Model.VoucherModel;
import com.example.AppOder.R;
import java.util.List;

public class Voucher_UersAdapter extends RecyclerView.Adapter<Voucher_UersAdapter.VoucherViewHolder> {

    private List<VoucherModel> voucherList;
    private Context context;
    public interface onClickListener {
        void onClick(VoucherModel voucher);
    }
    private onClickListener mListener;

    public Voucher_UersAdapter(Context context, List<VoucherModel> voucherList,onClickListener listener) {
        this.context = context;
        this.voucherList = voucherList;
        this.mListener = listener;

    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_voucher_user4, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        VoucherModel voucher = voucherList.get(position);
        if (voucher != null) { // Kiểm tra null
            holder.voucherName.setText(voucher.getMaGG());
            holder.voucherDescription.setText(voucher.getMota());
            holder.voucherDiscount.setText(String.valueOf(voucher.getGiamgia()));
        } else {
            holder.voucherName.setText("N/A");
            holder.voucherDescription.setText("N/A");
            holder.voucherDiscount.setText("N/A");
        }

        holder.itemVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(voucher);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public class VoucherViewHolder extends RecyclerView.ViewHolder {

        TextView voucherName;
        TextView voucherDescription,voucherDiscount;
        LinearLayout itemVoucher;
        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            voucherName = itemView.findViewById(R.id.voucherName);
            voucherDescription = itemView.findViewById(R.id.voucherDescription);
            voucherDiscount= itemView.findViewById(R.id.voucherDiscount);
            itemVoucher = (LinearLayout) itemView.findViewById(R.id.itemVoucher);
        }
    }
}
