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
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewNote extends AppCompatActivity {

    private static final String url = "https://mindpalaceservice.herokuapp.com/note/1";
    private static final String TAG = ViewNote.class.getName();
    private double xpos, ypos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        Intent intent = getIntent();
        xpos = intent.getDoubleExtra("xpos", 0);
        xpos = intent.getDoubleExtra("ypos", 0);
        viewNote();
    }

    public void viewNote() {
        final EditText mNoteTitle = findViewById(R.id.noteViewTitle);
        final EditText mNotePalaceId = findViewById(R.id.palace_id);
        final EditText mNoteDescription = findViewById(R.id.noteDescription);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONArray> jsonRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONObject noteJson = response.getJSONObject(0);
                            mNoteTitle.setText(""+noteJson.get("note_title"));
                            mNotePalaceId.setText(""+noteJson.get("palace_id"));
                            mNoteDescription.setText(""+noteJson.get("note_description"));
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
