package com.example.donateme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class uploadProfilePicActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 1001;
    private static final int CAMERA_REQUEST_CODE = 1002;
    private static final int PICK_IMAGE_REQUEST = 1;

    private AppCompatButton upload_pic_choose_btn, upload_pic_btn, upload_pic_camera_btn;  // Added camera button
    private ImageView imageView_Profile_dp;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private Uri uriImage;
    private ProgressBar progressBar;

    // Initialize PreferencesManager
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_pic);

        imageView_Profile_dp = findViewById(R.id.imageView_Profile_dp);
        upload_pic_choose_btn = findViewById(R.id.upload_pic_choose_btn);
        upload_pic_btn = findViewById(R.id.upload_pic_btn);
        upload_pic_camera_btn = findViewById(R.id.open_camera_btn);  // Initialize camera button
        progressBar = findViewById(R.id.progressBar);
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");

        // Initialize PreferencesManager
        preferencesManager = new PreferencesManager(this);

        // Load existing profile image from SharedPreferences if available
        String savedImageUri = preferencesManager.getProfileImageUri();
        if (savedImageUri != null) {
            Picasso.with(uploadProfilePicActivity.this).load(Uri.parse(savedImageUri)).into(imageView_Profile_dp);
        }

        // Gallery button click listener
        upload_pic_choose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        // Camera button click listener
        upload_pic_camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(uploadProfilePicActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    ActivityCompat.requestPermissions(uploadProfilePicActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                }
            }
        });

        // Upload button click listener
        upload_pic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                uploadPic();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            imageView_Profile_dp.setImageURI(uriImage);
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView_Profile_dp.setImageBitmap(photo);

            // Convert Bitmap to Uri
            uriImage = getImageUri(photo);
        }
    }

    private Uri getImageUri(Bitmap photo) {
        // Convert Bitmap to Uri for Firebase upload
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), photo, "profile_pic", null);
        return Uri.parse(path);
    }

    private void uploadPic() {
        if (uriImage != null) {
            final StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid() + "/profile_pic." + getFileExtension(uriImage));

            fileReference.putFile(uriImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    updateProfilePicture(downloadUri);
                                    // Save the image URI to SharedPreferences
                                    preferencesManager.saveProfileImageUri(downloadUri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(uploadProfilePicActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(uploadProfilePicActivity.this, "No file selected", Toast.LENGTH_LONG).show();
        }
    }

    private void updateProfilePicture(Uri downloadUri) {
        firebaseUser = authProfile.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(downloadUri)
                .build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(uploadProfilePicActivity.this, "Profile updated successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(uploadProfilePicActivity.this, UserProfileActivity.class);
                        intent.putExtra("imageUri", downloadUri.toString());
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(uploadProfilePicActivity.this, "Profile update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}