//사용자의 정보를 저장하는 클래스 User

package com.example.study;

import com.google.firebase.database.DatabaseReference;

public class User {

    //유저 데이터들들
   private String userName;
    private String email;
    private String address;
    private String gender;
    private String password;
    private String phone;
    private String myStudy;

    //Declaration of firebase references
    private DatabaseReference mDatabase;
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userName, String email, String password, String gender, String address, String phone) {
        this.userName = userName;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.password = password;
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String isGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMyStudy(){ return myStudy; }

    public void setMyStudy(String myStudy){
        this.myStudy = myStudy;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", P/W='" + password + '\'' +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", phone ='" + phone + '\'' +
                '}';
    }
}