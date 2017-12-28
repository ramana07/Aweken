package com.tallrocket.aweken;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//import static com.tallrocket.aweken.R.*;
import static com.tallrocket.aweken.R.drawable.roundedcorner;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationButtonClickListener {


    private GoogleMap mMap;
    private static final int MY_LOCATION_REQUEST_CODE = 100;
    private EditText mSearchBox;
    private final String TAG = " ";
    private Location l6;
    public LatLng mDest = null, latLng2 = null;
    private Marker mMarker;
    private LatLng userLocation = null;
    private boolean mIsFrom = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null != getIntent()) {
            mIsFrom = getIntent().getBooleanExtra("IsFrom", false);
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        checkPlayServices();


       /* ImageView btnMyLocation = ((View) mapFragment.getView().findViewById(1).getParent()).findViewById(2);
        // btnMyLocation.setImageResource(R.mipmap.ic_location_circle);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                btnMyLocation.getLayoutParams();
        // position on right bottom
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 0, 150);


        ImageView compass = ((View) mapFragment.getView().findViewById(1).getParent()).findViewById(5);
        // btnMyLocation.setImageResource(R.mipmap.ic_location_circle);

        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams)
                compass.getLayoutParams();
        // position on right bottom
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
        layoutParams1.setMargins(0, 0, 100, 150);

*/
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        mSearchBox = ((EditText) findViewById(R.id.place_autocomplete_search_input));
        mSearchBox.setMaxHeight(30);
        mSearchBox.setHint("Enter Destination");

//        ImageView searchImageView = findViewById(R.id.place_autocomplete_search_button);

        View view = autocompleteFragment.getView();
        view.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.roundedcorner));
        // searchImageView.setBackground(ContextCompat.getDrawable(MainActivity.this, drawable.roundedcorner));
        /*ImageView cancelImageView=findViewById(id.ca);
        cancelImageView.setVisibility(View.VISIBLE);
        cancelImageView.setBackground(ContextCompat.getDrawable(MainActivity.this, drawable.roundedcorner));*/
        // mSearchBox.setBackground(ContextCompat.getDrawable(MainActivity.this, drawable.roundedcorner));




        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPlaceSelected(Place place) {
               /* mDestination = place.getAddress().toString();
                getLocationFromAddress(MapsActivity.this, mDestination);
*/
                mDest = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                showMarker(mDest);
//                cameraCentral(mDest);

                // LatLng latLong=new LatLng(place.getLatLng().latitude,place.getLatLng().longitude);
                // TODO: Get info about the selected place.
                /*Log.i(TAG, "Place: " + place.getName());
                mJobSchedular.schedule(MyService.createJob(MapsActivity.this));
*/

            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }

        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {

            boolean hasPermissions = PermissionChecker.checkPermission(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
            if (!hasPermissions) {
                PermissionChecker.reqPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, "Location");
                return;
            }


            mMap.setOnMapClickListener(this);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.getUiSettings().setMapToolbarEnabled(false);


            if (mMap.isMyLocationEnabled() != true) {
//                Log.e("TAG", "isMyLocationEnabled  197  " + mMap.isMyLocationEnabled());
                mMap.setMyLocationEnabled(true);
//                Log.e("TAG", "isMyLocationEnabled  200  " + mMap.isMyLocationEnabled());
            }


            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);


            if (myLocation == null) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                String provider = lm.getBestProvider(criteria, true);
                myLocation = lm.getLastKnownLocation(provider);
            }

            if (myLocation != null) {
                userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                Log.e("TAG", "GPS_PROVIDER  " + myLocation);
                cameraCentral(userLocation);
//                Log.e("TAG", "userLocation    " + userLocation);
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14), 1500, null);
                //                showMarker(userLocation, "Current Place");
            } else {

                myLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.e("TAG", "NETWORK_PROVIDER  " + myLocation);

                if (myLocation == null) {
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    String provider = lm.getBestProvider(criteria, true);
                    myLocation = lm.getLastKnownLocation(provider);
                }

                if (myLocation != null) {
                    userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                    cameraCentral(userLocation);
                }
            }

