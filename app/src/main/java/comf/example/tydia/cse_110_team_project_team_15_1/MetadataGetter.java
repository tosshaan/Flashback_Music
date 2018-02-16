package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

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

    // Get song name based on ID
    public String getName(int id) {
        path = Uri.parse("android.resource://" + MainActivity.PACKAGE_NAME + "/" + id);
        retriever.setDataSource(context, path);
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
    }

    // Get song artist based on ID
    public String getArtist(int id) {
        path = Uri.parse("android.resource://" + MainActivity.PACKAGE_NAME + "/" + id);
        retriever.setDataSource(context, path);
        String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        if( artist == null ) {
            return "Unknown";
        }
        return artist;
    }

    // Get song album based on ID
    public String getAlbum(int id) {
        path = Uri.parse("android.resource://" + MainActivity.PACKAGE_NAME + "/" + id);
        retriever.setDataSource(context, path);
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
    }

    // Get track number based on ID
    public String getTrackNumber(int id) {
        path = Uri.parse("android.resource://" + MainActivity.PACKAGE_NAME + "/" + id);
        retriever.setDataSource(context, path);
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
    }

    public void release() {
        retriever.release();
    }

}

