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

import com.example.bikeaid.Model.FireBaseUserModel;
import com.example.bikeaid.Model.Upload;
import com.example.bikeaid.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
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
    private Intent userImageData, nagritaImageData, bluebookData;
    private Uri userImageUrlResponse = null, nagritaImageUrlResponse = null, blueBookImageUrlResponse = null;
    private StorageTask<UploadTask.TaskSnapshot> mUploadTask;
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
                    FireBaseUserModel firebaseUserModel = dataSnapshot.getValue(FireBaseUserModel.class);
                    editUserName.setText(firebaseUserModel.getUsername());
                    int spinnerPosition = adapter.getPosition(firebaseUserModel.getBikeType());
                    bikeSpinner.setSelection(spinnerPosition);
                    Picasso.get().load(firebaseUserModel.getUriNagrita()).centerCrop().resize(nagritaPreview.getWidth(), nagritaPreview.getHeight()).into(nagritaPreview);
                    Picasso.get().load(firebaseUserModel.getUriBlueBook()).centerCrop().resize(blueBookPreview.getWidth(), blueBookPreview.getHeight()).into(blueBookPreview);
                    Picasso.get().load(firebaseUserModel.getUriUserImage()).centerCrop().resize(userImagePreview.getWidth(), userImagePreview.getHeight()).into(userImagePreview);
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

                    Log.d("Image Url", "1" + userImageUrlResponse);
                    Log.d("Image Url", "2" + nagritaImageUrlResponse);
                    Log.d("Image Url", "3" + blueBookImageUrlResponse);
                    if (UserNameString.length() > 5) {
                        FireBaseUserModel fireBaseUserModel =
                                new FireBaseUserModel(UserNameString, userImageUrlResponse, nagritaImageUrlResponse, blueBookImageUrlResponse, bikeName);

                        uploadImage(fireBaseUserModel);
                        mDatabaseRef.setValue(fireBaseUserModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UserActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(UserActivity.this, "Failure!!!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
//
                //add to database
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
            Picasso.get().load(userImageUrl).centerCrop().resize(userImagePreview.getWidth(), userImagePreview.getHeight()).into(userImagePreview);
        } else if (requestCode == CHOOSE_IMAGE_NAGRITA && resultCode == RESULT_OK && data != null && data.getData() != null) {
            nagritaImageUrl = data.getData();
            Picasso.get().load(nagritaImageUrl).centerCrop().resize(nagritaPreview.getWidth(), nagritaPreview.getHeight()).into(nagritaPreview);
        } else if (requestCode == CHOOSE_IMAGE_BLUEBOOK && resultCode == RESULT_OK && data != null && data.getData() != null) {
            blueBookImageUrl = data.getData();
            Picasso.get().load(blueBookImageUrl).centerCrop().resize(blueBookPreview.getWidth(), blueBookPreview.getHeight()).into(blueBookPreview);
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private boolean status;

    private boolean uploadImage(FireBaseUserModel fireBaseUserModel) {
        status = true;
        if (userImageUrl != null) {
            final StorageReference userImageUrlResponseref = mStorageRef.child(auth.getUid() + "-userImage");
            UploadTask uploadTask = userImageUrlResponseref.putFile(userImageUrl);
            uploadTask.addOnFailureListener(e -> {
                Util.toast(e.getLocalizedMessage(), UserActivity.this);
                status = false;
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {


                        userImageUrlResponseref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d("OnComplete1", "true" + uri);
                                userImageUrlResponse = uri;
                                fireBaseUserModel.setUriUserImage(userImageUrlResponse.toString());
                                mDatabaseRef.setValue(fireBaseUserModel);
                            }
                        });

                    }
//                Log.d("userImageUrlResponse", "" + userImageUrlResponse);
//                userImageUrlResponseref.getDownloadUrl().addOnSuccessListener(uri -> {
//                    userImageUrlResponse = uri;
//                    Log.d("userImageUrl", "" + userImageUrlResponse);
//                });
                }
            });
        }
        if (nagritaImageUrl != null) {
            final StorageReference nagritaImageUrlResponseref = mStorageRef.child(auth.getUid() + "-nagritaImage");

            mUploadTask = nagritaImageUrlResponseref.putFile(nagritaImageUrl);
            mUploadTask.addOnFailureListener(e -> {
                Util.toast(e.getLocalizedMessage(), UserActivity.this);
                status = false;
            }).addOnCompleteListener(taskSnapshot -> {
                if (taskSnapshot.isSuccessful()) {
                    nagritaImageUrlResponseref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("OnComplete1", "true" + uri);
                            nagritaImageUrlResponse = uri;
                            fireBaseUserModel.setUriNagrita(nagritaImageUrlResponse.toString());
                            mDatabaseRef.setValue(fireBaseUserModel);
                        }
                    });


                }
//                Log.d("nagritaImageUrlResponse", "" + nagritaImageUrlResponse);
//                nagritaImageUrlResponseref.getDownloadUrl().addOnSuccessListener(uri -> {
//                    nagritaImageUrlResponse = uri;
//                    Log.d("nagritaImageUrl-", "" + blueBookImageUrlResponse);
//                });
            });

        } else {
            Log.d("If Not Running", "nagritaImageUrl");
            Toast.makeText(UserActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
        if (blueBookImageUrl != null) {
            final StorageReference blueBookImageUrlResponseref = mStorageRef.child(auth.getUid() + "-blueBookImage");

            mUploadTask = blueBookImageUrlResponseref.putFile(blueBookImageUrl);
            mUploadTask.addOnFailureListener(e -> {
                Util.toast(e.getLocalizedMessage(), UserActivity.this);
                status = false;
            }).addOnCompleteListener(taskSnapshot -> {
                if (taskSnapshot.isSuccessful()) {
                    blueBookImageUrlResponseref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("OnComplete1", "true" + uri);
                            blueBookImageUrlResponse = uri;
                            fireBaseUserModel.setUriBlueBook(blueBookImageUrlResponse.toString());
                            mDatabaseRef.setValue(fireBaseUserModel);
                        }
                    });


                }
//                Util.toast(taskSnapshot.getMetadata().toString(), UserActivity.this);
//                blueBookImageUrlResponseref.getDownloadUrl().addOnSuccessListener(uri -> {
//                    blueBookImageUrlResponse = uri;
//                    Log.d("blueBookImageUrl-", "" + blueBookImageUrlResponse);
//                });
            });
        } else {
            Log.d("If Not Running", "blueBookImageUrl");
            Util.toast("No File Selected", UserActivity.this);
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
