package com.example.onlinewordgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.onlinewordgame.databinding.ActivitySignUpBinding


import com.google.firebase.auth.FirebaseAuth


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginRedirectText.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val password = binding.password.text.toString()
            val username = binding.username.text.toString()
            val email = binding.email.text.toString()
            val passwordrepeat = binding.repeatpassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty() && passwordrepeat.isNotEmpty() && username.isNotEmpty()){
                if(password == passwordrepeat){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                        if(it.isSuccessful){
                            val intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }

            }
        }

    }
}