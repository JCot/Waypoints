package edu.rit.se.waypoints;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import edu.rit.se.waypoints.WaypointsContract.WaypointsTable;


/**
 * Created by justin.cotner on 9/23/15.
 */
public class WaypointsDBHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "Waypoints.db";
    private static final int DB_VERSION = 1;
    private static final String CREATE_ENTRIES =
            "CREATE TABLE " + WaypointsTable.TABLE_NAME + " (" +
            WaypointsTable._ID + " INTEGER PRIMARY KEY," +
            WaypointsTable.WAYPOINT_NAME + " TEXT UNIQUE, " +
            WaypointsTable.LATITUDE + " DOUBLE, " +
            WaypointsTable.LONGITUDE + " DOUBLE, " +
            WaypointsTable.COLOR + " VARCHAR(8) " +
            " )";

    public WaypointsDBHelper(Context context){
        super(context, DB_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

    // CRUD Operations

    //TODO pass in a Waypoint object and get values from that
    public void addWaypoint(){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WaypointsTable.WAYPOINT_NAME, "One");
        values.put(WaypointsTable.LATITUDE, 42.391009);
        values.put(WaypointsTable.LONGITUDE, -77.695313);
        values.put(WaypointsTable.COLOR, "3de45634");

        db.insert(WaypointsTable.TABLE_NAME, null, values);

        db.close();
    }

    //TODO rewrite to return Waypoint object
    public String getWaypoint(String waypointName){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                WaypointsTable.TABLE_NAME,
                null,
                WaypointsTable.WAYPOINT_NAME + "=?",
                new String[]{waypointName},
                null,
                null,
                null,
                null
        );

        if(cursor != null){
            cursor.moveToFirst();

            return cursor.getString(0);
        }

        db.close();

        return "";
    }

    //TODO rewrite to return Waypoint objects
    public ArrayList<String> getAllWaypoints(){
        ArrayList<String> waypoints = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + WaypointsTable.TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null){
            cursor.moveToFirst();

            do{
                waypoints.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }

        db.close();

        return waypoints;
    }

    //TODO redo to use Waypoint object, find by waypoint id.
    public void updateWaypoint(String waypointName){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WaypointsTable.WAYPOINT_NAME, "One");
        values.put(WaypointsTable.LATITUDE, 42.391009);
        values.put(WaypointsTable.LONGITUDE, -77.695313);
        values.put(WaypointsTable.COLOR, "3de45634");

        db.update(WaypointsTable.TABLE_NAME, values, WaypointsTable.WAYPOINT_NAME + "=?",
                new String[] {waypointName});
    }

    public void deleteWaypoint(String waypointName){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(WaypointsTable.TABLE_NAME,
                WaypointsTable.WAYPOINT_NAME + "=?",
                new String[] {waypointName});

        db.close();
    }

}
