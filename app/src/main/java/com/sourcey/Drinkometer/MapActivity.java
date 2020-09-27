package com.sourcey.Drinkometer;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.skyfishjy.library.RippleBackground;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_CYAN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_MAGENTA;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ORANGE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_ROSE;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_VIOLET;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_YELLOW;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Persondata user;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    private Location mLastKnowLocation;
    private LocationCallback locationCallback;

    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private Button btnFind;
    private Button btnFriend;
    private Button btnClear;
    @BindView(R.id.input_username) EditText _usernameText;
    private RippleBackground rippleBg;

    private final float  DEFAULT_ZOOM = 18;
    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        assert vectorDrawable != null;
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent i = getIntent();
        user = (Persondata) i.getSerializableExtra("Person");


        materialSearchBar = findViewById(R.id.searchBar);
        btnFind = findViewById(R.id.btn_find);
        btnFriend = findViewById(R.id.btn_friend);
        btnClear = findViewById(R.id.btn_clear);

        rippleBg = findViewById(R.id.ripple_bg);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        Places.initialize(MapActivity.this,"AIzaSyAaFMRlRWAkou0Ofqf9g8BsDdFwbxEp4xQ");
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(), true,null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    materialSearchBar.clearSuggestions();
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK){
                    materialSearchBar.closeSearch();
                }
            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry("orsz")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()){
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null){
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionsList = new ArrayList<>();
                                for (int i = 0; i < predictionList.size();i++){
                                    AutocompletePrediction prediction = predictionList.get(i);
                                    suggestionsList.add(prediction.getFullText(null).toString());
                                }
                                materialSearchBar.updateLastSuggestions(suggestionsList);
                                if (!materialSearchBar.isSuggestionsVisible()){
                                    materialSearchBar.showSuggestionsList();
                                }
                            }
                        } else {
                            Log.i("mytag", "prediction fetching task unsuccessful");
                        }
                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= predictionList.size()){
                    return;
                }
                AutocompletePrediction selectedPrediction = predictionList.get(position);
                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions();
                    }
                }, 1000);

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null){
                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                    String placeId = selectedPrediction.getPlaceId();
                    List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);

                    final FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId,placeFields).build();
                    placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @Override
                        public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                            Place place = fetchPlaceResponse.getPlace();
                            Log.i("mytag", "Place found: " + place.getName());
                            LatLng latLngOfPlace = place.getLatLng();
                            if (latLngOfPlace != null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOfPlace,DEFAULT_ZOOM));
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof ApiException){
                                ApiException apiException = (ApiException) e;
                                apiException.printStackTrace();
                                int statusCode = apiException.getStatusCode();
                                Log.i("mytag", "place not found: " + e.getMessage());
                                Log.i("mytag", "status code: " + statusCode);

                            }
                        }
                    });
                }
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });


        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng currentBarLocation = mMap.getCameraPosition().target;
                rippleBg.startRippleAnimation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rippleBg.stopRippleAnimation();
                        LatLng Kozpont = new LatLng( 47.089938, 17.910099);
                        LatLng Gesztiplace = new LatLng(47.0908647, 17.9015938);
                        mMap.addMarker(new MarkerOptions().position(Gesztiplace).title("Geszti").icon(BitmapDescriptorFactory.defaultMarker(HUE_ORANGE)));
                        LatLng DiplomaFoszto = new LatLng(47.0856461, 17.9086779);
                        mMap.addMarker(new MarkerOptions().position(DiplomaFoszto).title("Foszto").icon(BitmapDescriptorFactory.defaultMarker(HUE_ORANGE)));
                        LatLng Canyon = new LatLng(47.0926046, 17.9099169);
                        mMap.addMarker(new MarkerOptions().position(Canyon).title("Canyon").icon(BitmapDescriptorFactory.defaultMarker(HUE_ORANGE)));
                        LatLng SportPub = new LatLng(47.0842306227, 17.9127573967);
                        mMap.addMarker(new MarkerOptions().position(SportPub).title("SportPub").icon(BitmapDescriptorFactory.defaultMarker(HUE_ORANGE)));
                        LatLng Johnny = new LatLng(47.0933274, 17.9118798);
                        mMap.addMarker(new MarkerOptions().position(Johnny).title("Johnny").icon(BitmapDescriptorFactory.defaultMarker(HUE_ORANGE)));
                        LatLng Metro = new LatLng(47.0915639, 17.911835);
                        mMap.addMarker(new MarkerOptions().position(Metro).title("Metro").icon(BitmapDescriptorFactory.defaultMarker(HUE_ORANGE)));
                        LatLng Barka = new LatLng(47.0939028, 17.9086135);
                        mMap.addMarker(new MarkerOptions().position(Barka).title("Barka").icon(BitmapDescriptorFactory.defaultMarker(HUE_ORANGE)));
                        LatLng Gosser = new LatLng(47.0923308991, 17.9073916376);
                        mMap.addMarker(new MarkerOptions().position(Gosser).title("Gosser").icon(BitmapDescriptorFactory.defaultMarker(HUE_ORANGE)));
                        LatLng Pityoka = new LatLng(47.0922101, 17.9071449);
                        mMap.addMarker(new MarkerOptions().position(Pityoka).title("Pityoka").icon(BitmapDescriptorFactory.defaultMarker(HUE_ORANGE)));

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Kozpont,15));




                    }
                }, 3000);
                // Initialize a new GeoAddressStandardizer-class with your API-Key


            }
        });
        btnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double x = mLastKnowLocation.getLatitude();
                double y = mLastKnowLocation.getLongitude();
                //String x=String.valueOf(x_);
                //String y=String.valueOf(y_);
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
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
                    int id = user.getId();
                    stmt.executeUpdate("UPDATE Users SET [x] = "+x+",[y] = "+y+" WHERE Id = "+id+"");
                    ArrayList<String> Friends = new ArrayList<>();
                    ResultSet rs2 = stmt.executeQuery("SELECT * FROM Friends WHERE ID="+id+"");
                    ArrayList<Integer> Flist = new ArrayList<>();
                    while(rs2.next()) {
                        String FriendID = rs2.getString("FriendID");
                        Flist.add(Integer.parseInt(FriendID));
                    }

                    //ArrayList<Float> Color = new ArrayList<>(Arrays.asList(HUE_AZURE,HUE_CYAN,HUE_GREEN,HUE_MAGENTA,HUE_ORANGE,HUE_RED,HUE_ROSE,HUE_VIOLET,HUE_YELLOW));
                    ArrayList<Integer> Colors = new ArrayList<>(Arrays.asList(R.color.quantum_googblue,R.color.quantum_cyan,R.color.quantum_googgreen,R.color.quantum_pink,R.color.quantum_orange,R.color.quantum_googred,R.color.quantum_vanillaredA700,R.color.quantum_purple,R.color.quantum_yellow,R.color.quantum_brown,R.color.quantum_grey,R.color.quantum_indigo));
                    Random rand = new Random();

                    for(int i=0;i<Flist.size();i++){
                        ResultSet f = stmt.executeQuery("SELECT x,y,UserName FROM Users WHERE ID='"+Flist.get(i)+"'");
                        f.next();
                        String x_c = f.getString("x");
                        String y_c = f.getString("y");
                        String UserName = f.getString("UserName");
                        int randomElement = Colors.get(rand.nextInt(Colors.size()));
                        f=null;
                        BitmapDescriptor User = vectorToBitmap(R.drawable.ic_smileys, ContextCompat.getColor(getApplicationContext(),randomElement));
                        LatLng Pityoka = new LatLng(Double.parseDouble(x_c), Double.parseDouble(y_c));
                        mMap.addMarker(new MarkerOptions().position(Pityoka).title(UserName).icon(User));
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                BitmapDescriptor User = vectorToBitmap(R.drawable.ic_person_black_24dp, ContextCompat.getColor(getApplicationContext(),R.color.aluminum));
                mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnowLocation.getLatitude(), mLastKnowLocation.getLongitude())).title("Me").icon(User));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (mapView != null &&  mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0,0,40,180);
        }

        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(MapActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(MapActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(MapActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException){
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MapActivity.this, 51);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (materialSearchBar.isSuggestionsVisible())
                    materialSearchBar.clearSuggestions();
                if (materialSearchBar.isSuggestionsEnabled())
                    materialSearchBar.closeSearch();
                return false;
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (requestCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    mLastKnowLocation = task.getResult();
                    if (mLastKnowLocation != null) {
                        BitmapDescriptor User = vectorToBitmap(R.drawable.ic_person_black_24dp, ContextCompat.getColor(getApplicationContext(),R.color.aluminum));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnowLocation.getLatitude(), mLastKnowLocation.getLongitude())).title("Me").icon(User));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnowLocation.getLatitude(), mLastKnowLocation.getLongitude()), DEFAULT_ZOOM));
                    } else {
                        final LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setInterval(10000);
                        locationRequest.setFastestInterval(5000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationCallback = new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                if (locationResult == null){
                                    return;
                                }

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnowLocation.getLatitude(), mLastKnowLocation.getLongitude()), DEFAULT_ZOOM));
                                mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                            }
                        };
                        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                    }

                }   else{
                    Toast.makeText(MapActivity.this,"unable to get last location", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent openMenuActivity = new Intent(this, MenuActivity.class);
        openMenuActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(openMenuActivity, 0);
    }

}
