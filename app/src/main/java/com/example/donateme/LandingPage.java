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
    Button btnlogin;
    DBHelper DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        email = (EditText) findViewById(R.id.id7_textUserEmail);
        password = (EditText) findViewById(R.id.id7_textUserPassword);
        btnlogin = (Button) findViewById(R.id.id9_login);
        DB = new DBHelper(this);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userEmail = email.getText().toString();
                String pass = password.getText().toString();

                if(userEmail.equals("")||pass.equals(""))
                    Toast.makeText(LandingPage.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                else{
                    Boolean checkuserpass = DB.checkusernamepassword(userEmail, pass);
                    if(checkuserpass==true){
                        Toast.makeText(LandingPage.this, "Sign in successfull", Toast.LENGTH_SHORT).show();
                        Intent intent  = new Intent(getApplicationContext(), SplashScreen.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LandingPage.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}