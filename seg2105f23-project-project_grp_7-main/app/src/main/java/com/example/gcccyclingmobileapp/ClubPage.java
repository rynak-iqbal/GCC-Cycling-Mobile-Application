package com.example.gcccyclingmobileapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClubPage extends AppCompatActivity {

    // UI Components
    private EditText eventNameEditText;
    private EditText eventDescriptionEditText;
    private EditText ageRequirementEditText;
    private EditText paceEditText;
    private EditText levelEditText;
    private EditText socialMediaLinkEditText;
    private EditText mainContactEditText;
    private EditText phoneNumberEditText;
    private Button addButton;
    private Button deleteButton;
    private Button editEventButton;
    private Button saveButton;
    private Spinner eventTypeSpinner;
    private Spinner eventSpinner;





    // UI Components
    // ... [as previously defined]

    // Firebase reference
    private DatabaseReference databaseReference;
    private DatabaseReference eventsRef;
    private DatabaseReference userRef;

    // Current user's username (assuming you have a method to fetch this)
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubpage);
        String username = getIntent().getStringExtra("USERNAME");

        // Initialize UI components
        // ... [as previously defined]

        // Initialize Firebase references
        databaseReference = FirebaseDatabase.getInstance().getReference();
        eventsRef = databaseReference.child("events");
        userRef = databaseReference.child("users").child(currentUsername);

        // Get current user's username
        currentUsername = getCurrentUsername();

        // Set up listeners
        setupListeners();
    }

    private void setupListeners() {
        addButton.setOnClickListener(v -> addEvent());
        editEventButton.setOnClickListener(v -> editEvent());
        saveButton.setOnClickListener(v -> saveChanges());
        deleteButton.setOnClickListener(v -> deleteEvent());
        // More listeners...
    }

    private void addEvent() {
        String eventName = eventNameEditText.getText().toString();
        String eventDescription = eventDescriptionEditText.getText().toString();
        int ageRequirement = Integer.parseInt(ageRequirementEditText.getText().toString());
        String pace = paceEditText.getText().toString();
        String level = levelEditText.getText().toString();
        String eventType = eventTypeSpinner.getSelectedItem().toString();

        // Generate a unique key for the new event
        String eventId = eventsRef.push().getKey();

        // Create a HashMap to store event data
        HashMap<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", eventName);
        eventData.put("eventDescription", eventDescription);
        eventData.put("ageRequirement", ageRequirement);
        eventData.put("pace", pace);
        eventData.put("level", level);
        eventData.put("eventType", eventType);
        eventData.put("organizerUsername", currentUsername);

        // Save the event data under the 'events' node in Firebase
        eventsRef.child(eventId).setValue(eventData)
                .addOnSuccessListener(aVoid -> Toast.makeText(ClubPage.this, "Event added successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ClubPage.this, "Failed to add event", Toast.LENGTH_SHORT).show());

    }

    private void editEvent() {

    }

    private void deleteEvent() {
        String selectedEventId = eventSpinner.getSelectedItem().toString();

        if (selectedEventId.isEmpty()) {
            Toast.makeText(ClubPage.this, "Please select an event to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove the event from the database
        eventsRef.child(selectedEventId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ClubPage.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    // Optional: Refresh the eventSpinner items if needed
                })
                .addOnFailureListener(e -> Toast.makeText(ClubPage.this, "Failed to delete event", Toast.LENGTH_SHORT).show());

    }
    private void saveChanges() {
        String socialMediaLink = socialMediaLinkEditText.getText().toString();
        String mainContact = mainContactEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();

        // Update the user's profile in Firebase
        userRef.child("socialMediaLink").setValue(socialMediaLink);
        userRef.child("mainContact").setValue(mainContact);
        userRef.child("phoneNumber").setValue(phoneNumber);

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    private String getCurrentUsername() {
        //retrieve the current user's username
        return currentUsername;
    }


}
