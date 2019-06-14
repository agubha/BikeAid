package com.example.bikeaid;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bikeaid.DijkstraAlgorithm.Dijkstra;
import com.example.bikeaid.DijkstraAlgorithm.Edge;
import com.example.bikeaid.DijkstraAlgorithm.Vertex;
import com.example.bikeaid.Model.City.Cities;
import com.example.bikeaid.Model.CityPair.PairLists;
import com.example.bikeaid.Model.Locations;
import com.example.bikeaid.Model.OpenSourceConnection;
import com.example.bikeaid.Model.OpenSourceRouting.Geometry;
import com.example.bikeaid.Model.OpenSourceRouting.OverAllListOfRouting;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {
    boolean shouldStopLoop = false;
    Handler mHandler = new Handler();
    private GoogleMap mMap;
    private LocationManager locationManager;
    private static final long MIN_TIME = 3000;
    private static final float MIN_DISTANCE = 10;
    private LatLng latLng;
    private Geometry geometry = null;
    String start = null;
    String end = null;
    private Cities cities = null;
    private PairLists pairLists = null;
    private Marker currentmarker;
    private List<Marker> markerlist = new ArrayList<>();
    private Marker mClosestMarker;
    private int i = 0;
    private String name, img;
    private int id, price;
    private ConstraintLayout constraintLayout;
    private Polyline line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cities = loadcitiesJson();
        pairLists = loadpairjson();
        createLists();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setTitle("select Any Workshop");
        constraintLayout = findViewById(R.id.constraintLayout);
        if (getIntents()) {
            //intent from activity
            TextView textname = findViewById(R.id.title);
            TextView textPrie = findViewById(R.id.price);
            ImageView imageView = findViewById(R.id.imageView3);
            textname.setText(name);
            textPrie.setText("Rs. " + price);
            Picasso.get().load(img).into(imageView);


        } else
            constraintLayout.setVisibility(View.GONE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //location changes tracker
        //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
    }

    private boolean getIntents() {
        if (getIntent().hasExtra("id")) {
            id = getIntent().getIntExtra("id", 0);
            price = getIntent().getIntExtra("price", 0);
            name = getIntent().getStringExtra("name");
            img = getIntent().getStringExtra("img");
            return true;
        }
        return false;
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        Location mobileLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (mobileLocation != null)
            load(mobileLocation);
    }

    //UPDATE Map pointer to current location
    @Override
    public void onLocationChanged(Location location) {
        load(location);
    }


    private void load(Location location) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);
        currentmarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("My Location"));
        //OFLINE DATA LOADING
        List<LatLng> latLngList = new ArrayList<>();
        List<Locations> locationsList = createList();

        Log.d("LocationSize:", "" + locationsList.size());
        Log.d("LocationSize:", "" + locationsList.get(0).getLong() + locationsList.get(0).getLat());
        Marker[] marker = new Marker[locationsList.size()];
        for (int i = 0; i < locationsList.size(); i++) {
            double lat = Double.parseDouble(locationsList.get(i).getLat());
            double lon = Double.parseDouble(locationsList.get(i).getLong());
            LatLng hospital = new LatLng(lat, lon);
            marker[i] = createMarker(lat, lon, locationsList.get(i).getName(), "", R.drawable.ic_directions_bike_black_24dp);
            markerlist.add(mMap.addMarker(new MarkerOptions().position(hospital).title(locationsList.get(i).getName())));
            /*Request parameters exceed the server configuration limits. The approximated route distance must not be greater than 6000000.0 meters.*/
            latLngList.add(hospital);
            //29.699206,85.339565
            //STATIC FOR COLLEGE

//            start = 85.339565 + "," + 29.699206;//LONGITUDE, LATITUDe
            start = location.getLongitude() + "," + location.getLatitude();
            end = lon + "," + lat;
        }


        //online DATA LOADING
//        new loaddataevery5sec(start, end).execute();
        locationManager.removeUpdates(this);
    }

    private void createLists() {
        Gson gson = new Gson();
        Type type = new TypeToken<OverAllListOfRouting>() {
        }.getType();
        OverAllListOfRouting problemModels = gson.fromJson(loadjsonfromrawrouting(), type);

//        emergencyProblemListAdapter.setList(problemModels);
    }

    private String loadjsonfromrawrouting() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.workshoppathing);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        Log.d("OUTPUT", "1111" + json);
        return json;
    }

