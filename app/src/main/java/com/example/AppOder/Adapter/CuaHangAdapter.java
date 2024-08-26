package com.example.AppOder.Adapter;

import android.location.Location;
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
import com.example.AppOder.Model.CuaHangModel;
import com.example.AppOder.Model.SelectedStore;
import com.example.AppOder.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

public class CuaHangAdapter extends FirebaseRecyclerAdapter<CuaHangModel,CuaHangAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private GoogleMap googleMap;
    private Location currentLocation; // Trường để lưu trữ vị trí hiện tại

//    public void setCurrentLocation(Location currentLocation) {
//        this.currentLocation = currentLocation;
//    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
    public CuaHangAdapter(@NonNull FirebaseRecyclerOptions<CuaHangModel> options, CuaHangAdapter.OnItemCHClickListener listener) {
        super(options);
        this.mCHListener = listener;
    }

    public interface OnItemCHClickListener {
        void onItemClick(String idCH);
    }
    private CuaHangAdapter.OnItemCHClickListener mCHListener;

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull CuaHangModel model) {
        holder.nameCH.setText(model.getNameCH());
        holder.diachi.setText(model.getDiachi());
        holder.location.setText(model.getLocation());
        holder.sdt.setText(model.getSdt());
        float distance = model.getDistance();

//        float distance = calculateDistance(model.getVido(), model.getKinhdo());// Lấy giá trị khoảng cách từ model

        if (distance >= 1000) {
            // Nếu khoảng cách lớn hơn hoặc bằng 1000 mét, chuyển đổi sang kilômét và làm tròn đến 2 chữ số thập phân
            float distanceInKilometers = distance / 1000;
            holder.distance.setText(String.format("%.2f km", distanceInKilometers));
        } else {
            // Nếu khoảng cách nhỏ hơn 1000 mét, hiển thị nguyên giá trị khoảng cách và đơn vị là "m"
            holder.distance.setText(String.format("%.0f m", distance));
        }

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // Kiểm tra xem thời gian hiện tại có nằm trong khoảng từ 7 giờ sáng đến 10 giờ tối không
        if (hour >= 7 && hour < 22) {
            // Nếu có, hiển thị trạng thái "mở"
            holder.open.setText("mở");
            holder.open.setBackgroundResource(R.drawable.trangthai);
        } else {
            // Nếu không, hiển thị trạng thái "đóng"
            holder.open.setText("đóng");
            holder.open.setBackgroundResource(R.drawable.close_door);
        }

        if (SelectedStore.getInstance().getStoreName() != null &&
                SelectedStore.getInstance().getStoreName().equals(model.getNameCH())) {
            // Nếu có, hiển thị selectedCH
            holder.selectedCH.setVisibility(View.VISIBLE);

        } else {
            // Nếu không, ẩn selectedCH
            holder.selectedCH.setVisibility(View.GONE);
        }



        Glide.with(holder.img.getContext())
                .load(model.getImgUrl())
                .placeholder(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark)
                .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_normal)
                .into(holder.img);
        String idCH = getRef(position).getKey();
        Log.d("MarkerClickAdapter", "idCH: " + idCH);

        double latitude = model.getVido(); // Lấy vĩ độ
        double longitude = model.getKinhdo(); // Lấy kinh độ
        LatLng cuaHangLatLng = new LatLng(latitude, longitude);
        MarkerOptions cuaHangMarkerOptions = new MarkerOptions()
                .position(cuaHangLatLng)
                .title(model.getNameCH() +" ("+ model.getLocation()+ ")")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        // Thêm marker vào bản đồ và gán ID của cửa hàng là tag cho marker
        if (googleMap != null) {
            Marker marker = googleMap.addMarker(cuaHangMarkerOptions);
            marker.setTag(idCH);
        }

        holder.itemCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCHListener.onItemClick(idCH);
            }
        });
    }

//    private float calculateDistance(double latitude, double longitude) {
//        // Tính khoảng cách giữa vị trí hiện tại và vị trí của cửa hàng
//        if (currentLocation != null) {
//            Location cuaHangLocation = new Location("cuaHangLocation");
//            cuaHangLocation.setLatitude(latitude); // Đặt vĩ độ của cửa hàng
//            cuaHangLocation.setLongitude(longitude); // Đặt kinh độ của cửa hàng
//            return currentLocation.distanceTo(cuaHangLocation);
//        }
//        return 0; // Trả về 0 nếu vị trí hiện tại chưa được thiết lập
//    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cuahang_item,parent,false);

        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView nameCH,diachi, location, sdt, distance, selectedCH, open;
        LinearLayout itemCH;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            img = (ImageView)itemView.findViewById(R.id.imgUrl);
            nameCH = (TextView)itemView.findViewById(R.id.nameCH);
            diachi =(TextView) itemView.findViewById(R.id.diaChiCH);
            location = (TextView) itemView.findViewById(R.id.location);
            sdt = (TextView) itemView.findViewById(R.id.sdtCH);
            distance = (TextView) itemView.findViewById(R.id.distance);
            itemCH = (LinearLayout) itemView.findViewById(R.id.itemCH);
            selectedCH = (TextView) itemView.findViewById(R.id.selectedCH);
            open = (TextView) itemView.findViewById(R.id.open);
        }
    }

}
