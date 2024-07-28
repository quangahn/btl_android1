package com.example.btl.Client;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.btl.Adapters.ProductaSprinnerAdapter;
import com.example.btl.databinding.ActivityEditPostBinding;
import com.example.btl.entites.PostEntity;
import com.example.btl.entites.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditPost extends AppCompatActivity {
    private ActivityEditPostBinding binding;
    private Uri uri;
    FirebaseFirestore db;
    String selectedCategoryId;

    String selectedRestaurantId;
    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == RESULT_OK) {
                        Intent data = o.getData();
                        if (data == null) {
                            return;
                        }
                        uri = data.getData();
                        binding.imageDetail.setImageURI(uri);
                    }


                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        queyProduct();
        queryRestaurant();
        binding.addPhotosButton.setOnClickListener(v -> {
            checkPermissionReadExternalStorage();
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            return;
        PostEntity postEntity = (PostEntity) bundle.getSerializable("post");
        binding.title.setText(postEntity.getTitle());
        binding.contents.setText(postEntity.getContent());
        String id = postEntity.getId();
        binding.btnSave.setOnClickListener(v -> {
            updatePost(id);
        });

    }

    private void updatePost(String id) {
        String title = binding.title.getText().toString().trim();
        String content = binding.contents.getText().toString().trim();
        String image = uri.toString().trim();
        Map<String, Object> post = new HashMap<>();
        post.put("title", title);
        post.put("content", content);
        post.put("image", image);
        post.put("restaurant_id", selectedRestaurantId);
        post.put("product_id", selectedCategoryId);
        binding.progressBar.setVisibility(View.VISIBLE);
        db.collection("posts").document(id).update(post).addOnCompleteListener(task->{
            if(task.isSuccessful()){
                Toast.makeText(this, "Post updated successfully", Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
                finish();
            }
            else{
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Failed to update post", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkPermissionReadExternalStorage() {
        String readExternalImage = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            readExternalImage = android.Manifest.permission.READ_MEDIA_IMAGES;
        else
            readExternalImage = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, readExternalImage) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
            binding.addPhotosButton.setVisibility(View.GONE);
        } else
            ActivityCompat.requestPermissions(this, new String[]{readExternalImage}, 100);

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        launcher.launch(Intent.createChooser(intent, "Select image"));

    }

    private void queryRestaurant() {
        db.collection("restaurants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> categoryList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String name = document.getString("name");
                            categoryList.add(new Product(id, name));
                        }

                        ProductaSprinnerAdapter categoryAdapter = new ProductaSprinnerAdapter(this, categoryList);
                        binding.resturantSpinner.setAdapter(categoryAdapter);
                        binding.resturantSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                Product selectedProduct = (Product) parent.getItemAtPosition(position);
                                selectedRestaurantId = selectedProduct.getId();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                                selectedRestaurantId = null;
                            }
                        });
                    } else {
                        Toast.makeText(this, "Error fetching categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void queyProduct() {
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> categoryList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String name = document.getString("name");
                            categoryList.add(new Product(id, name));
                        }

                        ProductaSprinnerAdapter categoryAdapter = new ProductaSprinnerAdapter(this, categoryList);
                        binding.categorySpinner.setAdapter(categoryAdapter);
                        binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                Product selectedProduct = (Product) parent.getItemAtPosition(position);
                                selectedCategoryId = selectedProduct.getId();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                                selectedCategoryId = null;
                            }
                        });
                    } else {
                        Toast.makeText(this, "Error fetching categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}