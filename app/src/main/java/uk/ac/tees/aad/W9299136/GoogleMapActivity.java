package uk.ac.tees.aad.W9299136;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;


import android.view.View;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.model.AutocompleteSessionToken;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;

import org.jetbrains.annotations.NotNull;

import uk.ac.tees.aad.W9299136.RouteLibrary.FetchURL;
import uk.ac.tees.aad.W9299136.RouteLibrary.TaskLoadedCallback;


public class GoogleMapActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        LocationListener, TaskLoadedCallback {


    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    View mapView;
    LocationManager locationManager;
    public static final int GPS = 101;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef, mUserRef;
    LocationRequest mLocationRequest;
    Polyline currentPolyline;

    FusedLocationProviderClient fusedLocationProviderClient;
    Dialog dialog;
    SupportMapFragment mapFragment;
    LatLng latLngCurrentLocation, latLngTargetLocation;
    AutocompleteSessionToken token;
    GoogleMap mGoogleMap;
    double selectLatitude1 = 0;
    double selectLatitude2 = 0;
    double selectLongitude1 = 0;
    double selectLongitude2 = 0;
    String placesName1;
    String placesName2;

    TextView firstLocationTextView, secondLocationTextView;
    ImageView imageViewAddFirst, imageViewAddSecond;
    static final int MY_PERMISSIONS_REQUEST_LOCATION = 111;

    CardView cardDirection;
    TextView estimateTime, lable;
    Button btnDirection;
    ImageView ImageViewCar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_google_map);

        firstLocationTextView = findViewById(R.id.firstLocationTextView);
        secondLocationTextView = findViewById(R.id.secondLocationTextView);
        imageViewAddFirst = findViewById(R.id.imageViewAddFirst);
        imageViewAddSecond = findViewById(R.id.imageViewAddSecond);
        cardDirection = findViewById(R.id.cardDirection);
        estimateTime = findViewById(R.id.estimateTime);
        lable = findViewById(R.id.lable);
        btnDirection = findViewById(R.id.btnDirection);
        ImageViewCar = findViewById(R.id.car);
        cardDirection.setVisibility(View.GONE);

        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawRoute();
            }
        });


        imageViewAddFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GoogleMapActivity.this, PlacesActivity.class);
                startActivityForResult(i, 1);
            }
        });

        imageViewAddSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GoogleMapActivity.this, PlacesActivity.class);
                startActivityForResult(i, 2);
            }
        });


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Location");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        dialog = new Dialog(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(GoogleMapActivity.this);

        getLocationPermisiion();
        getLocationUpdate();


    }

    private void DrawRoute() {
        if (selectLatitude1 == 0 || selectLongitude1 == 0) {
            Toast.makeText(this, "Select Current Location", Toast.LENGTH_SHORT).show();
        } else if (selectLatitude2 == 0 | selectLongitude2 == 0) {
            Toast.makeText(this, "Select Desctination Location", Toast.LENGTH_SHORT).show();
        } else {


            cardDirection.setVisibility(View.VISIBLE);
            btnDirection.setVisibility(View.GONE);
            ImageViewCar.setVisibility(View.VISIBLE);


            lable.setVisibility(View.VISIBLE);
            String time = getTimeTaken(new LatLng(selectLatitude1, selectLongitude2), new LatLng(selectLatitude2, selectLongitude2));
            estimateTime.setText(time);

            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(selectLatitude2, selectLongitude2), 10));

            new FetchURL(this).execute(getUrl(new LatLng(selectLatitude1, selectLongitude2), new LatLng(selectLatitude2, selectLongitude2)), "driving");


        }

    }

    public String getTimeTaken(LatLng source, LatLng dest) {
        double distance = SphericalUtil.computeDistanceBetween(source, dest);
        String KM;
        if (distance > 1000) {
           KM= (int) distance / 1000 + "KM";
        } else {
            KM=(int) distance + "M";
        }


        double kms = distance / 1000;

        double kms_per_min = 0.5;

        double mins_taken = kms / kms_per_min;

        int totalMinutes = (int) mins_taken;


        if (totalMinutes < 60) {
            return "" + totalMinutes + " mins";
        } else {
            String minutes = Integer.toString(totalMinutes % 60);
            minutes = minutes.length() == 1 ? "0" + minutes : minutes;
            return (totalMinutes / 60) + " hour " + minutes + "mins (" + KM+" )";
        }

    }

    private String getUrl(LatLng origin, LatLng dest) {
        Toast.makeText(this, "Call", Toast.LENGTH_SHORT).show();
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + "walking";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyB8Ba8J_tPh2BVjpMaFoUvb_NQ1NNaIiqw";
    }


    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        getLocationPermisiion();

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


    @Override
    public void onLocationChanged(@NonNull @NotNull Location location) {
        mLastLocation = location;
        selectLongitude1 = location.getLongitude();
        selectLatitude1 = location.getLatitude();

    }


    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }


    private void getLocationUpdate() {
        if (locationManager != null) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, GoogleMapActivity.this);
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, GoogleMapActivity.this);
            } else {
                mLocationRequest = LocationRequest.create();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(5000);
                mLocationRequest.setFastestInterval(2000);


                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(mLocationRequest);
                builder.setAlwaysShow(true);
                Task<LocationSettingsResponse> requestTask = LocationServices.getSettingsClient(getApplicationContext())
                        .checkLocationSettings(builder.build());
                requestTask.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        try {
                            LocationSettingsResponse result = task.getResult(ApiException.class);
                        } catch (ApiException e) {
//                            e.printStackTrace();
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            try {
                                resolvableApiException.startResolutionForResult(GoogleMapActivity.this, GPS);
                            } catch (IntentSender.SendIntentException sendIntentException) {
                                sendIntentException.printStackTrace();
                            }
                        }
                    }
                });


            }
        }
    }


    private void getLocationPermisiion() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                buildGoogleApiClient();
            } else {
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(GoogleMapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS) {
            switch (GPS) {
                case Activity.RESULT_OK:
                    Toast.makeText(this, "GPS is ON", Toast.LENGTH_SHORT).show();

                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(this, "GPS is required to use feture of this app", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {


//            estimateTime = findViewById(R.id.estimateTime);
//            lable = findViewById(R.id.lable);
//            btnDirection = findViewById(R.id.btnDirection);
//            ImageViewCar = findViewById(R.id.car);


                selectLatitude2 = data.getDoubleExtra("latitude", 0.0);
                selectLongitude2 = data.getDoubleExtra("longitude", 0.0);
                placesName2 = data.getStringExtra("placesName");
                secondLocationTextView.setText(placesName2);

                cardDirection.setVisibility(View.VISIBLE);
                estimateTime.setText(placesName2);
                btnDirection.setVisibility(View.VISIBLE);
                ImageViewCar.setVisibility(View.GONE);

            }
        }


        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                selectLatitude1 = data.getDoubleExtra("latitude", 0.0);
                selectLongitude1 = data.getDoubleExtra("longitude", 0.0);
                placesName1 = data.getStringExtra("placesName");
                firstLocationTextView.setText(placesName1);
            }

        }

        if (selectLongitude1 != 0 && selectLatitude2 != 0) {
            Toast.makeText(this, "Show Direction", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        getLocationUpdate();

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mGoogleMap.addPolyline((PolylineOptions) values[0]);
    }
}