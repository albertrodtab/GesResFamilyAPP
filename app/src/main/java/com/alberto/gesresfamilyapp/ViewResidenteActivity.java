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
import com.alberto.gesresfamilyapp.domain.Residente;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class ViewResidenteActivity extends AppCompatActivity {

    public List<Residente> residentes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_residente);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        if (name == null)
            return;

        // Cargo los detalles de la tarea
        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries().build();
        Residente residente = db.residenteDao().getByName(name);
        fillData(residente);

    }

    private void fillData(Residente residente) {
        TextInputLayout name = findViewById(R.id.tilNombre);
        TextInputLayout apellidos = findViewById(R.id.tilApellidos);
        TextInputLayout dni = findViewById(R.id.tilDni);
        //TextView fechaNac = findViewById(R.id.tvProfesionalFechaNac);
        TextInputLayout sexo = findViewById(R.id.tilSexo);
        ImageView foto = findViewById(R.id.ivResidente);


        name.getEditText().setText(residente.getNombre());
        apellidos.getEditText().setText(residente.getApellidos());
        dni.getEditText().setText(residente.getDni());
        //fechaNac.setText((CharSequence) residente.getFechaNacimiento());
        sexo.getEditText().setText(residente.getSexo());
    }

    public void cancel(View view) {
        onBackPressed();
    }


}