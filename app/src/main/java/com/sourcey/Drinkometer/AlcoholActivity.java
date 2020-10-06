package com.sourcey.Drinkometer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;


public class AlcoholActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinneralcohol;
    private static final String[] paths = {"Beer 4,5%", "Wine 12%", "Shot", "Manual selection"};
    private Spinner spinnerbeer;
    private static final String[] paths1 = {"0,33 dl", "0,4 dl", "0,5 dl"};
    private Spinner spinnerwine;
    private static final String[] paths2 = {"1 dl", "2 dl", "3 dl", "5 dl"};
    private Spinner spinnershot;
    private static final String[] paths3 = {"2 cl - 40%", "4 cl - 40%", "2 cl - 80%", "4 cl - 80%"};
    private EditText _amount;
    private EditText _percent;
    private Button btn_add;
    private Persondata user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohol);

        Intent i = getIntent();

        user = (Persondata) i.getSerializableExtra("PersonData");

        _amount = (EditText) findViewById(R.id.input_amount);
        _percent = (EditText) findViewById(R.id.input_percent);
        btn_add = (Button) findViewById(R.id.button_add);
        spinneralcohol = (Spinner) findViewById(R.id.alcoholinput);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AlcoholActivity.this,
                android.R.layout.simple_spinner_item, paths);

        spinnerbeer = (Spinner) findViewById(R.id.beer);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(AlcoholActivity.this,
                android.R.layout.simple_spinner_item, paths1);

        spinnerwine = (Spinner) findViewById(R.id.wine);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(AlcoholActivity.this,
                android.R.layout.simple_spinner_item, paths2);

        spinnershot = (Spinner) findViewById(R.id.shot);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(AlcoholActivity.this,
                android.R.layout.simple_spinner_item, paths3);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinneralcohol.setAdapter(adapter);
        spinneralcohol.setOnItemSelectedListener(this);

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerbeer.setAdapter(adapter1);
        spinnerbeer.setOnItemSelectedListener(this);

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerwine.setAdapter(adapter2);
        spinnerwine.setOnItemSelectedListener(this);

        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnershot.setAdapter(adapter3);
        spinnershot.setOnItemSelectedListener(this);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date actualTime = new java.util.Date();
                Double alcoholForTemplate = 0.0;
                switch (spinneralcohol.getSelectedItemPosition()) {
                    case 0:
                        switch (spinnerbeer.getSelectedItemPosition()) {
                            case 0:
                                alcoholForTemplate = calculateAlcohol(33, 4.5);
                                break;
                            case 1:
                                alcoholForTemplate = calculateAlcohol(40, 4.5);
                                break;
                            case 2:
                                alcoholForTemplate = calculateAlcohol(50, 4.5);
                                break;
                        }
                        break;
                    case 1:
                        switch (spinnerwine.getSelectedItemPosition()) {
                            case 0:
                                alcoholForTemplate = calculateAlcohol(10, 12);
                                break;
                            case 1:
                                alcoholForTemplate = calculateAlcohol(20, 12);
                                break;
                            case 2:
                                alcoholForTemplate = calculateAlcohol(30, 12);
                                break;
                            case 3:
                                alcoholForTemplate = calculateAlcohol(50, 12);
                                break;
                        }
                        break;
                    case 2:
                        switch (spinnershot.getSelectedItemPosition()) {
                            case 0:
                                alcoholForTemplate = calculateAlcohol(2, 40);
                                break;
                            case 1:
                                alcoholForTemplate = calculateAlcohol(4, 40);
                                break;
                            case 2:
                                alcoholForTemplate = calculateAlcohol(2, 80);
                                break;
                            case 3:
                                alcoholForTemplate = calculateAlcohol(4, 80);
                                break;
                        }
                        break;
                    case 3:
                        alcoholForTemplate = calculateAlcohol(Integer.parseInt(_amount.getText().toString()), Double.parseDouble(_percent.getText().toString()));
                        break;
                }
                user.addAlcohol(new OwnPair<>(actualTime, alcoholForTemplate));
                Toast.makeText(AlcoholActivity.this, "Alcohol added", Toast.LENGTH_SHORT).show();
                Intent openMenuActivity = new Intent(AlcoholActivity.this, MenuActivity.class);
                openMenuActivity.putExtra("PersonData", user);
                openMenuActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openMenuActivity, 0);
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (spinneralcohol.getSelectedItemPosition()) {
            case 0:
                spinnerbeer.setVisibility(View.VISIBLE);
                spinnerwine.setVisibility(View.GONE);
                spinnershot.setVisibility(View.GONE);
                _amount.setVisibility(View.GONE);
                _percent.setVisibility(View.GONE);
                break;
            case 1:
                spinnerbeer.setVisibility(View.GONE);
                spinnerwine.setVisibility(View.VISIBLE);
                spinnershot.setVisibility(View.GONE);
                _amount.setVisibility(View.GONE);
                _percent.setVisibility(View.GONE);
                break;
            case 2:
                spinnerbeer.setVisibility(View.GONE);
                spinnerwine.setVisibility(View.GONE);
                spinnershot.setVisibility(View.VISIBLE);
                _amount.setVisibility(View.GONE);
                _percent.setVisibility(View.GONE);
                break;
            case 3:
                spinnerbeer.setVisibility(View.GONE);
                spinnerwine.setVisibility(View.GONE);
                spinnershot.setVisibility(View.GONE);
                _amount.setVisibility(View.VISIBLE);
                _percent.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private double calculateAlcohol(int amount, double percent) {
        double temp = (0.806 * ((amount / 100.0) * (percent / 100) * 1000 * 0.78506 / 10) * 1.2) / user.getWeight();
        if (user.getSex() == 'M') {
            return temp / 0.58;
        } else {
            return temp / 0.49;
        }
    }

    @Override
    public void onBackPressed() {
        Intent openMenuActivity = new Intent(this, MenuActivity.class);
        openMenuActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(openMenuActivity, 0);
    }

}
