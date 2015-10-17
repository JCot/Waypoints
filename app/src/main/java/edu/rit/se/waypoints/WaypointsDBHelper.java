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
            WaypointsTable.COLOR + " VARCHAR(8), " +
            WaypointsTable.PICTURE + " TEXT, " +
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
    public void addWaypoint(Waypoint waypoint){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WaypointsTable.WAYPOINT_NAME, waypoint.getName());
        values.put(WaypointsTable.LATITUDE, waypoint.getLatitude());
        values.put(WaypointsTable.LONGITUDE, waypoint.getLongitude());
        values.put(WaypointsTable.COLOR, waypoint.getColor());

        db.insert(WaypointsTable.TABLE_NAME, null, values);

        db.close();
    }

    //TODO rewrite to return Waypoint object
    public Waypoint getWaypoint(String waypointName){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                WaypointsTable.TABLE_NAME,
                null,
                WaypointsTable.WAYPOINT_NAME + "=?",
                new String[]{ waypointName },
                null,
                null,
                null,
                null
        );

        if(cursor.moveToFirst()){
            Waypoint waypoint = new Waypoint(waypointName);
            waypoint.setLatitude(cursor.getDouble(2));
            waypoint.setLongitude(cursor.getDouble(3));
            waypoint.setColor(cursor.getString(4));


            return waypoint;
        }

        db.close();

        return null;
    }

    //TODO rewrite to return Waypoint objects
    public ArrayList<Waypoint> getAllWaypoints(){
        ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + WaypointsTable.TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){

            do{
                Waypoint waypoint = new Waypoint(cursor.getString(1));
                waypoint.setLatitude(cursor.getDouble(2));
                waypoint.setLongitude(cursor.getDouble(3));
                waypoint.setColor(cursor.getString(4));

                waypoints.add(waypoint);
            }while(cursor.moveToNext());
        }

        db.close();

        return waypoints;
    }

    //TODO redo to use Waypoint object, find by waypoint id.
    public void updateWaypoint(Waypoint waypoint){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WaypointsTable.WAYPOINT_NAME, waypoint.getName());
        values.put(WaypointsTable.LATITUDE, waypoint.getLatitude());
        values.put(WaypointsTable.LONGITUDE, waypoint.getLongitude());
        values.put(WaypointsTable.COLOR, waypoint.getColor());

        db.update(WaypointsTable.TABLE_NAME, values, WaypointsTable.WAYPOINT_NAME + "=?",
                new String[] { waypoint.getName() });
    }

    public void deleteWaypoint(String waypointName){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(WaypointsTable.TABLE_NAME,
                WaypointsTable.WAYPOINT_NAME + "=?",
                new String[] {waypointName});

        db.close();
    }

}
