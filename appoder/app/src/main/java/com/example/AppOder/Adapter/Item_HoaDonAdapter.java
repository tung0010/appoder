package com.example.AppOder.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AppOder.Model.HoaDonModel;
import com.example.AppOder.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.DecimalFormat;

public class Item_HoaDonAdapter extends FirebaseRecyclerAdapter<HoaDonModel,Item_HoaDonAdapter.myViewHolder> {
    public interface OnItemHDClickListener {
        void onItemClick(String idHD);
    }

    private OnItemHDClickListener mListener;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public Item_HoaDonAdapter(@NonNull FirebaseRecyclerOptions<HoaDonModel> options, Item_HoaDonAdapter.OnItemHDClickListener listener) {
        super(options);
        this.mListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull Item_HoaDonAdapter.myViewHolder holder, int position, @NonNull HoaDonModel model) {
        holder.maHd.setText(model.getMaHd());
        holder.gioL.setText(model.getGioL());
        holder.nameCH.setText(model.getNameCH());
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        float giaSP = model.getTongTien();
        String formattedGiaSP = decimalFormat.format(giaSP);
        holder.tongTien.setText(formattedGiaSP);
        holder.CacSanP.setText(model.getSanPhamMua_TenSP());
        holder.SoSP.setText(String.format("Sản phẩm mua : %s", model.getSoSanPhamMua().toString()));
        holder.ngayL.setText(model.getNgayL());
        if(model.isTrangThaiD()==1){
            holder.trangTHD.setText("Đã giao");
            holder.denTT.setBackgroundResource(R.drawable.diemxanh);
        }else  {
            holder.trangTHD.setText("Đã huỷ");
            holder.denTT.setBackgroundResource(R.drawable.diemdo);
        }
        String idHD = getRef(position).getKey();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(idHD);
            }
        });
        holder.trangTHD.setText(getTrangThaiString(model.isTrangThaiD()));
    }
    private String getTrangThaiString(Integer trangThaiD) {
        switch (trangThaiD) {
            case 0:
                return "Đã Hủy";
            case 1:
                return "Đang Giao";
            case 2:
                return "Đã Giao";
            default:
                return "Chưa Xác Định";
        }
    }
    @NonNull
    @Override
    public Item_HoaDonAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hoadon,parent,false);

        return new Item_HoaDonAdapter.myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView maHd,gioL,nameCH,trangTHD,SoSP,CacSanP,tongTien,denTT,ngayL;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            nameCH = (TextView)itemView.findViewById(R.id.tvTenCH);
            maHd = (TextView)itemView.findViewById(R.id.idMaHD);
            gioL = (TextView)itemView.findViewById(R.id.idGioL);
            ngayL = (TextView)itemView.findViewById(R.id.tvNgayL);
            trangTHD = (TextView)itemView.findViewById(R.id.tvTrangThaiHD);
            SoSP = (TextView)itemView.findViewById(R.id.tvSoSanPham);
            CacSanP = (TextView)itemView.findViewById(R.id.tvSanPhamHD);
            tongTien = (TextView)itemView.findViewById(R.id.tvTTien);
            denTT = (TextView)itemView.findViewById(R.id.tvĐenTT);
        }
    }
}
