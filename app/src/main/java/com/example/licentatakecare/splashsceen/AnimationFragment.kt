package com.example.licentatakecare.splashsceen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.licentatakecare.R
import com.example.licentatakecare.map.MapsActivity

class AnimationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_animation, container, false)
        Handler(Looper.myLooper()!!).postDelayed({
            //if (context?.let { SharedPrefsUtils.getAccessToken(it) } == null) {
            //    findNavController().navigate(R.id.action_animationFragment_to_loginFragment)
            //} else {
                var intent:Intent = Intent(activity, MapsActivity::class.java)
                startActivity(intent)
          // }
        }, 4000)
        return view
    }

}