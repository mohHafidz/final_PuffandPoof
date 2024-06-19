package com.example.final_puffandpoof.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.final_puffandpoof.R
import com.example.final_puffandpoof.UserDatabaseHelper

class loginActivity : AppCompatActivity() {
    lateinit var user: EditText
    lateinit var pass: EditText
    lateinit var userDbHelper: UserDatabaseHelper
    lateinit var login: Button
    lateinit var goRegis: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        user = findViewById(R.id.user)
        pass = findViewById(R.id.pass)
        login = findViewById(R.id.Login)
        goRegis = findViewById(R.id.goRegis)
        userDbHelper = UserDatabaseHelper(this)

        goRegis.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener {
            val username = user.text.toString()
            val password = pass.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            } else {
                val userId = userDbHelper.checkUserCredentials(username, password)
                if (userId != null) {
                    // Save user ID in SharedPreferences
                    val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putInt("user_id", userId)
                    editor.apply()

                    val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putBoolean("is_logged_in", true)
                        apply()
                    }


                    // Navigate to the next screen
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
