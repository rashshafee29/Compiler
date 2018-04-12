package com.example.rashidshafee.comiper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "Compiler";

    Button execute;
    EditText codetext, inputtext;
    TextView answer;
    private static RequestQueue mRequestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        execute = (Button)findViewById(R.id.execute);
        codetext = (EditText) findViewById(R.id.codetext);
        inputtext = (EditText)findViewById(R.id.input);
        answer = (TextView)findViewById(R.id.show);
        mRequestQueue = Volley.newRequestQueue(this);

        execute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
                if(netInfo != null && netInfo.isConnected())
                {
                    System.out.println("Here ");
                    String code = codetext.getText().toString(), input_text = inputtext.getText().toString();
                    String URL = "https://api.judge0.com/submissions/?base64_encoded=false&wait=true";

                    Map<String, String>parameters = new HashMap<>();
                    parameters.put("source_code", code);
                    parameters.put("language_id", "10");
                    parameters.put("stdin",input_text);

                    final JSONObject postJson = new JSONObject(parameters);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                            URL, postJson, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("Getting resposne");
                            try {
                                JSONObject statusJson = response.getJSONObject("status");
                                if (Integer.parseInt(statusJson.getString("id")) == 3) {
                                    if (!response.isNull("stdout")) {
                                        answer.setText(response.getString("stdout"));
                                    } else {
                                        answer.setText("No Output");
                                    }
                                } else {
                                    String msg = statusJson.getString("description") + response.getString("compile_output");
                                    answer.setText(msg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            answer.setText("ERROR");
                        }
                });
                   // jsonObjectRequest.setTag(TAG);
                    mRequestQueue.add(jsonObjectRequest);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Not Connected to internet", Toast.LENGTH_SHORT).show();
                    answer.setText("Not Connected to Internet");
                }

            }
        });
    }
}
