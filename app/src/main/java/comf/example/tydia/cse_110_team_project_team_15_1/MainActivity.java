package comf.example.tydia.cse_110_team_project_team_15_1;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.Manifest;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static database data;
    public static LocationService locationService;
    public static String PACKAGE_NAME;
    private boolean bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database db = new database();
        PACKAGE_NAME = getPackageName();

        SharedPreferences lastScreen = getSharedPreferences("Screen", MODE_PRIVATE);
        String last = lastScreen.getString("Activity", "Main");
        if(last.equals("Flashback")){
            Intent intent2 = new Intent(this, LocationService.class);
            bindService(intent2, serviceChecker, Context.BIND_AUTO_CREATE);

            Intent intent = new Intent(this, FlashbackActivity.class);
            startActivity(intent);
        }
        else{
            SharedPreferences.Editor edit = lastScreen.edit();
            edit.putString("Activity", "Main");
            edit.apply();
        }

        final Button launchFlashbackActivity = (Button) findViewById(R.id.b_flashback);

        launchFlashbackActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchFlashback();
            }
        });

        data = DatabaseStorageFunctions.retreiveDatabase(this);

        final Button launchAlbums = (Button) findViewById(R.id.button_albums);
        launchAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAlbums();
            }
        });
        final Button launchSongs = (Button) findViewById(R.id.button_songs);
        launchSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSongs();
            }
        });
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            return;
        }

        Intent intent2 = new Intent(this, LocationService.class);
        bindService(intent2, serviceChecker, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceChecker = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocationService.Local local = (LocationService.Local)iBinder;
            locationService = local.getService();
            locationService.setUp();
            Log.d("THIS HAS HAPPENED: ", "OH YEAH");
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    public static Location getCurrLoc(){
        if( locationService != null ) {
            Log.d("I AM HERE NOW:", locationService.toString());
            return locationService.getCurrLoc();
        }
        else {
            // Initialize
          //  LocationService.Local local = (LocationService.Local) BIND_AUTO_CREATE;
          //  locationService = local.getService();
          //  locationService.setUp();
            Log.d("OOPS!", "Location was NULL");
            return null;
        }
    }

    public void launchFlashback() {
        Intent intent = new Intent (this, FlashbackActivity.class);
        startActivity(intent);
    }
    public void launchAlbums() {
        Intent intent = new Intent (this, AlbumsActivity.class);
        startActivity(intent);
    }
    public void launchSongs() {
        Intent intent = new Intent (this, SongsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, LocationService.class);
                    bindService(intent, serviceChecker, Context.BIND_AUTO_CREATE);
                }
                else {
                    Log.d("main", "App will not work without permission");
                    return;
                }
        }

    }


}
