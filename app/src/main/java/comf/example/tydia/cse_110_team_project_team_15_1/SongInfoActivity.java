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
import android.widget.Toast;

import java.io.IOException;

public class SongInfoActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    MetadataGetter metadataGetter;
    private static int MEDIA_RES_ID = 0;
    private boolean playFlag = true;
    private int songIndex;
    private int [] albumSongsIDs;
    private boolean albumMode = true;

    database myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myData = DatabaseStorageFunctions.retreiveDatabase(getApplicationContext());

        // hide action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        MEDIA_RES_ID = getIntent().getIntExtra("songID", 0);
        String songName = getIntent().getStringExtra("songName");
        Bundle bundle = getIntent().getExtras();
        albumMode = getIntent().getBooleanExtra("albumMode", false);
        if (albumMode) {
            albumSongsIDs = bundle.getIntArray("albumSongsIDs");
            songIndex = getIntent().getIntExtra("songIndex", 0);
        }

        mediaPlayer = MediaPlayer.create(this, MEDIA_RES_ID);
        mediaPlayer.start();

        // Storing info from song to database
        try {
            myData.startSongInfoRequest(songName, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Creating metadatagetter
        metadataGetter = new MetadataGetter(this);

        TextView showMetadata = (TextView) findViewById(R.id.text_SongName);
        showMetadata.setText("Title: " + songName + "\nArtist: " + metadataGetter.getArtist(MEDIA_RES_ID) + "\nAlbum: " + metadataGetter.getAlbum(MEDIA_RES_ID));

        metadataGetter.release();

        // play and pause music

        final Button playButton = (Button) findViewById(R.id.button_play2);
        final Button pauseButton = (Button) findViewById(R.id.button_pause2);
        playButton.setVisibility(View.GONE);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    Log.d("tag", "index = " + songIndex + " // arraysize = " + albumSongsIDs.length );
            //    Log.d("songid", "songid = " + albumSongsIDs[songIndex] );
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

        // finish playing a song
        if(albumMode) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayerX) {
                    if (songIndex < (albumSongsIDs.length - 1)) {
                        songIndex++;
                        mediaPlayer.reset();
                        MEDIA_RES_ID = albumSongsIDs[songIndex];

                        mediaPlayer = MediaPlayer.create(getApplicationContext(), MEDIA_RES_ID);
                        mediaPlayer.start();

                        //loadMedia(albumSongsIDs[songIndex]);
                        Uri path2 = Uri.parse("android.resource://" + getPackageName() + "/" + MEDIA_RES_ID);

                        MediaMetadataRetriever retriever2 = new MediaMetadataRetriever();
                        retriever2.setDataSource(getApplicationContext(), path2);

                        TextView showMetadata2 = (TextView) findViewById(R.id.text_SongName);
                        showMetadata2.setText("Title: " + retriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) + "\nArtist: " + retriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) + "\nAlbum: " + retriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));

                        retriever2.release();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "End of album", Toast.LENGTH_SHORT).show();
                        mediaPlayer.reset();
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), MEDIA_RES_ID);
                    }
                }
            });

            Button nextButton = (Button) findViewById(R.id.button_next2);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (songIndex < (albumSongsIDs.length - 1)) {
                        songIndex++;
                        mediaPlayer.reset();
                        MEDIA_RES_ID = albumSongsIDs[songIndex];

                        mediaPlayer = MediaPlayer.create(getApplicationContext(), MEDIA_RES_ID);
                        mediaPlayer.start();

                        //loadMedia(albumSongsIDs[songIndex]);
                        Uri path2 = Uri.parse("android.resource://" + getPackageName() + "/" + MEDIA_RES_ID);

                        MediaMetadataRetriever retriever2 = new MediaMetadataRetriever();
                        retriever2.setDataSource(getApplicationContext(), path2);

                        TextView showMetadata2 = (TextView) findViewById(R.id.text_SongName);
                        showMetadata2.setText("Title: " + retriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) + "\nArtist: " + retriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) + "\nAlbum: " + retriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));

                        retriever2.release();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "End of album", Toast.LENGTH_SHORT).show();
                        mediaPlayer.reset();
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), MEDIA_RES_ID);
                    }
                }
            });
        }

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

        Button dislikeButton = (Button) findViewById(R.id.button_dislike2);
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dislike song
                // stop song and skip song
            }
        });



        Button switchScreen = (Button) findViewById(R.id.button_songInfoBack);

        switchScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseStorageFunctions.storeDatabase(myData, getApplicationContext());
                finish();
            }
        });

        // launch flashback (temp)
        final Button launchFlashbackActivity = (Button) findViewById(R.id.b_flashback_songinfo);
        launchFlashbackActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseStorageFunctions.storeDatabase(myData, getApplicationContext());
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
