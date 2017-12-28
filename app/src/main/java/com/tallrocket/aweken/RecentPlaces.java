package com.tallrocket.aweken;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class RecentPlaces extends AppCompatActivity implements Communicator {

    RecyclerView mRecyclerView;
    private SQLiteAdapter mSqLiteAdapter;
    private ArrayList<RecentSearchPlace> mRecentSearchPlaces;
    private RecyclerView mRecylerView;

    Communicator mLisCommunicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_places);

//        View view = inflater.inflate(R.layout.places_item, container, false);
        mSqLiteAdapter = new SQLiteAdapter(RecentPlaces.this);
        mRecylerView = (RecyclerView) findViewById(R.id.recyler_view);
        mLisCommunicator = this;

        mSqLiteAdapter = new SQLiteAdapter(RecentPlaces.this);
//        mLisCommunicator = this;

        mRecentSearchPlaces = mSqLiteAdapter.getAllRecentPlaces();
        if (0 < mRecentSearchPlaces.size()) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.transition);

//            mRecylerView.setAnimation(animation);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RecentPlaces.this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecylerView.setLayoutManager(linearLayoutManager);
            MyRecylerViewAdapter myRecylerViewAdapter = new MyRecylerViewAdapter(RecentPlaces.this, mRecentSearchPlaces, mLisCommunicator);
            mRecylerView.setAdapter(myRecylerViewAdapter);
        } else {
            Toast.makeText(RecentPlaces.this, "No Recent Places", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void selectedPlace(int pos) {
        setLatLong(pos);

        Toast.makeText(RecentPlaces.this, "Selected Place " + mRecentSearchPlaces.get(pos).getAddress()+", "+ mRecentSearchPlaces.get(pos).getLat()+", "+ mRecentSearchPlaces.get(pos).getLng(), Toast.LENGTH_SHORT).show();
    }

    private void setLatLong(int pos) {
        LatLng latLng=new LatLng(mRecentSearchPlaces.get(pos).getLat(),mRecentSearchPlaces.get(pos).getLng());
//        LatLng latLng=mRecentSearchPlaces.get(pos).getLat();
        Toast.makeText(RecentPlaces.this,"  "+latLng,Toast.LENGTH_SHORT).show();
//        HomeA.getLatLong(latLng);
        SharedPreferences preferencesinRp  = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferencesinRp.edit();
        editor.putString("lat", String.valueOf(latLng.latitude));
        editor.putString("lon", String.valueOf(latLng.longitude));
        editor.apply();
//        HomeA.getLatLong();
    }

}

