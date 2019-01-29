package uk.ac.bath.mindpalaceapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class ViewNote extends AppCompatActivity {

    private static final String url = "https://mindpalaceservice.herokuapp.com/nearestnote/";
    private static final String statusUrl = "https://mindpalaceservice.herokuapp.com/updatenotestatus/";

    private static String palaceId;

    private double loc_x = 0.0;
    private double loc_y = 0.0;
    private double rad = 2.5;

    private final HashMap<String, String> noteTitleToId = new HashMap<>();
    private static final String TAG = ViewNote.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_view_note);
        palaceId = getIntent().getStringExtra("palace_id");
        viewNote();
    }

    public void viewNote() {
        final EditText mNoteTitle = findViewById(R.id.noteViewTitle);
        final EditText mNoteDescription = findViewById(R.id.noteDescription);
        final String viewUrl = url + palaceId + "?xpos=" + loc_x + "&ypos=" + loc_y + "&rad=" + rad;
        final Button rememberedButton = findViewById(R.id.rememberedButton);
        final Button unrememberedButton = findViewById(R.id.unrememberedButton);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONArray> jsonRequest = new JsonArrayRequest(Request.Method.GET, viewUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONObject noteJson = response.getJSONObject(0);
                            if (noteJson.has("Message")) {
                                mNoteTitle.setEnabled(false);
                                mNoteDescription.setEnabled(false);
                                rememberedButton.setClickable(false);
                                unrememberedButton.setClickable(false);
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "No notes within the given radius. Please move closer to the loci.",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                mNoteTitle.setText(""+noteJson.get("note_title"));
                                mNoteDescription.setText(""+noteJson.get("note_description"));
                                noteTitleToId.put(noteJson.get("note_title").toString(), noteJson.get("note_id").toString());
                            }
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

    public void noteRemembered(View view) {
        final EditText noteTitleTv = findViewById(R.id.noteViewTitle);
        final String noteTitle = noteTitleTv.getText().toString();
        String noteId = noteTitleToId.get(noteTitle);
        final String postUrl = statusUrl + noteId + "?status=true";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, postUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Note '" + noteTitle + "' marked as remembered.",
                                Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(jsonRequest);
    }

    public void noteNotRemembered(View view) {
        final EditText noteTitleTv = findViewById(R.id.noteViewTitle);
        final String noteTitle = noteTitleTv.getText().toString();
        String noteId = noteTitleToId.get(noteTitle);
        final String postUrl = statusUrl + noteId + "?status=false";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, postUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Note '" + noteTitle + "' marked as NOT remembered.",
                                Toast.LENGTH_SHORT);
                        toast.show();
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
