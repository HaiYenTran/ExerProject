import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.sun.xml.internal.ws.api.pipe.SyncStartForAsyncFeature;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by yen.h.tran on 5/29/2017.
 */
public class ReaderJson {

    public static void main (String args[]){

            Gson gson = new Gson();
            try  {
                //Read file Json by FileReader
                JsonElement json = gson.fromJson(new FileReader("D:\\Document\\JsonWriter\\src\\main\\java\\data.json"), JsonElement.class);
                //Convert json to String
                String result = gson.toJson(json);
                //System.out.println(result);

                //Add result to Array list
                List <String> tmp = new ArrayList();
                tmp.add(result);
                //Print
                for( int i = 0; i < tmp.size(); i++){
                    System.out.println(result);
                }

            }catch(IOException ex)
            {
                System.out.println("IOException");
                ex.printStackTrace();
            }
 }
}
