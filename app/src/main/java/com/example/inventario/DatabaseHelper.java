package com.example.inventario;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(@Nullable Context context) {
        super(context, "Login.db",null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL("Create table user(email text primary key, password text)");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists user");
    }

}
