package com.example.donateme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//texting
public class MainActivity extends AppCompatActivity {

    private CardView cardDonate,cardReceive,cardFoodMap,cardMyPin,cardHistory,profile,menu_setting,menu_logout,feedback,review_history;
    private FirebaseAuth authProfile;private ProgressBar progressBar; private FirebaseUser firebaseUser;
    private static final  String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardDonate=findViewById(R.id.cardDonate);
        cardReceive=findViewById(R.id.cardReceive);
        cardFoodMap=findViewById(R.id.cardFoodMap);
        cardMyPin=findViewById(R.id.cardMyPin);
        cardHistory=findViewById(R.id.cardHistory);
        profile=findViewById(R.id.profile);
        menu_setting=findViewById(R.id.menu_setting);
        menu_logout=findViewById(R.id.menu_logout);
        feedback=findViewById(R.id.cardReview);
        review_history=findViewById(R.id.cardReviewHistory);
        progressBar=findViewById(R.id.progressBar);
        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

        if(firebaseUser == null) {
            Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        else {
            checkIfEmailVerified(firebaseUser);
        }
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        menu_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, setting_activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        menu_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authProfile.signOut();
                Intent intent = new Intent(MainActivity.this, landing_page.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        cardReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { progressBar.setVisibility(View.VISIBLE);checkingPinExistOrNotForReceive();}
        });
        cardDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);checkingPinExistOrNotForDonate();
            }
        });
        cardFoodMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FoodMap.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        cardHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, History.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FdBack.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        review_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, feedback_history.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        cardMyPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                // Get the current user's ID
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                // Get the reference to the users node
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("DonateIdMapping");
                // Check if the user ID exists in the users node
                usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //exist
                        if (dataSnapshot.exists()) {
                            progressBar.setVisibility(View.GONE);
                            //procede further to delete it
                            String a = dataSnapshot.child("donationId").getValue(String.class);
                            showAlertDialogToRemoveCurrentPin(a);
                        } else { //exist not
                            progressBar.setVisibility(View.GONE);
                            //show that it does not exist
                            showAlertDialogPinDoesNotExist();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this, "Error checking user existence", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    private void checkingPinExistOrNotForDonate() {
        // Get the current user's ID
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Get the reference to the users node
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("DonateIdMapping");
        // Check if the user ID exists in the users node
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If the snapshot is not null, the user exists
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    showAlertDialogThree();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(MainActivity.this, Donate.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error checking user existence", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void checkingPinExistOrNotForReceive() {
        // Get the current user's ID
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Get the reference to the users node
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("DonateIdMapping");
        // Check if the user ID exists in the users node
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // If the snapshot is not null, the user exists
                if (dataSnapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    showAlertDialogThree();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(MainActivity.this, Receive.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error checking user existence", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if(!firebaseUser.isEmailVerified())
        {
            showAlertDialog();
        }
    }
    private void showAlertDialog() {
        //setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. If you have already verified your email close the app and Restart the application again");
        builder.setCancelable(false);
        builder.setPositiveButton("Get Verified", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent =new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //TO email app in new Window and not within our app
                startActivity(intent);
            }
        });
        //Create the AlertDialog
        AlertDialog alertDialog = builder.create();
        //change the button color (continue->blue)
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.light_blue));
                // Change the dialog box background color
                alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.button_bg1);

            }
        });
        //Show the AlertDialog
        alertDialog.show();
    }
    private void showAlertDialogToRemoveCurrentPin(String id) {
        //setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Remove Current Pin");
        //builder.setMessage("Please verify your email now. You can not login without email verification.");
        //open Email Apps if user clicks/taps Continue button
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DatabaseReference databaseReferenceTwo = FirebaseDatabase.getInstance().getReference("DonateIdMapping");
                DatabaseReference databaseReferenceOne = FirebaseDatabase.getInstance().getReference("FoodMap");
                //deleting DonateIdMapping
                databaseReferenceTwo.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: User data2 Deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                //delete FoodMap pin
                databaseReferenceOne.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: User data2 Deleted");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(MainActivity.this, "Your pin is removed", Toast.LENGTH_LONG).show();
            }
        });
        //Create the AlertDialog
        AlertDialog alertDialog = builder.create();
        //change the button color (continue->red)
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.light_blue));
                // Change the dialog box background color
                alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.button_bg1);

            }
        });
        progressBar.setVisibility(View.GONE);
        //Show the AlertDialog
        alertDialog.show();
    }
    private void showAlertDialogThree() {
        //setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Pin already exist");
        builder.setMessage("In order to proceed further, please remove your current pin");
        //Create the AlertDialog
        AlertDialog alertDialog = builder.create();
        //change the button color (continue->red)
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                // Change the dialog box background color
                alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.button_bg1);
            }
        });
        //Show the AlertDialog
        alertDialog.show();
    }
    private void showAlertDialogPinDoesNotExist() {
        //setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Pin does not exist");
        builder.setMessage("In order to proceed further, please first place Donation Pin");
        //Create the AlertDialog
        AlertDialog alertDialog = builder.create();
        //change the button color (continue->red)
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                // Change the dialog box background color
                alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.button_bg1);
            }
        });
        //Show the AlertDialog
        alertDialog.show();
    }
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        if(firebaseUser != null) {
            firebaseUser.reload();
            if(!firebaseUser.isEmailVerified()) {
                showAlertDialog();
            }
        }
    }
}