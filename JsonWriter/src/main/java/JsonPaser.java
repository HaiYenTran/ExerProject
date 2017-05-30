import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by yen.h.tran on 5/30/2017.
 */
public class JsonPaser {
    public static void main(String args[]){
        //parse a JSON string or stream into a tree structure of Java objects.
        JsonParser parser = new JsonParser();

        String json = "{ \"f1\":\"Hello\",\"f2\":{\"f3:\":\"World\"}}";

        JsonElement jsonTree = parser.parse(json);

        if(jsonTree.isJsonObject()){
            JsonObject jsonObject = jsonTree.getAsJsonObject();

            JsonElement f1 = jsonObject.get("f1");

            JsonElement f2 = jsonObject.get("f1");

            if(f2.isJsonObject()){
                JsonObject f2Obj = f2.getAsJsonObject();

                JsonElement f3 = f2Obj.get("f3");
            }
        }
        System.out.println(jsonTree);
    }
}
