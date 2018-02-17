package comf.example.tydia.cse_110_team_project_team_15_1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by Graham on 2/15/2018.
 */

public class SongInfoSerializer implements JsonSerializer<SongInfo>{

    @Override
    public JsonElement serialize(SongInfo src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("time", src.timeGetter().getTime());
        obj.addProperty("name", src.songGetter());
        obj.addProperty("location", src.locGetter());
        obj.addProperty("liked", src.isLiked());
        obj.addProperty("disliked", src.isDisliked());
        return obj;
    }
}
