package edu.rit.se.waypoints;

/**
 * Created by Ryan on 10/16/2015.
 */

import android.content.ClipData;

import edu.rit.se.waypoints.Waypoint;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Coordinates extends AppCompatActivity {

    WaypointsDBHelper dbHelper;

    private double latitude;
    private double longitude;
    private String name;
    private Waypoint waypoint;

    public Coordinates(String myName, Bundle savedInstanceState, Context context) {
        WaypointsDBHelper waypoint = new WaypointsDBHelper(context);
    }

    public Waypoint copyCoords(Waypoint copy) {
        double longitude;
        double latitude;
        String result;

        latitude = copy.getLatitude();
        longitude = copy.getLatitude();
        String converted = String.valueOf(latitude);
        String converted2 = String.valueOf(longitude);
        result = converted + converted2;

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText(result, "Text copied to clipboard");
        clipboard.setPrimaryClip(clip);

        return copy;
    }

}