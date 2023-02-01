package com.example.licentatakecare

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

class AnimationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_animation, container, false)
        Handler(Looper.myLooper()!!).postDelayed({
          //  if (context?.let { SharedPrefsUtils.getAccessToken(it) } == null) {
                findNavController().navigate(R.id.action_animationFragment_to_logFragment)
           // } else {
           //     findNavController().navigate(R.id.action_animationFragment_to_loginFragment)
           // }
        }, 4000)
        return view
    }

}