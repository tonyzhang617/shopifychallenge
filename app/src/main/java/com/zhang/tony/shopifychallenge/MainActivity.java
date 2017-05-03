package com.zhang.tony.shopifychallenge;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private OkHttpClient client;

    private final static String url = "https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";

    private TextView txtMainInfo;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new OkHttpClient();
        txtMainInfo = (TextView) findViewById(R.id.txt_main_info);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ParseJsonTask task = new ParseJsonTask();
        task.execute(url);
    }

    private class ParseJsonTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            Request jsonRequest = new Request.Builder().url(url).build();
            Response jsonResponse = null;
            try {
                jsonResponse = client.newCall(jsonRequest).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String result = null;
            try {
                result = jsonResponse.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray arrayOrders = null;
            try {
                arrayOrders = jsonObject.getJSONArray("orders");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            double accuRevenue = 0;
            int accuKeyboards = 0;

            for (int i = 0, s = arrayOrders.length(); i < s; ++i) {
                try {
                    accuRevenue += arrayOrders.getJSONObject(i).getDouble("total_price");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONArray arrayItems = null;
                try {
                    arrayItems = arrayOrders.getJSONObject(i).getJSONArray("line_items");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int j = 0, t = arrayItems.length(); j < t; ++j) {
                    try {
                        if (arrayItems.getJSONObject(j).getString("title").equals("Aerodynamic Cotton Keyboard")) {
                            accuKeyboards += arrayItems.getJSONObject(j).getInt("quantity");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            progressBar.setVisibility(View.GONE);
            txtMainInfo.setVisibility(View.VISIBLE);
            txtMainInfo.setText(String.format(Locale.CANADA,
                    "Total Revenue: %.2f CAD\nNumber of Aerodynamic Cotton Keyboards Sold: %d", accuRevenue, accuKeyboards));
        }
    }
}
