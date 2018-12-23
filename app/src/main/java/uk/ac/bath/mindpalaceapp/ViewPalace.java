package uk.ac.bath.mindpalaceapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewPalace extends AppCompatActivity {

    private static final String url = "https://mindpalaceservice.herokuapp.com/palace/";
    private static final String progressUrl = "https://mindpalaceservice.herokuapp.com/progress?ptitle=";
    private static final String unrememberedNotesUrl = "https://mindpalaceservice.herokuapp.com/unrememberednotes?ptitle=";
    private static final String TAG = ViewPalace.class.getName();
    private static String palaceId;
    private static String palaceTitle;
    private static final HashMap<String, String> noteTitleToId = new HashMap<>();
    private final static String spaceReplacement = "%20";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_view_palace);
        palaceId = getIntent().getStringExtra("palace_id");
        palaceTitle = getIntent().getStringExtra("palace_title");
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.getProgressDrawable().setColorFilter(
                Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
        viewPalaceDetails();
        viewPalaceProgress();
        viewUnrememberedNotes();
    }

    public void viewPalaceDetails() {
        final EditText mPalaceTitle = findViewById(R.id.palaceTitle);
        final EditText mPalaceDescription = findViewById(R.id.palaceDescription);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONArray> jsonRequest = new JsonArrayRequest(Request.Method.GET, url + palaceId, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONObject noteJson = response.getJSONObject(0);
                            mPalaceTitle.setText(""+noteJson.get("palace_title"));
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

    public void viewPalaceProgress() {
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final TextView progressMsg = findViewById(R.id.progress);
        palaceTitle = palaceTitle.replace(" ", spaceReplacement);
        final Handler handler = new Handler();

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.GET, progressUrl + palaceTitle, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            float remembered = Integer.parseInt(response.get("remembered").toString());
                            float total = Integer.parseInt(response.get("total").toString());
                            final float progress = (remembered / total) * 100;
                            handler.post(new Runnable() {
                                public void run() {
                                    progressBar.setProgress((int) progress);
                                    progressMsg.setText("Progress: " + (int) progress + "%");
                                }
                            });
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

    public void viewUnrememberedNotes() {
        final ListView lv = findViewById(R.id.unrememberedNotes);
        palaceTitle = palaceTitle.replace(" ", spaceReplacement);
        final List<String> note_titles = new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, note_titles);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONArray> jsonRequest = new JsonArrayRequest(Request.Method.GET, unrememberedNotesUrl + palaceTitle, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            for(int i=0; i<response.length(); i++) {
                                JSONObject noteJson = response.getJSONObject(i);
                                note_titles.add(noteJson.get("note_title").toString());
                                noteTitleToId.put(noteJson.get("note_title").toString(), noteJson.get("note_id").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        lv.setAdapter(arrayAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(jsonRequest);
    }
}
