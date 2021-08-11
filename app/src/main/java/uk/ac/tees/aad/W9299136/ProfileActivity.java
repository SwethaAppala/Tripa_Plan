package uk.ac.tees.aad.W9299136;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.tees.aad.W9299136.Utills.Common;
import uk.ac.tees.aad.W9299136.Utills.CustomDialog;
import uk.ac.tees.aad.W9299136.Utills.User;

public class ProfileActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef;
    CircleImageView profileImage;
    EditText edUsername;
    EditText edEmail;
    EditText edAddress;
    StorageReference mStorageProfileImage;
    Button btnEditProfile;
    Uri imageUri;
    Button btnUpdateProfile;
    boolean editMode = false;
    ImageView back;
    User user;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 9999;
    private static final int IMAGE_PICKER_SELECT = 5555;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        InitVariable();

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMode) {
                    RemoveEditMode();
                    editMode = false;
                    finish();
                    startActivity(getIntent());
                } else {
                    EditMode();
                    editMode = true;
                }
            }
        });

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfile();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMode) {
                    SelectImageFromCamera();
                }
            }
        });
        LoadMyProfile();


    }

    private void LoadMyProfile() {
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(User.class);
                    Common.user = user;
                    AssignProfile();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void AssignProfile() {
        Picasso.get().load(user.getImage()).placeholder(R.drawable.user).into(profileImage);
        edUsername.setText(user.getUsername());
        edEmail.setText(user.getEmail());
        edAddress.setText(user.getAddress());
    }

    private void SelectImageFromCamera() {

        if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ProfileActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.TITLE, "Select Pic From Camera");
            contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Front Camera Pic");
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, IMAGE_PICKER_SELECT);
        }
    }

    private void UpdateProfile() {
        String username = edUsername.getText().toString();
        String email = edEmail.getText().toString();
        String address = edAddress.getText().toString();


        if (username.isEmpty()) {
            edUsername.setError("Please Enter Username");
            edUsername.requestFocus();
        } else if (address.isEmpty()) {
            edAddress.setError("Please Enter Address");
            edAddress.requestFocus();
        } else if (!email.matches(Common.emailPattern)) {
            edAddress.setError("Please Enter Email");
            edAddress.requestFocus();
        } else if (imageUri == null) {
            Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show();
        } else {
            CustomDialog customDialog = new CustomDialog(ProfileActivity.this);
            customDialog.ShowDialog("Updating");


            mStorageProfileImage.child("Images").child(mUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        mStorageProfileImage.child("Images").child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                HashMap hashMap = new HashMap();
                                hashMap.put("userID", mUser.getUid());
                                hashMap.put("username", username);
                                hashMap.put("email", email);
                                hashMap.put("address", address);
                                hashMap.put("image", uri.toString());

                                mUserRef.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task task) {
                                        if (task.isSuccessful()) {
                                            customDialog.DismissDialog();
                                            Toast.makeText(ProfileActivity.this, "Profile is Update Now", Toast.LENGTH_SHORT).show();
                                        } else {
                                            customDialog.DismissDialog();
                                            Toast.makeText(ProfileActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });

        }

    }

    private void EditMode() {

        profileImage.setEnabled(true);
        edUsername.setEnabled(true);
        edEmail.setEnabled(true);
        edAddress.setEnabled(true);
        btnEditProfile.setText("Cancel");
    }

    private void InitVariable() {
        profileImage = findViewById(R.id.profileImage);
        edUsername = findViewById(R.id.edUsername);
        edEmail = findViewById(R.id.edEmail);
        edAddress = findViewById(R.id.edAddress);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        back = findViewById(R.id.imageView);

        RemoveEditMode();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorageProfileImage = FirebaseStorage.getInstance().getReference().child("ProfileImages");
    }

    private void RemoveEditMode() {
        profileImage.setEnabled(false);
        edUsername.setEnabled(false);
        edEmail.setEnabled(false);
        edAddress.setEnabled(false);
        btnEditProfile.setText("Edit Profile");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_SELECT) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(ProfileActivity.this.getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}