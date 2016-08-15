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
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
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
        //view.setBackgroundColor(Color.argb(1, 0, 0, 0)); // trying to remove white flash. Doesn't seem to be working
        //view.setBackgroundColor(Color.BLACK); // still trying to remove white flash. not working.

        view.loadUrl(url);
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView view=(WebView) this.findViewById(R.id.webView);

        //Detects every touch event. Currently will not run unless hardware back key pressed at least once beforehand.
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dbgPrint("Touch detected");
                //v.performClick();

                /*
                Need some way of detecting what was clicked. Since in javascript, there is no URI to parse.

                javascript: class="shelf btn" href="#"   <-- Left top button
                javascript: class="menu" href="#"   <-- Right top button (Menu)

                When the right top Menu is open, <header class="on"> | <a class="menu on"> | <ul id="nav" style="display: block;">
                When the right top Menu is closed, <header class> | <a class="menu"> | <ul id="nav" style="display: none;">

                When the left top menu (Service Times & Directions) is open, <section id="shelf" style="display: block;">
                When the left top menu (Service Times & Directions) is closed, <section id="shelf" style="display: none;">

                //From the web page source for "shelf btn":
                $(document).ready(function(){
                    $('header a.shelf').click(function(){
                        $('#shelf').slideToggle();
                        return false
                    })
                })


                Ways to detect what was clicked:
                1) parse the HTML elements to figure out the state of the menus, after each touch
                2) watch the javascript for function calls



                Usage:
                If either menu extended, set var1 showing status and var2 showing current http location
                Then, if hardware back pressed: clear var1 & view.loadUrl(var2). This effectively closes the menu without losing place.

                */
                return false;
            }
        });


        // Check if the key event was the Back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //Here we need to put a check to see if one of the dropdown menu buttons was clicked.
            //create a function to monitor for the javascript menu events. Global var.
            //If var returns positive, close the menu first. Otherwise continue checking for history.



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
            String urlQuery;
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


            // break out of webView if youtube video is played. WebView is unstable with video.
            if (Uri.parse(url).getPath().contains("video_embed")) {
                dbgPrint("VideoEmbed found.");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }

            // load appropriate links inside webView
            if (Uri.parse(url).getHost().equals("wakechapelchurch.org")) {
                dbgPrint("urlHost: " + urlHost + ", Loading inside webView. Url: " + url);
                //view.setBackgroundColor(Color.argb(1, 0, 0, 0)); //testing
                return false;
            }
            // Otherwise, the link is not for a page on the site, so launch another Activity that handles URLs
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
