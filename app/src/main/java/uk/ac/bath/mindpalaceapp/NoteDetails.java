package uk.ac.bath.mindpalaceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.estimote.indoorsdk.EstimoteCloudCredentials;
import com.estimote.indoorsdk_module.cloud.CloudCallback;
import com.estimote.indoorsdk_module.cloud.CloudCredentials;
import com.estimote.indoorsdk_module.cloud.EstimoteCloudException;
import com.estimote.indoorsdk_module.cloud.IndoorCloudManager;
import com.estimote.indoorsdk_module.cloud.IndoorCloudManagerFactory;
import com.estimote.indoorsdk_module.cloud.Location;
import com.estimote.indoorsdk_module.cloud.LocationPosition;
import com.estimote.indoorsdk_module.view.IndoorLocationView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NoteDetails extends AppCompatActivity {

    private String note_title;
    private String note_image_url;
    private String note_description;
    private String note_id;
    private static final String deleteNoteUrl = "https://mindpalaceservice.herokuapp.com/note/";

    private String note_location_x;
    private String note_location_y;
    private final String APP_ID = "mind-palace-eew";
    private final String APP_TOKEN = "3e6bcaf3b84c791b373a2bb439b3d239";
    private final String LOCATION_ID = "1w2101";
    private CloudCredentials cloudCredentials = new EstimoteCloudCredentials(APP_ID, APP_TOKEN);
    private static final String TAG = NoteDetails.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_note_details);

        note_id = getIntent().getStringExtra("note_id");
        note_title = getIntent().getStringExtra("note_title");
        note_image_url = getIntent().getStringExtra("note_image_url");
        note_description = getIntent().getStringExtra("note_description");
        note_location_x = getIntent().getStringExtra("note_location_x");
        note_location_y = getIntent().getStringExtra("note_location_y");

        EditText mNoteDetailsTitle = findViewById(R.id.noteDetailsTitle);
        EditText mNoteDetailsDescription = findViewById(R.id.noteDetailsDescription);
        mNoteDetailsTitle.setText(note_title);
        mNoteDetailsDescription.setText(note_description);

        ImageView noteImageView = findViewById(R.id.noteImage);
        ImageLoader.getLoader(getApplicationContext()).setLoggingEnabled(true);
        ImageLoader.getLoader(getApplicationContext())
                .load(note_image_url)
                .fit()
                .centerCrop()
                .into(noteImageView);

        IndoorCloudManager cloudManager = new IndoorCloudManagerFactory().create(this, cloudCredentials);
        cloudManager.getLocation(LOCATION_ID, new CloudCallback<Location>() {
            @Override
            public void success(Location location) {
                final IndoorLocationView indoorLocationView = findViewById(R.id.indoor_view_note_view);
                indoorLocationView.setLocation(location);
                indoorLocationView.updatePosition(new LocationPosition(Double.valueOf(note_location_x),
                        Double.valueOf(note_location_y),
                    0.0));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteNote(View view) {
        final Context curCtx = view.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Note");
        builder.setMessage("Are you sure you wish to delete the note : " + note_title
                + "? All the associated data stored under this note will be deleted. This cannot be undone. Click 'NO' to cancel or 'YES' to confirm.");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                RequestQueue queue = Volley.newRequestQueue(curCtx);
                JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.DELETE, deleteNoteUrl + note_id, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Note '" + note_title + "' deleted.",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
                };
                queue.add(jsonRequest);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
    }
}
