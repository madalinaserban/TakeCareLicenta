package com.example.licentatakecare.Authentification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.licentatakecare.R
import com.example.licentatakecare.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private lateinit var binding:FragmentLoginBinding
    private lateinit var email: String
    private lateinit var password:String
    private lateinit var signInInputsArray: Array<EditText>
    private lateinit var firebaseAuth:FirebaseAuth
    private lateinit var checkBox: CheckBox
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle?
    ): View? {

        binding= FragmentLoginBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        checkBox= binding.rememberme
        signInInputsArray= arrayOf(binding.signEmail,binding.signPassword)
        binding.btnSignIn.setOnClickListener{
            signInUser()
        }

        binding.btnCreateAccount.setOnClickListener{
          //  view?.findNavController()?.navigate(R.id.action_signInFragment_to_signUpFragment)
            // Toast.makeText(activity,"Click merge",Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }
    override fun onStart() {
        super.onStart()
    }

    private fun notEmpty():Boolean{
        return (email.isNotEmpty() && password.isNotEmpty())
    }

    private fun signInUser() {
        email = binding.signEmail.text.toString()
        password = binding.signPassword.text.toString()
        if (notEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener() { signIn ->
                    if (signIn.isSuccessful) {
                        if (context != null && checkBox.isChecked) {
                            SharedPrefsUtils.saveAccessToken(requireContext(), password)
                        }
                        val user = firebaseAuth.currentUser
                        Toast.makeText(activity, "Sign in successful", Toast.LENGTH_SHORT).show()
                        view?.findNavController()?.navigate(R.id.action_loginFragment_to_profileFragment)

                    } else {
                        Toast.makeText(activity, ":(", Toast.LENGTH_SHORT).show()
                        signInInputsArray.forEach {
                            if (it.text.toString().trim().isEmpty()) {
                                it.error = "${it.hint} is required"
                            }
                        }
                    }
                }
        }
    }
}
