package com.example.AppOder;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.AppOder.Model.SanPhamModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AddItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etItemName, etItemPrice, etItemDescription;
    private Spinner spinnerCategory, spinnerIsBanChay;
    private ImageView ivItemImage;
    private Button btnSelectImage, btnSaveItem;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        etItemName = findViewById(R.id.etItemName);
        etItemPrice = findViewById(R.id.etItemPrice);
        etItemDescription = findViewById(R.id.etItemDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerIsBanChay = findViewById(R.id.spinnerIsBanChay);
        ivItemImage = findViewById(R.id.ivItemImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSaveItem = findViewById(R.id.btnSaveItem);

        // Load data for spinners
        loadCategories();
        setupBanChaySpinner();

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnSaveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivItemImage.setImageURI(imageUri);
        }
    }

    private void uploadFile() {
        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("item_images");
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

            fileReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    saveItemToDatabase(downloadUri.toString());
                                }
                            }
                        });
                    } else {
                        Toast.makeText(AddItemActivity.this, "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveItemToDatabase(String imageUrl) {
        String ten = etItemName.getText().toString().trim();
        float gia = Float.parseFloat(etItemPrice.getText().toString().trim());
        String mota = etItemDescription.getText().toString().trim();
        String idLoai = spinnerCategory.getSelectedItem().toString();
        Boolean isBanChay = spinnerIsBanChay.getSelectedItem().toString().equals("có");

        SanPhamModel product = new SanPhamModel(ten,imageUrl, mota, gia, idLoai, isBanChay);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("items");
        String itemId = databaseReference.push().getKey();

        databaseReference.child(itemId).setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddItemActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddItemActivity.this, "Failed to add item: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadCategories() {
        DatabaseReference categoryReference = FirebaseDatabase.getInstance().getReference("categories");
        categoryReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> categories = new ArrayList<>();
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        String categoryName = snapshot.child("name").getValue(String.class);
                        categories.add(categoryName);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddItemActivity.this, android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                } else {
                    Toast.makeText(AddItemActivity.this, "Failed to load categories: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupBanChaySpinner() {
        List<String> options = new ArrayList<>();
        options.add("Có");
        options.add("Không");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIsBanChay.setAdapter(adapter);
    }
}
