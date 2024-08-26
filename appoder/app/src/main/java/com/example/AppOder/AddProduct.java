package com.example.AppOder;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddProduct#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProduct extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etItemName, etItemPrice, etItemDescription;
    private Spinner spinnerCategory, spinnerIsBanChay;
    private ImageView ivItemImage;
    private Button btnSelectImage, btnSaveItem;

    private Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);
        initializeViews(view);
        loadCategories();
        setupBanChaySpinner();
        setupButtonListeners();
        return view;
    }

    private void initializeViews(View view) {
        etItemName = view.findViewById(R.id.etItemName);
        etItemPrice = view.findViewById(R.id.etItemPrice);
        etItemDescription = view.findViewById(R.id.etItemDescription);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerIsBanChay = view.findViewById(R.id.spinnerIsBanChay);
        ivItemImage = view.findViewById(R.id.ivItemImage);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnSaveItem = view.findViewById(R.id.btnSaveItem);
    }

    private void setupButtonListeners() {
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnSaveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    uploadFile();
                }
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivItemImage.setImageURI(imageUri);
        }
    }

    private void uploadFile() {
        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("imgSanPham");
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
                                } else {
                                    showToast("Failed to get download URL");
                                }
                            }
                        });
                    } else {
                        showToast("Upload failed: " + task.getException().getMessage());
                    }
                }
            });
        } else {
            showToast("No file selected");
        }
    }

    private void saveItemToDatabase(final String imageUrl) {
        generateNewProductId(new OnProductIdGeneratedListener() {
            @Override
            public void onProductIdGenerated(String newProductId) {
                String ten = etItemName.getText().toString().trim();
                float gia = Float.parseFloat(etItemPrice.getText().toString().trim());
                String mota = etItemDescription.getText().toString().trim();
                String idLoai = spinnerCategory.getSelectedItem().toString();
                boolean isBanChay = spinnerIsBanChay.getSelectedItem().toString().equals("Có");

                SanPhamModel product = new SanPhamModel(ten, imageUrl, mota, gia, idLoai, isBanChay);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("sanpham");

                databaseReference.child(newProductId).setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showToast("Item added successfully");
                        } else {
                            showToast("Failed to add item: " + task.getException().getMessage());
                        }
                    }
                });
            }
        });
    }

    private void generateNewProductId(final OnProductIdGeneratedListener listener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("sanpham");

        databaseReference.orderByKey().limitToLast(1).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    String newId = "sp1"; // Default value if no products exist

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String currentId = dataSnapshot.getKey();
                        if (currentId != null && currentId.startsWith("sp")) {
                            int currentNumber = Integer.parseInt(currentId.substring(2));
                            newId = "sp" + (currentNumber + 1);
                        }
                    }
                    listener.onProductIdGenerated(newId);
                } else {
                    showToast("Failed to generate new product ID");
                }
            }
        });
    }

    public interface OnProductIdGeneratedListener {
        void onProductIdGenerated(String newProductId);
    }

    private void loadCategories() {
        DatabaseReference categoryReference = FirebaseDatabase.getInstance().getReference("loai");
        categoryReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    // Ensure the fragment is still attached before proceeding
                    if (isAdded() && getActivity() != null) {
                        List<String> categories = new ArrayList<>();
                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
                            String categoryName = snapshot.child("nameLoai").getValue(String.class);
                            categories.add(categoryName);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategory.setAdapter(adapter);
                    }
                } else {
                    showToast("Failed to load categories: " + task.getException().getMessage());
                }
            }
        });
    }


    private void setupBanChaySpinner() {
        List<String> options = new ArrayList<>();
        options.add("Có");
        options.add("Không");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIsBanChay.setAdapter(adapter);
    }

    private boolean validateInputs() {
        if (etItemName.getText().toString().trim().isEmpty()) {
            showToast("Vui lòng nhập tên sản phẩm");
            return false;
        }
        if (etItemPrice.getText().toString().trim().isEmpty()) {
            showToast("Vui lòng nhập giá sản phẩm");
            return false;
        }
        if (etItemDescription.getText().toString().trim().isEmpty()) {
            showToast("Vui lòng nhập mô tả sản phẩm");
            return false;
        }
        if (spinnerCategory.getSelectedItem() == null) {
            showToast("Vui lòng chọn loại sản phẩm");
            return false;
        }
        if (spinnerIsBanChay.getSelectedItem() == null) {
            showToast("Vui lòng chọn trạng thái bán chạy");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
