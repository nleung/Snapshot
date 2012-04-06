package com.nleung.snapshot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nleung.snapshot.facebook.FacebookActivity;

public class HomeActivity extends Activity 
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);        
    }
    
    public void onFacebookClick(View view)
    {
    	startActivity(new Intent(this, FacebookActivity.class));
    }
}