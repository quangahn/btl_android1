package com.example.btl.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.btl.Client.Login;
import com.example.btl.Client.UpdateUser;
import com.example.btl.Client.UserrClient;
import com.example.btl.R;
import com.example.btl.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == UpdateUser.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getBooleanExtra("isUpdated", false)) {
                        showInfo();
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        showInfo();
        binding.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UpdateUser.class);
            launcher.launch(intent);
        });
        binding.showUserr.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserrClient.class);
            launcher.launch(intent);
        });
        binding.logoutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Confirm Sign Out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        mAuth.signOut();
                        Intent intent = new Intent(getActivity(), Login.class);
                        startActivity(intent);
                        getActivity().finish();
                        Toast.makeText(getActivity(), "Sign out successfully", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
        return binding.getRoot();
    }

    public void showInfo() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            return;
        }
        Uri avatar = user.getPhotoUrl();
        if (user.getDisplayName() == null) {
            binding.name.setVisibility(View.GONE);
        } else {
            binding.name.setText(user.getDisplayName());
        }
        binding.email.setText(user.getEmail());
        Glide.with(this).load(avatar).error(R.drawable.no_avatar).into(binding.profileImage);
    }
}