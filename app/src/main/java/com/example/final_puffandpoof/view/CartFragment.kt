package com.example.final_puffandpoof.view

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_puffandpoof.OrderDatabaseHelper
import com.example.final_puffandpoof.R
import com.example.final_puffandpoof.model.CartDatabase
import com.example.final_puffandpoof.model.RecyclerViewCart
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment() {

    private lateinit var cartRV: RecyclerView
    private lateinit var cartAdapter: RecyclerViewCart
    private lateinit var dbHelperCart: OrderDatabaseHelper
    private lateinit var barHarga: Toolbar
    private lateinit var price: TextView
    private lateinit var deleteIcon: Drawable
    private lateinit var swipeBackground: Drawable
    private val iconWidth = 140 // Set icon width
    private val iconHeight = 140 // Set icon height
    private var userId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        barHarga = view.findViewById(R.id.bar_total_price)
        price = view.findViewById(R.id.price)
        dbHelperCart = OrderDatabaseHelper(requireContext())

        // Retrieve user ID from shared preferences
        userId = getUserIdFromPreferences()

        Log.d("userID", userId.toString())

        // If user ID is null, show an error message and return early
        if (userId == null) {
            showError("User ID not found. Please log in again.")
            val intent = Intent(requireContext(),loginActivity::class.java)
            startActivity(intent)
            return view
        }

        val dataOrder = dbHelperCart.getOrdersByUserId(userId!!, requireContext())

        cartRV = view.findViewById(R.id.cartRV)
        cartRV.layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = RecyclerViewCart(dataOrder.toMutableList(), ::updateQuantityInDatabase)
        cartRV.adapter = cartAdapter

        deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_delete_24)!!
        swipeBackground = ContextCompat.getDrawable(requireContext(), R.drawable.swipe_background)!!

        val totalPrice = calculateTotalPrice(dataOrder)
        val formattedPrice = formatPrice(totalPrice)

        if (dataOrder.isEmpty()) {
            barHarga.visibility = View.GONE
        } else {
            barHarga.visibility = View.VISIBLE
            price.text = formattedPrice
        }

        // Add ItemTouchHelper to handle swipe to delete
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val order = dataOrder[position]
                showDeleteConfirmationDialog(order.id, position)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - iconHeight) / 2
                if (dX < 0) { // Swipe to the left
                    swipeBackground.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.right - iconMargin - iconWidth,
                        itemView.top + iconMargin,
                        itemView.right - iconMargin,
                        itemView.top + iconMargin + iconHeight
                    )
                } else {
                    swipeBackground.setBounds(0, 0, 0, 0)
                    deleteIcon.setBounds(0, 0, 0, 0)
                }

                swipeBackground.draw(c)
                c.save()

                if (dX < 0) {
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                }
                deleteIcon.draw(c)
                c.restore()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        })

        itemTouchHelper.attachToRecyclerView(cartRV)

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

    private fun showDeleteConfirmationDialog(orderId: Int, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure you want to delete this item?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            deleteItemFromDatabase(orderId, position)
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            cartAdapter.notifyItemChanged(position)
            dialog.dismiss()
        }
        builder.show()
    }

    private fun deleteItemFromDatabase(orderId: Int, position: Int) {
        dbHelperCart.deleteOrder(orderId)
        cartAdapter.removeItem(position)
        val dataOrder = dbHelperCart.getOrdersByUserId(userId!!, requireContext())
        val totalPrice = calculateTotalPrice(dataOrder)
        val formattedPrice = formatPrice(totalPrice)
        price.text = formattedPrice
        if (dataOrder.isEmpty()) {
            barHarga.visibility = View.GONE
        }
    }

    private fun updateQuantityInDatabase(orderId: Int, newQuantity: Int) {
        if (newQuantity == 0) {
            showAlert()
        } else {
            dbHelperCart.updateOrderQuantity(orderId, newQuantity)
            // After updating the quantity, recalculate and update the total price
            val dataOrder = dbHelperCart.getOrdersByUserId(userId!!, requireContext())
            val totalPrice = calculateTotalPrice(dataOrder)
            val formattedPrice = formatPrice(totalPrice)
            price.text = formattedPrice
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Invalid Quantity")
        builder.setMessage("Quantity cannot be zero. Please enter a valid quantity.")
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun calculateTotalPrice(dataOrder: List<CartDatabase>): Int {
        var total = 0
        for (order in dataOrder) {
            total += order.quantity * order.price
        }
        return total
    }

    private fun formatPrice(price: Int): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        formatter.maximumFractionDigits = 0 // Set to 0 to avoid decimals
        return formatter.format(price)
    }

    private fun showError(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Error")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}
