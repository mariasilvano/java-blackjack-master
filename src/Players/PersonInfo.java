package Players;

import java.io.*;

public class PersonInfo {
    private String name;
    private int age;
    private String gender;

    /**
     * @param name
     * @param age
     * @param gender
     */
    public PersonInfo(String name, int age, String gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    
    public void setName(String name) {
        this.name = name;
    }


    public void setAge(int age) {
        this.age = age;
    }


    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }


}
