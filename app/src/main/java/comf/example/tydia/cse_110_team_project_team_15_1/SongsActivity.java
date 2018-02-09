package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class SongsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list;
    // Need to get list of song names from the database

    private String[] SongNames = {"everythingilove", "sweetsuejustyou", "heythere", "perdido", "whatsyourstorymorningglory", "allaboutronnie", "justintime", "igetackickoutofyou", "thisisalways", "whocares", "outofthisworld", "inthestillofthenight"};
    private int[] IDs = {R.raw.everythingilove, R.raw.sweetsuejustyou , R.raw.heythere, R.raw.perdido, R.raw.whatsyourstorymorningglory, R.raw.allaboutronnie, R.raw.justintime, R.raw.igetakickoutofyou, R.raw.thisisalways,
                         R.raw.whocares, R.raw.outofthisworld, R.raw.inthestillofthenight};

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
        // context, database structure, data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 ,SongNames);
        list.setAdapter(adapter);

        list.setOnItemClickListener(this);

        // launch flashback (temp)
        final Button launchFlashbackActivity = (Button) findViewById(R.id.b_flashback_song);
        launchFlashbackActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchFlashback();
            }
        });
    }

    // Item click method
    // int i is the index of the item clicked
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        TextView temp = (TextView) view;
        Toast.makeText(this, temp.getText()+ " row" + i, Toast.LENGTH_SHORT).show();

        launchSongInfoAct(i);
    }

    public void launchSongInfoAct(int i) {
        Intent intent = new Intent (this, SongInfoActivity.class);
        intent.putExtra("song1", IDs[i]);
        intent.putExtra("songName", SongNames[i]);
        startActivity(intent);
    }

    public void launchFlashback() {
        Intent intent = new Intent (this, FlashbackActivity.class);
        startActivity(intent);
    }



}
