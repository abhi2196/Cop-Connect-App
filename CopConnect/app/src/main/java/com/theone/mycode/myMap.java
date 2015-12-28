package com.theone.mycode;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class myMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double logi=0,lati=0;
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toast.makeText(getApplicationContext(),
                "Mark Position of Accident", Toast.LENGTH_LONG)
                .show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Button next=(Button)findViewById(R.id.button2);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lati==0 || logi==0)
                    Toast.makeText(getApplicationContext(),
                            "Mark Position of Accident", Toast.LENGTH_LONG)
                            .show();
                else{
                    Intent image = new Intent("image");
                    image.putExtra("lati", lati);
                    image.putExtra("logi", logi);

                    startActivity(image);
                }
            }
        });
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
        mMap.setMyLocationEnabled(true);
        // LatLng def = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
        // mMap.addMarker(new MarkerOptions().position(def).title("Latitude:\"+mMap.getMyLocation().getLatitude()+\"\\nLongitude:\"+mMap.getMyLocation().getLongitude()"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(def));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                logi=latLng.longitude;
                lati=latLng.latitude;
                markerOptions.title("Latitude :" + latLng.latitude + " Longitude :" + latLng.longitude);
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(markerOptions);
            }
        });
    }

    public void onSearch(View v) {
        EditText loc = (EditText) findViewById(R.id.ed_addr);
        String addr = loc.getText().toString();
        List<Address> addressList = null;
        int i = 5;
        if (addr != null || !addr.equals("")) {
            Geocoder g = new Geocoder(this);
            try {
                addressList = g.getFromLocationName(addr, i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(addressList.size()>0) {
                Address ad = addressList.get(0);
                LatLng lt = new LatLng(ad.getLatitude(), ad.getLongitude());
                mMap.addMarker(new MarkerOptions().position(lt).title("Latitude:" + ad.getLatitude() + "\nLongitude:" + ad.getLongitude()));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(lt));
            }
            else
                Toast.makeText(getApplicationContext(),
                        "Notthing Found.. try Again", Toast.LENGTH_SHORT)
                        .show();
        }



    }
}