//    private class loaddataevery5sec extends AsyncTask<Void, Void, Void> {
//        private String start2;
//        private String[] end2;
//
//        public loaddataevery5sec(String start, String[] end) {
//            start2 = start;
//            end2 = end;
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            int i = 0;
//            do {
////                loadPath(start2, end2[i]);
//                i++;
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            } while (i < end2.length);
//            return null;
//        }
//    }

    protected Marker createMarker(double latitude, double longitude, String title, String snippet, int iconResID) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 9f));

        Marker myMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
        return myMarker;

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
        Log.d("OUTPUT", "" + json);
        return json;

    }

    private void drawLine(List<LatLng> path) {
        if (path == null) {
            Log.e("Draw Line", "got null as parameters");
            return;
        }


        line = mMap.addPolyline(new PolylineOptions().width(3).color(Color.RED));
        line.setPoints(path);
    }


    private void loadPath(String v, String v1) {
        Log.d("V1", "" + v);
        Log.d("v2", "" + v1);
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

    private Dijkstra dijkstra;

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (line != null)
            line.remove();
        LatLng selected = marker.getPosition();
        Toast.makeText(this, "" + marker.getTitle(), Toast.LENGTH_SHORT).show();
        end = selected.longitude + "," + selected.latitude;
//        loadPath(start, end);
        Dijkstra dijkstra = new Dijkstra();
        loadNearestMarker();

        for (int i = 0; i < cities.getLocation().size(); i++) {
            float lon = Float.parseFloat(cities.getLocation().get(i).getLon());
            float lat = Float.parseFloat(cities.getLocation().get(i).getLat());
            Vertex vertex = new Vertex(cities.getLocation().get(i).getName(), lat, lon);
            dijkstra.addVertex(vertex);
        }


        for (int i = 0; i < pairLists.getPairs().size(); i++) {
            dijkstra.addUndirectedEdge(pairLists.getPairs().get(i).getA(), pairLists.getPairs().get(i).getB(), pairLists.getPairs().get(i).getDis());
        }

        List<Edge> weightedPath = dijkstra.getDijkstraPath(mClosestMarker.getTitle(), marker.getTitle());
        i = 0;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (i < weightedPath.size()) {
                    String start = weightedPath.get(i).source.y + "," + weightedPath.get(i).source.x;
                    String end = weightedPath.get(i).target.y + "," + weightedPath.get(i).target.x;
                    loadPath(start, end);
                    i++;
                    Toast.makeText(MapsActivity.this, "Loaded", Toast.LENGTH_SHORT).show();
                }
                if (!shouldStopLoop) {
                    mHandler.postDelayed(this, 5000);
                }
            }
        };
        mHandler.post(runnable);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (i != weightedPath.size()) {
//                    String start = weightedPath.get(i).source.y + "," + weightedPath.get(i).source.x;
//                    String end = weightedPath.get(i).target.y + "," + weightedPath.get(i).target.x;
//                    loadPath(start, end);
//                    i++;
//                }
//            }
//        });

        return true;
    }


    private void loadNearestMarker() {
        double mindist = 0;
        double distance2;
        int pos = 0;
        for (int i = 0; i < cities.getLocation().size(); i++) {
            distance2 = distanceFrom(currentmarker.getPosition().latitude, currentmarker.getPosition().longitude, markerlist.get(i).getPosition().latitude, markerlist.get(i).getPosition().longitude);
            if (i == 0) mindist = distance2;
            else if (mindist > distance2) {
                mindist = distance2;
                pos = i;
            }

        }
        mClosestMarker = markerlist.get(pos);
        String v1 = cities.getLocation().get(pos).getLon() + "," + cities.getLocation().get(pos).getLat();
        String v2 = currentmarker.getPosition().longitude + "," + currentmarker.getPosition().latitude;
//        String v2 = "85.346500,27.685321";
        loadPath(v2, v1);
    }

    public double distanceFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        int meterConversion = 1609;
        return new Double(dist * meterConversion).floatValue();    // this will return distance
    }

    private Cities loadcitiesJson() {
        Gson gson = new Gson();
        Type type = new TypeToken<Cities>() {
        }.getType();
        return gson.fromJson(loadCityJson(), type);
    }

    private String loadCityJson() {
        String json;
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

    private PairLists loadpairjson() {
        Gson gson = new Gson();
        Type type = new TypeToken<PairLists>() {
        }.getType();
        return gson.fromJson(loadPairjsons(), type);
    }

    private String loadPairjsons() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.citypairs);
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

}
