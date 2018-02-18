package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Graham on 2/14/2018.
 */

public class DatabaseStorageFunctions {
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
        System.out.println("Saved database");
    }
    public static database retreiveDatabase(Context context){
        SharedPreferences dataPref = context.getSharedPreferences("database", MODE_PRIVATE);
        String dataString = dataPref.getString("data","");
        if(dataString.equals("")){
            System.out.println("Didn't find an existing database");
            database db = new database();
            return db;
        }
        else{
            System.out.println("Found database");
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(SongInfo.class, new SongInfoDeserializer());
            builder.registerTypeAdapter(SongInfo.class, new SongInfoSerializer());
            Gson reader = builder.create();
            database db = reader.fromJson(dataString, database.class);
            System.out.println("Ran here");
            return db;
        }
    }
}
