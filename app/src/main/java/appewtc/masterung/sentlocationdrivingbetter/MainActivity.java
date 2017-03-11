package appewtc.masterung.sentlocationdrivingbetter;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //Explicit
    private TextView latTextView, lngTextView;
    private LocationManager locationManager;
    private Criteria criteria;
    private boolean GPSABoolean, networkABoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind Widget
        bindWidget();

        //Setup Location
        setupLocation();

        //Auto Update Location to mySQL
        updateLocationToMySQL();

    }   // Main Method

    private void updateLocationToMySQL() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        String strCurrentDate = dateFormat.format(date);
        String strLat = latTextView.getText().toString();
        String strLng = lngTextView.getText().toString();


        try {

            //Change Policy
            StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy
                    .Builder().permitAll().build();
            StrictMode.setThreadPolicy(threadPolicy);

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("isAdd", "true"));
            nameValuePairs.add(new BasicNameValuePair("Date", strCurrentDate));
            nameValuePairs.add(new BasicNameValuePair("Lat", strLat));
            nameValuePairs.add(new BasicNameValuePair("Lng", strLng));


            HttpClient httpClient = new DefaultHttpClient();


            //for น้องม.กรุงเทพ
            HttpPost httpPost = new HttpPost("http://swiftcodingthai.com/watch/php_add_location_master.php");

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            httpClient.execute(httpPost);



        } catch (Exception e) {
            Log.d("26Feb", "Error ==>>> " + e.toString());
        }


        myLoop();

    }   // updateLocationToMySQL

    private void myLoop() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateLocationToMySQL();
            }
        }, 5000);

    }   // myLoop

    @Override
    protected void onResume() {
        super.onResume();


        locationManager.removeUpdates(locationListener);
        String strLat = "13.711018";
        String strLng = "100.581514";

        Location networkLocation = requestLocation(LocationManager.NETWORK_PROVIDER,
                "network Error");
        if (networkLocation != null) {

            strLat = String.format("%.7f", networkLocation.getLatitude());
            strLng = String.format("%.7f", networkLocation.getLongitude());

        }   //if

        Location GPSLocation = requestLocation(LocationManager.GPS_PROVIDER,
                "GPS Error");
        if (GPSLocation != null) {

            strLat = String.format("%.7f", GPSLocation.getLatitude());
            strLng = String.format("%.7f", GPSLocation.getLongitude());

        }   // if

        latTextView.setText(strLat);
        lngTextView.setText(strLng);

    }   // onResume

    @Override
    protected void onStop() {
        super.onStop();


        locationManager.removeUpdates(locationListener);

    }   // onStop

    @Override
    protected void onStart() {
        super.onStart();

        GPSABoolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!GPSABoolean) {

            networkABoolean = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!networkABoolean) {
                Toast.makeText(MainActivity.this, "Cannot Find Location", Toast.LENGTH_SHORT).show();
            }   // if

        }   // if

    }   // onStart

    public Location requestLocation(String strProvider, String strError) {

        Location location = null;

        if (locationManager.isProviderEnabled(strProvider)) {


            locationManager.requestLocationUpdates(strProvider, 1000, 10, locationListener);
            location = locationManager.getLastKnownLocation(strProvider);

        } else {
            Log.d("gps", strError);
        } // if

        return location;
    }


    //Create Class
    public final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latTextView.setText(String.format("%.7f", location.getLatitude()));
            lngTextView.setText(String.format("%.7f", location.getLongitude()));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    private void setupLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);

    }   //setupLocation

    private void bindWidget() {
        latTextView = (TextView) findViewById(R.id.textView2);
        lngTextView = (TextView) findViewById(R.id.textView4);
    }

}   // Main Class
