package com.example.AppOder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.AppOder.R;
public class AdminQuanLyNhanVien extends AppCompatActivity {

    private Button btnAddEmployee, btnShowHighSalaryEmployees, btnSearchEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quan_ly_nhan_vien);

        btnAddEmployee = findViewById(R.id.btnAddEmployee);
        btnShowHighSalaryEmployees = findViewById(R.id.btnShowHighSalaryEmployees);
        btnSearchEmployee = findViewById(R.id.btnSearchEmployee);

        btnAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminQuanLyNhanVien.this, AddEmployeeActivity.class));
            }
        });

        btnShowHighSalaryEmployees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminQuanLyNhanVien.this, HighSalaryEmployeeActivity.class));
            }
        });

        btnSearchEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminQuanLyNhanVien.this, SearchEmployeeActivity.class));
            }
        });
    }
}
