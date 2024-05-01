package com.example.gcccyclingmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText, emailEditText;
    private CheckBox clubCheckBox, participantCheckBox;
    private Button loginButton, registerButton;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        emailEditText = findViewById(R.id.email);
        clubCheckBox = findViewById(R.id.check_club);
        participantCheckBox = findViewById(R.id.check_participant);
        loginButton = findViewById(R.id.loginBtn);
        registerButton = findViewById(R.id.registerBtn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill in both username and password", Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(username);

                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                boolean isClub = true;
                                String storedPassword = dataSnapshot.child("password").getValue(String.class);
                                if(dataSnapshot.child("role").getValue(String.class)=="club"){
                                    isClub = true;
                                }
                                else{
                                    isClub = false;
                                }

                                if (password.equals(storedPassword)) {
                                    // Password matches, login successful
                                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    //change the following code below to a switchcase when fully implement Participant and Club
                                    if (username.equals("admin") && password.equals("admin")){
                                        Intent intent = new Intent(MainActivity.this, AdminPage.class);
                                        startActivity(intent);
                                    }
                                    else if(isClub){
                                        Intent intent = new Intent(MainActivity.this, ClubPage.class);
                                        intent.putExtra("USERNAME", username);
                                        startActivity(intent);
                                        Toast.makeText(MainActivity.this, "!!!!isClub is verified", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Intent intent = new Intent(MainActivity.this, UserPage.class);
                                        intent.putExtra("USERNAME", username);
                                        startActivity(intent);
                                    }
                                    Toast.makeText(MainActivity.this, "past isClub if verified", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    // Password does not match
                                    Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Username does not exist in the database
                                Toast.makeText(MainActivity.this, "Username does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            Toast.makeText(MainActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String role;
                boolean isClub = clubCheckBox.isChecked();
                boolean isParticipant = participantCheckBox.isChecked();

                if (isClub) {
                    role = "Club";
                }else {
                    role = "Participant";
                }


                if (email.isEmpty() || (!isClub && !isParticipant) || username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Use FirebaseAuth to create a new user with email and password
                    FirebaseDatabase database = FirebaseDatabase.getInstance(); //gets an instance of your database so you can read/write to it
                    DatabaseReference newUserRoleRef = database.getReference("users/" + username + "/role");
                    DatabaseReference newUserEmailRef = database.getReference("users/" + username + "/email");
                    DatabaseReference newUserPasswordRef = database.getReference("users/" + username + "/password");

                    newUserRoleRef.setValue(role);
                    newUserEmailRef.setValue(email);
                    newUserPasswordRef.setValue(password);
                    Toast.makeText(MainActivity.this, "Registration Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, UserPage.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);

                }
            }
        });
    }




    //checkboxes when registering an account

    public void onClubCheckBoxClicked(View view){

        CheckBox participantCheck = (CheckBox) findViewById(R.id.check_participant);

        //if club is checked, participant is forced unchecked

        participantCheck.setChecked(false);

    }
    public void onParticipantCheckBoxClicked(View view){

        CheckBox clubCheck = (CheckBox) findViewById(R.id.check_club);

        //if participant is checked, club is forced unchecked
        clubCheck.setChecked(false);

    }

}