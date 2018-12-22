package uk.ac.bath.mindpalaceapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class SignIn extends AppCompatActivity {

    private static final String url = "https://mindpalaceservice.herokuapp.com/userbyusername/";
    private static final String TAG = SignIn.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    public void goToMainMenu(View view) {
        final EditText mUsername = findViewById(R.id.loginUsername);
        final EditText mPassword = findViewById(R.id.loginPassword);
        final String username = mUsername.getText().toString();
        final String password = mPassword.getText().toString();
        final View curView = view;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONArray> jsonRequest = new JsonArrayRequest(Request.Method.GET, url + username, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONObject userJson = response.getJSONObject(0);
                            String realPass = userJson.get("user_password").toString();
                            String name = userJson.get("user_name").toString();
                            if (realPass.equals(password)) {
                                Intent intent = new Intent(curView.getContext(), MainMenu.class);
                                intent.putExtra("user_username", username);
                                intent.putExtra("user_name", name);
                                startActivity(intent);
                                Toast toast = Toast.makeText(getApplicationContext(),"Login successful.",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            else {
                                Toast toast = Toast.makeText(getApplicationContext(),"Login failed.",
                                        Toast.LENGTH_SHORT);
                                toast.show();
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

    public void goToSignUp(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }
}
