package com.tallrocket.aweken;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Venkat on 06-10-2017.
 */

public class LocationHelper {

    public static LatLng getDestinationLatLngFromSP(Context context){
        SharedPreferences sf = PreferenceManager.getDefaultSharedPreferences(context);

        String lat = sf.getString("lat", "0.0");
        String lon = sf.getString("lon", "0.0");

        return new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
    }

    /*public static void clearDestinationLatLngFromSP(Context context){
        SharedPreferences sf = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor=sf.edit();
        editor.putString("lat", "0.0");
        editor.putString("lon", "0.0");
        editor.apply();
    }*/
}
