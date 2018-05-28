package michaellin.venture_10;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class CurrentLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location location)
    {
        String TAG = "VentureCoords";

        String longitude = "Longitude: " + location.getLongitude();
        Log.d(TAG, longitude);
        String latitude = "Latitude: " + location.getLatitude();
        Log.d(TAG, latitude);
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
