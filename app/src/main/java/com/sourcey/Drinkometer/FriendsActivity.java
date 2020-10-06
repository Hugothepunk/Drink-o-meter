package com.sourcey.Drinkometer;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {
    private String Username = null;
    private Button friends;
    private Button AddFriends;
    private Button Add;
    private TextInputLayout friendName;
    private ListView friendList;
    private String friendUsername;
    private Persondata user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Intent intent = getIntent();
        user = (Persondata) intent.getSerializableExtra("PersonData");
        Username = user.getUsername();

        friends = findViewById(R.id.btn_friends);
        friendList = findViewById(R.id.friendList);
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friends.setVisibility(View.GONE);
                AddFriends.setVisibility(View.GONE);
                friendList.setVisibility(View.VISIBLE);
                FriendList();
            }
        });

        AddFriends = findViewById(R.id.btn_addFriends);
        Add = findViewById(R.id.btn_add);
        friendName = findViewById(R.id.input_friendUsername1);
        AddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friends.setVisibility(View.GONE);
                AddFriends.setVisibility(View.GONE);
                friendName.setVisibility(View.VISIBLE);
                Add.setVisibility(View.VISIBLE);
            }
        });
        EditText _friendUsernameText = (EditText) findViewById(R.id.input_friendUsername);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendUsername = _friendUsernameText.getText().toString();
                _friendUsernameText.setText("");
                friends.setVisibility(View.VISIBLE);
                AddFriends.setVisibility(View.VISIBLE);
                friendName.setVisibility(View.GONE);
                Add.setVisibility(View.GONE);
                FriendAdd();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent openMenuActivity = new Intent(this, MenuActivity.class);
        openMenuActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(openMenuActivity, 0);
    }

    public void FriendList() {
        ArrayList<String> Friends = new ArrayList<>();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectionUrl =
                    "jdbc:jtds:sqlserver://SQL5080.site4now.net:1433;"
                            + "database=DB_A68361_drinkometer;"
                            + "user=DB_A68361_drinkometer_admin;"
                            + "password=drinkometer1;"
                            + "encrypt=false;"
                            + "trustServerCertificate=true;"
                            + "loginTimeout=30;";
            Connection connection = DriverManager.getConnection(connectionUrl);
            Statement stmt = connection.createStatement();

            ResultSet rs1 = stmt.executeQuery("SELECT ID,UserName FROM Users WHERE UserName='" + Username + "'");
            rs1.next();
            String ID = rs1.getString("ID");
            ResultSet rs2 = stmt.executeQuery("SELECT * FROM Friends WHERE ID=" + ID + "");
            ArrayList<Integer> Flist = new ArrayList<>();
            while (rs2.next()) {
                String FriendID = rs2.getString("FriendID");
                Flist.add(Integer.parseInt(FriendID));
            }
            for (int i = 0; i < Flist.size(); i++) {
                ResultSet f = stmt.executeQuery("SELECT * FROM Users WHERE ID='" + Flist.get(i) + "'");
                f.next();
                String Friend = f.getString("UserName");
                f = null;
                Friends.add(Friend);
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.listview, R.id.textView, Friends);
            friendList.setAdapter(arrayAdapter);
                /*ResultSet f = stmt.executeQuery("SELECT * FROM Users WHERE ID='"+FriendID+"'");
                f.next();
                String Friend = f.getString("UserName");
                System.out.println(Friend);*/
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void FriendAdd() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectionUrl =
                    "jdbc:jtds:sqlserver://SQL5080.site4now.net:1433;"
                            + "database=DB_A68361_drinkometer;"
                            + "user=DB_A68361_drinkometer_admin;"
                            + "password=drinkometer1;"
                            + "encrypt=false;"
                            + "trustServerCertificate=true;"
                            + "loginTimeout=30;";
            Connection connection = DriverManager.getConnection(connectionUrl);
            Statement stmt = connection.createStatement();

//            Statement stmt = connection.createStatement();
//            Scanner select = new Scanner(System.in);
//            System.out.println("Ki vagy?: ");
            ResultSet you = stmt.executeQuery("SELECT ID,UserName FROM Users WHERE UserName='" + Username + "'");
            you.next();
            String YouID = you.getString("ID");
//            System.out.println(YouID);
//            System.out.println("Kit akarsz barátnak?: ");
//            String Friend = select.nextLine();

            ResultSet friend = stmt.executeQuery("SELECT ID,UserName FROM Users WHERE UserName='" + friendUsername + "'");
            friend.next();
            String FriendID = friend.getString("ID");
//            System.out.println(FriendID);

            stmt.executeUpdate("INSERT INTO Friends ([ID],[FriendID]) VALUES (" + YouID + "," + FriendID + ")");
            stmt.executeUpdate("INSERT INTO Friends ([ID],[FriendID]) VALUES (" + FriendID + "," + YouID + ")");
            Toast.makeText(FriendsActivity.this, "Friend added", Toast.LENGTH_SHORT).show();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}



