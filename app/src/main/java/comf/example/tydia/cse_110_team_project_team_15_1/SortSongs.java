package comf.example.tydia.cse_110_team_project_team_15_1;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by Tydia on 3/10/18.
 */

public class SortSongs {

    private ArrayList<File> fileList = new ArrayList<File>();
    private String songNames[];

    public SortSongs(ArrayList<File> list) {
        fileList = list;
    }

    public void sortByName () {
        fileList = ViewDLSongsActivity.findSong(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
        songNames = new String[fileList.size()];
        for (int i = 0; i<=fileList.size(); i++) {
            songNames[i] = fileList.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
        }
        Collections.sort(fileList);
    }

    public ArrayList<File> returnlist() {
        return fileList;
    }
}
