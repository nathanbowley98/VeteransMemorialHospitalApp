package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.content.SharedPreferences;

import java.util.HashMap;

public class Session  {

    private static Context context;
    private static SharedPreferences sharedPref;
    private static SharedPreferences.Editor editor;

    public static final String SHARED_PREFS = "shared_prefs";

    public static final String EMAIL = "email";

    public static final String F_NAME = "first_name";

    public static final String L_NAME = "last_name";

    public static final String LOGIN = "logged_in";

    public static final String TYPE = "user_type";

    public static final String ID = "user_id";

    public static final String LONG = "longitude";

    public static final String LAT = "latitude";

    public static final String REVIEWING_USER = "reviewing_user";

    public static void startSession(Context appContext) {
         context = appContext;
         sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
         editor = sharedPref.edit();
    }


    public static boolean login(String email, String userID, String userType) {
        editor.putString(EMAIL, email);
        editor.putString(ID, userID);
        editor.putString(TYPE, userType);
        editor.putBoolean(LOGIN, true);


        return editor.commit();
    }
    public static boolean name(String first, String last) {
        editor.putString(F_NAME, first);
        editor.putString(L_NAME, last);

        return editor.commit();
    }
    public static boolean login(String email, String userID, String userType, Location location) {
        editor.putString(EMAIL, email);
        editor.putString(ID, userID);
        editor.putString(TYPE, userType);
        editor.putBoolean(LOGIN, true);
        setLocation(location);

        return editor.commit();
    }

    public static void reviewing_user(String email){
        editor.putString(REVIEWING_USER, email);
        editor.commit();
    }

    public static void logout() {
        editor.clear();
        editor.commit();

        redirectToLogin();
    }

    public static boolean checkLogin() {
        return sharedPref.getBoolean(LOGIN, false);

    }

    public static boolean setLocation(String longitude, String latitude) {
        editor.putString(LONG, longitude);
        editor.putString(LAT, latitude);

        return editor.commit();
    }

    public static boolean setLocation(Location location) {
        Double longitude = (Double)location.getLongitude();
        Double latitude = (Double)location.getLatitude();

        editor.putString(LONG, longitude.toString());
        editor.putString(LAT, latitude.toString());

        return editor.commit();
    }

    public static Location getLocation() {
        double latitude  = Double.parseDouble(sharedPref.getString(LAT, "0.0"));
        double longitude = Double.parseDouble(sharedPref.getString(LAT, "0.0"));

        return new Location(latitude, longitude);
    }

    public static void redirectIfNotLoggedIn() {
        if(!checkLogin()) {
            redirectToLogin();
        }
    }

    public static String getKey(String key) {
        return sharedPref.getString(key, "Value not found");
    }

    public static String getEmail() {
        return sharedPref.getString(EMAIL, "No Email");
    }

    public static String getReviewingUserEmail() {
        return sharedPref.getString(REVIEWING_USER, "No review");
    }

    public static String getFName() {
        return sharedPref.getString(F_NAME, "No First Name");
    }

    public static String getLName() {
        return sharedPref.getString(L_NAME, "No Last Name");
    }

    public static String getUserType() {
        return sharedPref.getString(TYPE, "No User Type");
    }

    public static boolean isEmployee() {
        return sharedPref.getString(TYPE, "Not Stored").equals("Employee");
    }

    public static boolean isEmployer() {
        return sharedPref.getString(TYPE, "Not Stored").equals("Employer");
    }

    public static String getUserID() {
        return sharedPref.getString(ID, "No user ID found");
    }



    private static void redirectToLogin() {
        Intent redirect = new Intent(context, LoginActivity.class);
        redirect.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(redirect);
    }

}
