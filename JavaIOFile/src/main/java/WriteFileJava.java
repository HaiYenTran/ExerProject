import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by yen.h.tran on 5/15/2017.
 */
public class WriteFileJava {
    public static void main(String args[]) {

        try {
            java.lang.String content = "TutorialsPoint is one the best site in the world";
            File file = new File("D:\\My Doucument\\JavaIOFile\\test1.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

            System.out.println("Done");
        } catch (
                IOException e)

        {
            e.printStackTrace();
        }

    }
}
