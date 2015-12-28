package com.example.abhishek.cop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.audiofx.BassBoost;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double logi=0,lati=0;
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if(!isConnectingToInternet())
         showAlertDialog(this,"Internet Connection","You Don't have internet connection");
        else {
            String provider = Settings.Secure.getString(getContentResolver()
                    ,Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if(!provider.equals("")){
                //GPS Enabled
                Toast.makeText(this, "GPS Enabled: " + provider,
                        Toast.LENGTH_LONG).show();
            }else{
                EnableGPS();

            }
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void EnableGPS() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS Settings Not Enabled !");
        alertDialog.setMessage("Click YES to change Settings");
        alertDialog.setIcon(R.mipmap.ic_launcher);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
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
                logi = latLng.longitude;
                lati = latLng.latitude;
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
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }
    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);



        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
