package com.alberto.gesresfamilyapp;

import static com.alberto.gesresfamilyapp.db.Constants.DATABASE_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alberto.gesresfamilyapp.adapter.CentroAdapter;
import com.alberto.gesresfamilyapp.db.AppDatabase;
import com.alberto.gesresfamilyapp.domain.Centro;

import java.util.ArrayList;
import java.util.List;

public class CentrosActivity extends AppCompatActivity {

    public static List<Centro> centroList = new ArrayList<>();
    private CentroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centros);

        centroList = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.centros_list);
        //esto le dice que tenga un tamaño fijo y que ocupe el máximo espacio asignado
        recyclerView.setHasFixedSize(true);
        //Esto le dice que lo va a gestionar un linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //así se ciñe al Layout manager
        recyclerView.setLayoutManager(layoutManager);
        //hago mi adapter propio no utilizo el arrayadapter de android
        adapter = new CentroAdapter(this, centroList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries().build();
        centroList.clear();
        centroList.addAll(db.centroDao().getAll());
        // con esto la lista siempre estára actualizada cuando vuelva de un segundo plano.
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.registrar) {
            //Con Intent de digo donde estoy y a donde quiero ir
            Intent intent = new Intent(this, RegisterCentroActivity.class);
            startActivity(intent);
            return true;
        } if (item.getItemId() == R.id.view_map) { //Para cuando pulsan en la boton del mapa en el actionbar
            Intent intent = new Intent(this, MapsActivity.class); //donde nos manda al pinchar sobre el boton mapas en el action bar
            startActivity(intent);
            return true;
        }
        return false;
    }

    public void cancel(View view) {
        onBackPressed();
    }

}