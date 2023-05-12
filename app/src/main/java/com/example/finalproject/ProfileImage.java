package com.example.finalproject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import de.hdodenhof.circleimageview.CircleImageView;

import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;


public class ProfileImage extends AppCompatActivity {

    private CircleImageView profileImageView;
    private Button closeButton, saveButton;
    Uri ImageUri;
    private String userId;


/*    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private Uri imageUri;
    private String myUri = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePicsRef;*/

    private FirebaseDatabase database;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);
        ActionBar actionBar = getSupportActionBar();

        // Set the background color of the action bar
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF"))); // Red color

        // Set other properties of the action bar as needed

        SpannableString s = new SpannableString("TravelEasy");
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#348881")), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(s);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.action_bar));
        }

        profileImageView = findViewById(R.id.profile_image);
        database = FirebaseDatabase.getInstance("https://finalproject-11004-default-rtdb.europe-west1.firebasedatabase.app/");
        firebaseStorage = FirebaseStorage.getInstance();
        closeButton = findViewById(R.id.btnClose);
        saveButton = findViewById(R.id.btnSave);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            userId = firebaseAuth.getCurrentUser().getUid();
        } else {
            // Handle authentication error
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide activity buttons
                closeButton.setVisibility(View.GONE);
                // Create and display the fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                ProfileFragment profileFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("imageUri", ImageUri.toString());
                profileFragment.setArguments(bundle);
                fragmentTransaction.replace(android.R.id.content, profileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId != null) {
                    StorageReference reference = firebaseStorage.getReference().child("users")
                            .child(userId)
                            .child("profile_picture")
                            .child(System.currentTimeMillis() + "");

                    reference.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUriString = uri.toString();
                                    database.getReference("users").child(userId).child("profile_picture").setValue(imageUriString)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(ProfileImage.this, "Profile picture saved successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ProfileImage.this, "Failed to save profile picture", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    });
                } else {
                    // Handle authentication error
                }
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
            }
        });
    }
    private void UploadImage() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 101);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(ProfileImage.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && resultCode == RESULT_OK){
            ImageUri = data.getData();
            profileImageView.setImageURI(ImageUri);
        }
    }
}
        /*mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://finalproject-11004-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users");
        storageProfilePicsRef = FirebaseStorage.getInstance("gs://finalproject-11004.appspot.com").getReference().child("Profile Picture");

        profileImageView = findViewById(R.id.profile_image);

        closeButton = findViewById(R.id.btnClose);
        saveButton = findViewById(R.id.btnSave);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                ProfileFragment profileFragment = new ProfileFragment();
                fragmentTransaction.replace(R.id.fragment_container, profileFragment);
                fragmentTransaction.commit();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override   
            public void onClick(View v) {
                uploadProfileImage();
            }

        });
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1).start(ProfileImage.this);
            }
        });
        getUserinfo();
    }

    private void getUserinfo() {
        databaseReference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount() > 0){
                    if(snapshot.hasChild("image")){
                        String image =  snapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
        }
        else {
            Toast.makeText(this, "Error,try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProfileImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Set your profile");
        progressDialog.setMessage("Please wait, while we are setting your data");
        progressDialog.show();

        if(imageUri != null && mAuth.getCurrentUser() != null){
            final StorageReference fileRef = storageProfilePicsRef
                    .child(mAuth.getCurrentUser().getUid()+ ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        myUri = downloadUrl.toString();

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("image", myUri);
                        databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
                        progressDialog.dismiss();
                    }
                }
            });
        }
        else{
            progressDialog.dismiss();
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }*/
