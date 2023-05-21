package com.alberto.gesresfamilyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btCentros;
    Button btProfesionales;
    Button btResidentes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btCentros = findViewById(R.id.btCentros);
        btProfesionales = findViewById(R.id.btProfesionales);
        btResidentes = findViewById(R.id.btResidentes);

        btCentros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, CentrosActivity.class));
            }
        });

        btProfesionales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, ProfesionalesActivity.class));
            }
        });

        btResidentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, ResidentesActivity.class));
            }
        });




    }
}