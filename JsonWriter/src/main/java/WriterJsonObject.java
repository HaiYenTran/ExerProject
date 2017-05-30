
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.*;


/**
 * Created by yen.h.tran on 5/29/2017.
 */
public class WriterJsonObject {
    public static void main(String args[]) {

        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        // Create a new instance of Gson
        Gson gson = new Gson();
        String daysJson = gson.toJson(days);


        //Create JSONObject
        JsonObject obj = new JsonObject();
        obj.addProperty("id", new Integer(11));
        obj.addProperty("name", "mkyong.com");
        obj.addProperty("age", new Integer(34));


        try {
            //path create file
            String path = "D:\\Document\\JsonWriter\\src\\main\\resources\\JSONwrite.json" ;
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(path));

            //add daysJson
            outputStream.writeBytes(daysJson);
            //Convert Object to String
            String stringObject = obj.toString();
            outputStream.writeBytes(stringObject);

            outputStream.flush();
            outputStream.close();
            System.out.println(" Write file Success");

        }catch(FileNotFoundException ex)
        {
            ex.getStackTrace();
        }catch (IOException ex)
        {
            ex.getStackTrace();
        }

    }
}
