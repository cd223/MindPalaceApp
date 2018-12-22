package uk.ac.bath.mindpalaceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrackMenu extends AppCompatActivity {

    private String username;
    private String name;
    private static final String url = "https://mindpalaceservice.herokuapp.com/palacesbyuser?user=";
    private static final String TAG = TrainMenu.class.getName();
    private final HashMap<String,String> palaceTitleToId = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_track_menu);
        username = getIntent().getStringExtra("user_username");
        name = getIntent().getStringExtra("user_name");
        populatePalaces();
    }

    private void populatePalaces() {
        final Spinner palaceChoice = findViewById(R.id.palacechoice);
        final List<String> spinnerArray = new ArrayList<String>();
        final Context ctx = this;
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONArray> jsonRequest = new JsonArrayRequest(Request.Method.GET, url + username, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            for (int i=0; i<response.length(); i++) {
                                JSONObject palaceJson = response.getJSONObject(i);
                                spinnerArray.add(palaceJson.get("palace_title").toString());
                                palaceTitleToId.put(palaceJson.get("palace_title").toString(), palaceJson.get("palace_id").toString());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, spinnerArray);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            palaceChoice.setAdapter(adapter);
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

    public void goToViewPalace(View view) {
    }
}
