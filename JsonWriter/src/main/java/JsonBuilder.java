import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by yen.h.tran on 5/30/2017.
 */
public class JsonBuilder {
    class Student {
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

    public static void main(String[] args) {
        String jsonString = "{\"name\":\"Mahesh\", \"age\":21}";

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();

        Gson gson = builder.create();
        Student student = gson.fromJson(jsonString, Student.class);
        System.out.println(student);

        jsonString = gson.toJson(student);
        System.out.println(jsonString);
    }

}
