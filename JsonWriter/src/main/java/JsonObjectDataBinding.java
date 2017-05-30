import com.google.gson.Gson;

/**
 * Created by yen.h.tran on 5/30/2017.
 */
public class JsonObjectDataBinding {
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
        public static void main(String args[]) {
        Gson gson = new Gson();
        Student student = new Student();
        student.setAge(10);
        student.setName("Mahesh");

        String jsonString = gson.toJson(student);
        System.out.println(jsonString);

        Student student1 = gson.fromJson(jsonString, Student.class);
        System.out.println(student1);
    }

}
