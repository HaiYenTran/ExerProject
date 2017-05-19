/**
 * Created by yen.h.tran on 5/15/2017.
 */
public class JavaCatString {
    public static void main(String args[]){
        String str1 = "Hello";
        String str2 = " world";

        // By "+"
        String str = str1 + str2;
        //use StringBuilder or String Buffer
       // String str = (new StringBuilder()).append("Hello").append(" world").toString();
        System.out.println(" String by '+': " +str);


        // By concat()
        String str3 = str1.concat(str2);
        System.out.println("String by concat(): " +str);

        //getByte()
        byte[] byteString = str1.getBytes();
        for ( int i = 0; i< byteString.length; i++){
            System.out.println(byteString[i]);
        }

        //hashCode
        int hashCode = str.hashCode();
        System.out.println("Hash code of str: " +hashCode);
    }
}
