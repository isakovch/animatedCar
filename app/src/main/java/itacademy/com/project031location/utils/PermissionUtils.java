package itacademy.com.project031location.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import static itacademy.com.project031location.utils.AppConstants.REQUEST_CODE_LOCATION_PERMISSION;

public final class PermissionUtils {

    public static boolean isLocationServicesEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null &&
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static boolean checkLocationPermission(Activity activity) {
        String[] permissions = new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (isLocationPermissionGranted(activity)) return true;

        ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE_LOCATION_PERMISSION);
        return false;
    }

    public static boolean isLocationPermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

}
