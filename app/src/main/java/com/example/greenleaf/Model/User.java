package com.example.greenleaf.Model;

public class User implements Comparable<User>{
    String phone;
    String firstName;
    String lastName;
    String password;
    String email;
    String image;

   public User(){

   }

    public User(String phone, String firstName, String lastName, String password, String email, String image) {
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {  return image; }

    public void setImage(String image) {    this.image = image; }

    @Override
    public int compareTo(User user) {
       return this.getEmail().replace(".","_DOT_").compareTo(user.getEmail().replace(".","_DOT_"));
    }
}
