package com.example.AppOder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AppOder.Adapter.CuaHangAdapter;
import com.example.AppOder.Model.CuaHangModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CuaHang#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CuaHang extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView icHoSoCH, icTimKiemCH;
    LinearLayout txtMap;
    FrameLayout map;
    ImageView icMap;
    TextView txtList;
    EditText txtTimKiemCH;
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    Marker marker;
    private GoogleMap googleMap;
    boolean isListViewVisible = true;

    Location currentLocation;
    FusedLocationProviderClient fusedClient;
    private static final int REQUEST_CODE = 101;

    RecyclerView recyclerView;
    CuaHangAdapter cuaHangAdapter;
    public CuaHang() {
        // Required empty public constructor
    }


    public static CuaHang newInstance(String param1, String param2) {
        CuaHang fragment = new CuaHang();
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
        View view = inflater.inflate(R.layout.fragment_cua_hang, container, false);
        txtMap = view.findViewById(R.id.txtMap);
        txtTimKiemCH = view.findViewById(R.id.edTimDiaC);
        recyclerView = view.findViewById(R.id.rvCuaHang);
        map = view.findViewById(R.id.map);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        icMap = view.findViewById(R.id.ic_map);
        txtList = view.findViewById(R.id.txtList);
        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        getLocation();

        supportMapFragment.getView().setVisibility(View.GONE);
        txtMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isListViewVisible) {
                    // Ẩn RecyclerView và hiển thị bản đồ
                    recyclerView.setVisibility(View.GONE);
                    supportMapFragment.getView().setVisibility(View.VISIBLE);
                    txtList.setText("DANH SÁCH");
                    icMap.setImageResource(R.drawable.ic_list); // Thay đổi icon
                    isListViewVisible = false;
                } else {
                    // Ẩn bản đồ và hiển thị RecyclerView
                    supportMapFragment.getView().setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    txtList.setText("BẢN ĐỒ");
                    icMap.setImageResource(R.drawable.ic_map); // Thay đổi icon
                    isListViewVisible = true;
                }
            }
        });

        txtTimKiemCH.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String query = s.toString().trim();
                startSearch(query);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        CuaHangAdapter.OnItemCHClickListener chClickListener = new CuaHangAdapter.OnItemCHClickListener() {
            @Override
            public void onItemClick(String idCH) {
                Intent intent = new Intent(getActivity(), ChiTietCH.class);
                intent.putExtra("idCH", idCH);
                Log.d("DatHangFragment", "idCH: " + idCH);
                startActivity(intent);
            }
        };

        FirebaseRecyclerOptions<CuaHangModel> optionCH =
                new FirebaseRecyclerOptions.Builder<CuaHangModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("cuahang"), CuaHangModel.class)
                        .build();
        cuaHangAdapter = new CuaHangAdapter(optionCH, chClickListener);
        recyclerView.setAdapter(cuaHangAdapter);
        cuaHangAdapter.startListening();


        icTimKiemCH = view.findViewById(R.id.icTimKiemCH);
        icTimKiemCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TimKiem.class);
                startActivity(intent);
            }
        });

        icHoSoCH = view.findViewById(R.id.icHoSoCH);
        icHoSoCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HoSo.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(CuaHang.this);

                }


            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation();
            }
            else{
                Toast.makeText(getActivity(), "Location is denied", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        cuaHangAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        cuaHangAdapter.stopListening();
    }


    private void startSearch(String query) {
        // Hủy bỏ các yêu cầu tìm kiếm trước đó nếu có
        searchHandler.removeCallbacksAndMessages(searchRunnable);

        // Tạo một yêu cầu tìm kiếm mới
        searchRunnable = new Runnable() {
            @Override
            public void run() {
                txtSearch(query); // Thực hiện tìm kiếm với query đã được nhập
            }
        };

        // Đặt thời gian chờ trước khi thực hiện tìm kiếm
        long debounceDuration = 1500; // milliseconds
        searchHandler.postDelayed(searchRunnable, debounceDuration);
    }



    private  void txtSearch(String str){
        CuaHangAdapter.OnItemCHClickListener chClickListener = new CuaHangAdapter.OnItemCHClickListener() {
            @Override
            public void onItemClick(String idCH) {
                Intent intent = new Intent(getActivity(), ChiTietCH.class);
                intent.putExtra("idCH", idCH);
                startActivity(intent);
            }
        };

        FirebaseRecyclerOptions<CuaHangModel> options =
                new FirebaseRecyclerOptions.Builder<CuaHangModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("cuahang").orderByChild("nameCH").startAt(str).endAt(str+"\uf8ff"), CuaHangModel.class)
                        .build();

        cuaHangAdapter = new CuaHangAdapter(options, chClickListener);
        recyclerView.setAdapter(cuaHangAdapter);
        cuaHangAdapter.startListening();


        DatabaseReference cuaHangRef = FirebaseDatabase.getInstance().getReference().child("cuahang");
        cuaHangRef.orderByChild("nameCH").startAt(str).endAt(str+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                googleMap.clear();

                // Tạo một LatLngBounds.Builder để chứa các vị trí của các cửa hàng
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                // Duyệt qua tất cả các cửa hàng từ Firebase


                // Duyệt qua tất cả các cửa hàng từ Firebase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CuaHangModel cuaHang = snapshot.getValue(CuaHangModel.class);
                    if (cuaHang != null) {
                        // Kiểm tra xem tên cửa hàng có chứa từ khóa tìm kiếm không
                        if (cuaHang.getNameCH().contains(str)) {
                            double kinhDo = cuaHang.getKinhdo();
                            double viDo = cuaHang.getVido();
                            String tenCuaHang = cuaHang.getNameCH();
                            String vtri = cuaHang.getLocation();
                            Log.d("FirebaseData", "Tên cửa hàng: " + tenCuaHang + ", Kinh độ: " + kinhDo + ", Vĩ độ: " + viDo);
                            String idCH = snapshot.getKey(); // Lấy idCH từ key của dataSnapshot
                            Log.d("MarkerTag", "idCH before setting tag: " + idCH); // Log giá trị idCH trước khi gán tag
                            LatLng cuaHangLatLng = new LatLng(viDo, kinhDo);

                            // Tạo và hiển thị marker cho cửa hàng phù hợp với tìm kiếm
                            MarkerOptions cuaHangMarkerOptions = new MarkerOptions().position(cuaHangLatLng).title(tenCuaHang +" ("+ vtri+ ")");
                            Marker marker = googleMap.addMarker(cuaHangMarkerOptions);
                            marker.setTag(idCH);

                            // Bổ sung vị trí của cửa hàng vào builder
                            builder.include(cuaHangLatLng);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("MapActivity", "Failed to read value.", databaseError.toException());
            }
        });

    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (currentLocation != null) {
                // Hiển thị vị trí hiện tại của người dùng
                LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                MarkerOptions currentMarkerOptions = new MarkerOptions().position(currentLatLng).title("My curent location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                googleMap.addMarker(currentMarkerOptions);

                // Hiển thị vị trí hiện tại và các cửa hàng từ Firebase trên bản đồ
                DatabaseReference cuaHangRef = FirebaseDatabase.getInstance().getReference().child("cuahang");
                cuaHangRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            CuaHangModel cuaHang = snapshot.getValue(CuaHangModel.class);
                            if (cuaHang != null) {
                                double kinhDo = cuaHang.getKinhdo();
                                double viDo = cuaHang.getVido();
                                String tenCuaHang = cuaHang.getNameCH();
                                String vtri = cuaHang.getLocation();
                                Log.d("FirebaseData", "Tên cửa hàng: " + tenCuaHang + ", Kinh độ: " + kinhDo + ", Vĩ độ: " + viDo);
                                String idCH = snapshot.getKey(); // Lấy idCH từ key của dataSnapshot
                                Log.d("MarkerTag", "idCH before setting tag: " + idCH); // Log giá trị idCH trước khi gán tag
                                LatLng cuaHangLatLng = new LatLng(viDo, kinhDo);
                                MarkerOptions cuaHangMarkerOptions = new MarkerOptions().position(cuaHangLatLng).title(tenCuaHang +" ("+ vtri+ ")");
                                marker = googleMap.addMarker(cuaHangMarkerOptions);
                                marker.setTag(idCH);
                                CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(cuaHangLatLng,12);

                                googleMap.moveCamera(cu);

                                Location cuaHangLocation = new Location("cuaHangLocation");
                                cuaHangLocation.setLatitude(viDo); // Lấy vĩ độ của cửa hàng
                                cuaHangLocation.setLongitude(kinhDo); // Lấy kinh độ của cửa hàng

                                // Tính khoảng cách giữa vị trí hiện tại và vị trí của cửa hàng
                                float khoangCach = currentLocation.distanceTo(cuaHangLocation);

                                // Lưu khoảng cách vào đối tượng CuaHangModel
                                cuaHang.setDistance(khoangCach);

                                // Cập nhật lại dữ liệu lên Firebase
                                snapshot.getRef().setValue(cuaHang);

                                builder.include(cuaHangLatLng);
                            }
                        }

                        // Bổ sung LatLng của vị trí hiện tại vào builder
//                        builder.include(currentLatLng);

//                         Di chuyển camera để hiển thị cả vị trí hiện tại và các cửa hàng trên bản đồ
//                        LatLngBounds bounds = builder.build();
//                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 10);
//                        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(cuaHangLatLng,10);
//
//                        googleMap.moveCamera(cu);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("MapActivity", "Failed to read value.", databaseError.toException());
                    }
                });



                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(@NonNull Marker marker) {
                            String idCH = (String) marker.getTag();
                            Log.d("MarkerClick", "idCH: " + idCH);

                            // Chuyển sang activity ChiTietCH và truyền ID của cửa hàng
                            Intent intent = new Intent(getActivity(), ChiTietCH.class);
                            intent.putExtra("idCH", idCH);
                            startActivity(intent);

                    }
                });

                cuaHangAdapter.setGoogleMap(googleMap);
        } else {
            Log.d("MapActivity", "Vị trí hiện tại là null");
        }
    }

}