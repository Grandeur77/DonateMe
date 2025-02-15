package com.example.donateme;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_PROFILE_PIC = "profile_pic";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MOBILE = "mobile";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    //private static final String PREF_NAME = "user_preferences";
    private static final String KEY_PROFILE_IMAGE_URI = "profile_image_uri";

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save the profile image URI
    public void saveProfileImageUri(String uri) {
        editor.putString(KEY_PROFILE_IMAGE_URI, uri);
        editor.apply();
    }

    // Get the profile image URI
    public String getProfileImageUri() {
        return sharedPreferences.getString(KEY_PROFILE_IMAGE_URI, null); // Default to null if not found
    }

    // Save user profile details
    public void saveUserProfile(String fullName, String email, String mobile, String profilePic) {
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_PROFILE_PIC, profilePic);
        editor.apply();
    }

    // Get user profile details
    public String getFullName() {
        return sharedPreferences.getString(KEY_FULL_NAME, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getMobile() {
        return sharedPreferences.getString(KEY_MOBILE, null);
    }

    public String getProfilePic() {
        return sharedPreferences.getString(KEY_PROFILE_PIC, null);
    }
}