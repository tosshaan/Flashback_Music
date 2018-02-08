package comf.example.tydia.cse_110_team_project_team_15_1;

import android.os.Bundle;
/*
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
*/
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SongsActivity extends AppCompatActivity {

    ListView list;
    // Need to get list of song names from the comf.example.tydia.cse_110_team_project_team_15_1.database
    String[] SongNames = {"song1", "song2", "song3", "song4", "song5", "song6", "song7", "song8", "song9"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button switchScreen = (Button) findViewById(R.id.btn_back2);

        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list = (ListView) findViewById(R.id.list_allsongs);
        // context, comf.example.tydia.cse_110_team_project_team_15_1.database structure, data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 ,SongNames);
        list.setAdapter(adapter);

    }

}
