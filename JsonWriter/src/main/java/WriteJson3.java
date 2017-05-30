import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

/**
 * Created by yen.h.tran on 5/30/2017.
 */
public class WriteJson3 {

    static class Student {
        private String name;
        private int age;

        public Student() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String toString() {
            return "Student [ name: " + name + ", age: " + age + " ]";
        }
    }

    private void writeJSON(Student student) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        FileWriter writer = new FileWriter("student.json");
        writer.write(gson.toJson(student));
        writer.close();
    }

    private Student readJSON() throws FileNotFoundException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        BufferedReader bufferedReader = new BufferedReader(
                new FileReader("student.json"));

        Student student = gson.fromJson(bufferedReader, Student.class);
        return student;
    }

    public static void main(String args[]) {
        WriteJson3 tester = new WriteJson3();
        try {
            Student student = new Student();
            student.setAge(10);
            student.setName("Mahesh");
            tester.writeJSON(student);
            System.out.println("Write success");
            Student student1 = tester.readJSON();
            System.out.println(student1);
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

}


