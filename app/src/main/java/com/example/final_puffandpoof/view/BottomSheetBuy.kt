package com.example.final_puffandpoof.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.final_puffandpoof.OrderDatabaseHelper
import com.example.final_puffandpoof.databinding.FragmentBottomSheetBuyBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetBuy : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBuyBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelperCart: OrderDatabaseHelper
    private var userId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetBuyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve arguments
        val imageUrl = arguments?.getString("IMAGE_URL")
        val itemName = arguments?.getString("ITEM_NAME")
        val itemId = arguments?.getInt("ITEM_ID")

        // Get user ID from shared preferences
        userId = getUserIdFromPreferences()

        if (userId == null) {
            showError("User ID not found. Please log in again.")
            navigateToLogin()
            return
        }

        dbHelperCart = OrderDatabaseHelper(requireContext())

        // Set up view elements
        binding.close.setOnClickListener { dismiss() }

        imageUrl?.let {
            Glide.with(this).load(it).into(binding.gambar)
        }
        binding.barang.text = itemName

        // Handle add to cart action
        binding.cart.setOnClickListener {
            val quantity = binding.quantity.text.toString().toIntOrNull() ?: 0
            if (itemId != null && quantity > 0) {
                dbHelperCart.insertOrder(itemId, userId!!, quantity)
                dismiss()
            } else {
                showError("Please enter a valid quantity.")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(imageUrl: String, itemName: String, itemId: Int): BottomSheetBuy {
            return BottomSheetBuy().apply {
                arguments = Bundle().apply {
                    putString("IMAGE_URL", imageUrl)
                    putString("ITEM_NAME", itemName)
                    putInt("ITEM_ID", itemId)
                }
            }
        }
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

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), loginActivity::class.java)
        startActivity(intent)
    }
}
