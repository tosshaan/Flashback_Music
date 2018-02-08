package comf.example.tydia.cse_110_team_project_team_15_1;

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

public class AlbumSongsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list;
    // Need to get list of Album names from the database
    String[] AlbumNames = {"albumSong1", "albumSong2", "albumSong3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_songs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Button switchScreen = (Button) findViewById(R.id.btn_backAlbumSongs);

        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list = (ListView) findViewById(R.id.list_AlbumSongs);
        // context, database structure, data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 ,AlbumNames);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
    }

    // Item click method
    // int i is the index of the item clicked
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        TextView temp = (TextView) view;
        Toast.makeText(this, temp.getText()+ " row" + i, Toast.LENGTH_SHORT).show();
    }

}
