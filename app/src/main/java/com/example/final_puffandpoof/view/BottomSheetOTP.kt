package com.example.final_puffandpoof.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.final_puffandpoof.R
import com.example.final_puffandpoof.UserDatabaseHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class BottomSheetOTP : BottomSheetDialogFragment() {
    private lateinit var otpEditText: EditText
    private lateinit var verifyOtpButton: Button
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private lateinit var userDbHelper: UserDatabaseHelper
    private var username: String? = null
    private var password: String? = null
    private var email: String? = null
    private var phoneNumber: String? = null
    private var gender: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_bottom_sheet_otp, container, false)

        otpEditText = view.findViewById(R.id.otpEditText)
        verifyOtpButton = view.findViewById(R.id.verifyOtpButton)
        auth = FirebaseAuth.getInstance()
        userDbHelper = UserDatabaseHelper(requireContext())

        verificationId = arguments?.getString("verificationId")
        username = arguments?.getString("username")
        password = arguments?.getString("password")
        email = arguments?.getString("email")
        phoneNumber = arguments?.getString("phoneNumber")
        gender = arguments?.getString("gender")

        verifyOtpButton.setOnClickListener {
            val otp = otpEditText.text.toString()
            if (otp.isNotEmpty() && verificationId != null) {
                verifyVerificationCode(otp)
            } else {
                Toast.makeText(context, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun verifyVerificationCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Verification successful", Toast.LENGTH_SHORT).show()
                    val isInserted = userDbHelper.insert(username!!, email!!, password!!, phoneNumber!!, gender!!)
                    if (isInserted) {
                        Toast.makeText(context, "User registered successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        Toast.makeText(context, "Failed to register user", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Verification failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    companion object {
        fun newInstance(
            verificationId: String,
            username: String,
            password: String,
            email: String,
            phoneNumber: String,
            gender: String
        ): BottomSheetOTP {
            val fragment = BottomSheetOTP()
            val args = Bundle()
            args.putString("verificationId", verificationId)
            args.putString("username", username)
            args.putString("password", password)
            args.putString("email", email)
            args.putString("phoneNumber", phoneNumber)
            args.putString("gender", gender)
            fragment.arguments = args
            return fragment
        }
    }
}
