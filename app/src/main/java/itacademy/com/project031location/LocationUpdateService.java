package itacademy.com.project031location;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;

import itacademy.com.project031location.utils.PermissionUtils;

public class LocationUpdateService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private static final int INTERVAL = 10000;
    private static final int FASTEST_INTERVAL = 3000;

    private final String LOG_TAG_LOCATION = "LOCATION_SERVICE";

    private GoogleApiClient mGoogleApiClient;

    private FusedLocationProviderClient mLocationProviderClient;

    private LocationRequest mLocationRequest;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initGoogleClient();
    }

    private void initGoogleClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();

        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        return START_STICKY;
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        setLocationRequestParams();

        if (PermissionUtils.isLocationPermissionGranted(getApplicationContext())) {
            mLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    private void stopLocationUpdate() {
        mLocationProviderClient.removeLocationUpdates(mLocationCallback);
        stopSelf();
    }

    private void setLocationRequestParams() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            onLocationChanged(locationResult.getLastLocation());
        }
    };

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG_LOCATION, "Connection suspended " + i);
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO send location to the server when ready
        EventBus.getDefault().post(location);
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
            stopLocationUpdate();
        }

        super.onDestroy();
    }
}
