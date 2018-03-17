package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import java.io.File;

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
        Log.d("path", pathx);
        path = Uri.parse("file://" + pathx);
        retriever.setDataSource(context, path);

    }

    public Uri getPath() {
        return path;
    }

    // Get song name based on ID
    public String getName() {
        String name = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if( name == null ) {
            //return " Unknown";
            File file = new File(path.toString());
            return file.getName().replace(".mp3", "").replace(".wav", "").replace("-", " ");
        }
        return name;
    }

    // Get song artist based on ID
    public String getArtist() {
        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if( artist == null ) {
            return " Unknown";
        }
        return artist;
    }

    // Get song album based on ID
    public String getAlbum() {
        String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        if( album == null ) {
            return " Unknown";
        }
        return album;
    }

    // Get track number based on ID
    public String getTrackNumber() {
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
    }

    public void release() {
        retriever.release();
    }
}
