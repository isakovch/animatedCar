package itacademy.com.project031location;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import itacademy.com.project031location.utils.AppConstants;
import itacademy.com.project031location.utils.PermissionUtils;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;

    private Marker mCarMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMap();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void locationReceived(Location location) {
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car));

        mCarMarker = mGoogleMap.addMarker(options);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationServices();
    }

    private void checkLocationServices() {
        if (PermissionUtils.isLocationServicesEnabled(this)) {
            enableMyLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppConstants.REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (PermissionUtils.checkLocationPermission(this) && mGoogleMap != null) {
            mGoogleMap.setMyLocationEnabled(true);
            startLocationService();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        enableMyLocation();
    }

    private void startLocationService() {
        startService(new Intent(this, LocationUpdateService.class));
    }
}
