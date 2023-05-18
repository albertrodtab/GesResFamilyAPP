package com.alberto.gesresfamilyapp.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.alberto.gesresfamilyapp.domain.Centro;

@Database(entities = {Centro.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CentroDao centroDao();
}

