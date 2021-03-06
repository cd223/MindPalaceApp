package uk.ac.bath.mindpalaceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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

public class SettingsMenu extends AppCompatActivity {

    private String username;
    private String name;
    private static final String url = "https://mindpalaceservice.herokuapp.com/palacesbyuser?user=";
    private static final String deleteUseruUrl = "https://mindpalaceservice.herokuapp.com/userbyusername/";
    private static final String deletePalaceUrl = "https://mindpalaceservice.herokuapp.com/palace/";
    private static final String TAG = TrainMenu.class.getName();
    private final HashMap<String,String> palaceTitleToId = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_settings_menu);
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

    public void deleteAccount(View view) {
        final Context curCtx = view.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure you wish to delete the account for user: " + username
                + "? All the associated data stored under this account will be deleted. This cannot be undone. Click 'NO' to cancel or 'YES' to confirm.");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                RequestQueue queue = Volley.newRequestQueue(curCtx);
                JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.DELETE, deleteUseruUrl + username, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());

                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "User '" + username + "' deleted.",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                                Intent intent = new Intent(curCtx, SignIn.class);
                                startActivity(intent);
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

    public void deletePalace(View view) {
        final Context curCtx = view.getContext();
        final Spinner palaceChoice = findViewById(R.id.palacechoice);

        if(palaceChoice.getSelectedItem() == null || palaceChoice.getSelectedItem().toString().isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No palaces exist yet. Please create a palace before attempting to delete any.",
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        final String palaceTitleToDelete = palaceChoice.getSelectedItem().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Palace");
        builder.setMessage("Are you sure you wish to delete the palace : " + palaceTitleToDelete
                + "? All the associated data stored under this palace will be deleted. This cannot be undone. Click 'NO' to cancel or 'YES' to confirm.");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                RequestQueue queue = Volley.newRequestQueue(curCtx);
                JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.DELETE, deletePalaceUrl + palaceTitleToId.get(palaceTitleToDelete), null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Palace '" + palaceTitleToDelete + "' deleted.",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                                populatePalaces();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.info_screen) {
            Intent intent = new Intent(getApplicationContext(), SettingsMenuInfo.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }
}
