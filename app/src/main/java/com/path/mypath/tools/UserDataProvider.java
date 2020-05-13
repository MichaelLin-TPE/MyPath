package com.path.mypath.tools;

import android.content.Context;
import android.content.SharedPreferences;

public class UserDataProvider {

    private static UserDataProvider instance = null;

    private SharedPreferences sharedPreferences;


    private UserDataProvider(Context context){
        sharedPreferences = context.getSharedPreferences("userData",Context.MODE_PRIVATE);
    }

    public static UserDataProvider getInstance(Context context){
        if (instance == null){
            instance = new UserDataProvider(context);
        }
        return instance;
    }

    public void saveUserEmail(String email){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email",email);
        editor.apply();
    }

    public void saveUserNickname(String nickname){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nickname",nickname);
        editor.apply();
    }
    public void saveUserPhotoUrl(String photoUrl){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("photo_url",photoUrl);
        editor.apply();
    }
    public void saveUserSentence(String sentence){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sentence",sentence);
        editor.apply();
    }


    public String getSentence(){
        return sharedPreferences.getString("sentence","");
    }

    public String getUserEmail(){
        return sharedPreferences.getString("email","");
    }

    public String getUserNickname(){
        return sharedPreferences.getString("nickname","");
    }
    public String getUserPHotoUrl(){
        return sharedPreferences.getString("photo_url","");
    }




}
