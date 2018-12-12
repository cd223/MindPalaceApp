package uk.ac.bath.mindpalaceapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewPalace extends AppCompatActivity {

    private static final String url = "https://mindpalaceservice.herokuapp.com/palace/1";
    private static final String TAG = ViewPalace.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_palace);
        viewPalace();
    }

    public void viewPalace() {
        final EditText mPalaceTitle = findViewById(R.id.palaceTitle);
        final EditText mPalaceDescription = findViewById(R.id.palaceDescription);
        final EditText mPalaceUserId = findViewById(R.id.user_id);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONArray> jsonRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONObject noteJson = response.getJSONObject(0);
                            mPalaceTitle.setText(""+noteJson.get("palace_title"));
                            mPalaceUserId.setText(""+noteJson.get("user_id"));
                            mPalaceDescription.setText(""+noteJson.get("palace_description"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(jsonRequest);
    }

    public void goToHome(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}
