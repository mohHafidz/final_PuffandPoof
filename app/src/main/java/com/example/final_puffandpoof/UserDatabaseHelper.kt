package com.example.final_puffandpoof

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "users.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_TLP_NUMBER = "tlpNumber"
        const val COLUMN_GENDER = "gender"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME VARCHAR(255),
                $COLUMN_EMAIL VARCHAR(255),
                $COLUMN_PASSWORD VARCHAR(50),
                $COLUMN_TLP_NUMBER VARCHAR(15),
                $COLUMN_GENDER VARCHAR(15)
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insert(username: String, email: String, password: String, phoneNumber: String, gender: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_USERNAME, username)
        contentValues.put(COLUMN_EMAIL, email)
        contentValues.put(COLUMN_PASSWORD, password)
        contentValues.put(COLUMN_TLP_NUMBER, phoneNumber)
        contentValues.put(COLUMN_GENDER, gender)

        val result = db.insert(TABLE_NAME, null, contentValues)
        if (result == -1L) {
            Log.e("UserDatabaseHelper", "Failed to insert data for user: $username")
            return false
        } else {
            Log.d("UserDatabaseHelper", "Data inserted for user: $username")
            return true
        }
    }

    fun isUsernameExists(username: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID),
            "$COLUMN_USERNAME = ?",
            arrayOf(username),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    @SuppressLint("Range")
    fun checkUserCredentials(username: String, password: String): Int? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID),
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val userId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            cursor.close()
            userId
        } else {
            cursor.close()
            null
        }
    }

    @SuppressLint("Range")
    fun getUserDetailsById(userId: Int): User? {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_TLP_NUMBER),
            "$COLUMN_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )
        return if (cursor.moveToFirst()) {
            val username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME))
            val email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
            val tlpNumber = cursor.getString(cursor.getColumnIndex(COLUMN_TLP_NUMBER))
            cursor.close()
            User(userId, username, email, tlpNumber)
        } else {
            cursor.close()
            null
        }
    }

    data class User(val id: Int, val username: String, val email: String, val tlpNumber: String)
}
