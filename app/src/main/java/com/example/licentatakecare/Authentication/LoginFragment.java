package com.example.licentatakecare.Authentication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.licentatakecare.R;
import com.example.licentatakecare.databinding.FragmentLoginBinding;
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
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                try {
                                    Date birthDate = dateFormat.parse(dateOfBirth);
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(birthDate);
                                    int year = calendar.get(Calendar.YEAR);
                                    if (String.valueOf(year).equals(password)) {
                                        Toast.makeText(getContext(), "YEY", Toast.LENGTH_LONG).show();
                                        if (checkBox.isChecked()) {
                                            sharedPreferencesHelper.saveUserCredentials(cardId, password);
                                        }
                                        Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_profileFragment);
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
