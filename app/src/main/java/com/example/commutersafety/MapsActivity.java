package com.example.commutersafety;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private Button bt1;

    DatabaseReference myRef;

    private MarkerOptions options = new MarkerOptions();
    String TAG = "BHAVYA";
    FirebaseAuth mAuth;

    private ArrayList<LatLng> latlngs = new ArrayList<>();
    private ArrayList<String> ar1 = new ArrayList<>();
    private ArrayList<String> ar2 = new ArrayList<>();
    private ArrayList<String> ar3 = new ArrayList<>();

    LocationManager locationManager;
    int count = 0;

    private double global_latitude;
    private double global_longitude;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mAuth = FirebaseAuth.getInstance();

        bt1=findViewById(R.id.maps_markzone);
        bt1.setOnClickListener(this);

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        myRef = FirebaseDatabase.getInstance().getReference("Zones");
    }
    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        global_latitude = latitude;
        global_longitude = longitude;

        LatLng latLng = new LatLng(latitude, longitude);
        //mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        Log.d("Latitude:", "latitude");
        Log.d("Longitude:", "longitude");
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //getLocation();

            //buildAlertMessageNoGps();

            mMap.setMyLocationEnabled(true);
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            global_latitude = latitude;
            global_longitude = longitude;

            LatLng latLng = new LatLng(latitude, longitude);
            //mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            Log.d("Latitude:", "latitude");
            Log.d("Longitude:", "longitude");
            onLocationChanged(location);
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            return;
        }
        locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);

        /// Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    Zone zn = dataSnapshot1.getValue(Zone.class);
                    double lat = Double.parseDouble(zn.getZoneLat());
                    double longi = Double.parseDouble(zn.getZoneLong());

                    String dep = zn.getZoneTitle();
                    String prob = zn.getZoneData();
                    String zoneId = dataSnapshot.getKey();

                    Log.d("zondId",zoneId); // Print & Debug

                    LatLng sydney = new LatLng(lat,longi);
                    latlngs.add(sydney);
                    ar1.add(dep);
                    ar2.add(prob);

                    //Geofire object is used to read and write geo location data to your Firebase database and to create queries.
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ZoneGeoFire"); //.child(mAuth.getUid());
                    GeoFire geoFire = new GeoFire(ref);

                    geoFire.setLocation(zoneId, new GeoLocation(lat, longi), new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {
                                System.err.println("There was an error saving the location to GeoFire: " + error);
                            } else {
                                System.out.println("Location saved on server successfully!");
                            }
                        }
                    });

                    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(global_latitude, global_longitude), 0.3);

                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                        }

                        @Override
                        public void onKeyExited(String key) {
                            System.out.println(String.format("Key %s is no longer in the search area", key));
                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {
                            System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
                        }

                        @Override
                        public void onGeoQueryReady() {
                            System.out.println("All initial data has been loaded and events have been fired!");
                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            System.err.println("There was an error with this query: " + error);
                        }
                    });
                }

                for (LatLng point : latlngs) {
                    options.position(point);
                    options.title(ar1.get(count));
                    options.snippet(ar2.get(count));
                    mMap.addMarker(options);
                    count = count+1;
                    Log.d("HELLO",point.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng ln = marker.getPosition();
                double latti = ln.latitude;
                double longgi = ln.longitude;
                //System.out.println("BUNDLEE"+String.valueOf(latti));

                Bundle bundle = new Bundle();

                /*ar3.add(String.valueOf(latti));
                ar3.add(String.valueOf(longgi));*/

                bundle.putString("LATITUDE",String.valueOf(latti));
                bundle.putString("LONGITUDE",String.valueOf(longgi));

                Intent intent = new Intent(MapsActivity.this,ZoneView.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view==bt1){
            startActivity(new Intent(MapsActivity.this,ZoneInfo.class));
        }
    }
}