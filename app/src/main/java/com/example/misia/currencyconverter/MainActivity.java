package com.example.misia.currencyconverter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.jar.Pack200.Packer.ERROR;

public class MainActivity extends AppCompatActivity {

    double rate;
    String rateStr = "";

    TextView currentRateTextView;

    public void convert(View view) {

        if (!rateStr.equals("")) {
            rate = Double.parseDouble(rateStr);
        } else {
            rate = 0.414;
        }


        EditText amountEditText = (EditText) findViewById(R.id.amountEditText);
        String message;

        if (amountEditText.getText().toString().isEmpty()) {
            message = "Input an amount";
        } else {
            String amountInSEK = amountEditText.getText().toString();
            double convertedToPLN = Double.parseDouble(amountInSEK) * rate;
            String amountInPLN = String.format("%.2f", convertedToPLN);
            message = "This is " + amountInPLN + " PLN";

        }
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 400);
        toast.show();

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(amountEditText.getWindowToken(), 0);//so that clicking the button hides the virtual keyboard


    }

    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            StringBuilder stringBuilder = new StringBuilder();

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data != -1) {
                    stringBuilder.append((char) data);
                    // read next char
                    data = reader.read();
                }
                //return stringBuilder.toString()
                result = stringBuilder.toString();
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find rate", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) { //here we convert the data into JSON Object
            super.onPostExecute(s);  //s == result from the backgroud method


            try {
                JSONObject jsonObject = new JSONObject(s);
                String rateInfo = "["+jsonObject.getString("pln")+"]";
                Log.i("Rate Info", rateInfo);
                JSONArray arr = new JSONArray(rateInfo);
                String message = "";
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);


                    rateStr = jsonPart.getString(("rate"));
                    Log.i("rate", rateStr);
                    rate = Double.parseDouble(rateStr);



                    if (!rateStr.equals("")) {
                        message = "Current rate: " + rateStr;

                    }

                }
                if (!message.equals("")) {
                    currentRateTextView.setText(message);


                } else {
                    Toast.makeText(getApplicationContext(), "Could not find rate", Toast.LENGTH_SHORT).show();

                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find rate", Toast.LENGTH_SHORT).show();

            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentRateTextView = findViewById(R.id.currentRateTextView);




    }

    public void updateRate(View view) {
        try {
            DownloadTask task = new DownloadTask();

            task.execute("http://www.floatrates.com/daily/sek.json");

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find rate", Toast.LENGTH_SHORT).show();

        }
    }
}







