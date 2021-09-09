package com.jawaadianinc.rubixcubesolver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseUsers extends SQLiteOpenHelper {

    public static final String USERS_NAME = "USERS_NAME";
    public static final String USERS_EMAIL = "USERS_EMAIL";
    public static final String USERS_ID = "USERS_ID";
    public static final String USERS_JOINED = "USERS_JOINED";
    public static final String USERS_TABLE = "USERS_TABLE";


    public DataBaseUsers(@Nullable Context context) {
        super(context, "users.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + USERS_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + USERS_NAME + " TEXT, " + USERS_EMAIL + " TEXT, " + USERS_ID + " TEXT, " + USERS_JOINED + " TIMESTAMP)";

        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addUser(UserModel userModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USERS_NAME, userModel.getName());
        cv.put(USERS_EMAIL, userModel.getEmail());
        cv.put(USERS_ID, userModel.getId());
        cv.put(USERS_JOINED, String.valueOf(new java.sql.Timestamp(new java.util.Date().getTime())));

        final long insert = db.insert(USERS_TABLE, null, cv);
        return insert != -1;
    }

    public List<UserModel> getAllUsers() {
        List<UserModel> returnList = new ArrayList<>();

        String SQLString = "SELECT * FROM " + USERS_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLString, null);
        if (cursor.moveToFirst()) {
            do {
                int UserID = cursor.getInt(0);
                String UserName = cursor.getString(1);
                String UserEmail = cursor.getString(2);
                String User_GoogleID = cursor.getString(3);
                String UserJoined = cursor.getString(4);

                UserModel newUser = new UserModel(User_GoogleID, UserName, UserEmail, UserJoined);
                returnList.add(newUser);

            } while (cursor.moveToNext());
        } else {
            //LOL
        }
        cursor.close();
        db.close();

        return returnList;
    }

}
