package com.nleung.snapshot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.nleung.snapshot.facebook.FacebookConstants;
import com.nleung.snapshot.facebook.FacebookSSO;
import com.nleung.snapshot.facebook.FacebookSSOActivity;

public class HomeActivity extends Activity 
{
    private Facebook facebook = new Facebook(FacebookConstants.APP_ID);
    FacebookSSO sso;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        this.sso = new FacebookSSO(this);
        
        refreshFacebookButtons();
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        
        refreshFacebookButtons();
    }
    
    private void refreshFacebookButtons()
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
    }
    
    public void onFacebookLoginClick(View view)
    {
    	startActivity(new Intent(this, FacebookSSOActivity.class));
    }
    
    public void onFacebookLogoutClick(View view)
    {
        
        AsyncFacebookRunner runner = new AsyncFacebookRunner(this.facebook);
        runner.logout(this, new RequestListener()
        {
            @Override
            public void onComplete(String response, Object state)
            {
                HomeActivity.this.sso.logout();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        refreshFacebookButtons();
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
}