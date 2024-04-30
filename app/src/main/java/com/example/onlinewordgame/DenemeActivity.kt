package com.example.onlinewordgame

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DenemeActivity : ComponentActivity() {
    private lateinit var texts:  MutableList<MutableList<TextView>>
    private val rowCount = 7
    private val colCount = 5
    private var countGames = 0
    private var countWins = 0
    private lateinit var gameCore: OyunMotoru

    private lateinit var db:DatabaseReference
    private lateinit var auth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deneme)

        gameCore = OyunMotoru(rowCount)
        initTexts()
        setEventListeners()


        db= FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        checkWin()


        newRound()
    }

    private fun setEventListeners() {
        for (c in 90 downTo 65) {
            val resID = resources.getIdentifier("button${c.toChar()}", "id", packageName)
            val btn = findViewById<Button>(resID)
            btn.setOnClickListener {
                if (gameCore.isPouse()) {
                    gameCore.startOver()
                    newRound()
                }
                val row = gameCore.getCurRow()
                val col = gameCore.getCurCol()
                if (gameCore.setNextChar(c.toChar())) {
                    texts[row][col].text = c.toChar().toString()
                }
            }
        }

        val btnEnter = findViewById<Button>(R.id.buttonEnter)
        btnEnter.setOnClickListener {
            if (gameCore.isPouse()) {
                gameCore.startOver()
                newRound()
            }
            val row = gameCore.getCurRow()
            if (gameCore.enter()) {
                for (col in 0 until colCount) {
                    val id = when (gameCore.validateChar(row, col)) {
                        gameCore.IN_WORD -> {
                            R.drawable.letter_in_word
                        }

                        gameCore.IN_PLACE -> {
                            R.drawable.letter_in_place
                        }

                        else -> {
                            R.drawable.letter_not_in
                        }
                    }

                    texts[row][col].background = ContextCompat.getDrawable(this, id)
                }
                if (gameCore.getResult()) {
                    val username = parseEmail(auth.currentUser?.email.toString())
                    if (username != null) {
                        db.child("rooms").child("Room 1").child(username).child("status").setValue(true)
                    }
                }
            }
        }

        val btnErase = findViewById<Button>(R.id.buttonErase)
        btnErase.setOnClickListener {
            if (gameCore.isPouse()) {
                gameCore.startOver()
                newRound()
            }
            gameCore.erase()
            val row = gameCore.getCurRow()
            val col = gameCore.getCurCol()
            texts[row][col].text = " "
        }
    }

    private fun checkWin(){
        var roomRef = db.ref.child("rooms").child("Room 1")
        var valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var anyPlayerWon = false

                for (playerSnapshot in dataSnapshot.children) {
                    val status = playerSnapshot.child("status").getValue(Boolean::class.java) ?: false

                    if (status) {

                        anyPlayerWon = true


                        Proceed(playerSnapshot)
                        Log.e("data",playerSnapshot.toString())
                        break
                    }
                }


                if (anyPlayerWon) {


                    //Proceed(id)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                Log.w("TAG", "onCancelled", databaseError.toException())
            }
        }

        // Add the ValueEventListener to the database reference
        roomRef.addValueEventListener(valueEventListener)
    }

    private fun Proceed(id: DataSnapshot){

        if(id.key == parseEmail(auth.currentUser?.email.toString())){
            Toast.makeText(this,"YOU WIN",Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this,"YOU LOSE",Toast.LENGTH_LONG).show()
        }

        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        db.child("rooms").removeValue()
    }

    fun parseEmail(email: String): String? {
        val parts = email.split("@")
        if (parts.size == 2) {
            val username = parts[0] // part before "@"

            return username
        }
        return null // Return null if email is invalid
    }


    private fun initTexts() {
        texts = MutableList(rowCount) { mutableListOf() }
        for (row in 0 until rowCount) {
            for (col in 0 until colCount) {
                val resID =
                    resources.getIdentifier("text${col + 1}col${row + 1}row", "id", packageName)
                texts[row].add(findViewById(resID))
            }
        }
    }



    private fun newRound() {
        gameCore.setWord2()
        for (row in 0 until rowCount) {
            for (col in 0 until colCount) {
                texts[row][col].background = ContextCompat.getDrawable(this,  R.drawable.letter_border)
                texts[row][col].text = " "
            }
        }





        Log.e("Word", "=============---- ${gameCore.getFinalWord()}")
    }
}