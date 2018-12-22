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

public class SignUp extends AppCompatActivity {

    private static final String url = "https://mindpalaceservice.herokuapp.com/newuser";
    private static final String TAG = SignUp.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void signUp(View view) {
        final EditText mUserName = findViewById(R.id.signupName);
        final EditText mUserUsername = findViewById(R.id.signupUsername);
        final EditText mUserPassword = findViewById(R.id.signupPassword);
        final View curView = view;

        Map<String, Object> json = new HashMap<>();
        json.put("user_name", mUserName.getText().toString());
        json.put("user_username", mUserUsername.getText().toString());
        json.put("user_password", mUserPassword.getText().toString());

        JSONObject jsonObject = new JSONObject(json);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        Intent intent = new Intent(curView.getContext(), MainMenu.class);
                        intent.putExtra("user_name", mUserName.getText().toString());
                        intent.putExtra("user_username", mUserUsername.getText().toString());
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
