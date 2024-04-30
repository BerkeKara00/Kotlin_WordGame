package com.example.onlinewordgame

import android.provider.ContactsContract.Data
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.random.Random

class OyunMotoru(
    private var rowCount: Int = 7
) {
    val IN_WORD = 0
    val IN_PLACE = 1
    val NOT_IN = 2

    private var pouse = false
    private var curRow: Int = 0;
    private var curCol: Int = 0;
    private var rows = mutableListOf<MutableList<Char>>();
    private var word:String = "N"
    private var db:DatabaseReference = FirebaseDatabase.getInstance().reference
    private var auth:FirebaseAuth = FirebaseAuth.getInstance()
    init {

        for (i in 0 until rowCount) {
            val row = MutableList(5) { ' ' }
            rows.add(row)
        }
    }

    fun getFinalWord(): String {
        return word
    }

    fun getResult(): Boolean {
        for (row in 0 until rowCount) {
            if (rows[row].joinToString(separator="") == word) {
                pouse = true
                return true
            }
        }
        return false
    }

    fun isPouse(): Boolean {
        return pouse
    }

    fun startOver() {
        curCol = 0
        curRow = 0
        pouse = false
        for (row in 0 until rowCount) {
            for (col in 0 until 5) {
                rows[row][col] = ' '
            }
        }
        setWord()
    }

    fun getChar(row: Int, col: Int): Char {
        if (row < 0 || row >= rowCount || col < 0 || col >= 5) {
            return ' '
        }

        return rows[row][col]
    }

    fun setNextChar(c: Char): Boolean {
        if (rows[curRow][curCol] == ' ') {
            rows[curRow][curCol] = c
            if (curCol < 4) {
                curCol++
            }
            return true
        }
        return false
    }

    fun erase() {
        if (curCol > 0 && rows[curRow][curCol] == ' ') {
            curCol--
        }
        rows[curRow][curCol] = ' '
    }

    fun enter(): Boolean {
        if (curCol == 4 && curRow <= rowCount) {
            curCol = 0
            curRow++
            if (curRow == rowCount) {
                pouse = true
            }
            return true
        }
        return false
    }

    fun validateChar(row: Int, col: Int): Int {
        if (rows[row][col] == word[col]) {
            return IN_PLACE
        } else if (rows[row][col] in word) {
            return IN_WORD
        }
        return NOT_IN
    }

    fun getCurRow(): Int {
        return curRow
    }

    fun getCurCol(): Int {
        return curCol
    }

    fun setWord() {
        var words = listOf<String>(
            "APPLE",
            "TIGER",
            "OCEAN",
            "ROBOT",
            "SNAIL",
            "PANDA",
            "IGLOO",
            "LEMON",
            "MOUSE",
            "PIZZA",
            "CHAIR",
            "EARTH",
            "PIANO",
            "RIVER",
            "EAGLE",
            "ZEBRA",
            "CLOWN",
            "CLOUD",
            "SPOON",
            "TRAIN",
            "CLOCK",
            "SHOES",
            "SOCKS",
            "MAGIC",
            "COMET",
            "WHALE",
            "JELLY",
            "SHIRT",
            "LEMON",
            "SMILE",
            "MOUSE",
            "ANGEL",
            "OCEAN",
            "ROBOT",
            "SWORD",
            "SUSHI",
            "HEART",
            "GHOST",
            "GRAPE",
            "HONEY",
            "MANGO",
            "PEACH",
            "SNACK",
            "JELLY",
            "PIZZA",
            "EAGLE",
            "ALARM",
            "FAIRY",
            "CLOUD",
            "PHONE",
            "PLANE",
            "WATCH",
            "CHAIR",
            "GRASS",
            "HOTEL",
            "LEMON",
            "TIGER",
            "WATER",
            "PAPER",
            "FRUIT",
            "SWING",
        )
        word = words[Random.nextInt(words.size)]
    }

    fun setWord2(){
        val username = parseEmail(auth.currentUser?.email.toString())
        val roomRef = db.child("rooms").child("Room 1")
        roomRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val players = dataSnapshot.children
                for (playerSnapshot in players) {
                    val guess = playerSnapshot.child("guess").getValue(String::class.java) ?: ""

                    // Assuming the player has a unique identifier called "id"
                    val playerId = playerSnapshot.key ?: ""
                    Log.d("ID",playerId)
                    if (playerId != username) {
                        // Player has joined and it's not the current player
                        word = guess
                        Log.e("word",guess)

                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("TAG", "onCancelled", databaseError.toException())
            }
        })
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