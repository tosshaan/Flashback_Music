package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SongInfoActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private static int MEDIA_RES_ID = 0;
    private boolean playFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // hide action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        MEDIA_RES_ID = getIntent().getIntExtra("songID", 0);
        String songName = getIntent().getStringExtra("songName");

        // Creating metadata retriever
        Uri path = Uri.parse("android.resource://" + getPackageName() + "/" + MEDIA_RES_ID);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, path);

        loadMedia(MEDIA_RES_ID);
        TextView showMetadata = (TextView) findViewById(R.id.text_SongName);
        showMetadata.setText("Title: " + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) + "\nArtist: " + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) + "\nAlbum: " + retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));

        mediaPlayer.start();

        // play and pause music

        final Button playButton = (Button) findViewById(R.id.button_play2);
        final Button pauseButton = (Button) findViewById(R.id.button_pause2);
        playButton.setVisibility(View.GONE);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
                if (playFlag == true) {
                    pauseButton.setVisibility(View.GONE);
                    playFlag = false;
                } else {
                    pauseButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);
                }
            }
        });


        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
                playFlag = true;
                playButton.setVisibility(View.VISIBLE);
                System.out.println(playFlag);
                if (playFlag == true) {
                    pauseButton.setVisibility(View.GONE);
                    playFlag = false;
                } else {
                    pauseButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);
                }

            }
        });


        Button switchScreen = (Button) findViewById(R.id.button_songInfoBack);

        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // launch flashback (temp)
        final Button launchFlashbackActivity = (Button) findViewById(R.id.b_flashback_songinfo);
        launchFlashbackActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchFlashback();
            }
        });
    }

    public void launchFlashback() {
        Intent intent = new Intent (this, FlashbackActivity.class);
        startActivity(intent);
    }

    public void loadMedia(int resourceId) {
        if(mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
        AssetFileDescriptor assetFileDescriptor = this.getResources().openRawResourceFd(resourceId);
        try {
            mediaPlayer.setDataSource(assetFileDescriptor);
            mediaPlayer.prepareAsync();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isChangingConfigurations() && mediaPlayer.isPlaying()) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }
}
