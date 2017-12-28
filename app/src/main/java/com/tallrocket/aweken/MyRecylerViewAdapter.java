package com.tallrocket.aweken;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Venkat on 22-Oct-17.
 */

public class MyRecylerViewAdapter extends RecyclerView.Adapter<MyRecylerViewAdapter.ViewHolder> {

    private Context mContext = null;
    private ArrayList<RecentSearchPlace> mRecentPlaces;
    private Communicator mListener;
    private LayoutInflater mLayoutInflater;

    public MyRecylerViewAdapter(Context context, ArrayList<RecentSearchPlace> places, Communicator communicator) {
        mContext = context;
        mRecentPlaces = places;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener = communicator;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.places_item, parent, false);
        //  View view = mLayoutInflater.inflate(R.layout.places_item, parent);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.address_tv.setText(mRecentPlaces.get(position).getAddress());
        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.selectedPlace(position);
            }
        });
    }



    @Override
    public int getItemCount() {
        return mRecentPlaces.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView address_tv;
        LinearLayout mLinearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            address_tv = itemView.findViewById(R.id.place_name);
            mLinearLayout = itemView.findViewById(R.id.linearlayout);
        }
    }
}

