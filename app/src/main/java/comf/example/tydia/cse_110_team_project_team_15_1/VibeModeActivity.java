package comf.example.tydia.cse_110_team_project_team_15_1;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class VibeModeActivity extends AppCompatActivity {

    VibeModeList VMList;
    FirebaseDB firebaseDB;
    ListView list;
    database myData;
    private myMusicPlayer musicPlayer;
    MetadataGetter metadataGetter;
    ArrayList<String> songNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myData = MainActivity.data;
        VMList = new VibeModeList(myData);
        firebaseDB = new FirebaseDB();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibe_mode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        String userName = "user";

        firebaseDB.getAllSongsForVibe(currAddress, currDate, userName, new FirebaseQueryObserver() {
            @Override
            public void update(ArrayList<String> songNameList, ArrayList<String> songURLList) {
                VMList.generateList(songNameList, songURLList);
                songNames = VMList.getVibeModeSongs();
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

    /*
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        TextView temp = (TextView) view;
        Toast.makeText(this, temp.getText()+ " row" + i, Toast.LENGTH_SHORT).show();
    }
    */

}
