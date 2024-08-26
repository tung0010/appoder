package com.example.AppOder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);
        ColorStateList iconColors = getResources().getColorStateList(R.color.bottom_nav_icon_colors);

// Thiết lập màu cho icon của các MenuItem trong BottomNavigationView
        bottomNavigationView.setItemIconTintList(iconColors);
        bottomNavigationView.setItemTextColor(iconColors);


// Thiết lập lắng nghe sự kiện khi chọn menu item
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        Bundle extras = getIntent().getExtras();
        if (getIntent().hasExtra("key")) {
            String data = extras.getString("key");
            chuyenFragment(data);

        }else if (getIntent().hasExtra("fragment")) {
            String data = extras.getString("fragment");
            chuyenFragment(data);

        }else if (getIntent().hasExtra("fragmentHS")) {
            String data = extras.getString("fragmentHS");
            chuyenFragment(data);

        }else{
            // Replace fragment mặc định khi khởi động
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, new TrangChu())
                    .commit();
        }


    }

    private void chuyenFragment(String TenFragment){
        if (TenFragment.toLowerCase(Locale.ROOT).equals("dathang")) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, danhMUc)
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.ic_DanhMuc);
        } else if (TenFragment.toLowerCase(Locale.ROOT).equals("lichsu")) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, lichSu)
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.ic_history);
        } else if (TenFragment.toLowerCase(Locale.ROOT).equals("giohang")) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, gioHang)
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.ic_giohang);
        }
    }
    TrangChu trangChu = new TrangChu();

    DanhMUc danhMUc = new DanhMUc();
    LichSu lichSu = new LichSu();
    CuaHang cuaHang = new CuaHang();
    GioHang gioHang = new GioHang();

    // Phương thức để thay đổi selectedItemId của BottomNavigationView
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();


        if (itemId == R.id.ic_trangchu) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, trangChu)
                    .commit();
            return true;
        } else if (itemId == R.id.ic_DanhMuc) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, danhMUc)
                    .commit();
            return true;
        } else if (itemId == R.id.ic_history) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, lichSu)
                    .commit();
            return true;
        } else if (itemId == R.id.ic_giohang) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, gioHang)
                    .commit();
            return true;
        }

        return false;
    }

}