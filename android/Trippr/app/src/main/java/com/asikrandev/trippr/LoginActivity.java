package com.asikrandev.trippr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.acision.acisionsdk.AcisionSdk;
import com.acision.acisionsdk.AcisionSdkCallbacks;
import com.acision.acisionsdk.AcisionSdkConfiguration;
import com.acision.acisionsdk.authentication.AuthenticationFailureData;
import com.acision.acisionsdk.messaging.Messaging;
import com.asikrandev.trippr.util.SessionWrapper;

/**
 * Created by JuanM on 18/02/2015.
 */
public class LoginActivity extends Activity {

    private TextView status;

    private EditText usernameTV;
    private EditText passwordTV;
    private Button signin;

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        status = (TextView) findViewById(R.id.status);
        usernameTV = (EditText) findViewById(R.id.username);
        passwordTV = (EditText) findViewById(R.id.password);
        signin = (Button) findViewById(R.id.signin);

        SharedPreferences session = getSharedPreferences("session", 0);
        if(session.contains("username") && session.contains("password")) {
            Log.d("trippr", "contains");
            username = session.getString("username", "");
            password = session.getString("password", "");
            usernameTV.setText(username);
            passwordTV.setText(password);
            status.setVisibility(View.VISIBLE);
            status.setText("Conecting ...");
            signin.setEnabled(false);
            start();
        }else {
            Log.d("trippr", "doesnt contains");
        }

    }

    public void login(View view){
        status.setVisibility(View.VISIBLE);
        status.setText("Conecting ...");

        username = usernameTV.getText().toString();
        password = passwordTV.getText().toString();

        start();

    }

    private void start() {
        signin.setEnabled(false);
        AcisionSdkConfiguration config = new AcisionSdkConfiguration("wvatXmaKZcmM", username, password);
        config.setPersistent(true);
        config.setApplicationActivity(this);
        SessionWrapper.acisionSdk = new AcisionSdk(config, new AcisionSdkCallbacks() {
            @Override
            public void onConnected(AcisionSdk acisionSdk) {
                status.setText("Conected");
                SessionWrapper.messaging = acisionSdk.getMessaging();

                SharedPreferences session = getSharedPreferences("session", 0);
                SharedPreferences.Editor editor = session.edit();
                editor.putString("username", username);
                editor.putString("password", password);
                editor.commit();

                startSession();
            }

            @Override
            public void onAuthenticationFailure(AcisionSdk acisionSdk, AuthenticationFailureData data){
                signin.setEnabled(true);
                status.setText("Conection failure, try again");
            }
        });
    }



    public void startSession(){
        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }


}
