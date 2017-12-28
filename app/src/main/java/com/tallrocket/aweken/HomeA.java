package com.tallrocket.aweken;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Locale;

import static com.tallrocket.aweken.R.id.map;

public class HomeA extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMyLocationButtonClickListener,GoogleMap.OnMarkerClickListener,GoogleMap.OnMarkerDragListener ,Communicator{

    private GoogleMap mMap;
    private static final int MY_LOCATION_REQUEST_CODE = 100;
    private EditText mSearchBox;
    private final String TAG = " ";
    private Location l6;
    public LatLng mDest = null, latLng2 = null;
    private Marker mMarker;
    private LatLng userLocation = null;
    private boolean mIsFrom = false;
    private Circle circle = null;
    Bitmap bitmap;
    Communicator mListener;
    private Place mPlace;
    ArrayList<RecentSearchPlace> mRecentSearchPlaces;
    private RecyclerView mRecylerView;
    private SQLiteAdapter mSqLiteAdapter;
    private RecentSearchPlace recentSearchPlace;
    private DrawerLayout drawer;
    private PolylineOptions polyline1;
    private Polyline polyline2;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mListener=this;
        mSqLiteAdapter = new SQLiteAdapter(HomeA.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
//        checkPlayServices();



        @SuppressLint("ResourceType") ImageView btnMyLocation = ((View) mapFragment.getView().findViewById(1).getParent()).findViewById(2);
        // btnMyLocation.setImageResource(R.mipmap.ic_location_circle);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                btnMyLocation.getLayoutParams();
        // position on right bottom
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 0, 150);


        @SuppressLint("ResourceType") ImageView compass = ((View) mapFragment.getView().findViewById(1).getParent()).findViewById(5);
        // btnMyLocation.setImageResource(R.mipmap.ic_location_circle);

        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams)
                compass.getLayoutParams();
        // position on right bottom
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
        layoutParams1.setMargins(0, 0, 100, 150);


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        mSearchBox = ((EditText) findViewById(R.id.place_autocomplete_search_input));
        mSearchBox.setMaxHeight(30);
        mSearchBox.setHint("Enter Destination");

