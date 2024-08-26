package com.example.AppOder;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class AddVoucher extends AppCompatActivity {

    private EditText etMaVoucher, etMota, etMinValue, etGiaTriGiam, etNgayBatDau, etNgayKetThuc;
    private Button btnThemVoucher;
    private DatabaseReference databaseReference;
    private DatePickerDialog datePickerDialog;
    private ImageView icBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_voucher);

        etMaVoucher = findViewById(R.id.etMaVoucher);
        etMota = findViewById(R.id.etMota);
        etMinValue = findViewById(R.id.etMinValue);
        etGiaTriGiam = findViewById(R.id.etGiaTriGiam);
        etNgayBatDau = findViewById(R.id.etNgayBatDau);
        etNgayKetThuc = findViewById(R.id.etNgayKetThuc);
        btnThemVoucher = findViewById(R.id.btnThemVoucher);
        icBack = findViewById(R.id.icBack);

        databaseReference = FirebaseDatabase.getInstance().getReference("vouchers");

        icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etNgayBatDau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(etNgayBatDau);
            }
        });

        etNgayKetThuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(etNgayKetThuc);
            }
        });

        btnThemVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewVoucher();
            }
        });
    }

    private void addNewVoucher() {
        String maVoucher = etMaVoucher.getText().toString();
        String moTa = etMota.getText().toString();
        int giaTriGiam;
        int giaTriToiThieu;
        String ngayBatDau = etNgayBatDau.getText().toString();
        String ngayKetThuc = etNgayKetThuc.getText().toString();

        try {
            giaTriGiam = Integer.parseInt(etGiaTriGiam.getText().toString());
            giaTriToiThieu = Integer.parseInt(etMinValue.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá trị giảm giá và giá trị sản phẩm tối thiểu phải là số.", Toast.LENGTH_LONG).show();
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

//            if (startDate.before(currentDate)) {
//                Toast.makeText(this, "Ngày bắt đầu không được nhỏ hơn ngày hiện tại.", Toast.LENGTH_LONG).show();
//                return;
//            }
        } catch (ParseException e) {
            Toast.makeText(this, "Định dạng ngày không hợp lệ.", Toast.LENGTH_LONG).show();
            return;
        }

        checkDuplicateVoucherCode(maVoucher, new OnCheckDuplicateListener() {
            @Override
            public void onResult(boolean isDuplicate) {
                if (isDuplicate) {
                    Toast.makeText(AddVoucher.this, "Mã Voucher đã tồn tại. Vui lòng nhập mã khác.", Toast.LENGTH_LONG).show();
                } else {
                    String voucherId = databaseReference.push().getKey();
                    VoucherModel voucher = new VoucherModel(maVoucher, moTa, giaTriGiam, giaTriToiThieu, ngayBatDau, ngayKetThuc);
                    if (voucherId != null) {
                        databaseReference.child(voucherId).setValue(voucher).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddVoucher.this, "Thêm Voucher thành công.", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(AddVoucher.this, "Thêm Voucher thất bại. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(AddVoucher.this, "Không thể tạo ID cho Voucher. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void checkDuplicateVoucherCode(String maVoucher, OnCheckDuplicateListener listener) {
        databaseReference.orderByChild("maGG").equalTo(maVoucher).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listener.onResult(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddVoucher.this, "Có lỗi xảy ra khi kiểm tra mã Voucher. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
            }
        });
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

    interface OnCheckDuplicateListener {
        void onResult(boolean isDuplicate);
    }
}
