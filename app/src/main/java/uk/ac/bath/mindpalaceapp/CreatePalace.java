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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreatePalace extends AppCompatActivity {

    private static final String url = "https://mindpalaceservice.herokuapp.com/newpalace";
    private static final String TAG = CreatePalace.class.getName();
    private static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_create_palace);
        username = getIntent().getStringExtra("user_username");
    }

    public void createNewPalace(View view) {
        final EditText mPalaceTitle = findViewById(R.id.palaceCreationTitle);
        final EditText mPalaceDescription = findViewById(R.id.palaceDescription);

        Map<String, Object> json = new HashMap<>();
        json.put("user_id", username);
        json.put("palace_title", mPalaceTitle.getText().toString());
        json.put("palace_description", mPalaceDescription.getText().toString());

        JSONObject jsonObject = new JSONObject(json);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(jsonRequest);
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    public void goToHome(View view) {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }
}
