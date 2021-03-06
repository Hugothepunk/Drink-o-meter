package com.sourcey.Drinkometer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sourcey.Drinkometer.BackgroundService.LocalBinder;

import java.time.Duration;
import java.util.Date;

public class MenuActivity extends AppCompatActivity {
    private static final int START_ALCOHOL_ACTIVITY = 1;
    private Persondata user;
    private Button Alcohol;
    private Button Map;
    private Button Friends;
    private Button Pubs;
    private Button Lock;
    private BackgroundService mService;
    private boolean mBound = false;
    private final static int INTERVAL = 1000 * 60 * 1; //1 minutes
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private TextView sober;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        TextView username = findViewById(R.id.UserName);
        sober = findViewById(R.id.soberMeter);
        Intent intent = getIntent();

        user = (Persondata) intent.getSerializableExtra("PersonData");

        username.setText(user.getUsername());

        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        Alcohol = findViewById(R.id.btn_alcohol);
        Alcohol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAlcoholActivity();
            }
        });

        Map = findViewById(R.id.btn_map);
        Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMapActivity();
            }
        });

        Friends = findViewById(R.id.btn_friends);
        Friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFriendsActivity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sober.setText(soberMeter(user));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == START_ALCOHOL_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                user = (Persondata) data.getSerializableExtra("PersonData");
                sober.setText(soberMeter(user));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }



    public void openAlcoholActivity() {
        Intent intent = new Intent(this, AlcoholActivity.class);
        intent.putExtra("PersonData", user);
        startActivityForResult(intent,START_ALCOHOL_ACTIVITY);
    }

    public void openMapActivity() {
        Intent intent = new Intent(this, PermissionsActivity.class);
        intent.putExtra("PersonData", user);
        startActivity(intent);
    }

    public void openFriendsActivity() {
        Intent intent = new Intent(this, FriendsActivity.class);
        intent.putExtra("PersonData", user);
        startActivity(intent);
    }

    public void openPubsActivity() {
        Intent intent = new Intent(this, PubsActivity.class);
        intent.putExtra("PersonData", user);
        startActivity(intent);
    }

    public void openLockActivity() {
        Intent intent = new Intent(this, LockActivity.class);
        intent.putExtra("PersonData", user);
        startActivity(intent);
    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to BackgroundService, cast the IBinder and get BackgroundService instance
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public String soberMeter(Persondata user) {
        String sober = "";
        double ebac = 0.0;
        if (!user.getAlcohol().isEmpty()) {
            for (int i = 0; i < user.getAlcohol().size(); i++) {
                Date before = user.getAlcohol().get(i).getFirst();
                double timeSince = Duration.between(before.toInstant(), (new java.util.Date()).toInstant()).toMinutes() / 60.0;
                if (user.getSex() == 'M') {
                    if (user.getAlcohol().get(i).getSecond() - timeSince * 0.015 > 0.0) {
                        ebac += user.getAlcohol().get(i).getSecond() - timeSince * 0.015;
                    }
                } else {
                    if (user.getAlcohol().get(i).getSecond() - timeSince * 0.017 > 0.0) {
                        ebac += user.getAlcohol().get(i).getSecond() - timeSince * 0.017;
                    }
                }
            }
        }
        if (user.getSex() == 'M') {
            sober = "You should be sober in " + String.format("%.2f%n", ebac / 0.015) + " hours";
        } else {
            sober = "You should be sober in " + ebac / 0.017 + " hours";
        }
        if (ebac != 0.0) {
            return sober;
        } else {
            return "You should be sober!";
        }
    }
}
