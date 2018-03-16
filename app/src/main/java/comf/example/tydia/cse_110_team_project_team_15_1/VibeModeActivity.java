package comf.example.tydia.cse_110_team_project_team_15_1;

import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import static comf.example.tydia.cse_110_team_project_team_15_1.FirebaseDB.MILLISECODNS_IN_DAY;
import static java.lang.Thread.sleep;

public class VibeModeActivity extends AppCompatActivity implements Observer {

    VibeModeList VMList;
    FirebaseDB firebaseDB;
    ListView list;
    database myData;
    private myMusicPlayer musicPlayer;
    MetadataGetter metadataGetter;
    private int songIndex;
    ArrayList<String> songNames;
    ArrayList<String> songURLs;
    private String[] songsUri;
    private myDownloadManager downloadManager;
    ArrayList<File> downloadedSongFiles = new ArrayList<>();
    ArrayList<String> downloadedSongs = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myData = MainActivity.data;
        metadataGetter = new MetadataGetter(this);
        VMList = new VibeModeList(myData);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myFireBaseRef = database.getReference();
        firebaseDB = new FirebaseDB(database, myFireBaseRef);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibe_mode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialise downloadmanager object
        downloadManager = new myDownloadManager(this, this, this);
        downloadManager.regObserver(this);
        downloadManager.checkExternalStorage();

        //arraylist of files; convert this to arraylist of downloaded song names
        downloadedSongFiles = SongsActivity.findSong(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        downloadedSongs = convertFileToSong(downloadedSongFiles);

        musicPlayer = new myMusicPlayer();
        musicPlayer.regObserver(this);
        downloadManager.regObserver(musicPlayer);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Location myLoc = MainActivity.getCurrLoc();
        String currAddress = "";
        try {
            currAddress = myData.getAddress(myLoc, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LocalDate currDate = LocalDate.now();
        String userName = MainActivity.myPersonalID;

        firebaseDB.getAllSongsForVibe(currAddress, currDate, userName, new FirebaseQueryObserver() {
            @Override
            public void update(ArrayList<String> songNameList, ArrayList<String> songURLList, String adr, String usr, long time) {
                VMList.generateList(songNameList, songURLList);
                songNames = VMList.getVibeModeSongs();
                songURLs = VMList.getVibeModeURLs();

                //index of the first already downloaded song
                int index = -1;
                boolean alreadyDownloaded = false;
                //auto download songs not already downloaded
                Log.d("length of songNames", " "+songNames.size());
                Log.d("length of songURLs", " "+songURLs.size());

                Log.d("songName array", songNames.toString());
                Log.d("songUrls", songURLs.toString());


                downloadManager.setDownloadedSongs();

                for(int j = 0; j < songNames.size(); j++){
                    Log.d("item in songNames ", j + " " + songNames.get(j));
                    String currDownloadedSong = songNames.get(j);
                    if(!downloadedSongs.contains(currDownloadedSong)) {
                        if (downloadManager.haveStoragePermission()) {
                            downloadManager.Download(songURLs.get(j));
                        }
                    }
                    else{
                        if(!alreadyDownloaded){
                            index = j;
                            alreadyDownloaded = true;
                        }
                    }
                }

                moveIndexToTop(index);

                String[] songURLsarr= new String[songURLs.size()];
                for( int i = 0; i < songURLs.size(); i++ ) {
                    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                            songURLs.get(i);
                    songURLsarr[i] = path;
                }


                //musicPlayer.setMusic(songURLsarr, 0);
                //musicPlayer.play();

                list = (ListView) findViewById(R.id.list_listofsongs);
                // context, database structure, data
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1 ,songNames);
                list.setAdapter(adapter);
                if (songNames.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Play some songs to use flashback mode.", Toast.LENGTH_SHORT).show();
                }

                //list.setOnItemClickListener(getApplicationContext());
            }
        });

     //   list = (ListView) findViewById(R.id.list_listofsongs);
        // context, database structure, data
    //    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 ,songNames);
    //    list.setAdapter(adapter);
       // list.setOnItemClickListener(this);
    }

    @Override
    public void update() {
        /*TextView lastTime = (TextView) findViewById(R.id.text_timeAndDateVibe);
        TextView lastLoc = (TextView) findViewById(R.id.text_locationVibe);
        TextView lastUsername = (TextView) findViewById(R.id.text_usernameVibe);

        String songName = metadataGetter.getName();

        firebaseDB.getLastSongPlayer(songName, System.currentTimeMillis(),new FirebaseQueryObserver() {
            @Override
            public void update(ArrayList<String> songNameList, ArrayList<String> songURLList, String latestAddress, String latestUser, long latestTime) {
                if(latestTime == 0){
                    lastLoc.setText("");
                    lastTime.setText("Song has not been played before!");
                    lastUsername.setText("");
                }
                else {
                    long time = latestTime / MILLISECODNS_IN_DAY;
                    LocalDate songDate = LocalDate.ofEpochDay(time);
                    lastTime.setText(songDate.toString());

                    lastLoc.setText(latestAddress);

                    String userToPrint = GoogleHelper.getDisplayName(latestUser);
                    lastUsername.setText(userToPrint);
                }
            }
        });*/
    }


    /**
     * convert the arraylist of files to songNames
     * @param downloadedSongFiles arraylist of files
     * @return arraylist os string songNames
     */
    private ArrayList<String> convertFileToSong(ArrayList<File> downloadedSongFiles){
        ArrayList<String> songNames = new ArrayList<>();
        for(File f : downloadedSongFiles){
            songNames.add(f.getName());
        }

        return songNames;
    }

    /**
     * shift the first downloaded song in to the top of the list to buy time to auto download the rest of songs
     * @param index index of first downloaded song
     */
    private void moveIndexToTop(int index){
        if(index == -1){
            return;
        }
        String tempName = songNames.get(index);
        String tempURL = songURLs.get(index);
        for(int i = 0; i < index; i++){
            songNames.set(i + 1, songNames.get(i));
            songURLs.set(i + 1, songURLs.get(i));
        }
        songNames.set(0, tempName);
        songURLs.set(0, tempURL);
    }

    /*
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView temp = (TextView) view;
        Toast.makeText(this, temp.getText()+ " row" + i, Toast.LENGTH_SHORT).show();
    }
    */

}
