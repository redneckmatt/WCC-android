package org.wakechapelchurch.webviewapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    public String dbg="ON";
    public void dbgPrint(String logPrint){
    if (dbg.equals("ON")) Log.w("AppLog_Debug", logPrint);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "http://wakechapelchurch.org";
        WebView view=(WebView) this.findViewById(R.id.webView);
        //view.setWebViewClient(new WebViewClient()); //make all links followed stay inside the app
        view.setWebViewClient(new CustomWebViewClient());//jump to CustomWebViewClient, which allows ?fullsite to jump out of webView
        view.getSettings().setJavaScriptEnabled(true); //necessary for CPM mobile functions
        //view.getSettings().setDomStorageEnabled(true); //maybe necessary?
        //view.setBackgroundColor(Color.argb(1, 0, 0, 0)); // trying to remove white flash. Doesn't seem to be working
        //view.setBackgroundColor(Color.BLACK); // still trying to remove white flash. not working.
        view.loadUrl(url);


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView view=(WebView) this.findViewById(R.id.webView);
        // Check if the key event was the Back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (view.canGoBack()) { //if there is a backwards traversible history
                dbgPrint("Back Pressed, navigating backwards");
                view.goBack();
                return true;
            }
            // Since there's no history if it gets to this point, reload pain page instead of exiting app
            //return super.onKeyDown(keyCode, event); // default system behavior: if there's no history, exit app
            dbgPrint("Back Pressed, nowhere to go");
            //String url = "http://wakechapelchurch.org";
            //view.loadUrl(url);
            return false;
        }
        return false;
    }





    // The link for fullsite: wakechapelchurch.org/?fullsite=yes
    // This will allow the page to jump out of webView
    private class CustomWebViewClient extends WebViewClient {


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String urlQuery="";
            urlQuery=Uri.parse(url).getQuery();
            String urlHost=Uri.parse(url).getHost();
            dbgPrint("urlQuery: " + urlQuery + " | urlHost: " + urlHost);

            // break out of webView if fullsite link clicked
            if (urlQuery != null && urlQuery.equals("fullsite=yes")) {
                dbgPrint("urlQuery: " + urlQuery + ", breaking out of webView");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }

            // load appropriate links inside webView
            if (Uri.parse(url).getHost().equals("wakechapelchurch.org")) {
                dbgPrint("urlHost: " + urlHost + ", Loading inside webView. Url: " + url);
                view.setBackgroundColor(Color.argb(1, 0, 0, 0)); //testing
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            dbgPrint("url: " + url + ", breaking out of webView");
            startActivity(intent);
            return true;
        }
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
