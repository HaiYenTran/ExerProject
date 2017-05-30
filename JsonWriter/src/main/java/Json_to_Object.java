import com.google.gson.Gson;

import java.util.Date;

/**
 * Created by yen.h.tran on 5/29/2017.
 */
public class Json_to_Object {
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

        public Date getDateOfBirth(){
            return dateOfBirth;
        }

        public void setDateOfBirth(Date dateOfBirth){
            this.dateOfBirth = dateOfBirth;
        }

    }

    public static void main(String[] args) {
        String json = "{" +
                "'name' : 'Duke'," +
                "'address' : 'Menlo Park'," +
                "'dateOfBirth' : 'Feb 1, 2000 12:00:00 AM'" +
                "}";

        Gson gson = new Gson();
        Student student = gson.fromJson(json, Student.class);

        System.out.println("s.getName()        = " + student.getName());
        System.out.println("s.getAddress()     = " + student.getAddress());
        System.out.println("s.getDateOfBirth() = " + student.getDateOfBirth());
    }

}
