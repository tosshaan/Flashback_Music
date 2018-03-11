package comf.example.tydia.cse_110_team_project_team_15_1;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteController;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.mock.MockContext;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.w3c.dom.Text;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EventListener;

/**
 * Activity Class for the list of all songs in memory.
 * Opened when "all songs" is clicked from MainActivity
 * Redirects to SongsInfoActivity, and FlashBackActivity
 */
public class SongsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list;
    // Need to get list of song names from the database
    public static String PACKAGE_NAME;

    MetadataGetter metadataGetter;

    //https://www.youtube.com/watch?v=atZRWb6_QRs
    // yt tutorial to download songs
    long queueid;
    DownloadManager dm;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 99;
    ArrayList<File> mySongs;
    Boolean mExternalStorageAvailable;
    String[] items;


    /**
     * This method runs when the activity is created
     * Contains all functionality for the activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // hide action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        MainActivity.PACKAGE_NAME = getPackageName();
        metadataGetter = new MetadataGetter(this);

        Button switchScreen = (Button) findViewById(R.id.btn_back2);

        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list = (ListView) findViewById(R.id.list_allsongs);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            checkStoragePermission();
        }
        display();

        // launch flashback (temp)
        final Button launchFlashbackActivity = (Button) findViewById(R.id.b_flashback_song);
        launchFlashbackActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchFlashback();
            }
        });

        Button deleteAll = (Button) findViewById(R.id.DeleteAll);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < mySongs.size(); i++) {
                    File f = mySongs.get(i);
                    f.delete();

                }
                display();
                //launchDLSong();
            }
        });

        Button b_dl = (Button) findViewById(R.id.b_download);
        b_dl.setOnClickListener(view -> {
            EditText editText = (EditText) findViewById(R.id.URLeditText);
            String input = "http://www.hubharp.com/web_sound/HarrisLilliburleroShort.mp3";
            //editText.getText().toString();
            //"http://www.sakisgouzonis.com/files/mp3s/Sakis_Gouzonis_-_Quest_For_Peace_And_Progress.mp3";
            Download(input);
            input = "http://www.hubharp.com/web_sound/WalloonLilliShort.mp3";
            Download(input);

            input = "http://www.hubharp.com/web_sound/PurcellSongMusShort.mp3";
            Download(input);


        });

        Button viewdl = (Button) findViewById(R.id.b_viewDownload);
        viewdl.setOnClickListener(view -> {
            View_Click(viewdl);
        });


        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    DownloadManager.Query req_query = new DownloadManager.Query();
                    req_query.setFilterById(queueid);
                    Cursor c = dm.query(req_query);

                    if(c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);

                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                            Toast.makeText(getApplicationContext(), "Download Sucessful", Toast.LENGTH_SHORT).show();
                            display();
                        }
                    }
                }
            }
        };

        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    public void Download(String input) {
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(input));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, input);
        //Log.d("DOWNLOADINGx", "Download path " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

        haveStoragePermission();

        queueid = dm.enqueue(request);

    }

    public  boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                return true;
            } else {

                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error","You already have the permission");
            return true;
        }
    }

    public void View_Click(View view) {
        Intent intent = new Intent();
        intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(intent);
    }

    /**
     * Item click method
     * @param i - index of the item clicked
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        TextView temp = (TextView) view;
        //Toast.makeText(this, temp.getText()+ " row" + i, Toast.LENGTH_SHORT).show();
        launchSongInfoAct(i);
    }

    /**
     * Goes to SongInfoActivity for the particular song clicked in the list
     * @param i - index of the song in the song list
     */
    public void launchSongInfoAct(int i) {
        Intent intent = new Intent (this, SongInfoActivity.class);
        //intent.putExtra("songID", IDs[i]);
        //intent.putExtra("songName", songNames[i]);
        //intent.putExtra("albumMode", false);
        intent.putExtra("songIndex", i);
        Bundle bundle = new Bundle();
        //bundle.putIntArray("SongsIDs", IDs);
        String[] list = getStringArray();

        bundle.putStringArray("list", list);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    public String[] getStringArray() {
        String[] ret = new String[mySongs.size()];
        for (int i = 0; i < mySongs.size(); i++) {
            ret[i] = mySongs.get(i).toString();
        }

        return ret;
    }

        /**
         * Goes to FlashbackActivity
         */
    public void launchFlashback() {
        Intent intent = new Intent (this, FlashbackActivity.class);
        startActivity(intent);
    }

    public void launchDLSong() {
        Intent intent = new Intent (this, ViewDLSongsActivity.class);
        startActivity(intent);
    }


    public ArrayList<File> findSong(File root) {

        ArrayList<File> at = new ArrayList<File>();
        File[] files = root.listFiles();
        Log.d("findSong", "findSong: length of this directory " + files.length);

        for(File singleFile : files) {

            if(singleFile.isDirectory() && !singleFile.isHidden()) {
                Log.d("findSong", "findSong: found a directory " + singleFile.getName());

                at.addAll(findSong(singleFile));
            }
            else {
                if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                    Log.d("findSong", "findSong: found a song " + singleFile.getName());
                    Log.d("absolutepath", "path = " +singleFile.getAbsolutePath());

                    at.add(singleFile);
                }
            }
        }

        return at;
    }

    public void display() {

        Log.d("display", "findSong: entering");

        Log.d("display", "name of external storage = " +
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

        //getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        mySongs = findSong(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        items = new String[mySongs.size()];

        for (int i = 0; i < mySongs.size(); i++) {
            items[i] = mySongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
        }
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        list.setAdapter(adp);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                launchSongInfoAct(i);
            }
        });
    }

    public void checkExternalStorage() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = true;
        }
        else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
        }
        else {
            mExternalStorageAvailable = false;
        }

        handleExternalStorageState();
    }

    public void handleExternalStorageState() {

        if (mExternalStorageAvailable) {

            display();
        }
        else {
            Toast.makeText(getApplicationContext(), "Please insert an SDcard", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkStoragePermission() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
            return false;
        }
        else {

            checkExternalStorage();
            return true;
        }
    }


}
