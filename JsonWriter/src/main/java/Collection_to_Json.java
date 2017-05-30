import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by yen.h.tran on 5/29/2017.
 */
public class Collection_to_Json {
    public static class Student {
        private String name;
        private String address;
        private Date dateOfBirth;

        public Student (String name, String address, Date dateOfBirth) {
            this.name = name;
            this.address = address;
            this.dateOfBirth = dateOfBirth;
        }

        public String getName (){
            return name;
        }

        public void setName (String name){
            this.name = name;
        }

        public String getAddress (){
            return address;
        }

        public void setAddress (String address){
            this.address = address;
        }

        public Date getDOB(){
            return dateOfBirth;
        }

        public void setDateOfBirth(Date dateOfBirth){
            this.dateOfBirth = dateOfBirth;
        }

    }

    public static void main(String[] args) {
        // Converts a collection of string object into JSON string.
        List<String> names = new ArrayList<String>();
        names.add("Alice");
        names.add("Bob");
        names.add("Carol");
        names.add("Mallory");

        Gson gson = new Gson();
        String jsonNames = gson.toJson(names);
        System.out.println("jsonNames = " + jsonNames);

        // Converts a collection Student object into JSON string
        Student a = new Student("Alice", "Apple St", getDOB(2000, 10, 1));
        Student b = new Student("Bob", "Banana St", null);
        Student c = new Student("Carol", "Grape St", getDOB(2000, 5, 21));
        Student d = new Student("Mallory", "Mango St", null);

        List<Student> students = new ArrayList<Student>();
        students.add(a);
        students.add(b);
        students.add(c);
        students.add(d);

        gson = new Gson();
        String jsonStudents = gson.toJson(students);
        System.out.println("jsonStudents = " + jsonStudents);

        // Converts JSON string into a collection of Student object.
        Type type = new TypeToken<List<Student>>() {}.getType();
        List<Student> studentList = gson.fromJson(jsonStudents, type);

        for (Student student : studentList) {
            System.out.println("student.getName() = " + student.getName());
        }
    }

    private static Date getDOB(int year, int month, int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

}
