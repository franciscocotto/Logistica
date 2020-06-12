package com.example.inventario;

import androidx.appcompat.app.AppCompatActivity;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etPassword;
    private TextView  Info;
    private Button  Login;
    private int counter = 5;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, Home.class));
        }

        progressBar = findViewById(R.id.progressBar);

        etName = (EditText)findViewById(R.id.etName);
        etPassword = (EditText)findViewById(R.id.etPassword);
        Login = (Button)findViewById(R.id.btnLogin);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // validate(Usuario.getText().toString(), Password.getText().toString());
                String mUser = etName.getText().toString().trim();
                String mPass = etPassword.getText().toString().trim();

                if (!mUser.isEmpty() || !mPass.isEmpty()){
                    userLogin();
                }else {
                    etName.setError("Favor Ingresar Usuario");
                    etPassword.setError("Favor Ingresar Password");
                }

            }
        });
    }

    private void userLogin() {
        final String username = etName.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        String url = "https://inventario-pdm115.000webhostapp.com/login.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);

                        try {
                            //converting response to json object
                            //JSONObject obj = new JSONObject(response);

                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("user");

                                //creating a new user object
                                User user = new User(
                                        userJson.getInt("id"),
                                        userJson.getString("name"),
                                        userJson.getString("username"),
                                        userJson.getString("email")
                                );

                                //storing the user in shared preferences
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                //starting the profile activity
                                finish();
                                Intent intent = new Intent(MainActivity.this, Home.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void login(){

        String url = "https://invetariopdm115.000webhostapp.com//login.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("success")){



                            Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivity.this, Home.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_LONG).show();

                        }
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Login error", Toast.LENGTH_LONG).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("UserName", etName.getText().toString().trim());
                params.put("Password", etPassword.getText().toString().trim());
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void validate(String userName, String userPassword){
        if(userName.equals("Admin")  && (userPassword.equals("1234") )){
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
        }else {
            counter--;
            Info.setText("Intentos Faltantes: " + String.valueOf(counter));
            if(counter==0){
                Login.setEnabled(false);
            }
        }
    }



}
