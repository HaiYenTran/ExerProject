import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by yen.h.tran on 5/15/2017.
 */

/*
public class ReadFileJava {
    public static void main(String args[]){
        try {
            BufferedReader in = new BufferedReader(new FileReader("D:\\My Doucument\\JavaIOFile\\test1.txt"));
            String str;

            while ((str = in.readLine()) != null) {
                System.out.println(str);
            }
            System.out.println(str);
        } catch (IOException e) {
        }
    }
}


*/
public class ReadFileJava {
    public static void main(String args[]) throws IOException {
        FileReader in = new FileReader("D:\\My Doucument\\JavaIOFile\\test1.txt");
    BufferedReader br = new BufferedReader(in);

    while (br.readLine() != null) {
        System.out.println(br.readLine());
    }
    in.close();

    }
}


