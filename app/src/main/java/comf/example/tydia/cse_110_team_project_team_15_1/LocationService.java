package comf.example.tydia.cse_110_team_project_team_15_1;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Class to manage location
 */
public class LocationService extends Service {

    private final IBinder binder = new Local();
    public Location currLoc;

    public LocationService() {
    }

    /**
     * sets up location listener
     */
    public void setUp(){
        LocationListener locList = new LocationListener(){

            @Override
            public void onLocationChanged(Location location) {
                Log.d("service", "Changed location");
                currLoc = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };
        LocationManager locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locProv = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("service","No permission");
            return;
        }
        //currLoc = locManager.getLastKnownLocation(locProv);
        if(currLoc == null){
            currLoc = new Location(LocationManager.GPS_PROVIDER);
            currLoc.setLatitude(32.715738);
            currLoc.setLongitude(-117.16108400000002);
        }
        locManager.requestLocationUpdates(locProv, 0, 0, locList);
    }

    /**
     * gets current location
     * @return location object
     */
    public Location getCurrLoc(){
        return currLoc;
    }

    /**
     * gets binder object
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    class Local extends Binder {
        public LocationService getService(){
            return LocationService.this;
        }
    }
}
