package com.example.bikeaid;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bikeaid.Model.FirebaseUserModel;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UserActivity extends AppCompatActivity {
    private static final int CHOOSE_IMAGE_USERIMAGE = 1;
    private static final int CHOOSE_IMAGE_NAGRITA = 2;
    private static final int CHOOSE_IMAGE_BLUEBOOK = 3;
    private FirebaseAuth auth;
    private Button btnUploadImage;
    private ImageView userImagePreview, blueBookPreview, nagritaPreview;
    private EditText editUserName;
    private String UserNameString;
    private Spinner bikeSpinner;
    private String bikeName;
    private Uri userImageUrl, nagritaImageUrl, blueBookImageUrl;
    private Uri userImageUrlResponse = null, nagritaImageUrlResponse = null, blueBookImageUrlResponse = null;
    private StorageTask mUploadTask;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    String[] arraySpinner = new String[]{
            "Yamaha FZ FI", "Bajaj V15", "Bajaj Pulsar NS 200", "Honda Navi", "Honda CB Hornet 160R", "Honda CRF250L", "Hero Splendor iSmart", "Royal Enfield Bullet Electra 350", "Royal Enfield Continental GT"
    };
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(UserActivity.this, LoginActivity.class));
            finish();
        }
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("UserDetails").child(auth.getUid());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("User Details");
        findViews();
        loadBikeDrop();

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    FirebaseUserModel firebaseUserModel = dataSnapshot.getValue(FirebaseUserModel.class);
                    if (firebaseUserModel != null) {
                        editUserName.setText(firebaseUserModel.getUsername());
                        int spinnerPosition = adapter.getPosition(firebaseUserModel.getBikeType());
                        bikeSpinner.setSelection(spinnerPosition);
//                    Picasso.get().load(firebaseUserModel.getUriNagrita()).centerCrop().resize(180, 180).into(nagritaPreview);
//                    Picasso.get().load(firebaseUserModel.getUriBlueBook()).centerCrop().resize(180, 180).into(blueBookPreview);
//                    Picasso.get().load(firebaseUserModel.getUsername()).centerCrop().resize(180, 180).into(nagritaPreview);
//                    Picasso.get().load(firebaseUserModel.getUsername()).fit().into(nagritaPreview);
//                    Picasso.get().load(firebaseUserModel.getUriBlueBook()).fit().into(blueBookPreview);
//                    Picasso.get().load(firebaseUserModel.getUsername()).fit().into(userImagePreview);
                        Log.d("IMAGE", "getUriUserImage" + firebaseUserModel.getUriUserImage());
                        Log.d("IMAGE", "getUriBlueBook" + firebaseUserModel.getUriBlueBook());
                        Log.d("IMAGE", "getUriNagrita" + firebaseUserModel.getUriNagrita());

                        Picasso.get().load(firebaseUserModel.getUriNagrita()).into(nagritaPreview);
                        Picasso.get().load(firebaseUserModel.getUriBlueBook()).into(blueBookPreview);
                        Picasso.get().load(firebaseUserModel.getUriUserImage()).into(userImagePreview);
                    } else
                        Toast.makeText(UserActivity.this, "Null Model Received", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(UserActivity.this, "Null dataSnapShop Received", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userImagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUserImage();
            }
        });

        blueBookPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBlueBookPreview();
            }
        });
        nagritaPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectNagritaImage();
            }
        });
        mStorageRef = storage.getReference().child("user_image_upload").child(auth.getUid());

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(UserActivity.this, "Upload in progress", Toast.LENGTH_LONG).show();
                } else {
                    getData();
                    if (uploadImage()) {
                        if (UserNameString.length() > 5) {
                            FirebaseUserModel firebaseUserModel = new FirebaseUserModel(UserNameString, userImageUrlResponse, nagritaImageUrlResponse, blueBookImageUrlResponse, bikeName);
                            mDatabaseRef.setValue(firebaseUserModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UserActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(UserActivity.this, "Failure!!!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
//
                        //add to database
                    }
                }
            }
        });


    }

    private void findViews() {
        bikeSpinner = findViewById(R.id.spinner_bike);
        editUserName = findViewById(R.id.editUserName);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        userImagePreview = findViewById(R.id.userImagePreview);
        blueBookPreview = findViewById(R.id.blueBookPreview);
        nagritaPreview = findViewById(R.id.nagritaPreview);
    }

    private void loadBikeDrop() {


        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bikeSpinner.setAdapter(adapter);
        bikeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bikeName = arraySpinner[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }


    private void getData() {
        UserNameString = editUserName.getText().toString();
    }

    private void selectNagritaImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CHOOSE_IMAGE_NAGRITA);
    }

    private void selectBlueBookPreview() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CHOOSE_IMAGE_BLUEBOOK);
    }

    private void selectUserImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, CHOOSE_IMAGE_USERIMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE_USERIMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            userImageUrl = data.getData();
            Picasso.get().load(userImageUrl).centerInside().resize(200, 200).into(userImagePreview);
        } else if (requestCode == CHOOSE_IMAGE_NAGRITA && resultCode == RESULT_OK && data != null && data.getData() != null) {
            nagritaImageUrl = data.getData();
            Picasso.get().load(nagritaImageUrl).centerInside().resize(200, 200).into(nagritaPreview);
        } else if (requestCode == CHOOSE_IMAGE_BLUEBOOK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            blueBookImageUrl = data.getData();
            Picasso.get().load(blueBookImageUrl).centerInside().resize(200, 200).into(blueBookPreview);
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private boolean status;

    private boolean uploadImage() {
        status = true;
        if (userImageUrl != null) {
            final StorageReference fileReference = mStorageRef.child(auth.getUid() + "-userImage");
            mUploadTask = fileReference.putFile(userImageUrl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    userImageUrlResponse = uri;
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            status = false;
                        }
                    });
        } else {
            Toast.makeText(UserActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
        if (nagritaImageUrl != null) {
            final StorageReference fileReference = mStorageRef.child(auth.getUid() + "-nagritaImage");

            mUploadTask = fileReference.putFile(nagritaImageUrl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    nagritaImageUrlResponse = uri;
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            status = false;
                        }
                    });
        } else {
            Toast.makeText(UserActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
        if (blueBookImageUrl != null) {
            final StorageReference fileReference = mStorageRef.child(auth.getUid() + "-blueBookImage");

            mUploadTask = fileReference.putFile(blueBookImageUrl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    blueBookImageUrlResponse = uri;

                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            status = false;
                        }
                    });
        } else {
            Toast.makeText(UserActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
        return status;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(UserActivity.this, LoginActivity.class));
            finish();
        }
    }
}
