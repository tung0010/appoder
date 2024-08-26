package com.example.AppOder;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.AppOder.Adapter.NhanVienAdapter;
import com.example.AppOder.Model.NhanVienModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HighSalaryEmployeeActivity extends AppCompatActivity {

    private RecyclerView rvHighSalaryEmployees;
    private List<NhanVienModel> employeeList;
    private NhanVienAdapter employeeAdapter;
    private DatabaseReference databaseEmployees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_salary_employee);

        rvHighSalaryEmployees = findViewById(R.id.rvHighSalaryEmployees);
        rvHighSalaryEmployees.setLayoutManager(new LinearLayoutManager(this));

        employeeList = new ArrayList<>();
        employeeAdapter = new NhanVienAdapter(this, employeeList);
        rvHighSalaryEmployees.setAdapter(employeeAdapter);

        databaseEmployees = FirebaseDatabase.getInstance().getReference("nhanvien");

        databaseEmployees.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                employeeList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        // Assuming the data in Firebase is stored as strings and needs conversion
                        String maNV = postSnapshot.child("maNV").getValue(String.class);
                        String hoTen = postSnapshot.child("hoTen").getValue(String.class);
                        String chucVu = postSnapshot.child("chucVu").getValue(String.class);
                        double hsl = Double.parseDouble(postSnapshot.child("hsl").getValue(String.class));
                        double lcb = Double.parseDouble(postSnapshot.child("lcb").getValue(String.class));
                        double phuCap = Double.parseDouble(postSnapshot.child("phuCap").getValue(String.class));

                        NhanVienModel employee = new NhanVienModel(maNV, hoTen, chucVu, hsl, lcb, phuCap);

                        // Calculate salary
                        double salary = (hsl + phuCap) * lcb;

                        if (salary > 10000000) {
                            employeeList.add(employee);
                        }
                    } catch (NumberFormatException e) {
                        // Handle possible errors
                        e.printStackTrace();
                    }
                }
                employeeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }
}
