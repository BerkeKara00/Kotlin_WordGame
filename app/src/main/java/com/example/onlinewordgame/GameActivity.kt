package com.example.onlinewordgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.onlinewordgame.databinding.ActivityGameBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityGameBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        db = FirebaseDatabase.getInstance().reference



        val username = parseEmail(auth.currentUser?.email.toString())

        binding.kelimeButton.setOnClickListener{
            var wordText = binding.kelimeKullaniciText.text.toString().uppercase()
            if (username != null) {
                db.child("rooms").child("Room 1").child(username).child("guess").setValue(wordText)

            }
            val intent = Intent(this,DenemeActivity::class.java)
            startActivity(intent)

        }


    }




    fun parseEmail(email: String): String? {
        val parts = email.split("@")
        if (parts.size == 2) {
            val username = parts[0] // part before "@"

            return username
        }
        return null // Return null if email is invalid
    }
}