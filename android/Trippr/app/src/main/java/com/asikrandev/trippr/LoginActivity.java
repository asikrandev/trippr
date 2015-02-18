package com.asikrandev.trippr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.acision.acisionsdk.AcisionSdk;
import com.acision.acisionsdk.AcisionSdkCallbacks;
import com.acision.acisionsdk.AcisionSdkConfiguration;
import com.acision.acisionsdk.messaging.Messaging;

/**
 * Created by JuanM on 18/02/2015.
 */
public class LoginActivity extends Activity {

    private EditText username;
    private EditText password;

    private AcisionSdk acisionSdk;
    private Messaging messaging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

    }

    public void login(View view){

        String user;
        String pass;

        user = username.getText().toString();
        pass = password.getText().toString();

        start(user, pass);

    }

    private void start(String username, String password) {
        AcisionSdkConfiguration config = new AcisionSdkConfiguration("wvatXmaKZcmM", username, password);
        config.setPersistent(true);
        config.setApplicationActivity(this);
        acisionSdk = new AcisionSdk(config, new AcisionSdkCallbacks() {
            @Override
            public void onConnected(AcisionSdk acisionSdk) {
                startSession();
                //TextView editMessage = (TextView) findViewById(R.id.text_display);
                //editMessage.setText("Connected");
                // Now start the messaging. /
                //messaging = acisionSdk.getMessaging();
                //testMessaging(messaging);
            }
        });
    }

    public void startSession(){
        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }


}
