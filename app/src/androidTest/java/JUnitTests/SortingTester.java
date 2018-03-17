package JUnitTests;

import android.support.test.rule.ActivityTestRule;
import android.util.Pair;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import comf.example.tydia.cse_110_team_project_team_15_1.AlbumsActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.MetadataGetter;
import comf.example.tydia.cse_110_team_project_team_15_1.R;
import comf.example.tydia.cse_110_team_project_team_15_1.SongsActivity;

import static org.junit.Assert.assertEquals;

/**
 * Created by tydia on 3/13/18.
 */

public class SortingTester {
    //@Rule
    //public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    /*
    @Rule
    public ActivityTestRule<SongsActivity> songsActivity = new ActivityTestRule<>(SongsActivity.class);

    private ArrayList<File> fileList = new ArrayList<File>();
    private String[] songNames;
    private ArrayList<Pair<String, File>> tuples;
    private MetadataGetter metadataGetter;

    @Before
    public void setup() {
        idsTest = SongsActivity.getSongIDs();
        metadataGetter = new MetadataGetter(albumsActivity.getActivity());
    }

    @Test
    public void testGetSongIDs(){
    }

    @Test
    public void tesGetSongName() {
    }

    @Test
    public void testGetAlbum() {
    }

    @Test
    public void testGetArtist() {
        assertEquals(metadataGetter.getArtist(idsTest[3]),"Unknown");
        assertEquals(metadataGetter.getArtist(idsTest[7]),"Stacy Jones");
        assertEquals(metadataGetter.getArtist(idsTest[9]),"Keaton Simons");
    }


    @Test
    public void testGetSongNames() {
        String[] songNames = songsActivity.getActivity().getSongNames(idsTest);
        // Checking names match expected values
        assertEquals("123 Go", songNames[0]);
        assertEquals("Windows Are The Eyes To The House", songNames[idsTest.length -1]);
        assertEquals(45, songNames.length);
    }

    @Rule
    public ActivityTestRule<AlbumsActivity> albumsActivity = new ActivityTestRule<>(AlbumsActivity.class);

    @Test
    public void testGetAlbumNames() {
        String[] albumNames = albumsActivity.getActivity().getAlbumNames(idsTest);
        // Checking names match expected values
        assertEquals("I Will Not Be Afraid (A Sampler)", albumNames[0]);
        // Checking length
        assertEquals(5, albumNames.length);
        assertEquals("Take Yourself Too Seriously", albumNames[albumNames.length - 1]);
    }

    @Test
    public void testGetAlbumSongs() {
        int[] testSongArr = albumsActivity.getActivity().getAlbumSongs(0);
        //test length
        assertEquals(7, testSongArr.length);
        //testing songs
        assertEquals(R.raw.americareligious, testSongArr[0]);
        assertEquals(R.raw.whenyougo, testSongArr[testSongArr.length - 1]);

    }
    */
}

