package com.example.bikeaid;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bikeaid.Model.Locations;
import com.example.bikeaid.Model.OpenSourceConnection;
import com.example.bikeaid.Model.OpenSourceRouting.Geometry;
import com.example.bikeaid.Model.OpenSourceRouting.ResponsePath;
import com.example.bikeaid.Model.WorkShopLocation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CosineSimilarityMap extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private static final long MIN_TIME = 3000;
    private static final float MIN_DISTANCE = 10;
    private LatLng latLng;
    private Marker currentmarker;
    List<Locations> locationsList;
    String shopName;
    Boolean isService;
    Locations locations;
    private Geometry geometry = null;
    private Polyline line;

    private void getIntents() {
        if (getIntent().hasExtra("id")) {
            shopName = getIntent().getStringExtra("id");
        }
        if (getIntent().hasExtra("isService")) {
            isService = getIntent().getBooleanExtra("isService", false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntents();
        setContentView(R.layout.activity_cosine_similarity_map);
        locationsList = createList();
        for (Locations item : locationsList) {
            if (shopName.equals(item.getName())) {
                locations = item;
            }
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(CosineSimilarityMap.this);
    }

    private void addMarker() {
        double lat = Double.parseDouble(locations.getLat());
        double lon = Double.parseDouble(locations.getLon());
        LatLng latLng2 = new LatLng(lat, lon);


        mMap.addMarker(new MarkerOptions()
                .position(latLng2)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title(locations.getTitle()).snippet(locations.getName()));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getSnippet().equals(locations.getName())) {

                    showDetails();
                }
                return false;
            }
        });
        /*Request parameters exceed the server configuration limits. The approximated route distance must not be greater than 6000000.0 meters.*/
    }

    private void showDetails() {
        BottomSheetDialog bottomSheerDialog = new BottomSheetDialog(CosineSimilarityMap.this);
        View parentView = getLayoutInflater().inflate(R.layout.bottom_dialog, null);
        bottomSheerDialog.setContentView(parentView);
        bottomSheerDialog.show();
        ImageView workshopImage = bottomSheerDialog.findViewById(R.id.displayImage);
        TextView titleView = bottomSheerDialog.findViewById(R.id.displayTitle);
        TextView description = bottomSheerDialog.findViewById(R.id.description);
        TextView getDirection = bottomSheerDialog.findViewById(R.id.getDirection);
        Picasso.get().load(locations.getImage()).into(workshopImage);
        titleView.setText(locations.getTitle());
        description.setText(locations.getName());
        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String v1 = locations.getLon() + "," + locations.getLat();
                String v2 = currentmarker.getPosition().longitude + "," + currentmarker.getPosition().latitude;
                loadPath(v1, v2);
                bottomSheerDialog.dismiss();
            }
        });

        if (isService) {
            TextView bookService = bottomSheerDialog.findViewById(R.id.bookService);
            bookService.setVisibility(View.VISIBLE);
            bookService.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CosineSimilarityMap.this, "Service has been Booked\n" +
                            "We will contact you soon.", Toast.LENGTH_SHORT).show();
                    bottomSheerDialog.dismiss();

                }
            });

        }
    }


    private List<Locations> createList() {
        Gson gson = new Gson();
        Type type = new TypeToken<WorkShopLocation>() {
        }.getType();
        WorkShopLocation workShopLocations = gson.fromJson(loadJSONFromRaw(), type);
        return workShopLocations.getLocation();
    }

    public String loadJSONFromRaw() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.workshop_location);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }


    @Override
    public void onLocationChanged(Location location) {
        load(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMarkerClickListener(this);
        loadtracker();
    }

    private void loadtracker() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
//        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
//
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        } catch (Exception e) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            load(location);
        }
    }

    private void loadPath(String v, String v1) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openrouteservice.org/v2/directions/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OpenSourceConnection openSourceConnection = retrofit.create(OpenSourceConnection.class);
        Call<ResponsePath> requestPath = openSourceConnection
                .requestPath(getResources().getString(R.string.openSourceApiKey),
                        v,
                        v1);
        requestPath.enqueue(new Callback<ResponsePath>() {
            @Override
            public void onResponse(Call<ResponsePath> call, Response<ResponsePath> response) {
                if (response.isSuccessful()) {
                    geometry = response.body().getFeatures().get(0).getGeometry();
                    draw();
                }

            }

            @Override
            public void onFailure(Call<ResponsePath> call, Throwable t) {
                Log.d("onFailure", "Running" + t.getMessage());
            }
        });
    }


    private void drawLine(List<LatLng> path) {
        if (path == null) {
            Log.e("Draw Line", "got null as parameters");
            return;
        }


        line = mMap.addPolyline(new PolylineOptions().width(3).color(Color.RED));
        line.setPoints(path);
    }

    private void draw() {
        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList();
        for (int i = 0; i < geometry.getCoordinates().size(); i++) {
            try {
                LatLng latLng = new LatLng(geometry.getCoordinates().get(i).get(1), geometry.getCoordinates().get(i).get(0));
                if (!path.contains(latLng)) {
                    path.add(latLng);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        drawLine(path);
    }

    private void load(Location location) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);
        currentmarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                .title("My Location"));
        locationManager.removeUpdates(this);
        addMarker();

    }
}
