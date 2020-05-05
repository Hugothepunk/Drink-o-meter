package com.sourcey.Drinkometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;

public class MenuActivity extends AppCompatActivity {
    private Persondata user;
    private Button Alcohol;
    private Button Map;
    private Button Friends;
    private Button Pubs;
    private Button Lock;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        TextView username = findViewById(R.id.UserName);
        TextView sober = findViewById(R.id.soberMeter);
        Intent i = getIntent();

            user = (Persondata) i.getSerializableExtra("PersonData");

        username.setText(user.getUsername());
        sober.setText(Sobermeter());
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

    public void openAlcoholActivity() {
        Intent intent = new Intent(this, AlcoholActivity.class);
        intent.putExtra("PersonData", user);
        startActivity(intent);
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

    public String Sobermeter() {
        String sober = "";
        double ebac = 0.0;
        if (user.getAlcohol().isEmpty()) {
            sober = "You should be sober!";
        }
        else{
            for (int i=0 ; i<user.getAlcohol().size() ; i++) {
               Date before = user.getAlcohol().get(i).getFrist();
               double timesince = Duration.between(before.toInstant(), (new java.util.Date()).toInstant()).toMinutes()/60.0;
                if (user.getSex() == 'M') {

                    if (user.getAlcohol().get(i).getSecond() - timesince*0.015 > 0.0){
                        ebac += user.getAlcohol().get(i).getSecond() - timesince*0.015;
                    }
                }
                else {
                    if (user.getAlcohol().get(i).getSecond() - timesince*0.017 > 0.0){
                        ebac += user.getAlcohol().get(i).getSecond() - timesince*0.017;
                    }
                }
            }
        }
        if (user.getSex() == 'M') {
            sober = "You should be sober in " + String.format("%.2f%n" , ebac/0.015) + " hours";
        }
        else {
            sober = "You should be sober in " + ebac/0.017 + " hours";
        }
        if (ebac != 0.0) {
            return sober;
        }
        else{
            return "You should be sober!";
        }
    }
}
