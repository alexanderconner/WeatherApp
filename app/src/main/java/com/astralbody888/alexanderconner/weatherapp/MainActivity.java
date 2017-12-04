package com.astralbody888.alexanderconner.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    String appid = "APPID=fcea8a16dd74363bff6a53362c2005c5";

    EditText cityName;
    TextView resultTextView;

    public void findWeather(View view) {

        //Move keyboard after input
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        String encodedCityName = null;
        try {
            encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.i("city Name", encodedCityName);

        //String city = "London";
        DownloadTask task = new DownloadTask();
        String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&" + appid;
        Log.i("Api Call", urlString);
        task.execute(urlString);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText) findViewById(R.id.cityName);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                if (result != "") {
                    return result;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
               // return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);

                    String weatherInfo = jsonObject.getString("weather");
                    Log.i("weatherInfo: ", weatherInfo);

                    JSONArray arr = new JSONArray(weatherInfo);

                    String message = "";

                    for (int i = 0; i<arr.length(); i++){
                        JSONObject jsonPart = arr.getJSONObject(i);

                        String main = "";
                        String description = "";


                        main = jsonPart.getString("main");
                        description = jsonPart.getString("description");

                        if (main != "" && description != "") {

                            message += main + ": " + description + "\r\n";
                        }

                        Log.i("main", jsonPart.getString("main"));
                        Log.i("description", jsonPart.getString("description"));
                        //Todo: get Icon and display it.

                    }

                    if (message != "") {
                        resultTextView.setText(message);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Could not find weather for city entered.",Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else {
                Toast.makeText(getApplicationContext(), "City Not Found.",Toast.LENGTH_SHORT).show();
            }


        }
    }

}
