package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Graham on 2/14/2018.
 */

/**
 * Class to store database
 */
public class DatabaseStorageFunctions {

    /**
     * stores database
     * @param currentState - databae object
     * @param context - context
     */
    public static void storeDatabase(database currentState, Context context){
        SharedPreferences dataPref = context.getSharedPreferences("database", MODE_PRIVATE);
        SharedPreferences.Editor saver = dataPref.edit();
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(SongInfo.class, new SongInfoDeserializer());
        builder.registerTypeAdapter(SongInfo.class, new SongInfoSerializer());
        Gson compressor = builder.create();
        String dataString = compressor.toJson(currentState);
        saver.putString("data", dataString);
        saver.apply();
        Log.d("storage", "Saved database");
    }

    /**
     * retreieves database
     * @param context - context
     * @return database object
     */
    public static database retreiveDatabase(Context context){
        SharedPreferences dataPref = context.getSharedPreferences("database", MODE_PRIVATE);
        String dataString = dataPref.getString("data","");
        if(dataString.equals("")){
            Log.d("storage", "Didn't find an existing database");
            database db = new database();
            return db;
        }
        else{
            Log.d("storage", "Found database");
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(SongInfo.class, new SongInfoDeserializer());
            builder.registerTypeAdapter(SongInfo.class, new SongInfoSerializer());
            Gson reader = builder.create();
            database db = reader.fromJson(dataString, database.class);
            return db;
        }
    }
}
