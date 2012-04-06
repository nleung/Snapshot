package com.nleung.snapshot.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.nleung.snapshot.R;
import com.nleung.snapshot.Utils;

public class FacebookActivity extends Activity
{
    Facebook facebook = new Facebook(FacebookConstants.APP_ID);
    private FacebookSSO sso;
    private AsyncFacebookRunner runner;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_sso);
        
        this.sso = new FacebookSSO(this);
        this.runner = new AsyncFacebookRunner(this.facebook);
        refreshFacebookInfo();
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        
        refreshFacebookInfo();
    }
    
    private void refreshFacebookInfo()
    {
        Button loginButton = (Button) findViewById(R.id.facebook_login_button);
        Button logoutButton = (Button) findViewById(R.id.facebook_logout_button);

        if (this.sso.isAuthenticated(this.facebook))
        {
            loginButton.setText("Already logged in via Facebook");
            loginButton.setEnabled(false);
            
            logoutButton.setText("Logout of Facebook");
            logoutButton.setEnabled(true);
        }
        else
        {
            loginButton.setText("Login to Facebook");
            loginButton.setEnabled(true);
            
            logoutButton.setText("Not logged into Facebook");
            logoutButton.setEnabled(false);
        }
        
        getUserInfo();
        getUserPicture();
    }
    
    public void onFacebookLoginClick(View view)
    {
        authorize();
    }
    
    private void authorize()
    {
        if (!this.sso.isAuthenticated(this.facebook))
        {
            this.facebook.authorize(this, FacebookConstants.PERMISSIONS, new DialogListener()
            {
                @Override
                public void onComplete(Bundle values)
                {
                    FacebookActivity.this.sso.setAccessToken(FacebookActivity.this.facebook.getAccessToken());
                    FacebookActivity.this.sso.setAccessExpires(FacebookActivity.this.facebook.getAccessExpires());
                }

                @Override
                public void onFacebookError(FacebookError error) {}

                @Override
                public void onError(DialogError e) {}

                @Override
                public void onCancel() {}
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        this.facebook.authorizeCallback(requestCode, resultCode, data);
    }
    
    public void onFacebookLogoutClick(View view)
    {
        runner.logout(this, new RequestListener()
        {
            @Override
            public void onComplete(String response, Object state)
            {
                FacebookActivity.this.sso.logout();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        refreshFacebookInfo();
                    }
                });
            }
            
            @Override
            public void onIOException(IOException e, Object state) {}
            
            @Override
            public void onFileNotFoundException(FileNotFoundException e,
                  Object state) {}
            
            @Override
            public void onMalformedURLException(MalformedURLException e,
                  Object state) {}
            
            @Override
            public void onFacebookError(FacebookError e, Object state) {}
          });
    }
    
    private void getUserInfo()
    {
        this.runner.request("me", new RequestListener()
        {
            
            @Override
            public void onMalformedURLException(MalformedURLException e, Object state)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onIOException(IOException e, Object state)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFileNotFoundException(FileNotFoundException e, Object state)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFacebookError(FacebookError e, Object state)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onComplete(String response, Object state)
            {
                Log.d(getLocalClassName(), "response is: " + response);
                
                final StringBuilder builder = new StringBuilder();
                try
                {
                    JSONObject responseJson = new JSONObject(response);
                    Iterator it = responseJson.keys();
                    while (it.hasNext())
                    {
                        String key = (String) it.next();
                        String value = responseJson.getString(key);
                        
                        builder.append(key).append("=").append(value).append("\n");
                    }
                }
                catch (JSONException e)
                {
                    Log.e(getLocalClassName(), e.toString());
                }
                
                FacebookActivity.this.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        TextView userInfo = (TextView) findViewById(R.id.facebook_user_info);
                        userInfo.setText(builder.toString());
                    }
                });
            }
        });
    }
    
    private void getUserPicture()
    {
        this.runner.request("me/photos", new RequestListener()
        {
            
            @Override
            public void onMalformedURLException(MalformedURLException e, Object state)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onIOException(IOException e, Object state)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFileNotFoundException(FileNotFoundException e, Object state)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFacebookError(FacebookError e, Object state)
            {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onComplete(String response, Object state)
            {
                Log.d(getLocalClassName(), "response is: " + response);
                
                Bitmap bitmap = null;
                try
                {
                    JSONObject responseJson = new JSONObject(response);
                    JSONArray data = responseJson.getJSONArray("data");
                    if (data.length() > 0)
                    {
                        JSONObject picture = data.getJSONObject(0);
                        String pictureUrl = picture.getString("picture");
                        bitmap = Utils.downloadImage(pictureUrl);
                    }
                }
                catch (JSONException e)
                {
                    Log.e(getLocalClassName(), e.toString());
                }
                
                final Bitmap copy = bitmap;
                FacebookActivity.this.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ImageView userPicture = (ImageView) findViewById(R.id.facebook_picture);
                        userPicture.setImageBitmap(copy);
                    }
                });
            }
        });
    }
}
