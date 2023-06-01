package com.example.licentatakecare.Authentication;

import static androidx.fragment.app.FragmentManager.TAG;

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
import android.widget.TextView;

import com.example.licentatakecare.Authentication.userData.Adapters.AllergiesAdapter;
import com.example.licentatakecare.Authentication.userData.Adapters.LogAdapter;
import com.example.licentatakecare.Authentication.userData.Allergy;
import com.example.licentatakecare.R;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private FirebaseFirestore db;
    private DatabaseReference userCardRef;
    private String userId;
    private TextView tv_userName;
    private TextView tv_userDateOfBirth;
    private TextView tv_userAge;
    private TextView tv_userCardId;
    private TextView tv_userBloodType;
    private TextView tv_userGender;
    private RecyclerView rv_allergiesRecyclerView;
    private RecyclerView rv_logRecyclerView;

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
        return inflater.inflate(R.layout.fragment_profile, container, false);
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
                    List<DocumentReference> allergyReferences = (List<DocumentReference>) snapshot.get("allergies");
                    tv_userName.setText(first_name + " " + last_name);
                    tv_userCardId.setText("ID: " + userId);
                    tv_userBloodType.setText("Blood type: " + bloodType);
                    tv_userDateOfBirth.setText(dateOfBirth);
                    tv_userAge.setText("Age: 21");

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
        // Set an empty adapter to the RecyclerView
        LogAdapter logAdapter = new LogAdapter(new ArrayList<>());
        rv_logRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_logRecyclerView.setAdapter(logAdapter);

        DatabaseReference userCardRef = FirebaseDatabase.getInstance().getReference().child("UserCard");
        Query query = userCardRef.orderByChild("id").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        DataSnapshot logSnapshot = userSnapshot.child("log");
                        if (logSnapshot.exists()) {
                            List<LogEntry> logList = new ArrayList<>();
                            for (DataSnapshot entrySnapshot : logSnapshot.getChildren()) {
                                String logId = entrySnapshot.getKey();
                                String entryTimestamp = entrySnapshot.child("entry_timestamp").getValue(String.class);
                                String exitTimestamp = entrySnapshot.child("exit_timestamp").getValue(String.class);
                                String section = entrySnapshot.child("section").getValue(String.class);
                                String hospital = entrySnapshot.child("hospital").getValue(String.class);

                                LogEntry logEntry = new LogEntry(entryTimestamp, exitTimestamp, section, hospital);
                                logList.add(logEntry);
                            }

                            // Update the adapter with the new log data
                            logAdapter.setLogList(logList);
                            logAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "Error retrieving user data: " + databaseError.getMessage());
            }
        });
    }

}
