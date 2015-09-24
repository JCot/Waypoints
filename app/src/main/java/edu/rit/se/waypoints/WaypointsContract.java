package edu.rit.se.waypoints;

import android.provider.BaseColumns;

/**
 * Created by justin.cotner on 9/23/15.
 */
public final class WaypointsContract {

    public WaypointsContract(){}

    public static abstract class WaypointsTable implements BaseColumns{
        public static final String TABLE_NAME = "waypoints";
        public static final String WAYPOINT_NAME = "waypoint_name";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String COLOR = "color";
    }
}
