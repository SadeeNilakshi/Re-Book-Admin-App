package lk.javainstitute.rebook_admin.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ReBook";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "user";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_FIRST_NAME = "fname";
    private static final String COLUMN_LAST_NAME = "lname";
    private static final String COLUMN_PHONE = "mobile";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_GENDER = "gender";

    private static final String COLUMN_PROFILE_PICTURE = "profile_img";


    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                COLUMN_PHONE + " TEXT NOT NULL, " +
                COLUMN_EMAIL + " TEXT NOT NULL, " +
                COLUMN_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_GENDER + " TEXT NOT NULL, " +
                COLUMN_PROFILE_PICTURE + " BLOB)";
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        Log.d("DBDebug", "Database Created Successfully!");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(sqLiteDatabase);
    }
    public boolean registerUser(String fname, String lname, String mobile, String email, String password, String gender) {
        return registerUser(fname, lname, mobile, email, password, gender, null);
    }

    public boolean registerUser(String fname, String lname, String mobile, String email, String password, String gender, byte[] profileImg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, fname);
        values.put(COLUMN_LAST_NAME, lname);
        values.put(COLUMN_PHONE, mobile);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_PROFILE_PICTURE, profileImg);

        long result = db.insert(TABLE_USERS, null, values);
        if (result == -1) {
            Log.e("SQLiteHelper", "Failed to insert user data into database");
            return false;
        } else {
            Log.d("SQLiteHelper", "User registered successfully with ID: " + result);
            return true;
        }
    }


    public boolean loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = ? AND password = ?",
                new String[]{email, password});

        boolean isLoggedIn = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isLoggedIn;
    }

    public Cursor getUserDetails(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email = ?", new String[]{email});
    }

    public boolean validateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE email=? AND password=?", new String[]{email, password});

        boolean userExists = cursor.getCount() > 0;

        if (userExists) {
            Log.d("LoginCheck", "User found : " + email);
        } else {
            Log.d("LoginCheck", "No matching user found : " + email);
        }

        cursor.close();
        db.close();
        return userExists;
    }

    public String[] getUserInfo(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT fname, lname, mobile, password FROM user WHERE email = ?", new String[]{email});

        if (cursor != null && cursor.moveToFirst()) {
            String fname = cursor.getString(0);
            String lname = cursor.getString(1);
            String mobile = cursor.getString(2);
            String password = cursor.getString(3);
            cursor.close();
            db.close();
            return new String[]{fname, lname, mobile, password};
        }

        return null;
    }

    public boolean updateProfilePicture(String email, byte[] profileImg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_PICTURE, profileImg);
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        db.close();
        return rowsAffected > 0;
    }

    public byte[] getProfilePicture(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_PROFILE_PICTURE + " FROM " + TABLE_USERS + " WHERE email = ?", new String[]{email});
        if (cursor != null && cursor.moveToFirst()) {
            byte[] image = cursor.getBlob(0);
            cursor.close();
            db.close();
            return image;
        }
        return null;
    }

}
