package com.nleung.snapshot;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Utils
{
    private static final String TAG = Utils.class.getSimpleName();
    
    public static Bitmap downloadImage(String urlString)
    {
        Bitmap bitmap = null;
        
        try
        {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        }
        catch (MalformedURLException e)
        {
            Log.e(TAG, "URL is malformed: " + urlString);
            return null;
        }
        catch (IOException e)
        {
            Log.e(TAG, "IOException caught when downloading " + urlString);
            return null;
        }
        
        return bitmap;
    }
}
