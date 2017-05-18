import java.io.File;

/**
 * Created by yen.h.tran on 5/15/2017.
 */
public class ReadDir {
    public static void main(String args[]){
        File file = null;
        String[] paths;

        try{
            //create new file
            file = new File("/temp");
            //move file in folder
            paths = file.list();
            //print file name in folder
            for (String path:paths)
            {
                System.out.println(path);
            }
        }catch(Exception e){
            //if have error
            e.printStackTrace();
        }
    }
}
