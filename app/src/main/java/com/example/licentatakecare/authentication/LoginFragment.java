package com.example.licentatakecare.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.licentatakecare.R;
import com.example.licentatakecare.databinding.FragmentLoginBinding;
import com.example.licentatakecare.map.MapsActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;

    private String cardId;
    private String password;
    private EditText[] signInInputsArray;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private FirebaseFirestore db;
    private CheckBox checkBox;
    private Button backToMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        checkBox = binding.rememberme;
        signInInputsArray = new EditText[]{binding.signEmail, binding.signPassword};
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });
        backToMap=binding.btnBackToMap;
        backToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        db = FirebaseFirestore.getInstance();
        sharedPreferencesHelper = new SharedPreferencesHelper(requireContext());
    }


    private boolean notEmpty() {
        return cardId != null && !cardId.isEmpty() && password != null && !password.isEmpty();
    }
    private void signInUser() {
        cardId = binding.signEmail.getText().toString();
        password = binding.signPassword.getText().toString();
        if (notEmpty()) {
            db.collection("Users")
                    .document(cardId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String dateOfBirth = documentSnapshot.getString("birth_date");
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                try {
                                    Date birthDate = dateFormat.parse(dateOfBirth);
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(birthDate);
                                    int year = calendar.get(Calendar.YEAR);
                                    if (String.valueOf(year).equals(password)) {
                                        if (checkBox.isChecked()) {
                                            sharedPreferencesHelper.saveUserCredentials(cardId, password);
                                        }
                                        Bundle bundle = new Bundle();
                                        bundle.putString("userCardId", cardId);

                                        ProfileFragment profileFragment = new ProfileFragment();
                                        profileFragment.setArguments(bundle);
                                        Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_profileFragment,bundle);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(getContext(), "No matching cardId found", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Please enter cardId and password", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Pair<String, String> savedCredentials = sharedPreferencesHelper.retrieveUserCredentials();
        if (savedCredentials != null) {
            String savedCardId = savedCredentials.first;
            String savedPassword = savedCredentials.second;
            binding.signEmail.setText(savedCardId);
            binding.signPassword.setText(savedPassword);
            checkBox.setChecked(savedCardId != null && savedPassword != null);
        }
    }

}
