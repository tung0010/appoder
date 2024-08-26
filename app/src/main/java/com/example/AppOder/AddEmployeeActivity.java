package com.example.AppOder;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.AppOder.Model.NhanVienModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddEmployeeActivity extends AppCompatActivity {

    private EditText etEmployeeId, etEmployeeName, etEmployeePosition, etEmployeeHSL, etEmployeeLCB, etEmployeePhuCap;
    private Button btnAddEmployee;
    private DatabaseReference databaseEmployees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        etEmployeeId = findViewById(R.id.etEmployeeId);
        etEmployeeName = findViewById(R.id.etEmployeeName);
        etEmployeePosition = findViewById(R.id.etEmployeePosition);
        etEmployeeHSL = findViewById(R.id.etEmployeeHSL);
        etEmployeeLCB = findViewById(R.id.etEmployeeLCB);
        etEmployeePhuCap = findViewById(R.id.etEmployeePhuCap);
        btnAddEmployee = findViewById(R.id.btnAddEmployee);

        databaseEmployees = FirebaseDatabase.getInstance().getReference("nhanvien");

        btnAddEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmployee();
            }
        });
    }

    private void addEmployee() {
        String id = etEmployeeId.getText().toString().trim();
        String name = etEmployeeName.getText().toString().trim();
        String position = etEmployeePosition.getText().toString().trim();
        float hsl = Float.parseFloat(etEmployeeHSL.getText().toString().trim());
        float lcb = Float.parseFloat(etEmployeeLCB.getText().toString().trim());
        float phuCap = Float.parseFloat(etEmployeePhuCap.getText().toString().trim());

        NhanVienModel employee = new NhanVienModel(id, name, position, hsl, lcb, phuCap);

        databaseEmployees.child(id).setValue(employee);
        Toast.makeText(this, "Nhân viên được thêm thành công", Toast.LENGTH_SHORT).show();
        finish();
    }
}
