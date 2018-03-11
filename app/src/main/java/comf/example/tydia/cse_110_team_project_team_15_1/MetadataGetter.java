package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.time.LocalTime;

import comf.example.tydia.cse_110_team_project_team_15_1.MainActivity;

/**
 * Created by Cadu on 15-Feb-18.
 * This class serves to encapsulate the MetadataRetriever in order to
 * clean up the code and allow us to use MetadataRetriever outside of
 * activities
 */

public class MetadataGetter {
    Uri path;
    MediaMetadataRetriever retriever;
    Context context;




    public MetadataGetter( Context context) {
        this.context = context;
        retriever = new MediaMetadataRetriever();
    }

    public void setPath(String pathx) {
        path = Uri.parse("file://" + pathx);
        retriever.setDataSource(context, path);

    }

    // Get song name based on ID
    public String getName() {
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
    }

    // Get song artist based on ID
    public String getArtist() {
        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if( artist == null ) {
            return "Unknown";
        }
        return artist;
    }

    // Get song album based on ID
    public String getAlbum() {
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
    }

    // Get track number based on ID
    public String getTrackNumber() {
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
    }

    public void release() {
        retriever.release();
    }
}
