package com.alberto.gesresfamilyapp;

import static com.alberto.gesresfamilyapp.db.Constants.DATABASE_NAME;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.alberto.gesresfamilyapp.db.AppDatabase;
import com.alberto.gesresfamilyapp.domain.Profesional;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class ViewProfesionalActivity extends AppCompatActivity {

    public List<Profesional> profesionales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profesional);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        if (name == null)
            return;

        // Cargo los detalles de la tarea
        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries().build();
        Profesional profesional = db.profesionalDao().getByName(name);
        fillData(profesional);

    }

    private void fillData(Profesional profesional) {
        TextInputLayout name = findViewById(R.id.tilNombre);
        TextInputLayout apellidos = findViewById(R.id.tilApellidos);
        TextInputLayout dni = findViewById(R.id.tilDni);
        //TextView fechaNac = findViewById(R.id.tvProfesionalFechaNac);
        TextInputLayout categoria = findViewById(R.id.tilCategoria);
        ImageView foto = findViewById(R.id.ivProfesional);


        name.getEditText().setText(profesional.getNombre());
        apellidos.getEditText().setText(profesional.getApellidos());
        dni.getEditText().setText(profesional.getDni());
        //fechaNac.setText((CharSequence) profesional.getFechaNacimiento());
        categoria.getEditText().setText(profesional.getCategoria());
    }

    public void cancel(View view) {
        onBackPressed();
    }

}