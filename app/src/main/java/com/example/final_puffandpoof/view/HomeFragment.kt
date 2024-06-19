// HomeFragment.kt
package com.example.final_puffandpoof.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_puffandpoof.*
import com.example.final_puffandpoof.model.Doll
import com.example.final_puffandpoof.model.RecyclerViewHome
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var barangRV: RecyclerView
    private lateinit var dollAdapter: RecyclerViewHome
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        dbHelper = DatabaseHelper(requireContext())

        barangRV = view.findViewById(R.id.barangRV)
        barangRV.layoutManager = GridLayoutManager(context, 2)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        barangRV.addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true)) // Mengatur jarak antar item

        if (!SyncStatus.isSynced(requireContext())) {
            fetchDolls()
        } else {
            displayDollsFromDatabase()
        }

        return view
    }

    private fun fetchDolls() {
        RetrofitInstance.api.getDolls().enqueue(object : Callback<DollResponse> {
            override fun onResponse(call: Call<DollResponse>, response: Response<DollResponse>) {
                if (response.isSuccessful) {
                    val dolls = response.body()?.dolls ?: emptyList()
                    for (doll in dolls) {
                        dbHelper.insertDoll(doll)
                    }
                    SyncStatus.setSynced(
                        requireContext(),
                        true
                    ) // Tandai bahwa data sudah disinkronkan
                    displayDollsFromDatabase()
                }
            }

            override fun onFailure(call: Call<DollResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun displayDollsFromDatabase() {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        val dolls = mutableListOf<Doll>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val desc = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_Desc))
                val name = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_Name))
                val size = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_Size))
                val price = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_Price))
                val rating = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_Rating))
                val imageLink = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ImageLink))
                dolls.add(Doll(id,desc, name, size, price, rating, imageLink))
            }
        }
        cursor.close()

        dollAdapter = RecyclerViewHome(dolls)
        barangRV.adapter = dollAdapter
//        Toast.makeText(context, "Jumlah data: ${dolls.size}", Toast.LENGTH_SHORT).show()
    }
}
