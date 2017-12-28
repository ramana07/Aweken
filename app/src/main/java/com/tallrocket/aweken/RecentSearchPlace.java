package com.tallrocket.aweken;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by venkat on 22-Oct-17.
 */

public class RecentSearchPlace {
    String address;
    double lat;
    double lng;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {

        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
