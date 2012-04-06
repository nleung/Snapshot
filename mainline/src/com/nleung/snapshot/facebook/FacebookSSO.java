package com.nleung.snapshot.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.android.Facebook;

public class FacebookSSO
{
    private static final String REPOSITORY_NAME = "sso_data";
    
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_ACCESS_EXPIRES = "access_expires";
    
    private SharedPreferences prefs;
    
    public FacebookSSO(Context context)
    {
        this.prefs = context.getSharedPreferences(REPOSITORY_NAME, Activity.MODE_PRIVATE);
    }
    
    public boolean isAuthenticated(Facebook facebook)
    {
        String accessToken = getAccessToken();
        if (accessToken != null)
        {
            facebook.setAccessToken(accessToken);
        }
        
        Long accessExpires = getAccessExpires();
        if (accessExpires > 0)
        {
            facebook.setAccessExpires(accessExpires);
        }
        
        return facebook.isSessionValid();
    }
    
    public void logout()
    {
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_ACCESS_EXPIRES);
        editor.commit();
    }
    
    String getAccessToken()
    {
        return this.prefs.getString(KEY_ACCESS_TOKEN, null);
    }
    
    void setAccessToken(String token)
    {
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.commit();
    }
    
    Long getAccessExpires()
    {
        return this.prefs.getLong(KEY_ACCESS_EXPIRES, 0);
    }
    
    void setAccessExpires(Long expiry)
    {
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.putLong(KEY_ACCESS_EXPIRES, expiry);
        editor.commit();
    }
}
