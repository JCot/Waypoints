package edu.rit.se.waypoints;

import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.util.Log;

import com.vuzix.hardware.GestureSensor;

/**
 * Created by Ryan on 10/9/2015.
 */
public class mySensor extends GestureSensor{

    public static final String TAG = "SENSOR";
    private Context context;

    public mySensor(Context arg0)
    {
        super(arg0);
        context = arg0;
    }

    @Override
    protected void onBackSwipe(int speed) { Log.i(TAG, "Left"); }

    @Override
    protected void onForwardSwipe(int speed){
        Log.i(TAG, "Right");

        Intent intent = new Intent(context, SaveLocationActivity.class);

        context.startActivity(intent);

    }

    @Override
        protected void onDown(int speed) { Log.i(TAG, "Down"); }

    @Override
    protected void onNear() {
        Log.i(TAG, "Near");
    }

    @Override
    protected void onFar() {
        Log.i(TAG, "Far");
    }

    @Override
    protected void onUp(int speed) { Log.i(TAG, "Up"); }


}
