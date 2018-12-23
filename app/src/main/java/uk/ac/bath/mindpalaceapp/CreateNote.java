package uk.ac.bath.mindpalaceapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateNote extends AppCompatActivity {

    private static final String url = "https://mindpalaceservice.herokuapp.com/newnote";
    private static final String palaceUrl = "https://mindpalaceservice.herokuapp.com/palacesbyuser?user=";
    private static final String TAG = CreateNote.class.getName();
    private String username;
    private String name;
    private final HashMap<String,String> palaceTitleToId = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_create_note);
        username = getIntent().getStringExtra("user_username");
        name = getIntent().getStringExtra("user_name");
        populatePalaces();
    }

    private void populatePalaces() {
        final Spinner palaceChoice = findViewById(R.id.palacechoice);
        final List<String> spinnerArray = new ArrayList<String>();
        final Context ctx = this;
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONArray> jsonRequest = new JsonArrayRequest(Request.Method.GET, palaceUrl + username, null,
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

    public void createNewNote(View view) {
        final EditText mNoteTitle = findViewById(R.id.noteCreationTitle);
        final EditText mNoteDescription = findViewById(R.id.noteDescription);
        final Spinner palaceChoice = findViewById(R.id.palacechoice);
        String chosenPalaceTitle = palaceChoice.getSelectedItem().toString();
        String chosenPalaceId = palaceTitleToId.get(chosenPalaceTitle);
        final View curView = view;

        Map<String, Object> json = new HashMap<>();
        json.put("palace_id", chosenPalaceId);
        json.put("note_title", mNoteTitle.getText().toString());
        json.put("note_description", mNoteDescription.getText().toString());
        json.put("note_location_x", "1.73895735893749");
        json.put("note_location_y", "1.4573894578935");
        json.put("note_status",false);

        JSONObject jsonObject = new JSONObject(json);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Note '" + mNoteTitle.getText().toString() + "' created.",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        Intent intent = new Intent(curView.getContext(), MainMenu.class);
                        intent.putExtra("user_username", username);
                        intent.putExtra("user_name", name);
                        startActivity(intent);
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
