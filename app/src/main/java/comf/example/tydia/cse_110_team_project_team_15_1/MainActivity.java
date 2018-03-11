package comf.example.tydia.cse_110_team_project_team_15_1;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.Manifest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

/**
 * Activity Class for the list of all songs in a particular album.
 * Opened when a particular album name is clicked from AlbumaActivity
 * Redirects to SongsInfoActivity, and FlashBackActivity
 */
public class MainActivity extends AppCompatActivity {

    public static database data;
    public static LocationService locationService;
    public static String PACKAGE_NAME;
    private boolean bound;
    static ArrayList<String> someList;

    /**
     * This method runs when the activity is created
     * Contains all functionality for the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database db = new database();
        PACKAGE_NAME = getPackageName();

        // TODO: DELETE THIS CRAP!!!

        /*

        FirebaseDB dbFunc = new FirebaseDB();
        URI testURL = null;
        try {
            testURL = new URI("Test");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.d("OH NO ERROR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!", "");
        }
        long date = System.currentTimeMillis();
        String address = "Decentralized Unpark";;
        String userName = "Tosh and I";
        String songName = "Ugly Pleasure";
        dbFunc.submit(userName, address, songName, date,testURL);
        userName = "Graham and Cory";
        songName = "Water on your splitends";
        date = System.currentTimeMillis();
        dbFunc.submit(userName, address, songName, date, testURL);
        userName = "Tong and Wei";
        songName = "malagnam";
        date = System.currentTimeMillis();
        dbFunc.submit(userName, address, songName, date, testURL);
        userName = "Tosh and I";
        songName = "Old Song";
        address = "Central Park";
        date = date - (FirebaseDB.MILLISECODNS_IN_DAY * 10);
        dbFunc.submit(userName, address, songName, date, testURL);

        LocalDate whenDis = LocalDate.now();
        dbFunc.getAllSongsForVibe("Central Park", whenDis, "someUser", new FirebaseQueryObserver() {
            @Override
            public void update(ArrayList<String> songNameList, ArrayList<String> songURLList) {
                //call generateList method here
                Log.d("THE WHOLE LIST OF SONGS IS: ", songNameList.toString());
                Log.d("THE WHOLE LIST OF URLS IS: ", songURLList.toString() );
            }
        });


/*
        someList = new ArrayList<>();
        dbFunc.getSongNamesAtLocation("Central Park", new FirebaseQueryObserver() {
            @Override
            public void update(ArrayList<String> songNameList, ArrayList<String> songURLList) {
                 Log.d("WHERE SONG_NAME_LIST IS: ", songNameList.toString());
                Log.d("WHERE SONG_URL_LIST IS: ", songURLList.toString());
            }
        });


        Log.d("FIRST LIST SIZE: ", "" +someList.size() );
        for( String name: someList ) {
            Log.d("SONG IS: ", name);
        }

        LocalDate whenDis = LocalDate.now();
        dbFunc.getSongsLastWeek(whenDis, new FirebaseQueryObserver() {
            @Override
            public void update(ArrayList<String> songNameList, ArrayList<String> songURLList) {
                Log.d("WHEN SONG_NAME_LIST IS: ", songNameList.toString());
                Log.d("WHEN SONG_URL_LIST IS: ", songURLList.toString());
            }
        });

        Log.d("SECOND LIST SIZE: ", ""+ someList.size() );
        for( String name: someList ) {
            Log.d("SONG IS: ", name);
        }
*/

        // TODO: END OF DELETABLE CRAP!!

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

        final Button launchVibe = (Button) findViewById(R.id.b_vibe);

        launchVibe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchVibe();
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

    /**
     * Method to get current location of device
     * @return Location object
     */
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

    /**
     * Goes to FlashbackActivity
     */
    public void launchVibe() {
        Intent intent = new Intent (this, VibeModeActivity.class);

        startActivity(intent);
    }

    /**
     * Goes to AlbumsActivity
     */
    public void launchAlbums() {
        Intent intent = new Intent (this, AlbumsActivity.class);
        startActivity(intent);
    }

    /**
     * Goes to SongsActivity
     */
    public void launchSongs() {
        Intent intent = new Intent (this, SongsActivity.class);
        startActivity(intent);
    }

    /**
     * Method to get permission for location access
     */
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
