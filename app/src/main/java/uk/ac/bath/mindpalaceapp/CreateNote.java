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

public class CreateNote extends AppCompatActivity {

    private static final String url = "https://mindpalaceservice.herokuapp.com/newnote";
    private static final String TAG = CreateNote.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
    }

    public void createNewNote(View view) {
        final EditText mNoteTitle = findViewById(R.id.noteCreationTitle);
        final EditText mNotePalaceId = findViewById(R.id.palace_id);
        final EditText mNoteDescription = findViewById(R.id.noteDescription);

        Map<String, Object> json = new HashMap<>();
        json.put("palace_id", mNotePalaceId.getText().toString());
        json.put("note_title", mNoteTitle.getText().toString());
        json.put("note_description", mNoteDescription.getText().toString());
        json.put("note_location", "1,1");
        json.put("note_status",true);

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
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }
}
