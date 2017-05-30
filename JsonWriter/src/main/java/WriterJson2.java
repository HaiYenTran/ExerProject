import com.google.gson.Gson;

/**
 * Created by yen.h.tran on 5/29/2017.
 */
public class WriterJson2 {
    public static void main (String args[])
    {
     int[] numbers = {1, 1, 2, 3, 5, 8, 13};
     String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

     // Create a new instance of Gson
     Gson gson = new Gson();

     // Convert numbers array into JSON string.
     String numbersJson = gson.toJson(numbers);

     // Convert strings array into JSON string
     String daysJson = gson.toJson(days);
     System.out.println("numbersJson = " + numbersJson);
     System.out.println("daysJson = " + daysJson);

     // Convert from JSON string to a primitive array of int.
     int[] fibonacci = gson.fromJson(numbersJson, int[].class);
     for (int number : fibonacci) {
        System.out.print(number + " ");
     }
     System.out.println("");

     // Convert from JSON string to a string array.
     String[] weekDays = gson.fromJson(daysJson, String[].class);
     for (String weekDay : weekDays) {
         System.out.print(weekDay + " ");
     }

     // Converting multidimensional array into JSON
     int[][] data = {{1, 2, 3}, {3, 4, 5}, {4, 5, 6}};
        String json = gson.toJson(data);
        System.out.println("Data = " + json);

     // Convert JSON string into multidimensional array of int.
     int[][] dataMap = gson.fromJson(json, int[][].class);
     for (int[] i : dataMap) {
        for (int j : i) {
         System.out.print(j + " ");
            }
     System.out.println("");
        }
    }

}

