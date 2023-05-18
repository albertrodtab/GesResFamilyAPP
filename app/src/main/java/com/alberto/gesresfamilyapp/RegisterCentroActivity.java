package com.alberto.gesresfamilyapp;

import static com.alberto.gesresfamilyapp.db.Constants.DATABASE_NAME;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.alberto.gesresfamilyapp.db.AppDatabase;
import com.alberto.gesresfamilyapp.domain.Centro;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class RegisterCentroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_centro);

        Intent intent = getIntent();
        int centroId = intent.getIntExtra("id", -1);
        if (centroId != -1) {
            final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries().build();
            Centro centro = db.centroDao().getById(centroId);
            if (centro != null) {
                fillData(centro);
            }
        }
    }

    private void fillData(Centro centro) {
        EditText nombre = findViewById(R.id.etNombre);
        EditText direccion = findViewById(R.id.etDireccion);
        EditText numRegistro = findViewById(R.id.etNumRegistro);
        EditText telefono = findViewById(R.id.etTelefono);
        EditText mail = findViewById(R.id.etMail);

        nombre.setText(centro.getNombre());
        direccion.setText(centro.getDireccion());
        mail.setText(centro.getEmail());
        numRegistro.setText(centro.getNumRegistro());
        telefono.setText(centro.getTelefono());
    }

    public void registerCentro(View view) {
        EditText etNombre = findViewById(R.id.etNombre);
        EditText etDireccion = findViewById(R.id.etDireccion);
        EditText etNumRegistro = findViewById(R.id.etNumRegistro);
        EditText etTelefono = findViewById(R.id.etTelefono);
        EditText etMail = findViewById(R.id.etMail);

        String nombre = etNombre.getText().toString();
        String direccion = etDireccion.getText().toString();
        String numRegistro = etNumRegistro.getText().toString();
        String telefono = etTelefono.getText().toString();
        String mail = etMail.getText().toString();

        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries().build();

        Intent intent = getIntent();
        boolean isModifyCentro = intent.getBooleanExtra("modify_centro", false);
        if (isModifyCentro) {
            long centroId = intent.getLongExtra("id", -1);
            if (centroId != -1) {
                Centro centro = db.centroDao().getById(centroId);
                centro.setNombre(nombre);
                centro.setDireccion(direccion);
                centro.setNumRegistro(numRegistro);
                centro.setTelefono(telefono);
                centro.setEmail(mail);
                db.centroDao().update(centro);
                Toast.makeText(this, "Centro modificado", Toast.LENGTH_LONG).show();
            }
        } else {
            Centro centro = new Centro(nombre, direccion, numRegistro, telefono, mail);
            db.centroDao().insert(centro);
            Toast.makeText(this, "Centro registrado", Toast.LENGTH_LONG).show();
        }

        etNombre.setText("");
        etDireccion.setText("");
        etMail.setText("");
        etNumRegistro.setText("");
        etTelefono.setText("");
        etNombre.requestFocus();
    }


    public void cancel(View view) {
        onBackPressed();
    }
}
