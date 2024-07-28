package com.example.btl.Client;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl.Adapters.ProductaSprinnerAdapter;
import com.example.btl.R;
import com.example.btl.databinding.ActivityPostBinding;
import com.example.btl.entites.PostEntity;
import com.example.btl.entites.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Post extends AppCompatActivity {
    private ActivityPostBinding binding;
    FirebaseFirestore db;
    private Uri uri;
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
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();

        binding.addPhotosButton.setOnClickListener(v -> {
            checkPermissionReadExternalStorage();
        });
        binding.btnSave.setOnClickListener(v -> addPost());
        binding.btnCancel.setOnClickListener(v -> finish());
        //test
        Spinner categorySpinner = findViewById(R.id.category_spinner);
        Spinner dishNameSpinner = findViewById(R.id.resturant_spinner);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_spinner, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        ArrayAdapter<CharSequence> dishNameAdapter = ArrayAdapter.createFromResource(this,
                R.array.resturant_spinner, android.R.layout.simple_spinner_item);
        dishNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dishNameSpinner.setAdapter(dishNameAdapter);
        queyProduct();
        queryRestaurant();




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

    private void addPost() {
        String title = binding.title.getText().toString().trim();
        String content = binding.contents.getText().toString().trim();
        String image = uri.toString().trim();
        String id = db.collection("posts").document().getId();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(title.isEmpty()|| content.isEmpty()|| image.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> likes = new ArrayList<>();
        PostEntity post = new PostEntity(id, title, content, userId, selectedRestaurantId, image, true, selectedCategoryId, likes);
        binding.progressBar.setVisibility(View.VISIBLE);
        db.collection("posts").document(id).set(post)
                .addOnSuccessListener(aVoid -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Post added", Toast.LENGTH_SHORT).show();
                    finish();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding post", Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
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



}