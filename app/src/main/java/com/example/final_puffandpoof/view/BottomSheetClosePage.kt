package com.example.final_puffandpoof.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import com.example.final_puffandpoof.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetClosePage : BottomSheetDialogFragment(), OnMapReadyCallback {

    private lateinit var close: ImageView
    private var mapView: MapView? = null
    private lateinit var gMap: GoogleMap
    private lateinit var logout: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_bottom_sheet_close_page, container, false)

        try {
            close = view.findViewById(R.id.closeBTN)
            mapView = view.findViewById(R.id.mapView)
            logout = view.findViewById(R.id.Logout)

            mapView?.onCreate(savedInstanceState)
            mapView?.getMapAsync(this)

            close.setOnClickListener {
                dismiss()
            }

            logout.setOnClickListener {
                logoutUser()
            }
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        } catch (e: Exception) {
            Log.e("BottomSheetClosePage", "Exception: ${e.message}")
        }

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        // Set a marker at a specific location
        val lokasi = LatLng(-6.20201, 106.78113)
        gMap.addMarker(MarkerOptions().position(lokasi).title("PuFF and Poof"))
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15f))
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        mapView?.onDestroy()
        super.onDestroyView()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    private fun logoutUser() {
        // Clear the shared preferences
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Redirect to login page
        val intent = Intent(activity, loginActivity::class.java)
        startActivity(intent)
        activity?.finish() // Close the current activity
    }
}
