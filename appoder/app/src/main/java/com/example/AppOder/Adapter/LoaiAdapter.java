package com.example.AppOder.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.AppOder.Model.LoaiModel;
import com.example.AppOder.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoaiAdapter extends FirebaseRecyclerAdapter<LoaiModel,LoaiAdapter.myViewHolder> {
    public LoaiAdapter(FirebaseRecyclerOptions<LoaiModel> optionsLoai) {
        super(optionsLoai);
    }

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
//     * @param options
//     */
    private FirebaseRecyclerOptions<LoaiModel> optionLoai;
    public interface OnItemClickListener {
        void onLoaiItemClick(String idLoai);
    }
    private OnItemClickListener mListener;

    // Constructor để thiết lập mListener
    public LoaiAdapter(@NonNull FirebaseRecyclerOptions<LoaiModel> options, OnItemClickListener listener) {
        super(options);
        this.mListener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull LoaiModel model) {


        holder.nameLoai.setText(model.getNameLoai());

        Glide.with(holder.imgLoai.getContext())
                .load(model.getImgLoai())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.imgLoai);
        // click item
        String idLoai = getRef(position).getKey();


        holder.itemLoai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLoaiItemClick(idLoai);
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horizontal,parent,false);

        return new LoaiAdapter.myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imgLoai;
        TextView nameLoai;
        RelativeLayout itemLoai;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imgLoai = (CircleImageView) itemView.findViewById(R.id.imgLoai);
            nameLoai = (TextView)itemView.findViewById(R.id.nameLoai);
            itemLoai = (RelativeLayout)itemView.findViewById(R.id.itemLoai);
        }
    }
}
