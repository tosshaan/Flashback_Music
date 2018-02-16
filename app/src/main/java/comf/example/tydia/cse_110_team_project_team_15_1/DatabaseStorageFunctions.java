package comf.example.tydia.cse_110_team_project_team_15_1;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Graham on 2/14/2018.
 */

public class DatabaseStorageFunctions {
    public static void storeDatabase(database currentState, Context context){
        SharedPreferences dataPref = context.getSharedPreferences("database", MODE_PRIVATE);
        SharedPreferences.Editor saver = dataPref.edit();
        Gson compressor = new Gson();
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
            Gson reader = new Gson();
            database db = reader.fromJson(dataString, database.class);
            return db;
        }
    }
}