//        ImageView searchImageView = findViewById(R.id.place_autocomplete_search_button);

        View view = autocompleteFragment.getView();
        view.setBackground(ContextCompat.getDrawable(HomeA.this, R.drawable.roundedcorner));
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
                mPlace=place;
                recentSearchPlace = new RecentSearchPlace();
                recentSearchPlace.setAddress(mPlace.getAddress().toString());
                recentSearchPlace.setLat(mPlace.getLatLng().latitude);
                recentSearchPlace.setLng(mPlace.getLatLng().longitude);
                Log.e("TAG",""+mPlace.getLatLng().longitude);

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
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        userLocation=null;
        mMap = googleMap;
        setMapType();
        try {

            boolean hasPermissions = PermissionChecker.checkPermission(HomeA.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
            if (!hasPermissions) {
                PermissionChecker.reqPermissions(HomeA.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, "Location");
                return;
            }


            mMap.setOnMapClickListener(this);
            mMap.setOnMarkerClickListener(this);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.getUiSettings().setMapToolbarEnabled(false);




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
        if (!mMap.isMyLocationEnabled()) {
//                Log.e("TAG", "isMyLocationEnabled  197  " + mMap.isMyLocationEnabled());
            mMap.setMyLocationEnabled(true);
//                Log.e("TAG", "isMyLocationEnabled  200  " + mMap.isMyLocationEnabled());
        }



        if (null != mMap) {
            if (mIsFrom) {
                mDest = LocationHelper.getDestinationLatLngFromSP(getApplicationContext());
                showMarker(mDest);
                mIsFrom = false;
            }
        }

    }

    private void getCurrentLocation() {

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
            Toast.makeText(HomeA.this, "Permissions Denied", Toast.LENGTH_SHORT).show();
        }

        if (null != mMap)
            onMapReady(mMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TAG", "onResume");

       setMapType();
       /* if (latLng2.latitude != 0.0 && latLng2.longitude != 0.0) {
            showMarker(latLng2);
        }*/
    }

    private void setMapType() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean k=SP.getBoolean("map_type",false);
        if (mMap!=null) {
            if(k)
            {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }else {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        }
    }

    /* *//* @Override
     protected void onResume() {
         super.onResume();
         Log.e("TAG","onResume");
         *//**//*latLng2=LocationHelper.getDestinationLatLngFromSP(MainActivity.this);
        if(latLng2.latitude!=0.0&&latLng2.longitude!=0.0){
            showMarker(latLng2);
        }*//**//*

    }
*/
    public void showMarker(LatLng hyd) {
//        Toast.makeText(MapsActivity.this, "in  showMarker  ", Toast.LENGTH_SHORT).show();

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        double distance= Double.parseDouble((SP.getString("circle_range", String.valueOf(2000))));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(hyd).include(userLocation);

        LatLngBounds bounds = builder.build();
        int p = 30;

        showCurvedPolyline(userLocation,hyd,0.3);
       /* polyline2 = mMap.addPolyline(new PolylineOptions()
                .width(100)

                .color(0x604A148C)
                .jointType(JointType.BEVEL)
                .add(
                        new LatLng(userLocation.latitude, userLocation.longitude),
                        new LatLng(hyd.latitude, hyd.longitude)
                        )
        );
*/

        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, p), 1500, null);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, p));

        if (circle != null) {
            circle.remove();
        }
        circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(hyd.latitude, hyd.longitude))
                .radius(distance)
                .strokeColor(0x9003A9F4)
                .strokeWidth(2)
                .fillColor(0x402196F3));

        if (null != mMarker)
            mMarker.remove();

        mMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).title("add alarm").position(hyd));

    }


    @Override
    public void onMapClick(LatLng latLng) {

        mDest = new LatLng(latLng.latitude, latLng.longitude);
//        getAddressFromLatLong(mDest);
        recentSearchPlace = new RecentSearchPlace();


        recentSearchPlace.setAddress(getAddressFromLatLong(mDest));
        recentSearchPlace.setLat(mDest.latitude);
        recentSearchPlace.setLng(mDest.longitude);
        Log.e("TAG",""+mDest.longitude);


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


    private String getAddressFromLatLong(LatLng latLng) {


        List<Address> addresses;
        try {
            Geocoder geocoder;
            geocoder = new Geocoder(HomeA.this, Locale.getDefault());
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String pl= null;
            try {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineInde:@)
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();


                Toast.makeText(HomeA.this, " " + address + " ," + city + " ," + state + " ," + country + " ," + postalCode + " ," + " ," + knownName, Toast.LENGTH_LONG).show();

                pl = city + " ," + state + " ," + country + " ," + postalCode;
                return pl;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

//            return pl;
        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }

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

    /*public void cancel(View view) {
        MyJobService.cancelJob(HomeA.this);
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(this, "onMarkerClick", Toast.LENGTH_SHORT).show();


        mSqLiteAdapter.addPlace(recentSearchPlace);

        if (mDest != null) {
            //LatLng m1 = getLocationFromAddress(MapsActivity.this, mDestination);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("lat", String.valueOf(mDest.latitude));
            editor.putString("lon", String.valueOf(mDest.longitude));
            editor.apply();
            //  Log.e("TAG", "jobScheduler");
            try {
                MyJobService.scheduleJob(HomeA.this);
//                Toast.makeText(this, "job was scheduled", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", " " + e);
            }


        } else {
            Toast.makeText(this, "Enter destination", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        switch (id) {
            case R.id.recent_places:
                Intent i =new Intent(HomeA.this,RecentPlaces.class);
                startActivity(i);
                break;
            case R.id.add_alram:
                Toast.makeText(HomeA.this,"There is add_alram pressed",Toast.LENGTH_SHORT).show();
                drawer.closeDrawers();
                mSearchBox.setFocusable(true);
                mSearchBox.setFocusableInTouchMode(true);
//                mSearchBox.
                break;
            case R.id.existing_alarm:
                getExistingAlarm();

                break;
            case R.id.settings:
                drawer.closeDrawers();
                Intent j =new Intent(HomeA.this,SettingsActivity.class);
                startActivity(j);
                break;

            case R.id.user_guide:

                break;

        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getExistingAlarm() {
        if(!MyJobService.isScheduled(getApplicationContext())){
            Toast.makeText(HomeA.this,"There is no alarm Scheduled",Toast.LENGTH_SHORT).show();
        }
        else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(HomeA.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Alarm Scheduled At");
            alertBuilder.setMessage(" "+getAddressFromLatLong(mDest));
            alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
                public void onClick(DialogInterface dialog, int which) {
//                    HomeA.this.requestPermissions(permissions, 100);//ActivityCompat.requestPermissions(context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},MY_LOCATION_REQUEST_CODE );
                }
            });
            alertBuilder.setNegativeButton("Cancel Alarm",new DialogInterface.OnClickListener() {
                @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
                public void onClick(DialogInterface dialog, int which) {
                    MyJobService.cancelJob(HomeA.this);
                    mMap.clear();

//                    HomeA.this.requestPermissions(permissions, 100);//ActivityCompat.requestPermissions(context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},MY_LOCATION_REQUEST_CODE );
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }

    }




    @Override
    public void selectedPlace(int pos) {

        mSearchBox.setText(mRecentSearchPlaces.get(pos).getAddress());
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tran);
        mRecylerView.setAnimation(animation);
        mRecylerView.setVisibility(View.GONE);
    }


   /* public static void getLatLong() {
        SharedPreferences sf = PreferenceManager.getDefaultSharedPreferences();

        String lat = sf.getString("lat", "0.0");
        String lon = sf.getString("lon", "0.0");
    }*/
   private void showCurvedPolyline (LatLng p1, LatLng p2, double k) {
       //Calculate distance and heading between two points
       double d = SphericalUtil.computeDistanceBetween(p1,p2);
       double h = SphericalUtil.computeHeading(p1, p2);

       //Midpoint position
       LatLng p = SphericalUtil.computeOffset(p1, d*0.5, h);

       //Apply some mathematics to calculate position of the circle center
       double x = (1-k*k)*d*0.5/(2*k);
       double r = (1+k*k)*d*0.5/(2*k);

       LatLng c;
       if(p1.longitude<p2.longitude) {
          c = SphericalUtil.computeOffset(p, x, h + 90.0);
       }else {
           c = SphericalUtil.computeOffset(p, x, h-90.0);
       }
       //Polyline options
       PolylineOptions options = new PolylineOptions();
       List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dash(30), new Gap(20));

       //Calculate heading between circle center and two points
       double h1 = SphericalUtil.computeHeading(c, p1);
       double h2 = SphericalUtil.computeHeading(c, p2);

       //Calculate positions of points on circle border and add them to polyline options
       int numpoints = 100;
       double step = (h2 -h1) / numpoints;

       for (int i=0; i < numpoints; i++) {
           LatLng pi = SphericalUtil.computeOffset(c, r, h1 + i * step);
           options.add(pi);
       }
       //Draw polyline

      mMap.clear();
       polyline1=options.width(10).color(Color.MAGENTA).geodesic(true).pattern(pattern);
       mMap.addPolyline(polyline1);
   }
   }
