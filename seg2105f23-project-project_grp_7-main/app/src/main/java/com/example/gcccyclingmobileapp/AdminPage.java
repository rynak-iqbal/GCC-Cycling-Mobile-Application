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

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class AdminPage extends AppCompatActivity {
    private EditText eventTypeEditText, eventDescriptionEditText, ageRequirementEditText, paceEditText, levelEditText;
    private Button addButton, editButton, deleteButton, deleteUserButton;

    private Spinner eventTypeSpinner;

    private Spinner userSpinner;

    private List<String> eventTypeList = new ArrayList<>();

    private List<String> userList = new ArrayList<>();

    private DatabaseReference eventTypesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminpage);

        eventTypeEditText = findViewById(R.id.eventTypeEditText);
        eventDescriptionEditText = findViewById(R.id.eventDescriptionEditText);
        ageRequirementEditText = findViewById(R.id.ageRequirementEditText);
        paceEditText = findViewById(R.id.paceEditText);
        levelEditText = findViewById(R.id.levelEditText);
        eventTypeSpinner = findViewById(R.id.eventTypeSpinner);
        userSpinner = findViewById(R.id.userSpinner);

        addButton = findViewById(R.id.addButton);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        deleteUserButton = findViewById(R.id.deleteUserButton);
        initializeEventTypeSpinner();
        initializeUserSpinner();
        eventTypesRef = FirebaseDatabase.getInstance().getReference("eventTypes");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEventType();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editEventType();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEventType();
            }
        });

        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });
    }

    private void addEventType() {
        String eventType = eventTypeEditText.getText().toString().trim();
        String eventDescription = eventDescriptionEditText.getText().toString().trim();
        int ageRequirement = Integer.parseInt(ageRequirementEditText.getText().toString().trim());
        String pace = paceEditText.getText().toString().trim();
        String level = levelEditText.getText().toString().trim();

        // Create a new unique key for the event type
        String eventTypeKey = eventTypesRef.push().getKey();

        // Create an EventTypeInfo object with the event details
        EventTypeInfo newEventType = new EventTypeInfo(eventTypeKey, eventType, eventDescription, ageRequirement, pace, level);

        eventTypesRef.child(eventType).child(eventTypeKey).setValue(newEventType);

        // Clear input fields
        clearInputFields();

        // Provide user feedback
        Toast.makeText(this, "Event type added successfully", Toast.LENGTH_SHORT).show();
        initializeEventTypeSpinner();
    }

    private void editEventType() {
        final String selectedEventType = eventTypeSpinner.getSelectedItem().toString();

        String eventType = eventTypeEditText.getText().toString().trim();
        String eventDescription = eventDescriptionEditText.getText().toString().trim();
        int ageRequirement = Integer.parseInt(ageRequirementEditText.getText().toString().trim());
        String pace = paceEditText.getText().toString().trim();
        String level = levelEditText.getText().toString().trim();

        // Check if a valid event type is selected
        if (selectedEventType.isEmpty()) {
            Toast.makeText(this, "Please select an event type to edit.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an EventTypeInfo object with the updated event details
        EventTypeInfo updatedEventType = new EventTypeInfo(selectedEventType, eventType, eventDescription, ageRequirement, pace, level);

        // Update the event type in the database
        eventTypesRef.child(selectedEventType).setValue(updatedEventType);

        // Clear input fields
        clearInputFields();

        // Provide user feedback
        Toast.makeText(this, "Event type updated successfully", Toast.LENGTH_SHORT).show();
        initializeEventTypeSpinner();
    }

    private void deleteEventType() {
        final String selectedEventType = eventTypeSpinner.getSelectedItem().toString();

        // Check if a valid event type is selected
        if (selectedEventType.isEmpty()) {
            Toast.makeText(this, "Please select an event type to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Confirm deletion with a dialog (for later implementation)

        // Remove the event type node from the database
        eventTypesRef.child(selectedEventType).removeValue();

        // Clear input fields and the selected item in the Spinner
        clearInputFields();
        eventTypeSpinner.setSelection(0);

        // Provide user feedback
        Toast.makeText(this, "Event type deleted successfully", Toast.LENGTH_SHORT).show();
        initializeEventTypeSpinner();
    }

    private void clearInputFields() {
        eventTypeEditText.setText("");
        eventDescriptionEditText.setText("");
        ageRequirementEditText.setText("");
        paceEditText.setText("");
        levelEditText.setText("");
    }

    private void initializeEventTypeSpinner() {
        eventTypesRef = FirebaseDatabase.getInstance().getReference("eventTypes");

        eventTypesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventTypeList.clear();

                for (DataSnapshot eventTypeSnapshot : dataSnapshot.getChildren()) {
                    String eventType = eventTypeSnapshot.getKey();
                    eventTypeList.add(eventType);
                }

                // Create an ArrayAdapter for the Spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminPage.this, android.R.layout.simple_spinner_item, eventTypeList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Set the ArrayAdapter to the Spinner
                eventTypeSpinner.setAdapter(adapter);

                // Set a listener to handle item selection
                eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Handle the case where nothing is selected, if needed
                    }
                });
            }





            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error, if needed
                Toast.makeText(AdminPage.this, "Error fetching event types: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeUserSpinner() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String username = userSnapshot.getKey();
                    userList.add(username);
                }

                ArrayAdapter<String> userAdapter = new ArrayAdapter<>(AdminPage.this, android.R.layout.simple_spinner_item, userList);
                userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                userSpinner.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error Handling
                Toast.makeText(AdminPage.this, "Error fetching users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUser() {
        final String selectedUser = userSpinner.getSelectedItem().toString();

        // Check if a valid user is selected
        if (selectedUser.isEmpty()) {
            Toast.makeText(AdminPage.this, "Please select a user to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(selectedUser).removeValue(); // Remove the selected user from the database

        Toast.makeText(AdminPage.this, "User deleted successfully", Toast.LENGTH_SHORT).show();

        initializeUserSpinner();
    }
}
