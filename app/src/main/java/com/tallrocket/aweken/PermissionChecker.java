package com.tallrocket.aweken;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;

/**
 * Created by administrator1 on 23-Feb-17.
 */

public class PermissionChecker {


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context,String[] permissions)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            int res = 0;
            for (String str : permissions) {

                res = context.checkCallingOrSelfPermission(str);

                if (!(res == PackageManager.PERMISSION_GRANTED)) {
                    return false;
                }
            }
            return true;


        } else {
            return true;
        }
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void reqPermissions(final Activity context, final String[] permissions, String location) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("Permission necessary");
            alertBuilder.setMessage(location+" permission is required");
            alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.M)
                public void onClick(DialogInterface dialog, int which) {
                    context.requestPermissions(permissions, 100);//ActivityCompat.requestPermissions(context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},MY_LOCATION_REQUEST_CODE );
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();

        }
    }
}