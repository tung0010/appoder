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
import com.example.AppOder.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.DecimalFormat;

public class SanPhamAdapter extends FirebaseRecyclerAdapter<SanPhamModel, SanPhamAdapter.myViewHolder> {
    private static final String TAG = "SanPhamAdapter";
    public SanPhamAdapter(@NonNull FirebaseRecyclerOptions<SanPhamModel> options, OnItemSPClickListener listener) {
        super(options);
        this.mListener = listener;

    }

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *

     */

    public interface OnItemSPClickListener {
        void onItemClick(String idSP);
    }
    private OnItemSPClickListener mListener;


    @SuppressLint("RecyclerView")
    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull SanPhamModel model) {
        Log.d(TAG, "Binding data for position: " + position);
        Log.d(TAG, "Product Name: " + model.getNameSP());
        Log.d(TAG, "Product Price: " + model.getGia());
        Log.d(TAG, "Product Image URL: " + model.getImgSP());
        holder.moTa.setText(model.getMoTa());
        holder.nameSP.setText(model.getNameSP());

        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        float giaSP = model.getGia();
        String formattedGiaSP = decimalFormat.format(giaSP);
        holder.gia.setText(formattedGiaSP);
        // Log giá trị đã định dạng
        Log.d(TAG, "Formatted Product Price: " + formattedGiaSP);
        Glide.with(holder.imgSP.getContext())
                .load(model.getImgSP())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.imgSP);

        String idSP = getRef(position).getKey();
        holder.itemSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(idSP);
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sanpham,parent,false);

        return new SanPhamAdapter.myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        ImageView imgSP;
        TextView nameSP, moTa, gia;
        LinearLayout itemSP;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imgSP = (ImageView)itemView.findViewById(R.id.imgSP);
            nameSP = (TextView)itemView.findViewById(R.id.nameSP);
            moTa =(TextView) itemView.findViewById(R.id.moTa);
            gia = (TextView) itemView.findViewById(R.id.gia);
            itemSP = (LinearLayout) itemView.findViewById(R.id.itemSP);

        }
    }
}
