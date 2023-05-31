package com.example.licentatakecare.Authentication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.os.Looper;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.licentatakecare.R;
import com.example.licentatakecare.databinding.FragmentLogoBinding;

public class LogoFragment extends Fragment {
    private FragmentLogoBinding binding;
    private SharedPreferencesHelper sharedPreferencesHelper;

    private static final long DELAY_DURATION = 2000; // Delay in milliseconds

    @Override
    public void onStart() {
        super.onStart();
        sharedPreferencesHelper = new SharedPreferencesHelper(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLogoBinding.inflate(inflater, container, false);
        delayAndNavigate();
        return binding.getRoot();
    }

    private void delayAndNavigate() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPreferencesHelper.isLoggedIn()) {
                    Toast.makeText(getContext(), "Navigating to Profile", Toast.LENGTH_LONG).show();

                    // Pass the ID as an argument
                    Bundle bundle = new Bundle();
                    bundle.putString("userCardId", sharedPreferencesHelper.getCardId());

                    ProfileFragment profileFragment = new ProfileFragment();
                    profileFragment.setArguments(bundle);

                    Navigation.findNavController(requireView()).navigate(R.id.action_logoFragment_to_profileFragment, bundle);
                } else {
                    Toast.makeText(getContext(), "Navigating to Login", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(requireView()).navigate(R.id.action_logoFragment_to_loginFragment);
                }
            }
        }, DELAY_DURATION);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


