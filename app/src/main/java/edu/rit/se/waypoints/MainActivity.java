package edu.rit.se.waypoints;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
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
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String j = MainActivity.mCurrentPhotoPath;
        Bitmap myBitmap = BitmapFactory.decodeFile(j);

        ImageView myImage = new ImageView(this);
        myImage.setImageBitmap(myBitmap);
        setContentView(R.layout.activity_main);

        buildGoogleApiClient();
        mDbHelper = new WaypointsDBHelper(this);

        Button button = (Button)findViewById(R.id.saveLocation);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSaveLocation();
            }
        });

        mAllWaypoints = mDbHelper.getAllWaypoints();

        if(!mAllWaypoints.isEmpty()){
            mCurWaypoint = mAllWaypoints.get(0);
            mMaxWaypointIndex = mAllWaypoints.size() - 1;
        }
    }

    private void goToSaveLocation(){
        Intent intent = new Intent(this, SaveLocationActivity.class);

        startActivity(intent);
    }

    private void changeWaypoint(){
        if(mCurWaypointIndex + 1 < mMaxWaypointIndex) {
            mCurWaypointIndex++;
        }
        else{
            mCurWaypointIndex = 0;
        }

        mCurWaypoint = mAllWaypoints.get(mCurWaypointIndex);
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

    @Override
    public void onLocationChanged(Location location) {
        if(mCurWaypoint != null) {
            calculateDistance(location);
            float distance = mNavArray[0];
            TextView waypointNameBox = (TextView) findViewById(R.id.navWaypointName);
            TextView distanceBox = (TextView) findViewById(R.id.distance);
            ImageView arrowView = (ImageView)findViewById(R.id.directionArrow);

            waypointNameBox.setText(mCurWaypoint.getName());
            distanceBox.setText("" + distance);


            Bitmap arrowBitmap = arrowView.getDrawingCache();
            float angle = mNavArray[2] - mNavArray[1];
            Bitmap canvasBitmap = arrowBitmap.copy(Bitmap.Config.ARGB_8888, true);
            canvasBitmap.eraseColor(0x00000000);

            // Create canvas
            Canvas canvas = new Canvas(canvasBitmap);

            // Create rotation matrix
            Matrix rotateMatrix = new Matrix();
            rotateMatrix.setRotate(angle, canvas.getWidth() / 2, canvas.getHeight() / 2);

            //Draw bitmap onto canvas using matrix
            canvas.drawBitmap(arrowBitmap, rotateMatrix, null);

            arrowView.setImageDrawable(new BitmapDrawable(this.getResources(), canvasBitmap));
        }
    }
}
