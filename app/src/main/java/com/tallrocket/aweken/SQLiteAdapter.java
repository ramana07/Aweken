package com.tallrocket.aweken;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static com.tallrocket.aweken.SQLiteAdapter.SQLiteHelper.ADDRESS;
import static com.tallrocket.aweken.SQLiteAdapter.SQLiteHelper.LATTITUDE;
import static com.tallrocket.aweken.SQLiteAdapter.SQLiteHelper.LONGITUDE;
import static com.tallrocket.aweken.SQLiteAdapter.SQLiteHelper.TABLE_NAME;
import static com.tallrocket.aweken.SQLiteAdapter.SQLiteHelper.UID;

/**
 * Created by Venkat on 18-10-2017.
 */

public class SQLiteAdapter {


    private SQLiteHelper mSQLiteHelper;

    public SQLiteAdapter(Context context) {
        mSQLiteHelper = new SQLiteHelper(context);
    }

    public long addPlace(RecentSearchPlace recentSearchPlace) {
        long sucessId = 0;
        try {
            SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(ADDRESS, recentSearchPlace.getAddress());
            values.put(UID, System.currentTimeMillis());
            values.put(LATTITUDE, recentSearchPlace.getLat());
            values.put(LONGITUDE, recentSearchPlace.getLng());

            sucessId = db.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.e("TAG","Exception"+e.getMessage());
        }
        return sucessId;
    }

    public ArrayList<RecentSearchPlace> getAllRecentPlaces() {

        ArrayList<RecentSearchPlace> places = null;
        try {
            SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

       /* Cursor cursor = db.query(TABLE_NAME, new String[]{ADDRESS, LATTITUDE,LONGITUDE},
                null, null, null, null, null);
*/

            String selectQuery = "SELECT  * FROM " + TABLE_NAME + "  ORDER BY " + UID + " DESC LIMIT 5";
            Cursor cursor = db.rawQuery(selectQuery, null);
            places = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    RecentSearchPlace place = new RecentSearchPlace();
                    place.setAddress(cursor.getString(1));
                    place.setLat(Double.parseDouble(cursor.getString(2)));
                    place.setLng(Double.parseDouble(cursor.getString(3)));
                    places.add(place);
                } while (cursor.moveToNext());
            }
        } catch (NumberFormatException e) {
            Log.e("TAG","NumberFormatException"+e.getMessage());
        }
        return places;
    }

    static class SQLiteHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "pdb";
        public static final String TABLE_NAME = "place";
        private static final int DTABASE_VERSION = 1;
        public static final String UID = "_id";
        public static final String ADDRESS = "address";
        public static final String LATTITUDE = "lat";
        public static final String LONGITUDE = "lng";

        private static final String CREATE_TABLE = " CREATE TABLE " + TABLE_NAME + " ( " +
                UID + " INTEGER PRIMARY KEY, " +
                ADDRESS + " TEXT NOT NULL, " +
                LATTITUDE + " TEXT NOT NULL, " +
                LONGITUDE + " TEXT NOT NULL );" ;

        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;


        public SQLiteHelper(Context context) {
            super(context, DATABASE_NAME, null, DTABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE);
                Log.e("TAG", "oncreate is called");
            } catch (SQLException e) {
                Log.e("TAG", "oncreae Exception " + e.getMessage());

            }


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            try {
                Log.e("TAG", "upgrade is called");

                db.execSQL(DROP_TABLE);
                onCreate(db);

            } catch (SQLException e) {
                Log.e("TAG", "upgrade is Excep" + e.getMessage());

            }

        }
    }



}
