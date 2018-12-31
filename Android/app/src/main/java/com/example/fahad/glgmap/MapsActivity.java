package com.example.fahad.glgmap;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.icu.util.ULocale.Category.DISPLAY;
import static android.os.Build.BOARD;
import static android.os.Build.BOOTLOADER;
import static android.os.Build.BRAND;
import static android.os.Build.DEVICE;
import static android.os.Build.FINGERPRINT;
import static android.os.Build.HARDWARE;
import static android.os.Build.HOST;
import static android.os.Build.ID;
import static android.os.Build.MANUFACTURER;
import static android.os.Build.MODEL;
import static android.os.Build.PRODUCT;
import static android.os.Build.SERIAL;
import static android.os.Build.TAGS;
import static java.lang.Double.TYPE;
import static java.sql.Types.TIME;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String	BOARD = Build.BOARD;
    public static final String	BOOTLOADER = Build.BOOTLOADER;
    public static final String	BRAND	= Build.BRAND;
    //public static final String	CPU_ABI = Build.CPU_ABI;
    //public static final String	CPU_ABI2 = Build.CPU_ABI2;
    public static final String	DEVICE	= Build.DEVICE;
    public static final String	DISPLAY	= Build.DISPLAY;
    public static final String	FINGERPRINT	= Build.FINGERPRINT;
    public static final String	HARDWARE = Build.HARDWARE;
    public static final String	HOST = Build.HOST;
    public static final String	ID = Build.ID;
    public static final String	MANUFACTURER = Build.MANUFACTURER;
    public static final String	MODEL = Build.MODEL;
    public static final String	PRODUCT = Build.PRODUCT;
    /*public static final String	RADIO = Build.getRadioVersion();*/
    public static final String	SERIAL = Build.SERIAL;
    /* public static final String[]	SUPPORTED_32_BIT_ABIS = Build.SUPPORTED_32_BIT_ABIS;
     public static final String[]	SUPPORTED_64_BIT_ABIS = Build.SUPPORTED_64_BIT_ABIS;
     public static final String[]	SUPPORTED_ABIS	= Build.SUPPORTED_ABIS;*/
    public static final String	TAGS = Build.TAGS;
    public static final long	TIME = Build.TIME;
    public static final String	TYPE = Build.TYPE;
    public static final String	USER = Build.USER;
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 10000;
    double latitude,longitude;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = RESTAPI.class.getSimpleName();

    public static double lat, lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        new AsyncTaskParseJson().execute();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED)
                    {
                        if(client == null)
                        {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied" , Toast.LENGTH_LONG).show();
                }
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        client.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }

    }

    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection Suspended!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed!", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onLocationChanged(Location location) {
        lastlocation = location;
        lat = location.getLatitude();
        lng = location.getLongitude();
        if(currentLocationmMarker != null)
        {
            currentLocationmMarker.remove();
        }


        LatLng latLng = new LatLng(location.getLatitude() , location.getLongitude());
       /* MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);*/
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if(client != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }

        new HttpAsyncTask().execute("https://glcn.herokuapp.com/api");

    }

    public static String POST(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            /*JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("appId", person.getName());
            jsonObject.accumulate("userName", person.getCountry());
            jsonObject.accumulate("appOnlineFlag", person.getTwitter());*/

            JSONObject jsonObject = new JSONObject();

            /*jsonObject.accumulate("board", BOARD);
            jsonObject.accumulate("bootloader", BOOTLOADER);
            jsonObject.accumulate("brand", BRAND);
            jsonObject.accumulate("device", DEVICE);
            jsonObject.accumulate("display", DISPLAY);
            jsonObject.accumulate("fingerprint", FINGERPRINT);
            jsonObject.accumulate("hardware", HARDWARE);
            jsonObject.accumulate("host", HOST);
            jsonObject.accumulate("id", ID);
            jsonObject.accumulate("manufacturer", MANUFACTURER);
            jsonObject.accumulate("model", MODEL);
            jsonObject.accumulate("product", PRODUCT);
            jsonObject.accumulate("serial", SERIAL);
            jsonObject.accumulate("tags", TAGS);
            jsonObject.accumulate("time", TIME);
            jsonObject.accumulate("type", TYPE);*/

            // jsonObject.accumulate("user", USER);

            jsonObject.accumulate("place", "Current Loc!" );
            jsonObject.accumulate("lat", lat);
            jsonObject.accumulate("lng", lng);
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    /*public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }*/

    /*@Override
    public void onClick(View view) {

        person = new Person();
        person.setName(etName.getText().toString());
        person.setCountry(etCountry.getText().toString());
        person.setTwitter(etTwitter.getText().toString());

        switch(view.getId()){
            case R.id.btnPost:
               *//* if(!validate())
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();*//*
                // call AsynTask to perform network operation on separate thread
                new HttpAsyncTask().execute("https://glcn.herokuapp.com/api");

                //new HttpAsyncTask().execute("http://requestb.in/11yd96i1");

                break;
        }

    }*/

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {



            return POST(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    /*@Override
    *//*public void onStart() {
        super.onStart();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }*//*

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    *//*protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }*/

}
