package uk.ac.bath.mindpalaceapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.estimote.indoorsdk.EstimoteCloudCredentials;
import com.estimote.indoorsdk.IndoorLocationManagerBuilder;
import com.estimote.indoorsdk_module.algorithm.OnPositionUpdateListener;
import com.estimote.indoorsdk_module.algorithm.ScanningIndoorLocationManager;
import com.estimote.indoorsdk_module.cloud.CloudCallback;
import com.estimote.indoorsdk_module.cloud.CloudCredentials;
import com.estimote.indoorsdk_module.cloud.EstimoteCloudException;
import com.estimote.indoorsdk_module.cloud.IndoorCloudManager;
import com.estimote.indoorsdk_module.cloud.IndoorCloudManagerFactory;
import com.estimote.indoorsdk_module.cloud.Location;
import com.estimote.indoorsdk_module.cloud.LocationPosition;
import com.estimote.indoorsdk_module.view.IndoorLocationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ViewNote extends AppCompatActivity {

    private static final String url = "https://mindpalaceservice.herokuapp.com/nearestnote/";
    private static final String statusUrl = "https://mindpalaceservice.herokuapp.com/updatenotestatus/";
    private final String APP_ID = "mind-palace-eew";
    private final String APP_TOKEN = "3e6bcaf3b84c791b373a2bb439b3d239";
    private final String LOCATION_ID = "1w2101";
    private static final String TAG = ViewNote.class.getName();
    private static String palaceId;

    private double loc_x = 0.0;
    private double loc_y = 0.0;
    private double rad = 2.5;

    private CloudCredentials cloudCredentials = new EstimoteCloudCredentials(APP_ID, APP_TOKEN);
    private ScanningIndoorLocationManager indoorLocationManager;
    private final HashMap<String, String> noteTitleToId = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_view_note);
        palaceId = getIntent().getStringExtra("palace_id");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) { checkPermission(); }

        IndoorCloudManager cloudManager = new IndoorCloudManagerFactory().create(this, cloudCredentials);
        cloudManager.getLocation(LOCATION_ID, new CloudCallback<Location>() {
            @Override
            public void success(Location location) {
                Toast toast = Toast.makeText(getApplicationContext(), "Location '" + location.getName() + "' loaded from Estimote Cloud.",
                        Toast.LENGTH_SHORT);
                toast.show();

                final IndoorLocationView indoorLocationView = findViewById(R.id.indoor_view_note_view);

                indoorLocationManager = new IndoorLocationManagerBuilder(getApplicationContext(), location, cloudCredentials)
                        .withPositionUpdateInterval(501L)
                        .withDefaultScanner()
                        .build();

                indoorLocationManager.setOnPositionUpdateListener(new OnPositionUpdateListener() {
                    @Override
                    public void onPositionUpdate(LocationPosition locationPosition) {
                        indoorLocationView.updatePosition(locationPosition);
                        loc_x = locationPosition.getX();
                        loc_y = locationPosition.getY();
                        Log.d(TAG, "X: " + loc_x + " Y: " + loc_y);
                    }

                    @Override
                    public void onPositionOutsideLocation() {
                        indoorLocationView.hidePosition();
                    }
                });

                indoorLocationView.setLocation(location);
                indoorLocationManager.startPositioning();
            }

            @Override
            public void failure(EstimoteCloudException e) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "ERROR: Failed to get location from Estimote Cloud.",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void viewNote(View view) {
        final EditText mNoteTitle = findViewById(R.id.noteViewTitle);
        final EditText mNoteDescription = findViewById(R.id.noteDescription);
        final TextView mNoteFound = findViewById(R.id.keepMovingOrFound);
        final String viewUrl = url + palaceId + "?xpos=" + loc_x + "&ypos=" + loc_y + "&rad=" + rad;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONArray> jsonRequest = new JsonArrayRequest(Request.Method.GET, viewUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONObject noteJson = response.getJSONObject(0);
                            if (noteJson.has("Message")) {
                                mNoteTitle.setText("");
                                mNoteDescription.setText("");
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "No notes within radius for this palace. Please move closer to the loci.",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Note found!",
                                        Toast.LENGTH_SHORT);
                                toast.show();
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

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.INTERNET,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        }
    }
}
