package ass.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JsonUtility {
    static final private Gson gson = new GsonBuilder().create();

    static public <T> T fromJson(String jsonString, Class<T> clazz) {

        return gson.fromJson(jsonString, clazz);
    }

    static public String toJson(Object object) {
        return gson.toJson(object);
    }
}
