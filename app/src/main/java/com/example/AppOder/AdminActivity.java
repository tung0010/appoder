package com.example.AppOder;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(new AddProduct());
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    int itemId = item.getItemId();

                    if (itemId == R.id.ic_add_product) {
                        selectedFragment = new AddProduct();
                    } else if (itemId == R.id.ic_HoaDon) {
                        selectedFragment = new QlyHoaDon();
                    } else if (itemId == R.id.ic_profile) {
                        selectedFragment = new GioHang();
                    }else if(itemId == R.id.ic_add_voucher){
                        selectedFragment = new VoucherList();
                    }
                    if (selectedFragment != null) {
                        loadFragment(selectedFragment);
                    }
                    return true;
                }
            };

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
