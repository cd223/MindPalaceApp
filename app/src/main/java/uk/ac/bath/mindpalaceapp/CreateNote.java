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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateNote extends AppCompatActivity {

    private static final String url = "https://mindpalaceservice.herokuapp.com/newnote";
    private static final String palaceUrl = "https://mindpalaceservice.herokuapp.com/palacesbyuser?user=";
    private final String APP_ID = "mind-palace-eew";
    private final String APP_TOKEN = "3e6bcaf3b84c791b373a2bb439b3d239";
    private final String LOCATION_ID = "1w2101";
    private static final String TAG = CreateNote.class.getName();
    private final HashMap<String,String> palaceTitleToId = new HashMap<>();
    private String noteImgUrl;

    private double loc_x = 0.0;
    private double loc_y = 0.0;

    private CloudCredentials cloudCredentials = new EstimoteCloudCredentials(APP_ID, APP_TOKEN);
    private ScanningIndoorLocationManager indoorLocationManager;
    private String username;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_create_note);
        username = getIntent().getStringExtra("user_username");
        name = getIntent().getStringExtra("user_name");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) { checkPermission(); }
        populatePalaces();
        IndoorCloudManager cloudManager = new IndoorCloudManagerFactory().create(this, cloudCredentials);
        cloudManager.getLocation(LOCATION_ID, new CloudCallback<Location>() {
            @Override
            public void success(Location location) {
                final IndoorLocationView indoorLocationView = findViewById(R.id.indoor_view_note_creation);
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
                        "Error: Failed to retrieve location for palace.",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
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

        if(noteImgUrl == null || noteImgUrl.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Please select an image before continuing. Click 'Choose Image' to search.",
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if(mNoteTitle.getText() == null || mNoteDescription.getText() == null
                || mNoteTitle.getText().toString().isEmpty() || mNoteDescription.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Please enter a title and description before clicking 'Create Palace'.",
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Map<String, Object> json = new HashMap<>();
        json.put("palace_id", chosenPalaceId);
        json.put("note_title", mNoteTitle.getText().toString());
        json.put("note_description", mNoteDescription.getText().toString());
        json.put("note_location_x", ""+loc_x);
        json.put("note_location_y", ""+loc_y);
        json.put("note_image_url", noteImgUrl);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.info_screen) {
            Intent intent = new Intent(getApplicationContext(), CreateNoteInfo.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToSelectImage(View view) {
        Intent intent = new Intent(this, SelectImage.class);
        final EditText mNoteTitle = findViewById(R.id.noteCreationTitle);
        String noteTitle = mNoteTitle.getText().toString();
        intent.putExtra("note_title", noteTitle);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String imageUrl = data.getStringExtra("note_image_url");
                noteImgUrl = imageUrl;
                TextView imageSelected = findViewById(R.id.imageSelected);
                imageSelected.setText("Image Selected");
                imageSelected.setTextColor(getResources().getColor(R.color.green));
                Button chooseImgBtn = findViewById(R.id.launch_browser);
                chooseImgBtn.setText("Choose Different Image");
                ImageView imageView = findViewById(R.id.checkedImage);
                ImageLoader.getLoader(getApplicationContext())
                        .load(noteImgUrl)
                        .fit()
                        .centerCrop()
                        .into(imageView);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }
}
