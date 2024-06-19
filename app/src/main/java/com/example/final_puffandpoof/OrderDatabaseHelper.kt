package com.example.final_puffandpoof

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.final_puffandpoof.model.CartDatabase

class OrderDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "orders.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "orders"
        const val COLUMN_ID = "id"
        const val COLUMN_ID_BARANG = "id_barang"
        const val COLUMN_ID_USER = "id_user"
        const val COLUMN_QUANTITY = "quantity"
    }

    private val databaseHelper = DatabaseHelper(context)

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ID_BARANG INTEGER,
                $COLUMN_ID_USER INTEGER,
                $COLUMN_QUANTITY INTEGER
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertOrder(idBarang: Int, idUser: Int, quantity: Int): Long {
        val db = this.writableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID_BARANG = ? AND $COLUMN_ID_USER = ?"
        val cursor = db.rawQuery(query, arrayOf(idBarang.toString(), idUser.toString()))

        return if (cursor.moveToFirst()) {
            val existingQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
            val newQuantity = existingQuantity + quantity
            val contentValues = ContentValues().apply {
                put(COLUMN_QUANTITY, newQuantity)
            }
            val whereClause = "$COLUMN_ID_BARANG = ? AND $COLUMN_ID_USER = ?"
            val whereArgs = arrayOf(idBarang.toString(), idUser.toString())
            db.update(TABLE_NAME, contentValues, whereClause, whereArgs).toLong()
        } else {
            val contentValues = ContentValues().apply {
                put(COLUMN_ID_BARANG, idBarang)
                put(COLUMN_ID_USER, idUser)
                put(COLUMN_QUANTITY, quantity)
            }
            db.insert(TABLE_NAME, null, contentValues)
        }.also {
            cursor.close()
        }
    }

    @SuppressLint("Range")
    fun getOrdersByUserId(userId: Int, context: Context): List<CartDatabase> {
        val orders = mutableListOf<CartDatabase>()
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID_USER = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        try {
            cursor.use {
                while (it.moveToNext()) {
                    val idBarang = it.getInt(it.getColumnIndex(COLUMN_ID_BARANG))
                    val quantity = it.getInt(it.getColumnIndex(COLUMN_QUANTITY))
                    val id = it.getInt(it.getColumnIndex(COLUMN_ID))

                    val barang = databaseHelper.getDollById(idBarang)
                    barang?.let {
                        val order = CartDatabase(id, userId, quantity, it.imageLink, it.price, it.name)
                        orders.add(order)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("getOrdersByUserId", "Error fetching orders", e)
        } finally {
            cursor?.close()
        }

        return orders
    }

    fun updateOrderQuantity(orderId: Int, newQuantity: Int): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_QUANTITY, newQuantity)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(orderId.toString())
        return db.update(TABLE_NAME, contentValues, whereClause, whereArgs)
    }

    fun deleteOrder(orderId: Int): Int {
        val db = this.writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(orderId.toString())
        return db.delete(TABLE_NAME, whereClause, whereArgs)
    }
}
