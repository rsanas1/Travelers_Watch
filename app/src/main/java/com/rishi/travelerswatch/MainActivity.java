package com.rishi.travelerswatch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int LOC_PERM = 5;
    LocationManager locationManager;
    LocationListener locationListener;
    Location mCurrentLocation;

    @BindView(R.id.latitudeTv) TextView latitude;
    @BindView(R.id.longitudeTv) TextView longitude;
    @BindView(R.id.accuracyTv) TextView accuracy;
    @BindView(R.id.altitudeTv) TextView altitude;
    @BindView(R.id.addressTv) TextView address;
    @BindView(R.id.navigateBt) Button navigate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateLocationInfo(location);


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {

            startListening();

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_PERM);
            } else {

                startListening();
            }
        }
    }

    public void updateLocationInfo(Location location){
        mCurrentLocation = location;
        Log.e("UPDATE","updateLocationInfo");

        latitude.setText(String.valueOf(location.getLatitude()));
        longitude.setText(String.valueOf(location.getLongitude()));
        accuracy.setText(String.valueOf(location.getAccuracy()));
        altitude.setText(String.valueOf(location.getAltitude()));

        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());


        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            if(addresses.size() > 0 && addresses !=null)
                        address.setText(getAddress(addresses));
            else
                address.setText("Location Not identified");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAddress(List<Address> addresses){


        Log.e("UPDATE","getAddress");
        StringBuilder sb=new StringBuilder();

        if(addresses!=null && addresses.size()>0){
            sb.append("Address :\n");
        }

        if (addresses.get(0).getSubThoroughfare() != null) {

            sb.append(addresses.get(0).getSubThoroughfare() + " ");

        }

        if (addresses.get(0).getThoroughfare() != null) {

            sb.append(addresses.get(0).getThoroughfare() + " ");

        }

        if (addresses.get(0).getLocality() != null) {

            sb.append(addresses.get(0).getLocality() + "\n");

        }

        if (addresses.get(0).getPostalCode() != null) {

            sb.append(addresses.get(0).getPostalCode() + "\n");

        }

        if (addresses.get(0).getCountryName() != null) {

            sb.append(addresses.get(0).getCountryName() + "\n");


        }

        return  sb.toString();
    }

    public void startListening(){
        Log.e("UPDATE","startListening");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location !=null)
                updateLocationInfo(location);


        }
    }

    @OnClick(R.id.navigateBt)
    public void submit(View view){
        Uri gmmIntentUri = Uri.parse("geo:"+ mCurrentLocation.getLatitude()+","+mCurrentLocation.getLongitude());
       // Uri gmmIntentUri = Uri.parse("google.navigation:q=" + mCurrentLocation.getLatitude()+","+mCurrentLocation.getLongitude());
        Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW,
                gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){

            startListening();


        }
    }
}
