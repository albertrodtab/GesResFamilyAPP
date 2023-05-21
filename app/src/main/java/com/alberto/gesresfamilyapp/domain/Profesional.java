package com.alberto.gesresfamilyapp.domain;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.alberto.gesresfamilyapp.util.Converters;

import java.util.Date;

import lombok.AllArgsConstructor;

@Entity
@AllArgsConstructor
public class Profesional {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo
    @NonNull
    private String nombre;

    @ColumnInfo
    private String apellidos;

    @ColumnInfo
    private String dni;

    @ColumnInfo (name = "fecha_nacimiento")
    @TypeConverters(Converters.class)
    private Date fechaNacimiento;

    @ColumnInfo
    private String categoria;

    @ColumnInfo
    private String photoUri;

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public Profesional() {
    }

    public Profesional(long id, @NonNull String nombre, String apellidos, String dni, Date fechaNacimiento, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.categoria = categoria;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getNombre() {
        return nombre;
    }

    public void setNombre(@NonNull String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
