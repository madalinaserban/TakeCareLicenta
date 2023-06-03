package com.example.licentatakecare.authentication;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Looper;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.example.licentatakecare.R;
import com.example.licentatakecare.databinding.FragmentLogoBinding;

public class LogoFragment extends Fragment {
    private FragmentLogoBinding binding;
    private SharedPreferencesHelper sharedPreferencesHelper;

    private static final long DELAY_DURATION = 1000; // Delay in milliseconds

    @Override
    public void onStart() {
        super.onStart();
        sharedPreferencesHelper = new SharedPreferencesHelper(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLogoBinding.inflate(inflater, container, false);
        animateLogo();
        delayAndNavigate();
        return binding.getRoot();
    }

    private void delayAndNavigate() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPreferencesHelper.isLoggedIn()) {

                    // Pass the ID as an argument
                    Bundle bundle = new Bundle();
                    bundle.putString("userCardId", sharedPreferencesHelper.getCardId());

                    ProfileFragment profileFragment = new ProfileFragment();
                    profileFragment.setArguments(bundle);

                    Navigation.findNavController(requireView()).navigate(R.id.action_logoFragment_to_profileFragment, bundle);
                } else {
                    Navigation.findNavController(requireView()).navigate(R.id.action_logoFragment_to_loginFragment);
                }
            }
        }, DELAY_DURATION);
    }

    private void animateLogo() {
        // Remove color filter
        binding.logo.clearColorFilter();

// Define the initial and final scale values for the zoom animation
        float initialScaleX = 1.0f;
        float initialScaleY = 1.0f;
        float finalScaleX = 2.0f;
        float finalScaleY = 2.0f;

// Define the initial and final alpha values for the fade animation
        float initialAlpha = 1.0f;
        float finalAlpha = 0.0f;

// Create ObjectAnimator for scaling the ImageView
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(binding.logo, "scaleX", initialScaleX, finalScaleX);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(binding.logo, "scaleY", initialScaleY, finalScaleY);

// Create ObjectAnimator for fading the ImageView
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(binding.logo, "alpha", initialAlpha, finalAlpha);

// Set the duration and interpolator for the animations
        long duration = 1000; // Animation duration in milliseconds
        Interpolator interpolator = new AccelerateDecelerateInterpolator();
        scaleXAnimator.setDuration(duration);
        scaleYAnimator.setDuration(duration);
        alphaAnimator.setDuration(duration);
        scaleXAnimator.setInterpolator(interpolator);
        scaleYAnimator.setInterpolator(interpolator);
        alphaAnimator.setInterpolator(interpolator);

// Create an AnimatorSet to play the animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator);
        animatorSet.start();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


