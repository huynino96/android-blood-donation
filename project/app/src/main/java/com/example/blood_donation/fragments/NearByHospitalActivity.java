package com.example.blood_donation.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.blood_donation.R;
import com.example.blood_donation.model.GetNearbyLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

// Query and show nearby hospitals on GoogleMap
public class NearByHospitalActivity extends Fragment implements
        OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    View view;
    private GoogleApiClient client;
    Location lastLocation;
    private static final int Permission_Request = 99;
    int PROXIMITY_RADIUS = 1000;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.near_by_hospitals, container, false);
        getActivity().setTitle("Nearest Hospitals");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        // Different check permissions for API > 25
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.gMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        else {
            Toast.makeText(getActivity(), "MapFragment is null, why?", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Permission_Request) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (client == null) {
                        buildGoogleApiClient();
                    }
                    mMap.setMyLocationEnabled(true);
                    mMap.setTrafficEnabled(true);
                }
            } else {
                Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
//            mMap.setTrafficEnabled(true);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(getActivity().getApplicationContext()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "You need to enable permissions to display location!", Toast.LENGTH_SHORT).show();
        }

        // If location permission granted, show hospitals according to user location
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                ShowHospitals(location.getLatitude(), location.getLongitude());
            }
        });

    }

    // Query for nearby places (within radius)
    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=").append(latitude).append(",").append(longitude);
        googlePlaceUrl.append("&radius=").append(PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type=").append(nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=" + "AIzaSyCKU2tDG3mIREWPRkglPD1Jv8dsTmLM0Go");

        Log.d("NearbyHospitalActivity", "url = " + googlePlaceUrl.toString());
        return googlePlaceUrl.toString();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Permission_Request);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Permission_Request);
            }
            return false;

        } else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // Update hospital location marker
    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16f);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(client!=null) {
            client.connect();
        }
    }

    // Get hospital information from Google Places API
    public void ShowHospitals(double latitude, double longitude) {
        mMap.clear();
        Object[] dataTransfer = new Object[2];
        GetNearbyLocation getNearbyLocation = new GetNearbyLocation();
        String url = getUrl(latitude, longitude, "hospital");
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        getNearbyLocation.execute(dataTransfer);
        Toast.makeText(getContext(), "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
    }
}
