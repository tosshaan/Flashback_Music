package comf.example.tydia.cse_110_team_project_team_15_1;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by tosshaan on 3/11/2018.
 */


public class myDownloadManager implements downloadSubject, playerSubject {
    private long queueid;
    private DownloadManager dm;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 99;
    private Boolean mExternalStorageAvailable;
    private Context context;
    private Activity activity;
    private downloadObserver observer;
    private Observer musicObs;
    private ArrayList<String> mySongs;
    private boolean zip = false;
    private String zipname;


    public myDownloadManager(Context c, Activity a, downloadObserver obs) {
        context = c;
        activity = a;
        regDownObs(obs);

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
                            if (zip) {
                                zip = false;
                                Log.d("DownloadAlbum", "ENTERED DOWNLOADALBUM" + zipname);
                                if (unpackZip(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), zipname.replaceAll("%20", " "))){
                                    Log.d("DownloadAlbum", "Download successful");
                                }
                                else {
                                    Log.d("UNZIP", "UNZIP FAILED");
                                }
                            }

                            //mySongs = findSong(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                            notifyDownDone();
                            notifyObservers();
                        }
                    }
                }
            }
        };

        activity.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void setDownloadedSongs() {
        ArrayList<File> files = SongsActivity.findSong(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        //Log.d("IN SET DOWNLOAD SONGS", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());

        mySongs = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            mySongs.add(files.get(i).getName()); //.getPath().replace(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() +"/", ""));
        }
        for (int i = 0; i < files.size(); i++) {
            Log.d("whatisinmysongs", mySongs.get(i));
        }

    }

    public void Download(String input) {
        if (input.equals("")) {
            return;
        }
        if (input.charAt(0) == '/') {
            input = input.substring(1);
        }

        if (input.contains(".zip")) {
            zipname = input.replace("?dl=1","");
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + zipname);
            Log.d("what is zipname", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + zipname);
            if (file.exists()) {
                Log.d("CALLING DOWNLOAD", "ALBUM already downloaded");
                notifyDownDone();
                return;
            }
            if (mySongs.contains(zipname)) {
                Log.d("CALLING DOWNLOAD", "album being downloaded right now");
                notifyDownDone();
                return;
            } else {
                mySongs.add(zipname);
            }
            zip = true;
            zipname = input.replace("?dl=1","");
        }

        String inputfile = input.replace("//", "/");
        inputfile = inputfile.replace(".mp3?dl=1", ".mp3");
        int index = inputfile.lastIndexOf('/');
        String inputfilename = inputfile.substring(index+1);
        Log.d("downloadcheck", inputfilename);
        if (mySongs.contains(inputfilename)) {
            Log.d("callingDownload", input + " ALREADY DOWNLOADED");
            notifyDownDone();
            return;
        } else {
            mySongs.add(inputfilename);
        }


        dm = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        if(!input.contains("?dl=1")) {
            input = input + "?dl=1";
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(input));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, input);
        //Log.d("DOWNLOADINGx", "Download path " + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());

        haveStoragePermission();

        queueid = dm.enqueue(request);

    }

    private boolean unpackZip(String path, String zipname) {
        InputStream is;
        ZipInputStream zis;
        try{
            String filename;
            is = new FileInputStream(path + "/" + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;
            while ((ze = zis.getNextEntry()) != null) {
                filename = ze.getName();
                if (ze.isDirectory()) {
                    File fmd = new File(path + "/" + filename);
                    Log.d("DOWNLOADALBUM", fmd.getPath() + " name " + fmd.getName());
                    fmd.mkdirs();
                    continue;
                }


                SharedPreferences albumPref = context.getSharedPreferences("albumSongs", context.MODE_PRIVATE);
                SharedPreferences.Editor edit = albumPref.edit();
                edit.putString(filename, zipname);
                edit.apply();

                FileOutputStream fout = new FileOutputStream(path + "/" + filename);
                Log.d("DOWNLOADALBUM", path + "/" + filename);



                while((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();

                if (mySongs.contains(filename)) {
                    File f = new File(path + "/" + filename);
                    if (f.delete()) {
                        Log.d("dup", "ALREADY DWONLOADED, delete");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
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

            //notifyObservers();
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
    public void notifyDownDone() {
        observer.finishDownload();
    }

    @Override
    public void regDownObs(downloadObserver obs) {
        observer = obs;
    }

    @Override
    public void delDownObs(downloadObserver obs) {
        observer = null;
    }

    @Override
    public void notifyObservers() {
        if (musicObs != null) {
            musicObs.update();
        }
    }

    @Override
    public void regObserver(Observer obs) {
        musicObs = obs;
    }

    @Override
    public void delObserver(Observer obs) {
        musicObs = null;
    }
}
