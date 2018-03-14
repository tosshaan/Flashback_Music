package comf.example.tydia.cse_110_team_project_team_15_1;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import static comf.example.tydia.cse_110_team_project_team_15_1.ViewDLSongsActivity.findSong;

/**
 * Created by tosshaan on 3/11/2018.
 */

public class myDownloadManager implements playerSubject {
    private long queueid;
    private DownloadManager dm;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 99;
    private Boolean mExternalStorageAvailable;
    private Context context;
    private Activity activity;
    private songObserver observer;

    public myDownloadManager(Context c, Activity a, songObserver obs) {
        context = c;
        activity = a;
        observer = obs;

        checkExternalStorage();

        dm = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);;
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
                            Toast.makeText(context, "Download Sucessful", Toast.LENGTH_SHORT).show();
                            //mySongs = findSong(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                            notifyObservers();
                        }
                    }
                }
            }
        };

        activity.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void Download(String input) {
        if (input.equals("")) {
            return;
        }
        dm = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(input));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, input);
        //Log.d("DOWNLOADINGx", "Download path " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

        haveStoragePermission();

        queueid = dm.enqueue(request);

    }

    public  boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                return true;
            } else {

                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error","You already have the permission");
            return true;
        }
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

            notifyObservers();
        }
        else {
            Toast.makeText(context, "Please insert an SDcard", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkStoragePermission() {

        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
            else {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
            return false;
        }
        else {

            checkExternalStorage();
            return true;
        }
    }

    @Override
    public void notifyObservers() {
        observer.update();
    }

    @Override
    public void regObserver(songObserver obs) {
        observer = obs;
    }
}