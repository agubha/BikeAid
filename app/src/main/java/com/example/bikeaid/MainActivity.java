package com.example.bikeaid;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bikeaid.Model.FireBaseUserModel;
import com.example.bikeaid.Utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ImageView slider_image, requestServiceImage, emergencyBreakDownImage, subscriptionPackageImage, bikeAccessoriesImage;
    private ConstraintLayout constraintLayout, constraintLayout2;
    private FirebaseAuth auth;
    private DatabaseReference mDatabaseRef;
    private ImageView imageView;
    private StorageReference mStorageRef;
    private Uri uri = null;
    private TextView username;
    private ConstraintLayout constraintLayout4;
    private ImageView iconPhone, iconYoutube, iconSetting, iconNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        ClickAction();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        //loadSideNavigationUserImageIfLoggedIn
        auth = FirebaseAuth.getInstance();
        View header = navigationView.getHeaderView(0);
        imageView = header.findViewById(R.id.imageViewsss);
        username = header.findViewById(R.id.userName);
        if (auth.getUid() != null) {
            Log.d("URI", "" + uri);
            //if Logged In Load User Image
            FirebaseStorage storage = FirebaseStorage.getInstance();
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("UserDetails").child(auth.getUid());
            mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    FireBaseUserModel firebaseUserModel = dataSnapshot.getValue(FireBaseUserModel.class);
                    if (firebaseUserModel != null) {
                        Picasso.get().load(firebaseUserModel.getUriUserImage()).into(imageView);
                        username.setText(firebaseUserModel.getUsername());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        //Slider Image
        slider_image = findViewById(R.id.slider_image);
        requestServiceImage = findViewById(R.id.reqSerimg);
        emergencyBreakDownImage = findViewById(R.id.emebredwnImg);
        subscriptionPackageImage = findViewById(R.id.subpakimg);
        bikeAccessoriesImage = findViewById(R.id.bikAccImg);

        //Emergency BreakDown
        constraintLayout = findViewById(R.id.emergency_break_down_layout);
        loadImages();
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoEmergencyBreakdown();
            }
        });
        constraintLayout4 = findViewById(R.id.constraintLayout4);
        constraintLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoRequestServicing();
            }
        });
        constraintLayout2 = findViewById(R.id.constraintLayout);
        constraintLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BuyProducts.class);
                startActivity(intent);
            }
        });

    }

    private void gotoRequestServicing() {
        Intent intent = new Intent(MainActivity.this, RequestServicing.class);
        startActivity(intent);
    }

    private void ClickAction() {
        iconPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri u = Uri.parse("tel:" + 984111111);
                Intent i = new Intent(Intent.ACTION_DIAL, u);
                try {
                    // Launch the Phone app's dialer with a phone
                    // number to dial a call.
                    startActivity(i);
                } catch (SecurityException s) {
                    // show() method display the toast with
                    // exception message.
                    Util.toast(s.getMessage(), MainActivity.this);
                }
            }
        });
        iconYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + "xchoFP4apQ4"));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + "xchoFP4apQ4"));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
            }
        });
        iconSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void findViews() {
        iconPhone = findViewById(R.id.iconPhone);
        iconYoutube = findViewById(R.id.iconYoutube);
        iconSetting = findViewById(R.id.iconSetting);
    }


    private void loadImages() {
        Picasso.get().load("https://the-drive-2.imgix.net/https%3A%2F%2Fs3.amazonaws.com%2Fthe-drive-staging%2Fmessage-editor%252F1520634822592-ninja400.jpg?auto=compress%2Cformat&ixlib=js-1.2.1&s=ca83f6d3d4f8aeebf93607d799658023")
                .into(slider_image);
        Picasso.get().load("https://www.argentum.org/wp-content/uploads/2017/01/Toolkit.png")
                .into(requestServiceImage);
        Picasso.get().load("https://previews.123rf.com/images/jovanas/jovanas1810/jovanas181001169/110421055-siren-icon-for-web-and-mobile-alarm-siren-vector-icon.jpg")
                .into(emergencyBreakDownImage);
        Picasso.get().load("https://cdn4.iconfinder.com/data/icons/small-n-flat/24/calendar-512.png")
                .into(subscriptionPackageImage);
        Picasso.get().load("https://thumbs.dreamstime.com/b/gift-box-logo-business-company-simple-logotype-idea-design-corporate-identity-concept-creative-icon-accessories-138038082.jpg")
                .into(bikeAccessoriesImage);

    }

    private void gotoEmergencyBreakdown() {
        Intent intent = new Intent(this, EmergencyBreakDown.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_user) {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
