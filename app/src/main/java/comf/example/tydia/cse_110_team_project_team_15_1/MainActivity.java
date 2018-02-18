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
        if(last.equals("Albums")){
            Intent intent = new Intent(this, AlbumsActivity.class);
            startActivity(intent);
        }
        else if(last.equals("AlbumSpecific")){
            String lastAlbum = lastScreen.getString("AlbumName", "NoAlbum"); //name of album that was being looked at before closing
            Intent intent = new Intent(this, AlbumSongsActivity.class);
            //TODO: Launch back to the specific album
        }
        else if(last.equals("Flashback")){
            Intent intent = new Intent(this, FlashbackActivity.class);
            startActivity(intent);
        }
        else if(last.equals("SongInfo")){
            String lastSong = lastScreen.getString("SongName", "NoSong"); // name of song that was being played before closing
            Intent intent = new Intent(this, SongInfo.class);
            //TODO: Launch back to the song directly
        }
        else if(last.equals("Songs")){
            Intent intent = new Intent(this, SongsActivity.class);
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

        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, serviceChecker, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceChecker = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocationService.Local local = (LocationService.Local)iBinder;
            locationService = local.getService();
            locationService.setUp();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    public static Location getCurrLoc(){
        return locationService.getCurrLoc();
    }

    /*
    public void databaseTester() throws IOException {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
            System.out.println("Don't have permission");
            return;
        }
        LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Location loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        database testData = new database();
        String address = testData.getAddress(loc,this);
        System.out.println("Adding a song now!");
        testData.startSongInfoRequest("Graham Mosbrucker's doesn't matter album", this);
        testData.finishSongInfoRequest();
        boolean check = testData.isSongHere("Graham Mosbrucker's doesn't matter album");
        System.out.println("Song is " + check);
        ArrayList<String> getSong = testData.getSongsPlayedAtLocation(address);
        System.out.println(loc.getLatitude() + " , " + loc.getLongitude());

        if (getSong.size() != 0 ) {
            for (int i = 0; i < getSong.size(); i++) {
                System.out.println(getSong.get(i));
            }
        }


    }
    */

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
