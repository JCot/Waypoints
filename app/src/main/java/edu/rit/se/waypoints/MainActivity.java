package edu.rit.se.waypoints;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    static String mCurrentPhotoPath;
    GoogleApiClient mGoogleApiClient;
    WaypointsDBHelper mDbHelper;
    Waypoint mCurWaypoint = null;
    float[] mNavArray = new float[3];
    ArrayList<Waypoint> mAllWaypoints;
    int mCurWaypointIndex = 0;
    int mMaxWaypointIndex = 0;
    LocationRequest mLocationRequest;
    private float currentHeading = 0f;
    int prevMeasureTime = 0;
    float prevDistance;
    mySensor mSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String j = MainActivity.mCurrentPhotoPath;
        Bitmap myBitmap = BitmapFactory.decodeFile(j);

        ImageView myImage = new ImageView(this);
        myImage.setImageBitmap(myBitmap);
        setContentView(R.layout.activity_main);

        buildGoogleApiClient();
        createLocationRequest();
        mDbHelper = new WaypointsDBHelper(this);

        Button button = (Button) findViewById(R.id.saveLocation);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSaveLocation();
            }
        });


        Button next = (Button) findViewById(R.id.nextWaypoint);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeWaypoint();
            }
        });

        mSensor = new mySensor(this);
        mSensor.register();
    }

    @Override
    protected void onStart(){

        super.onStart();

        mAllWaypoints = mDbHelper.getAllWaypoints();
        if(!mAllWaypoints.isEmpty()) {
            mCurWaypoint = mAllWaypoints.get(mCurWaypointIndex);
            mMaxWaypointIndex = mAllWaypoints.size() - 1;
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensor.unregister();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensor.register();
    }

    private void goToSaveLocation(){
        Intent intent = new Intent(this, SaveLocationActivity.class);

        startActivity(intent);
    }

    public void changeWaypoint(){
        if(mCurWaypointIndex + 1 <= mMaxWaypointIndex) {
            mCurWaypointIndex++;
        }
        else{
            mCurWaypointIndex = 0;
        }

        mCurWaypoint = mAllWaypoints.get(mCurWaypointIndex);

        TextView waypointNameBox = (TextView) findViewById(R.id.navWaypointName);
        waypointNameBox.setText(mCurWaypoint.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Image saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }
    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println("Failed to create the file");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void addPicture() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void calculateDistance(Location curLocation){
        Location.distanceBetween(curLocation.getLatitude(), curLocation.getLongitude(),
                mCurWaypoint.getLatitude(), mCurWaypoint.getLongitude(), mNavArray);
        float distance = (float)(mNavArray[0] * 3.28084); // Convert meters to feet
        mNavArray[0] = distance;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    private float caclulateSpeed(float distance){
        float distanceTraveled = Math.abs(distance - prevDistance);

        Calendar cal = Calendar.getInstance();
        int time = (int)System.currentTimeMillis();
        int timeElapsed = (time - prevMeasureTime)/1000;

        prevMeasureTime = time;

        float speed = distanceTraveled/timeElapsed;

        return speed;
    }

    private float calculateEta(float speed, float distance){
        float eta = distance/speed;

        return eta;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mCurWaypoint != null) {
            calculateDistance(location);
            float distance = mNavArray[0];
            float speed = caclulateSpeed(distance);
            float eta = calculateEta(speed, distance);
            TextView waypointNameBox = (TextView) findViewById(R.id.navWaypointName);
            TextView distanceBox = (TextView) findViewById(R.id.distance);
            TextView etaView = (TextView)findViewById(R.id.eta);
            //ImageView arrowView = (ImageView)findViewById(R.id.directionArrow);

            waypointNameBox.setText(mCurWaypoint.getName());
            distanceBox.setText("" + distance);
            etaView.setText(String.valueOf(eta));


            TextView warmerView = (TextView)findViewById(R.id.warmer);
            TextView colderView = (TextView)findViewById(R.id.colder);
            if(prevDistance > distance){
                warmerView.setVisibility(View.VISIBLE);
                colderView.setVisibility(View.INVISIBLE);
            }

            else if(prevDistance < distance){
                warmerView.setVisibility(View.INVISIBLE);
                colderView.setVisibility(View.VISIBLE);
            }

            prevDistance = distance;

            //Location waypoint = new Location("GPS");
            //waypoint.setLongitude(mCurWaypoint.getLongitude());
            //waypoint.setLatitude(mCurWaypoint.getLatitude());
            //float a1 = location.getBearing();
            //float a2 = mNavArray[2];

            //float angle = Math.min((a1-a2)<0?a1-a2+360:a1-a2, (a2-a1)<0?a2-a1+360:a2-a1);

            // Create rotation matrix
            //Matrix rotateMatrix = new Matrix();
            //arrowView.setScaleType(ImageView.ScaleType.MATRIX);
            //TODO get appropriate rotation point;
            //rotateMatrix.postRotate(angle, 0, 0);
            //arrowView.setImageMatrix(rotateMatrix);
        }
    }
}
