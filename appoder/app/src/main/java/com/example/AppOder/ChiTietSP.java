package com.example.AppOder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.AppOder.Model.SanPhamModel;
import com.example.AppOder.Model.SanPhamMuaModel;
import com.example.AppOder.Utils.DsSanPhamMuaUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;

public class ChiTietSP extends AppCompatActivity {
        ImageView imgSP, btnHuyCT, btnTang, btnTru;
        TextView txtNameSP;
    TextView txtGiaSP, txtSizeS, txtSizeM, txtSizeL, txtTong, txtSoLuong, txtThem, txtDonViT;
    TextView txtMoTaSP,txtTextThem;
    LinearLayout ll_btnThem,ll_btnXoa;
    EditText edGhiCHu;
    float giaSP;

    float giaSizeS;  // Giá của size S
    float giaSizeM; // Giá của size M
    float giaSizeL;
    DecimalFormat decimalFormat;
    ArrayList<SanPhamMuaModel> dsSanPhamMua =new ArrayList<>();
    SanPhamMuaModel sanphammua = new SanPhamMuaModel();
    String formattedGiaSP;
    String idSP;
    Integer sTT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_sp);
        getView();
        Intent intent = getIntent();
        if (intent != null) {
            idSP = intent.getStringExtra("idSP");

            sTT = intent.getIntExtra("STT",-1);

            if(sTT >=0 ){
                updateChiTietSanPM();
            }
            else updateChiTietSP(idSP);
        }
        btnHuyCT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnTang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int soLuongHienTai = Integer.parseInt(txtSoLuong.getText().toString());
                soLuongHienTai++; // Tăng số lượng lên 1
                txtSoLuong.setText(String.valueOf(soLuongHienTai));
            }
        });
        btnTru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int soLuongHienTai = Integer.parseInt(txtSoLuong.getText().toString());
                if (soLuongHienTai > 0) {
                    soLuongHienTai--; // Giảm số lượng đi 1, nhưng không nhỏ hơn 1
                    txtSoLuong.setText(String.valueOf(soLuongHienTai));
                }
            }
        });
        txtSizeS.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                txtSizeS.setSelected(true);
                txtSizeM.setSelected(false);
                txtSizeL.setSelected(false);

                txtSizeS.setBackground(getResources().getDrawable(R.drawable.textview_selected));
                txtSizeS.setTextColor(Color.WHITE);

                // Thiết lập drawable cho các TextView không được chọn
                txtSizeM.setBackground(getResources().getDrawable(R.drawable.textview_unselected));
                txtSizeM.setTextColor(Color.parseColor("#B0272F"));
                txtSizeL.setBackground(getResources().getDrawable(R.drawable.textview_unselected));
                txtSizeL.setTextColor(Color.parseColor("#B0272F"));

//                txtTong.setText(formattedGiaSP);
                giaSizeS = giaSP;
                formattedGiaSP = decimalFormat.format(giaSizeS);
                txtGiaSP.setText(formattedGiaSP);
                updateTotalPrice(giaSizeS);

            }
        });

        txtSizeM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSizeS.setSelected(false);
                txtSizeM.setSelected(true);
                txtSizeL.setSelected(false);

                txtSizeM.setBackground(getResources().getDrawable(R.drawable.textview_selected));
                txtSizeM.setTextColor(Color.WHITE);

                // Thiết lập drawable cho các TextView không được chọn
                txtSizeS.setBackground(getResources().getDrawable(R.drawable.textview_unselected));
                txtSizeS.setTextColor(Color.parseColor("#B0272F"));
                txtSizeL.setBackground(getResources().getDrawable(R.drawable.textview_unselected));
                txtSizeL.setTextColor(Color.parseColor("#B0272F"));

                giaSizeM = giaSP + 10000;
                formattedGiaSP = decimalFormat.format(giaSizeM);
                txtGiaSP.setText(formattedGiaSP);// Giả sử tăng giá thêm 10.000 cho size M
