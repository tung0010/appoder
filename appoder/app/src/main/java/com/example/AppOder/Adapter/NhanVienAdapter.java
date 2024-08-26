package com.example.AppOder.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.AppOder.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.AppOder.Model.NhanVienModel;

import java.util.List;
public class NhanVienAdapter extends RecyclerView.Adapter<NhanVienAdapter.EmployeeViewHolder> {

    private Context context;
    private List<NhanVienModel> employeeList;

    public NhanVienAdapter(Context context, List<NhanVienModel> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nhanvien, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        NhanVienModel employee = employeeList.get(position);
        holder.tvEmployeeId.setText(employee.getMaNV());
        holder.tvEmployeeName.setText(employee.getHoTen());
        holder.tvEmployeePosition.setText(employee.getChucVu());
        holder.tvEmployeeHSL.setText(String.valueOf(employee.getHsl()));
        holder.tvEmployeeLCB.setText(String.valueOf(employee.getLcb()));
        holder.tvEmployeePhuCap.setText(String.valueOf(employee.getPhuCap()));
        double salary = (employee.getHsl() + employee.getPhuCap()) * employee.getLcb();
        holder.tvEmployeeSalary.setText(String.valueOf(salary));
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmployeeId, tvEmployeeName, tvEmployeePosition, tvEmployeeHSL, tvEmployeeLCB, tvEmployeePhuCap, tvEmployeeSalary;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmployeeId = itemView.findViewById(R.id.tvEmployeeId);
            tvEmployeeName = itemView.findViewById(R.id.tvEmployeeName);
            tvEmployeePosition = itemView.findViewById(R.id.tvEmployeePosition);
            tvEmployeeHSL = itemView.findViewById(R.id.tvEmployeeHSL);
            tvEmployeeLCB = itemView.findViewById(R.id.tvEmployeeLCB);
            tvEmployeePhuCap = itemView.findViewById(R.id.tvEmployeePhuCap);
            tvEmployeeSalary = itemView.findViewById(R.id.tvEmployeeSalary);
        }
    }
}
