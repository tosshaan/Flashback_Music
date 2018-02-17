package test1;

import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import comf.example.tydia.cse_110_team_project_team_15_1.AlbumsActivity;
import comf.example.tydia.cse_110_team_project_team_15_1.R;
import comf.example.tydia.cse_110_team_project_team_15_1.SongsActivity;
import static org.junit.Assert.assertEquals;

/**
 * Created by tosshaan on 2/14/2018.
 */

public class JUnitTest01 {

    //@Rule
    //public ActivityTestRule<SongsActivity> songsActivity = new ActivityTestRule<>(SongsActivity.class);

    int[] idsTest;

    @Before
    public void setup() {
        idsTest = SongsActivity.getSongIDs();
    }

    @Test
    public void testGetSongIDs(){
        // Checking that indexes match expected values
        assertEquals(R.raw.afterthestorm, idsTest[1]);
        assertEquals(R.raw.windowsaretheeyestothehouse, idsTest[idsTest.length -1 ]);
        // Checking if length is correct
        assertEquals(45, idsTest.length );
    }

    /*
    @Test
    public void testGetSongNames() {
        String[] songNames = songsActivity.getActivity().getSongNames(idsTest);
        // Checking names match expected values
        assertEquals("123 Go", songNames[0]);
        assertEquals("Windows Are The Eyes To The House", songNames[idsTest.length -1]);
        assertEquals(45, songNames.length);
    }
    */

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

}
