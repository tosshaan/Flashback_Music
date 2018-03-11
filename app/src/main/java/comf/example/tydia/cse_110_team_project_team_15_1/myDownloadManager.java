package comf.example.tydia.cse_110_team_project_team_15_1;

import android.*;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import static comf.example.tydia.cse_110_team_project_team_15_1.ViewDLSongsActivity.findSong;

/**
 * Created by tosshaan on 3/11/2018.
 */

public class myDownloadManager {
    private long queueid;
    private DownloadManager dm;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 99;
    private Boolean mExternalStorageAvailable;
    private Context context;
    private Activity activity;

    public myDownloadManager(Context c, Activity a) {
        context = c;
        activity = a;

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
                            mySongs = findSong(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                            display();
                        }
                    }
                }
            }
        };

        activity.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
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
}
