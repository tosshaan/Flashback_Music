package comf.example.tydia.cse_110_team_project_team_15_1;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.Manifest;

import java.util.ArrayList;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.api.services.people.v1.model.Person;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static java.lang.Thread.sleep;

/**
 * Activity Class for the list of all songs in a particular album.
 * Opened when a particular album name is clicked from AlbumaActivity
 * Redirects to SongsInfoActivity, and FlashBackActivity
 */

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, AsyncObserver, Observer {
    private static final String SIGN_IN_TAG = "X";
    GoogleApiClient signInClient;


    final int RET_CODE = 200;
    final int CHECK_CODE = 100;

    public static database data;
    public static LocationService locationService;
    public static String PACKAGE_NAME;
    private boolean bound;
    static ArrayList<String> someList;
    public static ArrayList<Person> friendsList = new ArrayList<Person>();
    public static Person myPerson;
    public static String myPersonalID;

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


        googleSignIn();

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
    public void googleSignIn() {
        GoogleSignInOptions googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // The serverClientId is an OAuth 2.0 web client ID
                .requestServerAuthCode("781790350902-i1j0re1i0i8rc22mhugerv5p6okadnj9.apps.googleusercontent.com")
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN),
                        new Scope(PeopleServiceScopes.CONTACTS_READONLY),
                        new Scope(PeopleServiceScopes.USERINFO_PROFILE),
                        new Scope(PeopleServiceScopes.USER_EMAILS_READ))
                .build();


        // Begin Sign In Code
        signInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleOptions)
                .build();
        signInClient.connect();

        getGoogleToken();
    }
    @Override
    protected void onStart() {
        super.onStart();
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
        myDownloadManager dm = new myDownloadManager(this, this, this);
        dm.haveStoragePermission();
        dm.checkExternalStorage();
    }

    //Sign in screen
    private void getGoogleToken() {
        Intent signIn = Auth.GoogleSignInApi.getSignInIntent(signInClient);
        startActivityForResult(signIn, RET_CODE);
    }
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent retData) {
        super.onActivityResult(reqCode, resCode, retData);

        switch (reqCode) {
            case RET_CODE:
                Log.d(SIGN_IN_TAG, "sign in fromIntent");
                GoogleSignInResult fromIntent = Auth.GoogleSignInApi.getSignInResultFromIntent(retData);

                if (fromIntent.isSuccess()) {
                    GoogleSignInAccount account = fromIntent.getSignInAccount();
                    Log.d(SIGN_IN_TAG, "Sign in result: " + fromIntent.getStatus().isSuccess());
                    Log.d(SIGN_IN_TAG, "Server Authorization Code: " + account.getServerAuthCode());

                    new FriendAsync(this).execute(account.getServerAuthCode());


                } else {

                    Log.d(SIGN_IN_TAG, fromIntent.getStatus().toString() + " Error Message: " + fromIntent.getStatus().getStatusMessage());
                }
                break;
        }

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.d("onConnectionFailed", "Error: " + result.getErrorMessage());

        GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
        Dialog d = availability.getErrorDialog(this, result.getErrorCode(), CHECK_CODE);
        d.show();
    }

    public void update() {

    }

    @Override
    public void onConnected(Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int x) {

    }


    @Override
    public void callback() {
        //Checking if we need to generate a unique anon name for the current user
        SharedPreferences username = getSharedPreferences("Names", MODE_PRIVATE);
        String storedName = username.getString("Username", null);
        Log.d("Callback Main", "I'm in the callback of mainactivity");
        if(storedName == null){
            Log.d("Main Activity", "Email Address should be: " + myPerson.getEmailAddresses().get(0).getValue());
            myPersonalID = GoogleHelper.generateUserName(myPerson.getEmailAddresses().get(0).getValue());
            SharedPreferences.Editor edit = username.edit();
            edit.putString("Username",myPersonalID);
            Log.d("Callback", "Username generated: " + myPersonalID);
            edit.apply();
        }
        else{
            Log.d("Callback", "Username found: " + storedName);
            myPersonalID = storedName;
        }
    }
}
