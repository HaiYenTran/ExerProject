import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by yen.h.tran on 5/29/2017.
 */
public class HandleObject {
    public static class People {
        private String name;
        private String address;
        private Date dateOfBirth;
        private Integer age;
        private transient String secret;

        public People(String name, String address, Date dateOfBirth) {
            this.name = name;
            this.address = address;
            this.dateOfBirth = dateOfBirth;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
    public static void main(String[] args) {
        Calendar dob = Calendar.getInstance();
        dob.set(1980, Calendar.NOVEMBER, 11);
        People people = new People("John", "350 Banana St.", dob.getTime());
        people.setSecret("This is a secret!");

        Gson gson = new Gson();
        String json = gson.toJson(people);
        System.out.println("json = " + json);
    }

}
