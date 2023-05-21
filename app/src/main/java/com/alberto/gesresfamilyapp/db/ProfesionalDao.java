package com.alberto.gesresfamilyapp.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.alberto.gesresfamilyapp.domain.Centro;
import com.alberto.gesresfamilyapp.domain.Profesional;

import java.util.Date;
import java.util.List;

@Dao
public interface ProfesionalDao {

    @Query("SELECT * FROM Profesional")
    List<Profesional> getAll();

    @Query("SELECT * FROM Profesional WHERE nombre = :name")
    Profesional getByName(String name);

    @Query("DELETE FROM Profesional WHERE nombre = :name")
    void deleteByName(String name);

    @Query("UPDATE Profesional SET nombre = :nombre, apellidos = :apellidos, dni = :dni, categoria = :categoria WHERE nombre = :nombre ")
    void update(String nombre, String apellidos, String dni, /*Date fecha_nacimiento,*/ String categoria);


    @Query("SELECT * FROM Profesional WHERE id = :id")
    Profesional getById(long id);
    @Insert
    void insert(Profesional profesional);

    @Delete
    void delete(Profesional profesional);

    @Update
    void update(Profesional profesional);

}
