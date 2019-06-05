package com.example.bikeaid;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.bikeaid.Model.OpenSourceConnection;
import com.example.bikeaid.Model.OpenSourceRouting.Geometry;
import com.example.bikeaid.Model.OpenSourceRouting.ResponsePath;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private LatLng latLng;
    private Geometry geometry = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //location changes tracker
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

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        Location mobileLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        if(mobileLocation!=null)
//        load(mobileLocation);
        //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    //UPDATE Map pointer to current location
    @Override
    public void onLocationChanged(Location location) {
        load(location);

    }

    private void load(Location location) {
        Log.v("onLocationChanged", "IN ON LOCATION CHANGE, lat=" + location.getLatitude() + ", lon=" + location.getLongitude());
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("My Location"));
        LatLng hospital = new LatLng(27.670103, 85.311188);

        mMap.addMarker(new MarkerOptions().position(hospital).title("Marker in hospital"));        /*Request parameters exceed the server configuration limits. The approximated route distance must not be greater than 6000000.0 meters.*/
        String start = latLng.longitude + "," + latLng.latitude;
        String end = "85.311188,27.670103";
        loadPath(start, end);


//        GeoApiContext context = new GeoApiContext.Builder()
//                .apiKey(getResources().getString(R.string.google_api_key))
//                .build();
//        String orign = "" + latLng.latitude + "," + latLng.longitude;
//
//
//        DirectionsApiRequest req = DirectionsApi.getDirections(context, orign, end);//STATIC FOR NOW
//        try {
//            DirectionsResult res = req.await();
//
//            //Loop through legs and steps to get encoded polylines of each step
//            if (res.routes != null && res.routes.length > 0) {
//                DirectionsRoute route = res.routes[0];
//
//                if (route.legs != null) {
//                    for (int i = 0; i < route.legs.length; i++) {
//                        DirectionsLeg leg = route.legs[i];
//                        if (leg.steps != null) {
//                            for (int j = 0; j < leg.steps.length; j++) {
//                                DirectionsStep step = leg.steps[j];
//                                if (step.steps != null && step.steps.length > 0) {
//                                    for (int k = 0; k < step.steps.length; k++) {
//                                        DirectionsStep step1 = step.steps[k];
//                                        EncodedPolyline points1 = step1.polyline;
//                                        if (points1 != null) {
//                                            //Decode polyline and add points to list of route coordinates
//                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
//                                            for (com.google.maps.model.LatLng coord1 : coords1) {
//                                                path.add(new LatLng(coord1.lat, coord1.lng));
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    EncodedPolyline points = step.polyline;
//                                    if (points != null) {
//                                        //Decode polyline and add points to list of route coordinates
//                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
//                                        for (com.google.maps.model.LatLng coord : coords) {
//                                            path.add(new LatLng(coord.lat, coord.lng));
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            Log.e("ERROR", ex.getLocalizedMessage());
//        }
//        //Draw the polyline
//        if (path.size() > 0) {
//            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
//            mMap.addPolyline(opts);
//        }
//
//        mMap.getUiSettings().setZoomControlsEnabled(true);

//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zaragoza, 6));
        locationManager.removeUpdates(this);
    }

    private void drawLine(List<LatLng> path) {
        if (path == null) {
            Log.e("Draw Line", "got null as parameters");
            return;
        }
        Log.d("PATH ", "" + path.size());

        Polyline line = mMap.addPolyline(new PolylineOptions().width(3).color(Color.RED));
        line.setPoints(path);
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
                    geometry = response.body().getFeatures().get(0).getGeometry();
                    Log.d("ONSUCCESS", "Running");
                    draw();
            }

            @Override
            public void onFailure(Call<ResponsePath> call, Throwable t) {
                Log.d("onFailure", "Running"+t.getMessage());

            }
        });
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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
