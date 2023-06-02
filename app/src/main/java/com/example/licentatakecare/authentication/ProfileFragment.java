package com.example.licentatakecare.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.licentatakecare.authentication.model.LogEntry;
import com.example.licentatakecare.authentication.userData.adapters.AllergiesAdapter;
import com.example.licentatakecare.authentication.userData.adapters.LogAdapter;
import com.example.licentatakecare.authentication.model.Allergy;
import com.example.licentatakecare.R;
import com.example.licentatakecare.map.MapsActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {
    private FirebaseFirestore db;
    private DatabaseReference userCardRef;
    private View view;
    private String userId;
    private TextView tv_userName;
    private TextView tv_userDateOfBirth;
    private TextView tv_userAge;
    private TextView tv_userCardId;
    private TextView tv_userBloodType;
    private TextView tv_userGender;
    private RecyclerView rv_allergiesRecyclerView;
    private RecyclerView rv_logRecyclerView;
    private LogAdapter logAdapter;
    private List<LogEntry> logList;
    private LinearLayout toolbarButtonsLayout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userCardRef = database.getReference("UserCard");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);

        toolbarButtonsLayout = view.findViewById(R.id.toolbarButtonsLayout);
        toolbarButtonsLayout.setOrientation(LinearLayout.VERTICAL);
        ImageView expandButton = view.findViewById(R.id.expandButton);
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButtonsLayoutVisibility();
            }
        });

        Button signOutButton = view.findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear shared preferences
                SharedPreferences preferences = getActivity().getSharedPreferences("user.Authentication", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                // Go back to map activity
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        Button backToMapButton = view.findViewById(R.id.backToMapButton);
        backToMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tv_userName = view.findViewById(R.id.profile_name);
        tv_userBloodType = view.findViewById(R.id.profile_blood_type);
        tv_userGender = view.findViewById(R.id.profile_gender);
        tv_userCardId = view.findViewById(R.id.profile_card_id);
        tv_userDateOfBirth = view.findViewById(R.id.profile_date_of_birth);
        tv_userAge = view.findViewById(R.id.profile_age);
        rv_allergiesRecyclerView = view.findViewById(R.id.allergiesRecyclerView);
        rv_logRecyclerView = view.findViewById(R.id.logRecyclerView);
        logList = new ArrayList<>();
        logAdapter = new LogAdapter(logList, rv_logRecyclerView);

        rv_logRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_logRecyclerView.setAdapter(logAdapter);

        // Retrieve the Bundle from arguments
        Bundle args = getArguments();
        if (args != null) {
            userId = args.getString("userCardId", "");

            // Query Firestore for user data
            queryFirestore();

            // Query realtime database for log data
            queryRealtime();
        }
    }

    void queryFirestore() {
        DocumentReference userRef = db.collection("Users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot != null && snapshot.exists()) {
                    // Document exists, retrieve the data
                    String first_name = snapshot.getString("first_name");
                    String last_name = snapshot.getString("last_name");
                    String dateOfBirth = snapshot.getString("birth_date");
                    String bloodType = snapshot.getString("blood_type");
                    String gender = snapshot.getString("gender");
                    List<DocumentReference> allergyReferences = (List<DocumentReference>) snapshot.get("allergies");
                    tv_userName.setText(first_name + " " + last_name);
                    tv_userCardId.setText("ID: " + userId);
                    tv_userBloodType.setText("Blood type: " + bloodType);
                    tv_userDateOfBirth.setText(dateOfBirth);
                    String age = ageCalculator(dateOfBirth);
                    tv_userAge.setText(age);
                    tv_userGender.setText(gender);


                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    for (DocumentReference reference : allergyReferences) {
                        tasks.add(reference.get());
                    }

                    Tasks.whenAllComplete(tasks).addOnCompleteListener(allergiesTask -> {
                        List<Allergy> allergyList = new ArrayList<>();
                        for (Task<?> allergyTask : allergiesTask.getResult()) {
                            if (allergyTask.isSuccessful()) {
                                DocumentSnapshot allergySnapshot = (DocumentSnapshot) allergyTask.getResult();
                                if (allergySnapshot.exists()) {
                                    String name = allergySnapshot.getString("name");
                                    String type = allergySnapshot.getString("type");
                                    allergyList.add(new Allergy(name, type));
                                }
                            }
                        }

                        // Create an instance of the custom adapter for allergies
                        AllergiesAdapter allergiesAdapter = new AllergiesAdapter(allergyList);

                        // Set the layout manager and adapter for the allergies RecyclerView
                        rv_allergiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        rv_allergiesRecyclerView.setAdapter(allergiesAdapter);
                    });

                } else {
                    // Document does not exist
                    Log.d("ProfileFragment", "Document does not exist");
                }
            } else {
                // Handle the error
                Exception e = task.getException();
                Log.e("ProfileFragment", "Error getting document: " + e);
            }
        });
    }

    void queryRealtime() {
        DatabaseReference userCardRef = FirebaseDatabase.getInstance().getReference().child("UserCard");
        Query query = userCardRef.orderByChild("id").equalTo(userId);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                updateLogList(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                updateLogList(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Handle child removed event if needed
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                // Handle child moved event if needed
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Log.e("ProfileFragment", "Error retrieving log data: " + databaseError.getMessage());
            }
        });
    }

    private void updateLogList(DataSnapshot dataSnapshot) {
        DataSnapshot logSnapshot = dataSnapshot.child("log");
        if (logSnapshot.exists()) {
            List<LogEntry> updatedList = new ArrayList<>();

            for (DataSnapshot entrySnapshot : logSnapshot.getChildren()) {
                // Retrieve log entry data
                String entryTimestamp = entrySnapshot.child("entry_timestamp").getValue(String.class);
                String exitTimestamp = entrySnapshot.child("exit_timestamp").getValue(String.class);
                String section = entrySnapshot.child("section").getValue(String.class);
                String hospital = entrySnapshot.child("hospital").getValue(String.class);
                LocalDateTime entryDateTime = LocalDateTime.parse(entryTimestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                LocalDateTime exitDateTime = LocalDateTime.parse(exitTimestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                long durationInMillis = ChronoUnit.MILLIS.between(entryDateTime, exitDateTime);

                long days = durationInMillis / (1000 * 60 * 60 * 24);
                long hours = (durationInMillis / (1000 * 60 * 60)) % 24;
                long minutes = (durationInMillis / (1000 * 60)) % 60;

                String timeDifference;
                if (days > 0) {
                    timeDifference = String.format(Locale.getDefault(), "%d day(s) %d hours %2 minutes", days, hours, minutes);
                } else {
                    if (hours > 1) {
                        if (minutes >= 1) {
                            timeDifference = String.format(Locale.getDefault(), "%d hours %d minute(s)", hours, minutes);
                        } else {
                            timeDifference = String.format(Locale.getDefault(), "%d hours", hours);
                        }
                    } else {
                        timeDifference = String.format(Locale.getDefault(), "%d hour %d minutes", hours, minutes);
                    }
                }

                // Create LogEntry object
                LogEntry logEntry = new LogEntry(entryTimestamp, exitTimestamp, section, hospital, timeDifference);
                updatedList.add(0, logEntry); // Add the entry at the beginning of the list
            }
            logList.clear(); // Clear the existing list
            logList.addAll(0, updatedList);
            logAdapter.notifyDataSetChanged(); // Notify the adapter of the data change
        }
    }


    public String ageCalculator(String birthdate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        try {
            // Parse the birthdate string into a Date object
            Date birthdateDate = dateFormat.parse(birthdate);

            // Convert the Date object to a LocalDate object
            LocalDate birthdateObj = birthdateDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Get the current date
            LocalDate currentDate = LocalDate.now();

            // Calculate the age
            Period age = Period.between(birthdateObj, currentDate);

            // Convert the age to a string representation
            String ageString = String.valueOf(age.getYears());

            // Return the age as a string
            return ageString;
        } catch (ParseException e) {
            // Handle parsing exception
            e.printStackTrace();
        }

        return ""; // Return an empty string if parsing fails
    }

    private void toggleButtonsLayoutVisibility() {
        if (toolbarButtonsLayout.getVisibility() == View.VISIBLE) {
            toolbarButtonsLayout.setVisibility(View.GONE);
        } else {
            toolbarButtonsLayout.setVisibility(View.VISIBLE);
        }
    }
}
