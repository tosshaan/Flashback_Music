package comf.example.tydia.cse_110_team_project_team_15_1;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ViewDLSongsActivity extends AppCompatActivity {
    ListView list;
    String[] items;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 99;

    Boolean mExternalStorageAvailable;

    ArrayList<File> mySongs;

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_dlsongs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button back = (Button)findViewById(R.id.btn_backTest);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if (mp != null) {
                    mp.stop();
                    mp.release();
                }
            }
        });


        list = findViewById(R.id.list_allsongs_test);
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            checkStoragePermission();
        }

        Button delete = (Button) findViewById(R.id.b_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = (EditText) findViewById(R.id.Delete);
                int index = Integer.parseInt(text.getText().toString());

                if (index < items.length) {
                    File f = mySongs.get(index);
                    f.delete();
                    display();
                }
                else {
                    Toast.makeText(getApplicationContext(), "bad index", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    static public ArrayList<File> findSong(File root) {

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

                if (mp != null) {
                    mp.stop();
                    mp.release();
                }

                mp = new MediaPlayer();
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

                //if (checkStoragePermission()) {

                    try {
                        //Log.d("before Parse" , "onItemClick: " + "file://" + mySongs.get(i).toString());
                        //mp.setDataSource(getApplicationContext(), Uri.parse("file://" + mySongs.get(i).toString()));
                        mp.setDataSource("file://" + mySongs.get(i).toString());
                        mp.prepare();
                        mp.start();

                    } catch (IllegalArgumentException e) {
                        Log.d("setDataSource", "failed to set uri");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("setDataSource", "failed to set uri2");

                    }

                /*}
                else {
                    Log.d("permission to read", "CANNOT READ");
                }*/
                //mp = MediaPlayer.create(getApplicationContext(), Uri.parse(mySongs.get(i).toString()));
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

    // NOT CALLED
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantedResults) {

        switch(requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantedResults.length > 0 && grantedResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        checkExternalStorage();
                    }
                }
                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


}
