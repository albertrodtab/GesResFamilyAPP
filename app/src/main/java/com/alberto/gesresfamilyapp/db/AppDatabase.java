package com.alberto.gesresfamilyapp.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.alberto.gesresfamilyapp.domain.Centro;
import com.alberto.gesresfamilyapp.domain.Profesional;
import com.alberto.gesresfamilyapp.domain.Residente;
import com.alberto.gesresfamilyapp.util.Converters;

@Database(entities = {Centro.class, Profesional.class, Residente.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract CentroDao centroDao();


    public abstract ProfesionalDao profesionalDao();


    public abstract ResidenteDao residenteDao();
}

