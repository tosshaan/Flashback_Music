package JUnitTests;

import android.os.Environment;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import comf.example.tydia.cse_110_team_project_team_15_1.AlbumsActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.MetadataGetter;
import comf.example.tydia.cse_110_team_project_team_15_1.R;
import comf.example.tydia.cse_110_team_project_team_15_1.SongsActivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by tosshaan on 2/14/2018.
 */

public class MetadataTester {

    //@Rule
    //public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Rule
    public ActivityTestRule<SongsActivity> songsActivity = new ActivityTestRule<>(SongsActivity.class);

    MetadataGetter metadataGetter;

    @Before
    public void setup() {
        metadataGetter = new MetadataGetter(songsActivity.getActivity().getApplicationContext());
    }

    @Test
    public void testGetSongName() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() +
                "/https:/www.soundjay.com/button/button-2.mp3";
        metadataGetter.setPath(path);
        assertEquals(metadataGetter.getName(), "button 2");
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() +
                "/https:/www.soundjay.com/button/sounds/button-3.mp3";
        metadataGetter.setPath(path);
        assertEquals(metadataGetter.getName(), "button 3");
    }

    @Test
    public void testGetArtistName() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() +
                "/https:/www.soundjay.com/button/button-2.mp3";
        metadataGetter.setPath(path);
        assertEquals(metadataGetter.getArtist(), "SoundJay.com Sound Effects");
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() +
                "/https:/www.soundjay.com/button/sounds/button-3.mp3";
        metadataGetter.setPath(path);
        assertEquals(metadataGetter.getArtist(), "SoundJay.com Sound Effects");
    }

    @Test
    public void testGetAlbumName() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() +
                "/https:/www.soundjay.com/button/button-2.mp3";
        metadataGetter.setPath(path);
        assertEquals(metadataGetter.getAlbum(), "Unknown");
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() +
                "/https:/www.soundjay.com/button/sounds/button-3.mp3";
        metadataGetter.setPath(path);
        assertEquals(metadataGetter.getAlbum(), "Unknown");
    }

}
