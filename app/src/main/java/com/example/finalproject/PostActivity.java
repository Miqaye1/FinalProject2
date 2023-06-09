package com.example.finalproject;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {
    TextView description;
    ImageView uploadbtn, productImage;
    Button submit;
    Uri ImageUri;
    RelativeLayout relativeLayout;
    private FirebaseDatabase database;
    private FirebaseStorage firebaseStorage;
    private ProgressBar progressBar;
    private TagsInputEditText tagsInputEditText;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);
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

        tagsInputEditText = findViewById(R.id.tagsET);
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String userId2 = currentUser.getUid();
        database = FirebaseDatabase.getInstance("https://finalproject-11004-default-rtdb.europe-west1.firebasedatabase.app/");
        firebaseStorage = FirebaseStorage.getInstance();
        description = findViewById(R.id.description);
        description = findViewById(R.id.textInputEditText);
        uploadbtn = findViewById(R.id.uploadbtn);
        productImage = findViewById(R.id.productImage);
        progressBar = findViewById(R.id.progressBar);
        submit = findViewById(R.id.postbtn);
        relativeLayout = findViewById(R.id.relative);
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
                relativeLayout.setVisibility(View.VISIBLE);
                uploadbtn.setVisibility(View.GONE);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    final String userId = currentUser.getUid();
                    final StorageReference reference = firebaseStorage.getReference().child("post")
                            .child(System.currentTimeMillis() + "");

                    // Check if an image is selected
                    if (ImageUri == null) {
                        Toast.makeText(PostActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    Bitmap bitmap = ((BitmapDrawable) productImage.getDrawable()).getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] imageByte = stream.toByteArray();
                    reference.putBytes(imageByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    ProjectModel model = new ProjectModel();
                                    model.setProductImage(uri.toString());

                                    // Check if a description is entered
                                    String descriptionText = description.getText().toString().trim();
                                    if (!descriptionText.isEmpty()) {
                                        model.setDescription(descriptionText);
                                    }

                                    model.setUserId(userId);
                                    String tagsString = tagsInputEditText.getText().toString().trim();

                                    // Check if tags are entered
                                    if (!tagsString.isEmpty()) {
                                        String[] tagsArray = tagsString.split(" ");

                                        // Create a HashMap to store the tags
                                        Map<String, Boolean> tagsMap = new HashMap<>();
                                        for (String tag : tagsArray) {
                                            // Add each tag to the HashMap
                                            tagsMap.put(tag, true);
                                        }
                                        // Set the tags in the model
                                        model.setTags(tagsMap);
                                    }

                                    DatabaseReference userPostRef = database.getReference("users")
                                            .child(userId)
                                            .child("post");
                                    String postKey = userPostRef.push().getKey();
                                    model.setPostId(postKey);
                                    userPostRef.child(postKey).setValue(model)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(PostActivity.this, "Posted Successfully", Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    Intent intent = new Intent(PostActivity.this, MainActivity.class);
                                                    intent.putExtra("fragmentToLoad", "home_fragment");
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(PostActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PostActivity.this, "Error retrieving download URL", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(PostActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                }
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
                        Toast.makeText(PostActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
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
            if(data != null){
                ImageUri = data.getData();
                try {
                    Bitmap original = MediaStore.Images.Media.getBitmap(getContentResolver(),ImageUri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    original.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    productImage.setImageBitmap(original);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}