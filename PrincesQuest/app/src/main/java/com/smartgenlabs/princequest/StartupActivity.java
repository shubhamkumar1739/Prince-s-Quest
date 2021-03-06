package com.smartgenlabs.princequest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;


import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class StartupActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    public static final String TAG = "startActivity";

    SharedPreference SP;
    NetworkStateReceiver networkStateReceiver;

    Users curUserLocal;

    TextInputEditText login_username_et;
    Button login_btn;

    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_startup);
        getSupportActionBar().hide();

        SP = new SharedPreference(this);

        if(SP.getBoolean("game_initiated")){
            sendToHome();
        }

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Please Wait...");

        startLoginFlow();

    }

    @Override
    protected void onResume() {
        super.onResume();
        networkStateReceiver = new NetworkStateReceiver(this);
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }



    private void startLoginFlow() {

        login_username_et = findViewById(R.id.login_username_et);
        login_btn = findViewById(R.id.login_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name;
                name = login_username_et.getText().toString();
                if (TextUtils.isEmpty(name) ) {
                    login_username_et.setError("Enter your name Prince");
                } else {
                    mProgress.setMessage("Starting Your Quest");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();

                    SP.set("name",name);
                    SP.set("game_initiated",true);

                    mProgress.dismiss();
                    sendToHome();
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
    }



    private void sendToHome() {
        if(mProgress!=null) mProgress.dismiss();
        startActivity(new Intent(StartupActivity.this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }


    //******** NETWORK METHODS
    @Override
    public void onNetworkAvailable() {
        M.showNoNetworkAlert(this, false);
    }

    @Override
    public void onNetworkUnavailable() {
        M.showNoNetworkAlert(this, true);
    }
}
