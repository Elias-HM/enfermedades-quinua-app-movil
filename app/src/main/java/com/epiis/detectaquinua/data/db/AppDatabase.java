package com.epiis.detectaquinua.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.epiis.detectaquinua.data.dao.HistorialDao;
import com.epiis.detectaquinua.data.entity.HistorialConsulta;

@Database(entities = {HistorialConsulta.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract HistorialDao historialDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "appquinua_db")
                    .allowMainThreadQueries() // usar en pruebas, luego usar hilo
                    .build();
        }
        return INSTANCE;
    }
}