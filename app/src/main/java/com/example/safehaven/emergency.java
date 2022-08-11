package com.example.safehaven;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import android.database.Cursor;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;

import android.content.DialogInterface;
import android.location.Location;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.material.snackbar.Snackbar;

import com.github.tbouron.shakedetector.library.ShakeDetector;

public class emergency extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Button emergency;
    DatabaseHandler db;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private Location location;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;

    //5 seconds update interval for location
    private static final long UPDATE_INTERVAL = 5000;

    //Create lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    //Integer for results request from permissions
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        //Sets the button to emergency button in XML layout
        emergency = findViewById(R.id.but_em);
        db = new DatabaseHandler(this);
        emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

                permissionsToRequest = permissionsToRequest(permissions);
                //Requests location permissions if not already given
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (permissionsToRequest.size() > 0) {
                        requestPermissions(permissionsToRequest.toArray(
                                new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                    }
                }
                googleApiClient = new GoogleApiClient.Builder(emergency.this).
                        addApi(LocationServices.API).
                        addConnectionCallbacks(emergency.this).
                        addOnConnectionFailedListener(emergency.this).build();
            }
        });

    /*Creates shake detector object, sensibility has been set to an amount which requires an adequate amount of shake force
      Requires at least 3 shakes to trigger*/
        ShakeDetector.create(this, new ShakeDetector.OnShakeListener() {
            @Override
            public void OnShake() {
                ShakeDetector.updateConfiguration(10, 10);
                Toast.makeText(getApplicationContext(), "Shake Detected!", Toast.LENGTH_SHORT).show();
                enableSmsButton();
            }
        });
    }

    //Checks to see if any wanted permissions have yet to be added, and adds them to list of permissions to request
    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();
        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    //Starts shake detection
    @Override
    protected void onResume() {
        super.onResume();
        ShakeDetector.start();

        if (!checkPlayServices()) {
            Toast.makeText(this, "Google play services is required for this application to function", Toast.LENGTH_SHORT).show();
        }
    }

    //Pauses shake detection
    @Override
    protected void onStop() {
        super.onStop();
    }

    //Stop shake detector
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShakeDetector.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; }
        // If all permissions are granted, location will be retrieved using API
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);


        if (location != null) {
            checkForSmsPermission();
        }

        startLocationUpdates();
    }

    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(emergency.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(emergency.this, new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            enableSmsButton();
        }
    }

    //Function to send location SMS to all pre-assigned contacts found in contact list
    private void enableSmsButton() {
        try {
            Cursor cursor = db.getAllContacts();
            ArrayList list = new ArrayList();
            while (cursor.moveToNext()) {
                list.add(cursor.getString(2));
            }
            for (int i = 0; i < list.size(); i++) {
                String num = list.get(i).toString();
                String msg = "I NEED HELP!\nCurrent location is:\nhttp://maps.google.com/maps?q=loc:"+location.getLatitude()+","+location.getLongitude();
                SmsManager.getDefault().sendTextMessage(num, null, msg, null, null);
                Toast.makeText(getApplicationContext(), "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
            } }catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS Failed to Send,Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    //Starts searching for location
    private void startLocationUpdates() {
        locationRequest = new LocationRequest();

        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Permissions are needed to retrieve location", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }
    
    @Override
    public void onLocationChanged(Location location) {
    }
    
    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {

                            //Create new alert message to let user know that permissions are needed
                            new AlertDialog.Builder(emergency.this).
                                    setMessage("Please allow these permissions in settings in order for function to work").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;

            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (permissions[0].equalsIgnoreCase(Manifest.permission.SEND_SMS) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableSmsButton();

                } else {
                    Toast.makeText(emergency.this, "sorry", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Starts shake detection, linked to button in XML layout
    public void startShakeDetect(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED  ) {
            Intent intentNotify = new Intent(this,emergency.class);
            intentNotify.setAction("Start");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(intentNotify);
                Snackbar.make(findViewById(android.R.id.content),"Shake detection has started!", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    //Stops shake detection, linked to button in XML layout
    public void stopShakeDetect(View view) {

        Intent intentNotify = new Intent(this,emergency.class);
        intentNotify.setAction("stop");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplicationContext().startForegroundService(intentNotify);
            Snackbar.make(findViewById(android.R.id.content),"Shake detection stopped", Snackbar.LENGTH_LONG).show();
        }
    }
}



