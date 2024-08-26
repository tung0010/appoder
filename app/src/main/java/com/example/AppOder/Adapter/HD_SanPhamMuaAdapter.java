package com.example.AppOder.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AppOder.Model.SanPhamMuaModel;
import com.example.AppOder.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HD_SanPhamMuaAdapter extends RecyclerView.Adapter<HD_SanPhamMuaAdapter.ViewHolder>{
    private List<SanPhamMuaModel> mData;
    private LayoutInflater mInflater;
    public interface OnItemSanPhamMClickListener {
        void onItemClick(String idHD,Integer stt);
    }
    private OnItemSanPhamMClickListener mListener;
    public HD_SanPhamMuaAdapter(LayoutInflater context, ArrayList<SanPhamMuaModel> data,OnItemSanPhamMClickListener mListener) {
        this.mListener = mListener;
        this.mInflater= LayoutInflater.from(context.getContext());
        this.mData = data;
    }
    @NonNull
    @Override
    public HD_SanPhamMuaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_sanphammua, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull HD_SanPhamMuaAdapter.ViewHolder holder, int position) {
        String labelSP = "(X"+mData.get(position).getSoLuong()+") "+mData.get(position).getNameSP();
        holder.SanPhamM.setText(labelSP);
        holder.KichThuocSP.setText(mData.get(position).getSize());
        if(!mData.get(position).getGhiChu().equals("")){
            holder.GhiChuSP.setVisibility(View.VISIBLE);
            holder.GhiChuSP.setText(mData.get(position).getGhiChu());
        }else holder.GhiChuSP.setVisibility(View.GONE);
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        float giaSP = mData.get(position).getGia();
        String formattedGiaSP = decimalFormat.format(giaSP);
        holder.GiaSP.setText(String.format("%s đ", formattedGiaSP));
        String idSP = mData.get(position).getMaSP();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(idSP,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView SanPhamM,KichThuocSP,GhiChuSP,GiaSP;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            SanPhamM= (TextView)itemView.findViewById(R.id.tvChiTietHD_SanP);
            KichThuocSP= (TextView)itemView.findViewById(R.id.tvChiTietHD_KTSanP);
            GhiChuSP= (TextView)itemView.findViewById(R.id.tvChiTietHD_GhiChu);
            GiaSP= (TextView)itemView.findViewById(R.id.tvChiTietHD_GiaSP);
        }
    }
}
