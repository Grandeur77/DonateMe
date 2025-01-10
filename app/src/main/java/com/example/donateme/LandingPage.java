package com.example.donateme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LandingPage extends AppCompatActivity {
    EditText email, password;
    Button btnlogin, btnsignup, btnforgot;
    DBHelper DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        email = (EditText) findViewById(R.id.id7_textUserEmail);
        password = (EditText) findViewById(R.id.id7_textUserPassword);
        btnlogin = (Button) findViewById(R.id.id9_login);
        btnsignup = (Button) findViewById(R.id.id10_sign_in);
        btnforgot = (Button) findViewById(R.id.id8_Forget_password);
        DB = new DBHelper(this);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString();
                String pass = password.getText().toString();

                if(userEmail.equals("") || pass.equals("")) {
                    Toast.makeText(LandingPage.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean checkuserpass = DB.checkusernamepassword(userEmail, pass);
                    if(checkuserpass == true) {
                        Toast.makeText(LandingPage.this, "Sign in successful", Toast.LENGTH_SHORT).show();
                        // Navigate to the Dashboard if login is successful
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LandingPage.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });

        btnforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
            }
        });


    }
}