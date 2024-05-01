package com.example.gcccyclingmobileapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UserPage extends AppCompatActivity {

    private EditText searchEventTypeEditText, searchEventNameEditText, searchClubNameEditText, ratingCommentEditText;
    private Button searchButton, submitRatingButton, joinEventButton;
    private ListView searchResultsListView;
    private RatingBar clubRatingBar;
    private String selectedEventId;
    private String selectedClubName;
    private String username;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userpage);
        username = getIntent().getStringExtra("USERNAME");

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Initialize UI components
        searchEventTypeEditText = findViewById(R.id.searchEventTypeEditText);
        searchEventNameEditText = findViewById(R.id.searchEventNameEditText);
        searchClubNameEditText = findViewById(R.id.searchClubNameEditText);
        ratingCommentEditText = findViewById(R.id.ratingCommentEditText);
        searchButton = findViewById(R.id.searchButton);
        submitRatingButton = findViewById(R.id.submitRatingButton);
        searchResultsListView = findViewById(R.id.searchResultsListView);
        clubRatingBar = findViewById(R.id.clubRatingBar);
        joinEventButton = findViewById(R.id.joinEventButton);
        joinEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinEvent();
            }
        });

        // Set up listeners
        setupListeners();
    }

    private void setupListeners() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });

        submitRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRating();
            }
        });
    }

    private void performSearch() {
        String eventType = searchEventTypeEditText.getText().toString();
        String eventName = searchEventNameEditText.getText().toString();
        String clubName = searchClubNameEditText.getText().toString();

        if (!eventType.isEmpty()) {
            searchByEventType(eventType);
        } else if (!eventName.isEmpty()) {
            searchByEventName(eventName);
        } else if (!clubName.isEmpty()) {
            searchByClubName(clubName);
        } else {
            Toast.makeText(UserPage.this, "Please enter a search criteria", Toast.LENGTH_SHORT).show();
        }
    }
    private void searchByEventType(String eventType) {
        // Logic to search for events by type
        // Use Firebase to query events of this type
        // Update searchResultsListView with the results
        // Handle case when no events found
        DatabaseReference eventTypeRef = databaseReference.child("eventTypes").child(eventType);

        eventTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> eventNames = new ArrayList<>();

                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    String eventName = eventSnapshot.getKey(); // Assuming event name is the key
                    eventNames.add(eventName);
                }

                if (eventNames.isEmpty()) {
                    Toast.makeText(UserPage.this, "No events found for this type", Toast.LENGTH_SHORT).show();
                } else {
                    // Update your ListView with these event names
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(UserPage.this, android.R.layout.simple_list_item_1, eventNames);
                    searchResultsListView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserPage.this, "Error fetching data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchByEventName(String eventName) {
        // Similar logic for searching by event name
    }

    private void searchByClubName(String clubName) {
        // Similar logic for searching by club name
        // In this case, the joinEventButton should be hidden
        joinEventButton.setVisibility(View.GONE);
        DatabaseReference usersRef = databaseReference.child("users");

        Query query = usersRef.orderByKey().equalTo(clubName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> clubNames = new ArrayList<>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userType = userSnapshot.child("role").getValue(String.class);
                    String userName = userSnapshot.getKey();

                    // Check if it's a club or the username matches the search term
                    if ("club".equalsIgnoreCase(userType) || clubName.equalsIgnoreCase(userName)) {
                        clubNames.add(userName);
                    }
                }

                if (clubNames.isEmpty()) {
                    Toast.makeText(UserPage.this, "No clubs found matching the criteria", Toast.LENGTH_SHORT).show();
                } else {
                    // Update your ListView with these club names
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(UserPage.this, android.R.layout.simple_list_item_1, clubNames);
                    searchResultsListView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserPage.this, "Error fetching data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void joinEvent() {
        if (selectedEventId != null && !selectedEventId.isEmpty()) {
            DatabaseReference eventRef = databaseReference.child("events").child(selectedEventId).child("participants");
            eventRef.push().setValue(username);
            Toast.makeText(UserPage.this, "Joined event successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(UserPage.this, "No event selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitRating() {
        String comment = ratingCommentEditText.getText().toString();
        float rating = clubRatingBar.getRating();

        if (comment.isEmpty() || rating == 0) {
            Toast.makeText(UserPage.this, "Please leave a comment and a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        // Assuming clubName is the name of the selected club
        String clubName =  selectedClubName;// ... get the selected club name

        DatabaseReference clubRef = databaseReference.child("users").child(clubName).child("Reviews");
        String reviewId = clubRef.push().getKey(); // Generate a random ID for the review

        HashMap<String, Object> reviewData = new HashMap<>();
        reviewData.put("comment", comment);
        reviewData.put("rating", rating);

        clubRef.child(reviewId).setValue(reviewData);
        Toast.makeText(UserPage.this, "Rating submitted", Toast.LENGTH_SHORT).show();
    }
}