//                String formattedGiaSizeM = decimalFormat.format(giaSizeM);
//                txtTong.setText(formattedGiaSizeM);
                updateTotalPrice(giaSizeM);
            }
        });

        txtSizeL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSizeS.setSelected(false);
                txtSizeM.setSelected(false);
                txtSizeL.setSelected(true);
                txtSizeL.setBackground(getResources().getDrawable(R.drawable.textview_selected));
                txtSizeL.setTextColor(Color.WHITE);

                // Thiết lập drawable cho các TextView không được chọn
                txtSizeM.setBackground(getResources().getDrawable(R.drawable.textview_unselected));
                txtSizeM.setTextColor(Color.parseColor("#B0272F"));
                txtSizeS.setBackground(getResources().getDrawable(R.drawable.textview_unselected));
                txtSizeS.setTextColor(Color.parseColor("#B0272F"));

                giaSizeL = giaSP + 15000;
                formattedGiaSP = decimalFormat.format(giaSizeL);
                txtGiaSP.setText(formattedGiaSP);// Giả sử tăng giá thêm 15.000 cho size L
//                String formattedGiaSizeL = decimalFormat.format(giaSizeL);
//                txtTong.setText(formattedGiaSizeL);
                updateTotalPrice(giaSizeL);

//                String soLuongText = txtSoLuong.getText().toString();
//                int soLuong = Integer.parseInt(soLuongText);
//
//                // Lấy giá trị từ txtTong và chuyển đổi thành số
//                String tongText = txtTong.getText().toString();
//                float giaTong = Float.parseFloat(tongText);
//
//                // Tính giá mới cho size M (tăng giá hiện tại lên 10.000)
//                float giaMoi = giaSP + 15000;
//                float tongMoi = giaMoi * soLuong;
//
//                // Cập nhật giá trị mới vào txtTong
//                String formattedTongMoi = decimalFormat.format(tongMoi);
//                txtTong.setText(formattedTongMoi);
            }
        });


    }
    ArrayList<SanPhamMuaModel> arrSanPhamM = new ArrayList<>();
    private  void updateChiTietSanPM(){
        decimalFormat = new DecimalFormat("#,###");


        arrSanPhamM = DsSanPhamMuaUtil.getInstance().getDsSanPhamMuaUtil();

        DatabaseReference SPMua = FirebaseDatabase.getInstance().getReference().child("sanpham").child(idSP);
        SPMua.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    SanPhamModel sanPhamModel = snapshot.getValue(SanPhamModel.class);
                    if (sanPhamModel != null) {
                        String nameSP = sanPhamModel.getNameSP();
                        String motaSP = sanPhamModel.getMoTa();
                        String imgSanPham = sanPhamModel.getImgSP();
                        txtNameSP.setText(nameSP);
                        txtMoTaSP.setText(motaSP);

                        giaSP = sanPhamModel.getGia();
                        formattedGiaSP = decimalFormat.format(giaSP);
                        txtGiaSP.setText(formattedGiaSP);
                        Glide.with(getApplicationContext())
                                .load(imgSanPham)
                                .into(imgSP);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // cap nhat thong tin
        txtSoLuong.setText(arrSanPhamM.get(sTT).getSoLuong().toString());
        formattedGiaSP = decimalFormat.format(arrSanPhamM.get(sTT).getGia());
        txtTong.setText(formattedGiaSP);
        txtTextThem.setText("Cập Nhật");
        switch (arrSanPhamM.get(sTT).getSize()) {
            case "M":{
                txtSizeS.setSelected(false);
                txtSizeM.setSelected(true);
                txtSizeL.setSelected(false);

                txtSizeM.setBackground(getResources().getDrawable(R.drawable.textview_selected));
                txtSizeM.setTextColor(Color.WHITE);

                // Thiết lập drawable cho các TextView không được chọn
                txtSizeL.setBackground(getResources().getDrawable(R.drawable.textview_unselected));
                txtSizeL.setTextColor(Color.parseColor("#B0272F"));
                txtSizeS.setBackground(getResources().getDrawable(R.drawable.textview_unselected));
                txtSizeS.setTextColor(Color.parseColor("#B0272F"));
                break;
            }

            case "L":{
                txtSizeS.setSelected(false);
                txtSizeM.setSelected(false);
                txtSizeL.setSelected(true);

                txtSizeL.setBackground(getResources().getDrawable(R.drawable.textview_selected));
                txtSizeL.setTextColor(Color.WHITE);

                // Thiết lập drawable cho các TextView không được chọn
                txtSizeM.setBackground(getResources().getDrawable(R.drawable.textview_unselected));
                txtSizeM.setTextColor(Color.parseColor("#B0272F"));
                txtSizeS.setBackground(getResources().getDrawable(R.drawable.textview_unselected));
                txtSizeS.setTextColor(Color.parseColor("#B0272F"));
                break;
            }
            default:
                txtSizeS.setSelected(true);
                txtSizeM.setSelected(false);
                txtSizeL.setSelected(false);

                txtSizeS.setBackground(getResources().getDrawable(R.drawable.textview_selected));
                txtSizeS.setTextColor(Color.WHITE);

                // Thiết lập drawable cho các TextView không được chọn
                txtSizeM.setBackground(getResources().getDrawable(R.drawable.textview_unselected));
                txtSizeM.setTextColor(Color.parseColor("#B0272F"));
                txtSizeL.setBackground(getResources().getDrawable(R.drawable.textview_unselected));
                txtSizeL.setTextColor(Color.parseColor("#B0272F"));
        }

        txtSoLuong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotalPrice();
                if(Integer.parseInt(txtSoLuong.getText().toString())==0){
                    Toast.makeText(ChiTietSP.this, "NNNN", Toast.LENGTH_SHORT).show();
                    txtTong.setVisibility(View.GONE);
                    txtDonViT.setVisibility(View.GONE);
                    txtTextThem.setText("Xoá");
                }
                else{
                    txtTong.setVisibility(View.VISIBLE);
                    txtDonViT.setVisibility(View.VISIBLE);
                    txtTextThem.setText("Cập Nhật");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        ll_btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idMHD = null;
                int soLuong = Integer.parseInt(txtSoLuong.getText().toString());
                if (soLuong > 0) {
                    String ghiChu = edGhiCHu.getText().toString().trim();
                    String nameSP = txtNameSP.getText().toString();
                    String size = getSizeSelected();
                    String gia = txtTong.getText().toString().replace(".", "");
                    int giaInt = Integer.parseInt(gia);

                    // Tạo đối tượng SanPhamMua
                    SanPhamMuaModel sanPhamMua = new SanPhamMuaModel();
                    sanPhamMua.setSoLuong(soLuong);
                    sanPhamMua.setGhiChu(ghiChu.isEmpty() ? "" : ghiChu);
                    sanPhamMua.setNameSP(nameSP);
                    sanPhamMua.setSize(size);
                    sanPhamMua.setGia(giaInt);
                    sanPhamMua.setMaSP(idSP);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("hoadon").child(idMHD).child("sanPhamMua");
                    String key = databaseReference.push().getKey(); // Tạo node con mới với ID duy nhất
                    if (key != null) {
                        databaseReference.child(key).setValue(sanPhamMua);
                    }

                    // Cập nhật danh sách sản phẩm mua
                    arrSanPhamM.add(sanPhamMua);
                    DsSanPhamMuaUtil.getInstance().setDsSanPhamMuaUtil(arrSanPhamM);

                    onBackPressed();
                } else {
                    Toast.makeText(ChiTietSP.this, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                }
            }

            private String getSizeSelected() {
                if (txtSizeL.getCurrentTextColor() == getResources().getColor(R.color.white)) {
                    return "L";
                } else if (txtSizeM.getCurrentTextColor() == getResources().getColor(R.color.white)) {
                    return "M";
                } else {
                    return "S";
                }
            }
        });

    }
    private void updateChiTietSP(String idSP){

        DatabaseReference SPRef = FirebaseDatabase.getInstance().getReference().child("sanpham").child(idSP);
        SPRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    SanPhamModel sanPhamModel = dataSnapshot.getValue(SanPhamModel.class);
                    if (sanPhamModel != null) {
                        String nameSP = sanPhamModel.getNameSP();
                        String motaSP = sanPhamModel.getMoTa();
                        String imgSanPham = sanPhamModel.getImgSP();
                        txtNameSP.setText(nameSP);
                        txtMoTaSP.setText(motaSP);
                        decimalFormat = new DecimalFormat("#,###");
                        giaSP = sanPhamModel.getGia();
                        formattedGiaSP = decimalFormat.format(giaSP);
                        txtGiaSP.setText(formattedGiaSP);
                        txtTong.setText(formattedGiaSP);

                        Glide.with(getApplicationContext())
                                .load(imgSanPham)
                                .into(imgSP);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ll_btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(txtSoLuong.getText().toString())>0){
                    if("" == edGhiCHu.getText().toString()){
                        sanphammua.setGhiChu("");
                    }
                    else sanphammua.setGhiChu(edGhiCHu.getText().toString());
                    sanphammua.setNameSP(txtNameSP.getText().toString());

                    sanphammua.setSoLuong(Integer.parseInt(txtSoLuong.getText().toString()));
                    if (txtSizeL.getCurrentTextColor() == getResources().getColor(R.color.white)){
                        sanphammua.setSize("L");
                    }else if (txtSizeM.getCurrentTextColor() == getResources().getColor(R.color.white)){
                        sanphammua.setSize("M");
                    }else if (txtSizeS.getCurrentTextColor() == getResources().getColor(R.color.white)){
                        sanphammua.setSize("S");
                    }
                    DecimalFormat decimalFormat1 = new DecimalFormat("#,####");
                    String gia = txtTong.getText().toString();
                    try {
                        Number giaNumber = decimalFormat1.parse(gia);
                        String formattedGia = decimalFormat1.format(giaNumber);
                        sanphammua.setGia(giaNumber.intValue());
                        Log.d("FormattedGia", formattedGia);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e("Error", "Cannot parse given String as a Number");
                    }

                    sanphammua.setMaSP(idSP);
                    DsSanPhamMuaUtil.getInstance().addDsSanPhamMuaUtil(sanphammua);
                    onBackPressed();
                }
                else Toast.makeText(ChiTietSP.this, "Vui lòng chọn số lượng sản phẩm cần mua", Toast.LENGTH_SHORT).show();
            }
        });

        txtSoLuong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotalPrice();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    private void calculateTotalPrice() {
        // Lấy giá của size đã chọn
        float giaHienTai = getSelectedSizePrice();
        int soLuong = Integer.parseInt(txtSoLuong.getText().toString());
        float giaTong = giaHienTai * soLuong;

        DecimalFormat decimalFormat = new DecimalFormat("#,###"); // Định dạng giá tiền
        String formattedGiaTong = decimalFormat.format(giaTong);

        txtTong.setText(formattedGiaTong);
    }

    // Phương thức lấy giá của size đã chọn
    private float getSelectedSizePrice() {
        if (txtSizeS.isSelected()) {
            return giaSizeS;
        } else if (txtSizeM.isSelected()) {
            return giaSizeM;
        } else {
            return giaSizeL;
        }
    }


    private void updateTotalPrice(float giaSize) {
        // Lấy số lượng hiện tại
        int soLuong = Integer.parseInt(txtSoLuong.getText().toString());
        // Tính giá tổng mới
        float giaTongMoi = giaSize * soLuong;
        // Cập nhật giá tổng mới
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        String formattedGiaTongMoi = decimalFormat.format(giaTongMoi);
        txtTong.setText(formattedGiaTongMoi);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // If there are fragments, pop the back stack to go back to the previous fragment
            getSupportFragmentManager().popBackStack();
        } else {
            // If there are no fragments in the back stack, finish the activity to go back to the previous activity
            finish();
        }
    }

    private void getView(){
        imgSP = findViewById(R.id.imgAnhCT);
        txtNameSP = findViewById(R.id.txtNameCT);
        txtGiaSP = findViewById(R.id.txtGiaCT);
        txtMoTaSP = findViewById(R.id.txtMoTaCT);
        btnHuyCT = findViewById(R.id.btnHuyCT);
        txtSizeM = findViewById(R.id.txtSizeM);
        txtSizeS = findViewById(R.id.txtSizeS);
        txtSizeL = findViewById(R.id.txtSizeL);
        txtTong = findViewById(R.id.txtTong);
        txtSoLuong = findViewById(R.id.txtSoLuong);
        btnTang = findViewById(R.id.btnTang);
        btnTru = findViewById(R.id.btnTru);
        edGhiCHu = findViewById(R.id.etGhiChu);
        ll_btnThem = findViewById(R.id.ll_BTNThem);

        txtTextThem =findViewById(R.id.tvChiTietSP_txtThem);

        txtDonViT = findViewById(R.id.ChiTietSP_tvDonViT);
//        txtThem = findViewById(R.id.btnThemText);
    }

}