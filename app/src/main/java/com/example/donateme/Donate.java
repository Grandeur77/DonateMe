package com.example.donateme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.donateme.databinding.ActivityDonateBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.content.pm.PackageManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Donate extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityDonateBinding binding;
    private AppCompatButton mButtonAddPin;
    private TextInputEditText item, description;
    private TextInputLayout itemLayout, descriptionLayout;
    private Spinner category;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private double latitude;
    private double longitude;
    private String number = "not a number flag";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    public static int LOCATION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDonateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize UI components
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        progressBar = findViewById(R.id.progressBar);
        mButtonAddPin = findViewById(R.id.button_add_pin);
        item = findViewById(R.id.Item);
        description = findViewById(R.id.Description);
        itemLayout = findViewById(R.id.ItemLayout);
        descriptionLayout = findViewById(R.id.DescriptionLayout);
        category = findViewById(R.id.spinner_category);

        // Initialize the Spinner
        Spinner spinner = findViewById(R.id.spinner_category);
        String[] categories = {
                "Food and Groceries",
                "Clothing and Textiles",
                "Books and Stationery",
                "Toys and Games",
                "Electronics and Devices",
                "Furniture and Household Items",
                "Personal Care Products",
                "Sports and Fitness Equipment"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Handle selection events
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                Toast.makeText(Donate.this, "Selected: " + selectedCategory, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize Firebase Auth
        authProfile = FirebaseAuth.getInstance();

        // Initialize location services
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        };

        // Request location updates
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000); // Update location every 5 seconds
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }

        // Set button click listener
        mButtonAddPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchItemAndDescription();
            }
        });
    }

    private void fetchItemAndDescription() {
        if (!validateItem() || !validateDescription()) {
            return;
        } else {
            showAlertDialog();
        }
    }

    private Boolean validateItem() {
        String val = item.getText().toString();
        if (val.isEmpty()) {
            itemLayout.setError("Field cannot be empty");
            itemLayout.requestFocus();
            return false;
        } else {
            itemLayout.setError(null);
            itemLayout.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateDescription() {
        String val = description.getText().toString();
        if (val.isEmpty()) {
            descriptionLayout.setError("Field cannot be empty");
            descriptionLayout.requestFocus();
            return false;
        } else {
            descriptionLayout.setError(null);
            descriptionLayout.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Donate.this, R.raw.map_style)); // map style
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Donate.this);
        builder.setTitle("Phone number ");
        builder.setMessage("Do you want to show your Phone number to receivers? It will help to connect with you.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                        if (readUserDetails != null) {
                            number = readUserDetails.mobile;
                            WorkingRandomKey();
                        } else {
                            Toast.makeText(Donate.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Donate.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Donate.this, "You rejected the number request", Toast.LENGTH_SHORT).show();
                WorkingRandomKey();
            }
        });

        //Create the AlertDialog
        AlertDialog alertDialog = builder.create();
        //change the button color (continue->red)
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.light_blue));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.light_blue));
                alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.button_bg1);
            }
        });
        alertDialog.show();
    }

    private void WorkingRandomKey() {
        String randomKey = generateRandomKey();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uniqueIdListRef = databaseReference.child("UniqueIdList");
        uniqueIdListRef.orderByValue().equalTo(randomKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Toast.makeText(Donate.this,"exist", Toast.LENGTH_SHORT).show();
                    WorkingRandomKey();
                } else {
                    //Toast.makeText(Donate.this,randomKey, Toast.LENGTH_SHORT).show();
                    uniqueIdListRef.push().setValue(randomKey);
                    MapWorkingRandomKeyWithUid(randomKey); // do proper mapping + also on success place a pin
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Donate.this, "error1", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String generateRandomKey() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int length = 10;
        StringBuilder keyBuilder = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            keyBuilder.append(randomChar);
        }

        String key = keyBuilder.toString();

        if (!key.matches(".*\\d.*")) {
            // If the key doesn't contain a digit, replace a random character with a random digit
            int randomIndex = random.nextInt(length);
            key = key.substring(0, randomIndex) + random.nextInt(10) + key.substring(randomIndex + 1);
        }

        if (!key.matches(".*[A-Z].*")) {
            // If the key doesn't contain an uppercase letter, replace a random character with a random uppercase letter
            int randomIndex = random.nextInt(length);
            key = key.substring(0, randomIndex) + (char) (random.nextInt(26) + 'A') + key.substring(randomIndex + 1);
        }

        return key;
    }

    private void MapWorkingRandomKeyWithUid(String key) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DonateIdMapping");
        ReadWriteMapDonationAndUserId readWriteMapDonationAndUserId = new ReadWriteMapDonationAndUserId(key);
        ref.child(firebaseUser.getUid()).setValue(readWriteMapDonationAndUserId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // FoodMapPinFlag = true;
                    FoodMapDataPushFunction(key);
                } else {
                    Toast.makeText(Donate.this, "error2", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void FoodMapDataPushFunction(String key) {
        LatLng userLocation = new LatLng(latitude, longitude);
        String Item = item.getText().toString();
        String name = getName();
        String selectedCategory = category.getSelectedItem().toString(); // Get the selected category as a String
        DatabaseReference refTwo = FirebaseDatabase.getInstance().getReference("FoodMap");
        ReadWriteLocation readWriteLocation = new ReadWriteLocation(latitude, longitude, name, number, Item, selectedCategory); // Pass the selected category
        refTwo.child(key).setValue(readWriteLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Donate.this, "Location added successfully", Toast.LENGTH_SHORT).show();
                    // ADD a marker to user location
                    Marker marker = mMap.addMarker(new MarkerOptions().position(userLocation).title(name + "||" + item + "||" + selectedCategory).icon(setIcon(Donate.this, R.drawable.marker_donator_style)));
                    // Disable the navigation button
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                    // Animate the marker to the centre of the screen
                    marker.showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                    mButtonAddPin.setEnabled(false);
                    item.setEnabled(false);
                    item.setFocusable(false);
                    description.setEnabled(false);
                    description.setFocusable(false);
                    category.setEnabled(false);
                    category.setFocusable(false);
                    // Changing color of button
                    Drawable grayBackground = getResources().getDrawable(R.drawable.button_bg6);
                    mButtonAddPin.setBackgroundDrawable(grayBackground);
                    insertDataInHistoryNodeInDataBase();
                } else {
                    Toast.makeText(Donate.this, "Error occurred while adding location", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void insertDataInHistoryNodeInDataBase() {
        Map<String, Object> map = new HashMap<>();
        map.put("category", category.getSelectedItem().toString());
        map.put("item", item.getText().toString());
        map.put("description", description.getText().toString());
        FirebaseDatabase.getInstance().getReference().child("History").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push()
                .setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // do nothing
                        progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Donate.this, "error while pushing data into History node", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getName() {
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        return firebaseUser != null ? firebaseUser.getDisplayName() : "Unknown User";
    }

    public BitmapDescriptor setIcon(Activity context, int drawableId) {
        Drawable drawable = ActivityCompat.getDrawable(context, drawableId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
