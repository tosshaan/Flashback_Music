package comf.example.tydia.cse_110_team_project_team_15_1;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class FlashbackActivity extends AppCompatActivity {

    ListView list;
    // Need to get list of song names from the database
    String[] songNames = {"song1", "song2", "song3", "song4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Button switchScreen = (Button) findViewById(R.id.normal_mode);

        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list = (ListView) findViewById(R.id.list_listofsongs);
        // context, database structure, data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 ,songNames);
        list.setAdapter(adapter);

    }

}
