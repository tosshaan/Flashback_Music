package comf.example.tydia.cse_110_team_project_team_15_1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        database db = new database();

        final Button launchFlashbackActivity = (Button) findViewById(R.id.b_flashback);

        launchFlashbackActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchFlashback();
            }
        });

        final Button launchAlbums = (Button) findViewById(R.id.button_albums);
        launchAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAlbums();
            }
        });
        final Button launchSongs = (Button) findViewById(R.id.button_songs);
        launchSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSongs();
            }
        });

    }

    public void launchFlashback() {
        Intent intent = new Intent (this, FlashbackActivity.class);
        startActivity(intent);
    }
    public void launchAlbums() {
        Intent intent = new Intent (this, AlbumsActivity.class);
        startActivity(intent);
    }
    public void launchSongs() {
        Intent intent = new Intent (this, SongsActivity.class);
        startActivity(intent);
    }
}
