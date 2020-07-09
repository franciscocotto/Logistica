package com.example.logistica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LoginButton loginButton;
  private CallbackManager callbackManager;
  private AccessTokenTracker accessTokenTracker;
  private ProfileTracker profileTracker;


    private EditText etName;
    private EditText etPassword;
    private TextView  Info;
    private Button  Login;
    private int counter = 5;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

       callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                nextActivity(newProfile);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Profile profile = Profile.getCurrentProfile();
                nextActivity(profile);
                Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        };
      loginButton.setReadPermissions(Arrays.asList("user_friends"));
      loginButton.registerCallback(callbackManager, callback);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            User user = SharedPrefManager.getInstance(this).getUser();
            if(user.getRol()== 1){
                finish();
                startActivity(new Intent(this, Administrador.class));
            }
            else{
            finish();
            startActivity(new Intent(this, Driver.class));
            }
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
        printKeyHash();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        nextActivity(profile);

    }


    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        //Facebook login
        callbackManager.onActivityResult(requestCode, responseCode, intent);

    }


    private void nextActivity(Profile profile){
        if(profile != null){
            Intent main = new Intent(MainActivity.this, Facebook.class);
            main.putExtra("name", profile.getFirstName());
            main.putExtra("surname", profile.getLastName());
            main.putExtra("imageUrl", profile.getProfilePictureUri(200,200).toString());
            startActivity(main);
        }
    }





    private void printKeyHash() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo("com.example.logistica", PackageManager.GET_SIGNATURES);
                    for(Signature signature:info.signatures){
                        MessageDigest md = MessageDigest.getInstance("SHA");
                        md.update(signature.toByteArray());
                        Log.d("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
                    }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void userLogin() {
        final String username = etName.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        String url = "https://inventario-pdm115.000webhostapp.com/Logistica/ws_ca06025/login.php";

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
                                        userJson.getInt("id_conductor"),
                                        userJson.getInt("rol"),
                                        userJson.getString("name"),
                                        userJson.getString("username"),
                                        userJson.getString("email")
                                );


                                if(user.getRol()== 1){
                                    //storing the user in shared preferences
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                    //starting the profile activity
                                    finish();
                                    Intent intent = new Intent(MainActivity.this, Administrador.class);
                                    startActivity(intent);
                                }
                                else{
                                    //storing the user in shared preferences
                                    SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                    //starting the profile activity
                                    finish();
                                    Intent intent = new Intent(MainActivity.this, Driver.class);
                                    startActivity(intent);
                                }
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



    private void validate(String userName, String userPassword){
        if(userName.equals("Admin")  && (userPassword.equals("1234") )){
            Intent intent = new Intent(MainActivity.this, Administrador.class);
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
