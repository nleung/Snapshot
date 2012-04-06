package com.nleung.snapshot.facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.nleung.snapshot.R;

public class FacebookSSOActivity extends Activity
{
    Facebook facebook = new Facebook(FacebookConstants.APP_ID);
    private TextView message;
    private FacebookSSO sso;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_sso);
        
        this.message = (TextView) findViewById(R.id.facebook_sso_message);

        this.sso = new FacebookSSO(this);
        authorize();
    }
    
    private void authorize()
    {
        if (!this.sso.isAuthenticated(this.facebook))
        {
            this.message.setText("Authorizing with Facebook...");
            this.facebook.authorize(this, FacebookConstants.PERMISSIONS, new DialogListener()
            {
                @Override
                public void onComplete(Bundle values)
                {
                    FacebookSSOActivity.this.sso.setAccessToken(FacebookSSOActivity.this.facebook.getAccessToken());
                    FacebookSSOActivity.this.sso.setAccessExpires(FacebookSSOActivity.this.facebook.getAccessExpires());
                }

                @Override
                public void onFacebookError(FacebookError error) {}

                @Override
                public void onError(DialogError e) {}

                @Override
                public void onCancel() {}
            });
        }
        else
        {
            this.message.setText("Already authorized with Facebook!");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        this.facebook.authorizeCallback(requestCode, resultCode, data);
        
        Log.d(this.getLocalClassName(), "access token is " + this.facebook.getAccessToken() + ", expires is " + this.facebook.getAccessExpires());
        if (this.facebook.getAccessToken() != null && this.facebook.getAccessExpires() != 0)
        {
            this.message.setText("Successfully authorized with Facebook!");
        }
        else
        {
            this.message.setText("Failed to authorize with Facebook!");
        }
    }
}
