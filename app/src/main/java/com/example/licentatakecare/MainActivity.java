package com.example.licentatakecare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.licentatakecare.Authentication.SharedPreferencesHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this);

        if (sharedPreferencesHelper.isLoggedIn()) {
            setContentView(R.layout.activity_main);
          //  navigateToProfileFragment();
            return;
        }

        setContentView(R.layout.activity_main);

        // Rest of your onCreate code...
    }







}

