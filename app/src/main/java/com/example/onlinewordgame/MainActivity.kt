package com.example.onlinewordgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.onlinewordgame.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        listenForPlayerCount()

        binding.joinButton.setOnClickListener {
            val username = parseEmail(auth.currentUser?.email.toString())
            if (username != null) {
                db.child("rooms").child("Room 1").child(username).child("joined").setValue(true)
                db.child("rooms").child("Room 1").child(username).child("status").setValue(false)

                Toast.makeText(this,"Waiting for other player to connect", Toast.LENGTH_SHORT).show()
            }
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

    private fun StartGame (){
        val intent = Intent(this,GameActivity::class.java)
        startActivity(intent)
    }



    private fun listenForPlayerCount() {
        val roomRef = db.child("rooms").child("Room 1")
        roomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val playerCount = snapshot.childrenCount.toInt()
                if (playerCount == 2) {
                    StartGame()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error appropriately
                // For example, print an error message
                println("Firebase Database Error: ${error.message}")
            }
        })
    }


}