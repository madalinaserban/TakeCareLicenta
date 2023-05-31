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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.licentatakecare.Authentication.userData.Adapters.AllergiesAdapter;
import com.example.licentatakecare.Authentication.userData.Allergy;
import com.example.licentatakecare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private FirebaseFirestore db;
    private String userId;
    private TextView tv_userName;
    private TextView tv_userDateOfBirth;
    private TextView tv_userAge;
    private TextView tv_userCardId;
    private TextView tv_userBloodType;
    private TextView tv_userGender;
    private RecyclerView allergiesRecyclerView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
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
        allergiesRecyclerView = view.findViewById(R.id.allergiesRecyclerView);

        // Retrieve the Bundle from arguments
        Bundle args = getArguments();
        if (args != null) {
            userId = args.getString("userCardId", "");

            // Query Firestore for the document with matching userId
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
                        tv_userCardId.setText("ID: "+ userId);
                        tv_userBloodType.setText("Blood type: "+bloodType);
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

                            // Create an instance of the custom adapter
                            AllergiesAdapter adapter = new AllergiesAdapter(allergyList);

                            // Set the layout manager and adapter for the RecyclerView
                            allergiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                            allergiesRecyclerView.setAdapter(adapter);
                        });

                    } else {
                        // Document does not exist
                        Log.d("A", "Document does not exist");
                    }
                } else {
                    // Handle the error
                    Exception e = task.getException();
                    Log.e("A", "Error getting document: " + e);
                }
            });
        }
    }
}
