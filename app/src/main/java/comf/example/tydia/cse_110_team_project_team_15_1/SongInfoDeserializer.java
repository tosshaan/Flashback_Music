package comf.example.tydia.cse_110_team_project_team_15_1;

import android.location.Location;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.sql.Timestamp;

/**
 * Created by Graham on 2/15/2018.
 */

/**
 * Parases songs info objects into strings so they can be stored properly
 */
public class SongInfoDeserializer implements JsonDeserializer<SongInfo>{

    @Override
    public SongInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject o = json.getAsJsonObject();
        String loc = o.getAsJsonPrimitive("location").getAsString();
        Timestamp time = new Timestamp(o.getAsJsonPrimitive("time").getAsLong());
        if(time.equals(new Timestamp(0))){
            time = null;
        }
        String name = o.getAsJsonPrimitive("name").getAsString();
        boolean liked = o.getAsJsonPrimitive("liked").getAsBoolean();
        boolean disliked = o.getAsJsonPrimitive("disliked").getAsBoolean();
        return new SongInfo(time, name, loc, liked, disliked);
    }
}
