package com.example.safehaven;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String NAME_OF_DATABASE = "SafeHaven.db";
    public static final String NAME_OF_TABLE = "Contacts";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";
    public static final String COL_3 = "CONTACT_NO";


    public DatabaseHandler(@Nullable Context context) {
        super(context, NAME_OF_DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table " + NAME_OF_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT , NAME TEXT , CONTACT_NO INTEGER )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NAME_OF_TABLE);
        onCreate(db);
    }

    //Method to add new contacts
    public boolean insertData(String name, String num) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, num);
        long result = db.insert(NAME_OF_TABLE, null, contentValues);
        return result != -1;
    }

    //Retrieves all existing contacts
    public Cursor getAllContacts() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + NAME_OF_TABLE + " ORDER BY " + COL_2 + " ASC", null);
    }

    //Method to delete an existing contact
    public boolean deleteContact(String phoneNumber) {

        SQLiteDatabase db = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM " + NAME_OF_TABLE + " WHERE " + COL_3 + " = " + phoneNumber;
        Cursor cursor = db.rawQuery(deleteQuery, null);

        if (cursor.moveToFirst()) {
            db.close();
            cursor.close();
            return true;
        } else {
            db.close();
            cursor.close();
            return false;
        }
    }

    //Function to search for contacts within the database
    public Cursor searchContacts(String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + NAME_OF_TABLE + " WHERE " + COL_2 + " LIKE ?" + " ORDER BY " + COL_2 + " ASC", new String[]{"%" + keyword + "%"});
        return cursor;
    }

    //Method to update existing contacts details
    public void updateContact(String oldPhoneNumber, String name, String newPhoneNumber) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_2, name);
        values.put(COL_3, newPhoneNumber);

        db.update(NAME_OF_TABLE, values, COL_3 + " = ?",
                new String[]{oldPhoneNumber});
        db.close();
    }

    //Retrieves a contacts phone number from database
    public int getContact(String phoneNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + NAME_OF_TABLE + " WHERE " + COL_3 + " = " + phoneNo, null);
        int numberOfContacts = cursor.getCount();
        cursor.close();
        return numberOfContacts;
    }

    //Retrieves a contacts name from database
    public int getName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + NAME_OF_TABLE + " WHERE " + COL_2 + " = '" + name + "'", null);
        int numberOfContacts = cursor.getCount();
        cursor.close();
        return numberOfContacts;
    }
}
