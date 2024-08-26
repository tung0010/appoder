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

public class SearchEmployeeActivity extends AppCompatActivity {

    private Button btnShowManagers;
    private RecyclerView rvManagers;
    private List<NhanVienModel> employeeList;
    private NhanVienAdapter employeeAdapter;
    private DatabaseReference databaseEmployees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_employee);

        btnShowManagers = findViewById(R.id.btnShowManagers);
        rvManagers = findViewById(R.id.rvManagers);
        rvManagers.setLayoutManager(new LinearLayoutManager(this));

        employeeList = new ArrayList<>();
        employeeAdapter = new NhanVienAdapter(this, employeeList);
        rvManagers.setAdapter(employeeAdapter);

        databaseEmployees = FirebaseDatabase.getInstance().getReference("nhanvien");

        btnShowManagers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchManagers();
            }
        });
    }

    private void fetchManagers() {
        databaseEmployees.orderByChild("chucVu").equalTo("truong phong").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                employeeList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    NhanVienModel employee = postSnapshot.getValue(NhanVienModel.class);
                    if (employee != null) {
                        employeeList.add(employee);
                    }
                }
                employeeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
    }
}
