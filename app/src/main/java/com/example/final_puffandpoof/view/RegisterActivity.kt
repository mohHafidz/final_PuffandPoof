package com.example.final_puffandpoof.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.final_puffandpoof.R
import com.example.final_puffandpoof.UserDatabaseHelper
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class RegisterActivity : AppCompatActivity() {
    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var cpassword: EditText
    lateinit var email: EditText
    lateinit var phoneNumber: EditText
    lateinit var genderRG: RadioGroup
    lateinit var goLogin: TextView
    lateinit var regis: Button
    lateinit var alertuser: TextView
    lateinit var alertpass: TextView
    lateinit var alertcpass: TextView
    lateinit var alertemail: TextView
    lateinit var alertphone: TextView
    lateinit var alertgender: TextView
    lateinit var userDbHelper: UserDatabaseHelper
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private var userToRegister: User? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        cpassword = findViewById(R.id.cpassword)
        email = findViewById(R.id.email)
        phoneNumber = findViewById(R.id.phonenumber)
        genderRG = findViewById(R.id.gender)
        goLogin = findViewById(R.id.goLogin)
        regis = findViewById(R.id.register)
        alertuser = findViewById(R.id.alert_user)
        alertpass = findViewById(R.id.alert_pass)
        alertcpass = findViewById(R.id.alert_cpass)
        alertemail = findViewById(R.id.alert_email)
        alertphone = findViewById(R.id.alert_phone)
        alertgender = findViewById(R.id.alert_gender)
        userDbHelper = UserDatabaseHelper(this)

        auth = FirebaseAuth.getInstance()

        // Navigate to login activity
        goLogin.setOnClickListener {
            val intent = Intent(this, loginActivity::class.java)
            startActivity(intent)
        }

        // Reset alerts visibility
        alertemail.visibility = View.GONE
        alertpass.visibility = View.GONE
        alertcpass.visibility = View.GONE
        alertuser.visibility = View.GONE
        alertphone.visibility = View.GONE
        alertgender.visibility = View.GONE

        // Handle registration
        regis.setOnClickListener {
            val user = username.text.toString()
            val pass = password.text.toString()
            val cpass = cpassword.text.toString()
            val mail = email.text.toString()
            val tlp = phoneNumber.text.toString()
            val formattedPhoneNumber = formatPhoneNumber(tlp)
            val selectedGenderId = genderRG.checkedRadioButtonId
            val gender = if (selectedGenderId != -1) findViewById<RadioButton>(selectedGenderId).text.toString() else ""

            // Input validation
            if (user.isEmpty() || pass.isEmpty() || cpass.isEmpty() || mail.isEmpty() || formattedPhoneNumber.isEmpty() || gender.isEmpty()) {
                if (user.isEmpty()) alertuser.visibility = View.VISIBLE
                if (pass.isEmpty()) alertpass.visibility = View.VISIBLE
                if (cpass.isEmpty()) alertcpass.visibility = View.VISIBLE
                if (mail.isEmpty()) alertemail.visibility = View.VISIBLE
                if (formattedPhoneNumber.isEmpty()) alertphone.visibility = View.VISIBLE
                if (gender.isEmpty()) alertgender.visibility = View.VISIBLE
            } else if (pass.length < 8 || cpass.length < 8) {
                Toast.makeText(this, "Password length must be at least 8 characters", Toast.LENGTH_SHORT).show()
            } else if (!isValidEmail(mail)) {
                Toast.makeText(this, "Email must end with ‘@puff.com’", Toast.LENGTH_SHORT).show()
            } else if (!isValidPhoneNumber(tlp)) {
                Toast.makeText(this, "Phone number length must be between 11 – 13 characters", Toast.LENGTH_SHORT).show()
            } else {
                if (cpass != pass) {
                    Toast.makeText(this, "Password and confirmation password do not match", Toast.LENGTH_SHORT).show()
                } else {
                    // Check if the username already exists
                    if (userDbHelper.isUsernameExists(user)) {
                        Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        // Store user information temporarily
                        userToRegister = User(user, pass, mail, formattedPhoneNumber, gender)
                        sendVerificationCode(formattedPhoneNumber)
                    }
                }
            }
        }
    }

    fun isValidEmail(email: String): Boolean {
        return email.endsWith("@puff.com")
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        return phoneNumber.length in 11..13
    }

    private fun formatPhoneNumber(phoneNumber: String): String {
        return if (!phoneNumber.startsWith("+")) {
            // Assuming default country code is Indonesia (+62)
            "+62${phoneNumber.removePrefix("0")}"
        } else {
            phoneNumber
        }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("error", "Verification failed: ${e.message}")
                    Toast.makeText(this@RegisterActivity, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    this@RegisterActivity.verificationId = verificationId
                    val bottomSheetOTP = BottomSheetOTP.newInstance(
                        verificationId,
                        userToRegister?.username ?: "",
                        userToRegister?.password ?: "",
                        userToRegister?.email ?: "",
                        userToRegister?.phoneNumber ?: "",
                        userToRegister?.gender ?: ""
                    )
                    bottomSheetOTP.show(supportFragmentManager, "BottomSheetOTP")
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification successful", Toast.LENGTH_SHORT).show()
                    // Save user data to database
                    userToRegister?.let { user ->
                        val isInserted = userDbHelper.insert(user.username, user.email, user.password, user.phoneNumber, user.gender)
                        if (isInserted) {
                            Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                            // Navigate to the main activity or home screen
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to register user", Toast.LENGTH_SHORT).show()
                        }
                        userToRegister = null // Clear temporary storage
                    }
                } else {
                    Toast.makeText(this, "Verification failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    data class User(val username: String, val password: String, val email: String, val phoneNumber: String, val gender: String)
}
