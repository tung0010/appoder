
package com.example.AppOder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.AppOder.Model.VoucherModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChiTietVoucher extends AppCompatActivity {

    private EditText etVoucherCode, etDiscountValue, etMinValue, etStartDate, etEndDate, etDescription;
    private Button btnUpdateVoucher, btnDeleteVoucher;
    private DatabaseReference databaseReference;
    private String voucherId;
    private DatePickerDialog datePickerDialog;
    ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_voucher);

        etVoucherCode = findViewById(R.id.etVoucherCode);
        etDiscountValue = findViewById(R.id.etDiscountValue);
        etMinValue = findViewById(R.id.etMinValue);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        etDescription = findViewById(R.id.etMoTa);
        btnUpdateVoucher = findViewById(R.id.btnUpdateVoucher);
        btnDeleteVoucher = findViewById(R.id.btnDeleteVoucher);
        imgBack= findViewById(R.id.imgBack);

        voucherId = getIntent().getStringExtra("idVoucher");

        databaseReference = FirebaseDatabase.getInstance().getReference("vouchers").child(voucherId);
        loadVoucherDetails();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    getOnBackInvokedDispatcher();
                }
                finish();
            }
        });
        etStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(etStartDate);
            }
        });

        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(etEndDate);
            }
        });

        btnUpdateVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVoucherDetails();
            }
        });

        btnDeleteVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteVoucher();
            }
        });
    }

    private void loadVoucherDetails() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                VoucherModel voucher = dataSnapshot.getValue(VoucherModel.class);
                if (voucher != null) {
                    etVoucherCode.setText(voucher.getMaGG());
                    etDiscountValue.setText(String.valueOf(voucher.getGiamgia()));
                    etMinValue.setText(String.valueOf(voucher.getDieuKien()));
                    etStartDate.setText(voucher.getNgayBatDau());
                    etEndDate.setText(voucher.getNgayKetThuc());
                    etDescription.setText(voucher.getMota());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                databaseError.getMessage();
            }
        });
    }

    private void updateVoucherDetails() {
        String maVoucher = etVoucherCode.getText().toString();
        int giaTriGiam;
        int giaTriToiThieu;
        String ngayBatDau = etStartDate.getText().toString();
        String ngayKetThuc = etEndDate.getText().toString();
        String moTa = etDescription.getText().toString();


        try {
             giaTriGiam = Integer.parseInt(etDiscountValue.getText().toString());
            giaTriToiThieu = Integer.parseInt(etMinValue.getText().toString());
        }
        catch (NumberFormatException e)
        {
            Toast.makeText(this,"Giá trị giảm giá và giá trị sản phẩm tối thiểu phải là số",Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date startDate = sdf.parse(ngayBatDau);
            Date endDate = sdf.parse(ngayKetThuc);
            Date currentDate = new Date();

            if (startDate.after(endDate)) {
                Toast.makeText(this, "Ngày bắt đầu phải trước ngày kết thúc.", Toast.LENGTH_LONG).show();
                return;
            }
            //check loi ngay startdate
//            if (startDate.before(currentDate)) {
//                Toast.makeText(this, "Ngày bắt đầu không được nhỏ hơn ngày hiện tại.", Toast.LENGTH_LONG).show();
//                return;
//            }
        } catch (ParseException e) {
            Toast.makeText(this, "Định dạng ngày không hợp lệ.", Toast.LENGTH_LONG).show();
            return;
        }

        VoucherModel voucher = new VoucherModel(maVoucher, moTa, giaTriGiam, giaTriToiThieu, ngayBatDau, ngayKetThuc);
        databaseReference.setValue(voucher);
        Toast.makeText(this, "Cập nhật thành công.", Toast.LENGTH_LONG).show();
    }

    private void deleteVoucher() {
        databaseReference.removeValue();
        finish();
    }

    private void showDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                editText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            }
        }, year, month, day);

        datePickerDialog.show();
    }
}