//            mMap.setMyLocationEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "Exception onReady " + e.getMessage());
        }




        if (null != mMap) {
            if (mIsFrom) {
                mDest = LocationHelper.getDestinationLatLngFromSP(getApplicationContext());
                showMarker(mDest);
                mIsFrom = false;
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;
        switch (requestCode) {
            case 100:
                for (int i : grantResults) {
                    allowed = allowed && (i == PackageManager.PERMISSION_GRANTED);
                }
                break;

            default:
                allowed = false;
                break;
        }

        if (!allowed) {
            Toast.makeText(MainActivity.this, "Permissions Denied", Toast.LENGTH_SHORT).show();
        }

        if (null != mMap)
            onMapReady(mMap);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TAG","onResume");
        /*latLng2=LocationHelper.getDestinationLatLngFromSP(MainActivity.this);
        if(latLng2.latitude!=0.0&&latLng2.longitude!=0.0){
            showMarker(latLng2);
        }*/

    }

    public void showMarker(LatLng hyd) {
//        Toast.makeText(MapsActivity.this, "in  showMarker  ", Toast.LENGTH_SHORT).show();
        if (null != mMarker)
            mMarker.remove();
        mMarker = mMap.addMarker(new MarkerOptions().position(hyd));
        cameraCentral(hyd);
    }



    @Override
    public void onMapClick(LatLng latLng) {

        mDest = new LatLng(latLng.latitude, latLng.longitude);
        getAddressFromLatLong(mDest);
//        getCompleteAddressString(mDestlt.latitude,mDestlt.longitude);

        showMarker(latLng/*, 15, city + " " + steer + " " + country + "lat " + latLng.latitude*/);

//        try {
//            List<Address> addresses = gecGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 10);
//            try {
//                String country = addresses.get(0).getCountryName();
//                String city = addresses.get(0).getAdminArea();
//                String steer = addresses.get(0).getLocality();
//                Log.e(TAG, "country" + country + " city " + city + " steer" + steer);
//                Toast.makeText(MapsActivity.this,"in onMapClick ",Toast.LENGTH_SHORT).show();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    private void getAddressFromLatLong(LatLng latLng) {


        List<Address> addresses;
        try {
            Geocoder geocoder;
            geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineInde:@)
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            Toast.makeText(MainActivity.this, " " + address + " ," + city + " ," + state + " ," + country + " ," + postalCode + " ," + " ," + knownName, Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        }


        // return addresses.get(o).getCountryName()+""+addresses.get(o).getLocality();
    }


    @Override
    public boolean onMyLocationButtonClick() {
        cameraCentral(userLocation);
//        Log.e("TAG","   "+userLocation);
        return true;
    }


    public void cameraCentral(LatLng l2) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(l2).zoom(17).tilt(45).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
//        Log.e("TAG", "cameraCentral" + l2);

    }


  /*  public void schedule(View view) {
        if (mDest != null) {
            //LatLng m1 = getLocationFromAddress(MapsActivity.this, mDestination);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("lat", String.valueOf(mDest.latitude));
            editor.putString("lon", String.valueOf(mDest.longitude));
            editor.apply();
            //  Log.e("TAG", "jobScheduler");
            try {
                MyJobService.scheduleJob(MainActivity.this);
//                Toast.makeText(this, "job was scheduled", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", " " + e);
            }


        } else {
            Toast.makeText(this, "Enter destination", Toast.LENGTH_SHORT).show();
        }

    }*/


   /* public void cancel(View view) {
        MyJobService.cancelJob(MainActivity.this);
    }*/



    @Override
    protected void onStart() {
        super.onStart();
        Log.e("TAG","onStart");

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("TAG","onRestart");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("TAG","onStop");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("TAG","onPause");

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("TAG","onDestroy");
    }

}