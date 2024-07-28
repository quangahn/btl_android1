package com.example.btl.Client;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.btl.Fragments.Profile;
import com.example.btl.R;
import com.example.btl.databinding.ActivityLoginBinding;
import com.example.btl.databinding.ActivityUpdateUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateUser extends AppCompatActivity {
    private ActivityUpdateUserBinding binding;
    private Uri uri;
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
                        binding.uploadImage.setImageURI(uri);
                    }

                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        binding = ActivityUpdateUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.uploadImage.setOnClickListener(v -> {
            checkPermissionReadExternalStorage();
        });
        binding.btnSave.setOnClickListener(v -> {
            updateUser();
        });
        binding.btnCancel.setOnClickListener(v -> {
            finish();
        });
        binding.changePassword.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateUser.this, ChangePassword.class);
            startActivity(intent);
        });

    }



    private void checkPermissionReadExternalStorage() {
        String readExternalImage = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            readExternalImage = android.Manifest.permission.READ_MEDIA_IMAGES;
        else
            readExternalImage = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, readExternalImage) == PackageManager.PERMISSION_GRANTED)
            openGallery();
        else
            ActivityCompat.requestPermissions(this, new String[]{readExternalImage}, 100);

    }

    private void openGallery() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        launcher.launch(Intent.createChooser(intent, "select image"));
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        launcher.launch(Intent.createChooser(intent, "Select image"));

    }
    private void updateUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            return;
        }

        String name = binding.etUsername.getText().toString().trim();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(uri)
                .build();
        binding.progressBar.setVisibility(View.VISIBLE);
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            updateProfileFragment();
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("isUpdated", true);
                            setResult(RESULT_OK, resultIntent);
                            binding.progressBar.setVisibility(View.GONE);
                            Map<String, Object> data = new HashMap<>();
                            data.put("avatar", uri.toString());
                            data.put("name", name);
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Users").document(user.getUid()).update(data);
//                                    .addOnCompleteListener(task->{
//                                if(task.isSuccessful()){
//
//
//                                }
//                                else{
//
//                                    Toast.makeText(UpdateUser.this, "Failed to update post", Toast.LENGTH_SHORT).show();
//
//                                }
//                            });
//                            FirebaseFirestore db = FirebaseFirestore.getInstance();
//                            DocumentReference userRef = db.collection("Users").document(user.getUid());
//                            userRef.update("avatar", uri.toString());
                            Toast.makeText(UpdateUser.this, "Update success", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(UpdateUser.this, "Update failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void updateProfileFragment() {
        Profile profileFragment = (Profile) getSupportFragmentManager().findFragmentById(R.id.profile_layout);
        if (profileFragment != null) {
            profileFragment.showInfo();
        }
    }
}