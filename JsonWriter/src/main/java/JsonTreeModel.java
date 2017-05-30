import com.google.gson.*;

/**
 * Created by yen.h.tran on 5/30/2017.
 */
public class JsonTreeModel {
    public static void main (String args[]) {

        String jsonString =
                "{\"name\":\"Mahesh Kumar\", \"age\":21,\"verified\":false,\"marks\": [100,90,85]}";
        JsonParser parser = new JsonParser();
        //node using relative path to the root node while traversing the tree and process the data
        JsonElement rootNode = parser.parse(jsonString);

        if (rootNode.isJsonObject()) {
            JsonObject details = rootNode.getAsJsonObject();
            JsonElement nameNode = details.get("name");
            System.out.println("Name: " + nameNode.getAsString());

            JsonElement ageNode = details.get("age");
            System.out.println("Age: " + ageNode.getAsInt());

            JsonElement verifiedNode = details.get("verified");
            System.out.println("Verified: " + (verifiedNode.getAsBoolean() ? "Yes" : "No"));
            JsonArray marks = details.getAsJsonArray("marks");

            for (int i = 0; i < marks.size(); i++) {
                JsonPrimitive value = marks.get(i).getAsJsonPrimitive();
                System.out.print(value.getAsInt() + " ");
            }
        }
    }
}
