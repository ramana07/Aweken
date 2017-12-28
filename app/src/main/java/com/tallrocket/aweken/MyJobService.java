package com.tallrocket.aweken;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import static android.content.ContentValues.TAG;


@TargetApi(Build.VERSION_CODES.N)
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService implements LocationListener {

    public static JobInfo JOB_INFO;
    //    private static final int LOCATION_INTERVAL = 10000;
//    private static final float LOCATION_DISTANCE = 10;
    public static boolean mIsJobFinished = false;

    static {
        JobInfo.Builder builder = new JobInfo.Builder(3,
                new ComponentName("com.tallrocket.aweken", MyJobService.class.getName()));

        builder.setBackoffCriteria(30000,JobInfo.BACKOFF_POLICY_EXPONENTIAL);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            builder.setPeriodic(20000);

        } else {
            builder.setMinimumLatency(20000);
        }
//        JobScheduler js = context.getSystemService(JobScheduler.class);*/
        JOB_INFO = builder.build();
    }

    final Handler mHandler = new Handler();

    final Runnable mWorker = new Runnable() {
        @Override
        public void run() {
//                Log.e("TAG","mWorker");

            scheduleJob(MyJobService.this);
            jobFinished(mRunningParams, false);
        }
    };

    JobParameters mRunningParams;
    private Location destination;
    //    private LocationManager mLocationManager;
//    private MyJobService mLocationListener;
    private Location mLastLocation = null;
    private LatLng mDest;
//    private GoogleApiClient mGoogleApiClient;
//    private LocationRequest mLocationRequest;

    public static void scheduleJob(Context context) {

        JobScheduler js = context.getSystemService(JobScheduler.class);


        try {
            if (js != null) {
                if (JOB_INFO != null)
                    js.schedule(JOB_INFO);
                else
                    Toast.makeText(context, "Job INfo required", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Job Schedular required", Toast.LENGTH_SHORT).show();
            }
            Log.e("TAG","scheduleJob");
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e.getMessage());

            return;
        }

        Toast.makeText(context, "scheduleJob", Toast.LENGTH_SHORT).show();

    }


    public static void cancelJob(Context context) {
        Log.e("TAG", "cancelJob");
        if (!isScheduled(context)) {
            Toast.makeText(context, "there is no job scheduled", Toast.LENGTH_SHORT).show();

        } else {
            JobScheduler js = context.getSystemService(JobScheduler.class);
            js.cancel(3);
            js.cancelAll();
            mIsJobFinished = true;

        }

    }

    @Override
    public boolean onStartJob(JobParameters params) {

        mRunningParams = params;
        Log.e("TAG", "onStartJob");
        Toast.makeText(this, "onStartJob", Toast.LENGTH_LONG).show();
        Location currentLocation = new Location(getLocation());
//        distance(currentLocation);
        double dist = distance(currentLocation);
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        double distance= Double.parseDouble((SP.getString("circle_range", String.valueOf(2000))));

        if (dist < distance) {
            Log.e("TAG","condition true");

            PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isScreenOn();
            PowerManager.WakeLock wl_cpu = null;


            if (!isScreenOn) {
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
                wl.acquire(10000);
                wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
                wl_cpu.acquire(10000);
            }
            Intent intent = new Intent(getApplicationContext(), com.tallrocket.aweken.Notification.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
            jobFinished(mRunningParams, false);
            MyJobService.cancelJob(this);

            if (wl_cpu != null) {
                wl_cpu.release();
            }
        } else {
            mHandler.postDelayed(mWorker, 10 * 1000);
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mHandler.removeCallbacks(mWorker);
        Log.e("TAG", "onStopJob");

        return true;
    }


    public double distance(Location current) {

        mDest = LocationHelper.getDestinationLatLngFromSP(getApplicationContext());
        Location destLocation = new Location("mDest");
        destLocation.setLatitude(mDest.latitude);
        destLocation.setLongitude(mDest.longitude);

        double distance = current.distanceTo(destLocation);
        Toast.makeText(this, "  " + distance + " meters", Toast.LENGTH_LONG).show();
        Log.e("TAG", "distance" + distance + "     " + current + "  " + destination);

        return distance;

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation.set(location);
        Log.e("TAG", "mLastLocation  " + mLastLocation);
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

    private Location getLocation() {
        if (mLastLocation != null) {
            return mLastLocation;
        } else {

            boolean gps_enabled;
            boolean network_enabled;
            LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.e("TAG", " gps_loc " + gps_enabled);

            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Location net_loc = null, gps_loc = null, finalLoc = null;

            PermissionChecker.checkPermission(this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION});
            if (gps_enabled) {
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Log.e("TAG", " gps_loc " + gps_loc);
            }
            if (network_enabled) {
                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (gps_loc != null && net_loc != null) {

                //smaller the number more accurate result will

                if (gps_loc.getAccuracy() < net_loc.getAccuracy())
                    finalLoc = net_loc;
                else
                    finalLoc = gps_loc;

                // I used this just to get an idea (if both avail, its upto you which you want to take as I've taken location with more accuracy)

            } else {

                if (gps_loc != null) {
                    finalLoc = gps_loc;
                    Log.e("TAG", " gps_loc " + finalLoc);
                } else if (net_loc != null) {
                    finalLoc = net_loc;
                    Log.e("TAG", "net_loc" + finalLoc);

                }
                return finalLoc;

            }
            return finalLoc;
        }
    }

    public static boolean isScheduled(Context context) {
        JobScheduler js = context.getSystemService(JobScheduler.class);
        List<JobInfo> jobs = js.getAllPendingJobs();
        Log.e("TAG","isScheduled");

        if (jobs == null) {
            return false;
        }
        for (int i = 0; i < jobs.size(); i++) {
            if (jobs.get(i).getId() == 3) {
                return true;
            }
        }
        return false;
    }

}



























































/*
package com.tallrocket.aweken;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

*/
/**
 * Created by Venkat Ganji on 26-09-2017.
 *//*


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class mainService extends JobService implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,ActivityCompat.OnRequestPermissionsResultCallback,
        PermissionResultCallback  {
    private static final int PLAY_SERVICES_REQUEST = 10;
    private Location mLastLocation=null;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {


        GoogleApiClient mGoogleApiClient;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());


        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }
}
*/
