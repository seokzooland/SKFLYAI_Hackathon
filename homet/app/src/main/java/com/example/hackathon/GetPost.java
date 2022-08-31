package com.example.hackathon;


import android.content.SharedPreferences;


public class GetPost {
    private String userID;

    private String userPW;

    private String userName;

    private int userBirth;

    private String userAddress;

    private String userPhone;

    private String parentsPhone;

    private  String stt_Text;

    public String postID(String s) {
        return userID = s;
    }

    public String postPW(String s) {

        return userPW = s;
    }

    public String postName(String s) {

        return userName = s;
    }

    /* public int postBirth(int i) {

        return userBirth = i;
    } */

    public String postAddress(String s) {

        return userAddress = s;
    }

    public String getID() {
        return userID;

    }

    public String getName() {

        return userName;
    }

    public void stt_Text(String s){
         stt_Text = s;
    }

    public String getStt_Text(){
        return stt_Text;
    }

    public String postPhone(String s) {
        return userPhone = s;
    }

    public String postPhone2(String s) {
        return parentsPhone = s;
    }

}
