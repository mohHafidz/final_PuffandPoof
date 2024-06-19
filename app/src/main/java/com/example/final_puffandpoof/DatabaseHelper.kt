package com.example.final_puffandpoof

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.final_puffandpoof.model.Doll

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "PuffandPoof.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "barang_table"
        const val COLUMN_ID = "id"
        const val COLUMN_Desc = "desc"
        const val COLUMN_Name = "name"
        const val COLUMN_Size = "size"
        const val COLUMN_Price = "price"
        const val COLUMN_Rating = "rating"
        const val COLUMN_ImageLink = "imageLink"

        private const val SQL_CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_Desc TEXT, " +
                "$COLUMN_Name TEXT, " +
                "$COLUMN_Size TEXT, " +
                "$COLUMN_Price REAL, " +
                "$COLUMN_Rating REAL, " +
                "$COLUMN_ImageLink TEXT)"

        private const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_TABLE)
        onCreate(db)
    }

    fun insertDoll(doll: Doll) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_Desc, doll.desc)
            put(COLUMN_Name, doll.name)
            put(COLUMN_Size, doll.size)
            put(COLUMN_Price, doll.price)
            put(COLUMN_Rating, doll.rating)
            put(COLUMN_ImageLink, doll.imageLink)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getDollById(id: Int): Doll? {
        val db = this.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_NAME,
            null,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var doll: Doll? = null
        if (cursor.moveToFirst()) {
            val desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_Desc))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_Name))
            val size = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_Size))
            val price = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_Price))
            val rating = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_Rating))
            val imageLink = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ImageLink))
            doll = Doll(id, desc, name, size, price, rating, imageLink)
        }
        cursor.close()
        return doll
    }

    fun getPriceById(itemId: Int): Double {
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_Price FROM $TABLE_NAME WHERE $COLUMN_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(itemId.toString()))
        var price = 0.0

        if (cursor.moveToFirst()) {
            price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_Price))
        }
        cursor.close()
        return price
    }



}
