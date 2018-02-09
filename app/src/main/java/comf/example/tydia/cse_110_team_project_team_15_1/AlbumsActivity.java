package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.util.ArrayList;

public class AlbumsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list;
    // Need to get list of Album names from the comf.example.tydia.cse_110_team_project_team_15_1.database

  //  File rawContents = Environment.getDataDirectory();
  //  String path = "C:\\Users\\thapa\\AndroidStudioProjects\\cse-110-team-project-team-15-1\\app\\src\\main\\res\\raw";
  //  File rawDir = new File(path);
  //  ArrayList<String> AlbumNames = getAlbumNames(rawContents);

    String[] AlbumNames = {"album1", "album2", "album3", "album4", "album5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button switchScreen = (Button) findViewById(R.id.btn_back);

        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        list = (ListView) findViewById(R.id.list_allalbums);
        // context, database structure, data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 ,AlbumNames);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);


        // launch flashback (temp)
        final Button launchFlashbackActivity = (Button) findViewById(R.id.b_flashback_album);
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

        launchAlbumSongs();
    }

    public void launchAlbumSongs() {
        Intent intent = new Intent (this, AlbumSongsActivity.class);
        startActivity(intent);
    }

    public void launchFlashback() {
        Intent intent = new Intent (this, FlashbackActivity.class);
        startActivity(intent);
    }

    public ArrayList<String> getAlbumNames(File dir ) {
        ArrayList<String> albumNames = new ArrayList<String>();
        for( File f : dir.listFiles() ) {
            albumNames.add( f.getName() );
        }
        return albumNames;

    }

}
