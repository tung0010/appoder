package com.example.AppOder.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AppOder.Model.HoaDonModel;
import com.example.AppOder.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.DecimalFormat;

public class Item_Admin_HoadonAdapter extends FirebaseRecyclerAdapter<HoaDonModel, Item_Admin_HoadonAdapter.ViewHolder> {

    public interface OnItemHDClickListener {
        void onItemClick(String idHD);
    }

    private final OnItemHDClickListener mListener;

    public Item_Admin_HoadonAdapter(@NonNull FirebaseRecyclerOptions<HoaDonModel> options, OnItemHDClickListener listener) {
        super(options);
        this.mListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull HoaDonModel model) {
        holder.bind(model);
        // Set item click listener
        holder.itemView.setOnClickListener(v -> {
            String idHD = getRef(position).getKey();
            mListener.onItemClick(idHD);
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_hoadon, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView idMaHD;
        private final TextView idGioL;
        private final TextView tvNgayL;
        private final TextView tvTenCH;
        private final TextView tvTrangThaiHD;
        private final TextView tvSanPhamHD;
        private final TextView tvSoSanPham;
        private final TextView tvTTien;
        private final LinearLayout linearLayoutTrangThai;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idMaHD = itemView.findViewById(R.id.idMaHD);
            idGioL = itemView.findViewById(R.id.idGioL);
            tvNgayL = itemView.findViewById(R.id.tvNgayL);
            tvTenCH = itemView.findViewById(R.id.tvTenCH);
            tvTrangThaiHD = itemView.findViewById(R.id.tvTrangThaiHD);
            tvSanPhamHD = itemView.findViewById(R.id.tvSanPhamHD);
            tvSoSanPham = itemView.findViewById(R.id.tvSoSanPham);
            tvTTien = itemView.findViewById(R.id.tvTTien);
            linearLayoutTrangThai = itemView.findViewById(R.id.linearLayoutTrangThai);

            if (linearLayoutTrangThai == null) {
                throw new NullPointerException("LinearLayout with ID 'linearLayoutTrangThai' is not found in the layout");
            }
        }

        public void bind(HoaDonModel hoaDon) {
            idMaHD.setText(hoaDon.getMaHd());
            idGioL.setText(hoaDon.getGioL());
            tvNgayL.setText(hoaDon.getNgayL());
            // Format total price
            DecimalFormat decimalFormat = new DecimalFormat("#,###");
            String formattedTotalPrice = decimalFormat.format(hoaDon.getTongTien());
            tvTTien.setText(String.format("%s đ", formattedTotalPrice));

            // Set products and number of products
            tvSanPhamHD.setText(hoaDon.getSanPhamMua().toString()); // Assuming this returns a string
            tvSoSanPham.setText(String.format("Sản phẩm: %d món", hoaDon.getSanPhamMua().size()));

            // Update status
            int trangThaiD = hoaDon.isTrangThaiD();
            switch (trangThaiD) {
                case 0:
                    tvTrangThaiHD.setText("Đã Hủy");
                    linearLayoutTrangThai.setBackgroundResource(R.drawable.trangthai);
                    break;
                case 1:
                    tvTrangThaiHD.setText("Đang Giao");
                    linearLayoutTrangThai.setBackgroundResource(R.drawable.nenhoadontt);
                    break;
                case 2:
                    tvTrangThaiHD.setText("Đã Giao");
                    linearLayoutTrangThai.setBackgroundResource(R.drawable.btn_on);
                    break;
            }
        }
    }

}
