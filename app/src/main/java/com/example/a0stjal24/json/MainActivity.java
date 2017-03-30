package com.example.a0stjal24.json;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button downloadButton = (Button) findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        EditText artistEditText = (EditText) findViewById(R.id.artistEditText);
        String artist = artistEditText.getText().toString();

        (new GetSongsAsyncTask()).execute(artist);


    }
    // 1st paramater to AsyncTask is input data type for doInBackground method
    //2nd parameter to Async Task is the progress data type
    //3rd paramater tp Async Task is the return type of doInBackground method
    class GetSongsAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String artist = params[0];

            try {
                //Open connection to url
                String url = "http://www.free-map.org.uk/course/mad/ws/hits.php?artist=" + artist + "&format=json";
                URL urlObj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

                // get back Jsondata
                if(connection.getResponseCode() == 200) {
                    InputStream in = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));

                    String jsonData = "";
                    String line = br.readLine();

                    while (line != null) {
                        jsonData += line;
                        line = br.readLine();
                    }

                    // make the json pretty i.e plain text
                    JSONArray jsonArray = new JSONArray(jsonData);

                    String result = "";

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject songObj = jsonArray.getJSONObject(i);

                        String songTitle = songObj.getString("song");
                        String artistName = songObj.getString("artist");
                        String year = songObj.getString("year");

                        String pretty = "Song title: " + songTitle;
                        pretty += ", Artist: " + artistName;
                        pretty += ", Year: " + year + "\n";

                        result += pretty;
                    }

                    return result;
                } else {
                    return connection.getResponseCode() + " Error!";
                }
            }
            catch(IOException e) {
                return "Error!" + e.getMessage();
            }
            catch(JSONException e) {
                return "Error! " + e.getMessage();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Display the string in the result text view
            TextView resultTextView = (TextView) findViewById(R.id.resultTextView);
            resultTextView.setText(s);
        }
    }
}
