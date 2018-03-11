package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by Tydia on 3/10/18.
 */

public class SortSongs {

    private ArrayList<File> fileList = new ArrayList<File>();
    private String[] songNames;
    private ArrayList<Pair<String, File>> tuples;
    private MetadataGetter metadataGetter;

    public SortSongs(Context c) {
        metadataGetter = new MetadataGetter(c);
    }

    public void sortByTitle () {
        makeStrList();
        makeTuples();
        Collections.sort(tuples, comparator);
        fileList = sortedList();
    }

    public void sortByArtist() {
        makeArtistList();
        makeTuples();
        Collections.sort(tuples, comparator);
        fileList = sortedList();
    }

    public void sortByAlbum() {
        makeAlbumList();
        makeTuples();
        Collections.sort(tuples,comparator);
        fileList = sortedList();
    }

    public ArrayList<File> sortedList () {
        ArrayList<File> temp = new ArrayList<File>();
        for (int i=0; i<fileList.size(); i++) {
            temp.add(tuples.get(i).second);
        }
        return temp;
    }

    public void makeStrList() {
        songNames = new String[fileList.size()];
        for (int i=0; i<fileList.size(); i++) {
            songNames[i] = fileList.get(i).getName();
        }
    }

    public void makeArtistList () {

        songNames = new String[fileList.size()];
        for (int i=0; i<fileList.size(); i++) {
            metadataGetter.setPath(fileList.get(i).getPath());
            songNames[i] = metadataGetter.getArtist();
        }
    }

    public void makeAlbumList () {
        songNames = new String[fileList.size()];
        for (int i=0; i<fileList.size(); i++) {
            metadataGetter.setPath(fileList.get(i).getPath());
            songNames[i] = metadataGetter.getAlbum();
        }
    }

    public void makeTuples() {
        tuples = new ArrayList<>();
        for (int i=0; i<fileList.size(); i++) {
            Pair<String, File> tuple = new Pair<>(songNames[i], fileList.get(i));
            tuples.add(tuple);
        }
    }

    public ArrayList<File> returnlist() {
        return fileList;
    }

    public void setArrayList (ArrayList<File> arr_list) {
        fileList = arr_list;
    }

    Comparator<Pair<String, File>> comparator = new Comparator<Pair<String, File>>() {
        @Override
        public int compare(Pair<String, File> o1, Pair<String, File> o2) {
            if (o1.first == null || o2.first == null) {
                return -1;
            }
            return o1.first.compareTo(o2.first);
        }
    };
}
