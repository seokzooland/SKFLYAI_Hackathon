package skt_flyai.com.example.tmapapitest;

import android.content.SharedPreferences;

public class GetPost {
    private String userID;

    private String userPW;

    private String userName;

    private int userBirth;

    public String postID(String s) {

        return userID = s;
    }

    public String postPW(String s) {
        return userPW = s;
    }

    public String postName(String s) {
        return userName = s;
    }

    public int postBirth(int i) {
        return userBirth = i;
    }

    public String getID(String s) {
        return userID = s;
    }


}