package com.example.final_puffandpoof.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.final_puffandpoof.R
import com.example.final_puffandpoof.UserDatabaseHelper

class AccountFragment : Fragment() {

    lateinit var logout: ImageView
    private var userId: Int? = null
    private lateinit var dbHelper: UserDatabaseHelper
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var tlpNumberTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        userId = getUserIdFromPreferences()
        dbHelper = UserDatabaseHelper(requireContext())

        Log.d("userID", userId.toString())

        // If user ID is null, show an error message and return early
        if (userId == null) {
            showError("User ID not found. Please log in again.")
            val intent = Intent(requireContext(), loginActivity::class.java)
            startActivity(intent)
            return view
        }

        logout = view.findViewById(R.id.button)
        usernameTextView = view.findViewById(R.id.usernameTv)
        emailTextView = view.findViewById(R.id.emailTV)
        tlpNumberTextView = view.findViewById(R.id.tlpTV)

        // Fetch and display user details
        userId?.let {
            val userDetails = dbHelper.getUserDetailsById(it)
            userDetails?.let { user ->
                usernameTextView.text = user.username
                emailTextView.text = user.email
                tlpNumberTextView.text = user.tlpNumber
            } ?: run {
                showError("User details not found. Please log in again.")
                val intent = Intent(requireContext(), loginActivity::class.java)
                startActivity(intent)
            }
        }

        logout.setOnClickListener {
            val bottomSheet = BottomSheetClosePage()
            bottomSheet.show(childFragmentManager, "BottomSheetDialog")
        }

        return view
    }

    private fun getUserIdFromPreferences(): Int? {
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return try {
            if (sharedPreferences.contains("user_id")) {
                sharedPreferences.getInt("user_id", -1).takeIf { it != -1 }
            } else {
                null
            }
        } catch (e: ClassCastException) {
            null
        }
    }

    private fun showError(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}
