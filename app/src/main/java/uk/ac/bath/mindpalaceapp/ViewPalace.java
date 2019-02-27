package uk.ac.bath.mindpalaceapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewPalace extends AppCompatActivity {

    private static final String url = "https://mindpalaceservice.herokuapp.com/palace/";
    private static final String progressUrl = "https://mindpalaceservice.herokuapp.com/progress?ptitle=";
    private static final String unrememberedNotesUrl = "https://mindpalaceservice.herokuapp.com/unrememberednotes?ptitle=";
    private static final String notesByPalaceUrl = "https://mindpalaceservice.herokuapp.com/notesbypalace/";
    private static final String TAG = ViewPalace.class.getName();
    private static String palaceId;
    private static String palaceTitle;
    private String username;
    private String name;
    private static final HashMap<String, String[]> noteTitleToNoteArray = new HashMap<>();
    private final static String spaceReplacement = "%20";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_view_palace);
        palaceId = getIntent().getStringExtra("palace_id");
        checkNotesExist();

        palaceTitle = getIntent().getStringExtra("palace_title");
        username = getIntent().getStringExtra("user_username");
        name = getIntent().getStringExtra("user_name");
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
        final TextView stillToGoLbl = findViewById(R.id.stillToGo);
        final TextView progressMsg = findViewById(R.id.progress);
        palaceTitle = palaceTitle.replace(" ", spaceReplacement);
        final Handler handler = new Handler();
        final TextView progressCounter = findViewById(R.id.counter);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.GET, progressUrl + palaceTitle  + "&user=" + username, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            float remembered = Integer.parseInt(response.get("remembered").toString());
                            float total = Integer.parseInt(response.get("total").toString());
                            progressCounter.setText("Remembered " + (int) remembered + " of " + (int) total);
                            if (remembered == total) {
                                stillToGoLbl.setText("All notes remembered.");
                                stillToGoLbl.setTextColor(getResources().getColor(R.color.green));
                            }
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
        final Context ctx = this;
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, note_titles);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONArray> jsonRequest = new JsonArrayRequest(Request.Method.GET, unrememberedNotesUrl + palaceTitle  + "&user=" + username, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            for(int i=0; i<response.length(); i++) {
                                JSONObject noteJson = response.getJSONObject(i);
                                note_titles.add(noteJson.get("note_title").toString());
                                noteTitleToNoteArray.put(noteJson.get("note_title").toString(),
                                        new String[]{noteJson.get("note_id").toString(),
                                        noteJson.get("note_description").toString(),
                                        noteJson.get("note_image_url").toString(),
                                        noteJson.get("note_location_x").toString(),
                                        noteJson.get("note_location_y").toString()
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        lv.setAdapter(arrayAdapter);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,
                                                    long id) {
                                String noteTitle = parent.getAdapter().getItem(position).toString();
                                Intent intent = new Intent(ctx, NoteDetails.class);
                                intent.putExtra("note_title", noteTitle);
                                intent.putExtra("note_id", noteTitleToNoteArray.get(noteTitle)[0]);
                                intent.putExtra("note_description", noteTitleToNoteArray.get(noteTitle)[1]);
                                intent.putExtra("note_image_url", noteTitleToNoteArray.get(noteTitle)[2]);
                                intent.putExtra("note_location_x", noteTitleToNoteArray.get(noteTitle)[3]);
                                intent.putExtra("note_location_y", noteTitleToNoteArray.get(noteTitle)[4]);

                                startActivity(intent);
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(jsonRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.info_screen) {
            Intent intent = new Intent(getApplicationContext(), ViewPalaceInfo.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNotesExist();
        viewPalaceDetails();
        viewPalaceProgress();
        viewUnrememberedNotes();
    }

    private void checkNotesExist() {
        final Context ctx = this;
        final TextView stillToGoLbl = findViewById(R.id.stillToGo);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONArray> jsonRequest = new JsonArrayRequest(Request.Method.GET, notesByPalaceUrl + palaceId, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        if (response.isNull(0)){
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "No notes exist for this palace. Please create notes in this palace before tracking progress.",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                            stillToGoLbl.setText("No notes exist for this palace!");
                            stillToGoLbl.setTextColor(getResources().getColor(R.color.red));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }
}
