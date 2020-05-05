package com.sourcey.Drinkometer;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private Persondata person = new Persondata();

    @BindView(R.id.input_username) EditText _usernameText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }



    public void login() {
        Log.d(TAG, "Login");

        boolean success = false;

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // TODO: Implement your own authentication logic here.
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectionUrl =
                    "jdbc:jtds:sqlserver://193.6.19.58:14033;"
                            + "database=DrinkOMeterDb;"
                            + "user=drinkometer;"
                            + "password=UEuMAi0j0MzZ;"
                            + "encrypt=false;"
                            + "trustServerCertificate=true;"
                            + "loginTimeout=30;";
            Connection connection = DriverManager.getConnection(connectionUrl);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Id,FirstName,LastName,UserName,Password,Sex,Weight,Email FROM Users");
            while (rs.next() && !success) {
                person.setId (Integer.parseInt(rs.getString("Id")));
                person.setUsername (rs.getString("UserName"));
                person.setFirstName (rs.getString("FirstName"));
                person.setLastName (rs.getString("LastName"));
                person.setSex (rs.getString("Sex").charAt(0));
                person.setWeight (Integer.parseInt(rs.getString("Weight")));
                person.setEmail (rs.getString("Email"));
                String Password = rs.getString("Password");
                if(person.getUsername().equals(username)&&Password.equals(password)){
                    success=true;
                    //ide hogy jelentkezzen be mert a név jelszó jó.

                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }


        boolean finalSuccess = success;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (finalSuccess) {
                            onLoginSuccess();
                        }
                        else {
                         onLoginFailed();
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here

                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess()   {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra("PersonData", person);
        startActivity(intent);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty() ) {
            _usernameText.setError("enter your username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
