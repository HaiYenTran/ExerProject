import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yen.h.tran on 5/29/2017.
 */
public class GsonWriter  {
    public static void main(String args[]){
    try {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", "mkyong.com");
        obj.addProperty("age", new Integer(100));

        //JsonArray list = new JsonArray();
        List<String> tmp2 = new ArrayList();
        tmp2.add("msg 0");
        tmp2.add("msg 1");
        tmp2.add("msg 2");
        tmp2.add("msg 8");

        String result ;
        result = new Gson().toJson(tmp2);

        System.out.println(result);

//        obj.add("messages", tmp2);

        try {
        FileWriter file = new FileWriter("D:\\test.json");
        file.write(result);
        file.close();


        }
        catch (Exception ex){

        }
//        try (JsonWriter file = new JsonWriter("D:\\test.json")) {
//
//            file.value(obj.toString());
//            file.flush();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.print(obj);

    }catch(JsonIOException ex)
    {
        System.out.println("JsonIOException ");
        ex.printStackTrace();
    }
}

}
