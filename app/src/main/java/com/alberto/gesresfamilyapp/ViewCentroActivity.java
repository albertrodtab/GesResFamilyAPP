package com.alberto.gesresfamilyapp;

import static com.alberto.gesresfamilyapp.db.Constants.DATABASE_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alberto.gesresfamilyapp.adapter.CentroAdapter;
import com.alberto.gesresfamilyapp.db.AppDatabase;
import com.alberto.gesresfamilyapp.domain.Centro;
import com.google.android.gms.maps.MapView;

import java.util.List;

public class ViewCentroActivity extends AppCompatActivity {

    public List<Centro> centros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_centro);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        if (name == null)
            return;

        // Cargo los detalles de la tarea
        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries().build();
        Centro centro = db.centroDao().getByName(name);
        fillData(centro);
    }

    private void fillData(Centro centro) {
        TextView name = findViewById(R.id.tvNombre);
        TextView direccion = findViewById(R.id.tvDireccion);
        TextView registro = findViewById(R.id.tvNumRegistro);
        TextView telefono = findViewById(R.id.tvTelefono);
        TextView mail = findViewById(R.id.tvEmail);
        ImageView foto = findViewById(R.id.ivCentro);
        MapView mapa = findViewById(R.id.mvCentro);

        name.setText(centro.getNombre());
        direccion.setText(centro.getDireccion());
        registro.setText(centro.getNumRegistro());
        telefono.setText(centro.getTelefono());
        mail.setText(centro.getEmail());

    }


    /**
     * Método para cuando se crea el menu contextual, infle el menu con las opciones
     *
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.actionbar_main, menu);
        //MenuItem menuItem = menu.findItem(R.id.add_menu);
        //menuItem.setTitle("➕ AÑADIR");
    }

    /**
     * Opciones del menu contextual
     *
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        Intent intent = new Intent(this, ViewCentroActivity.class);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final int itemSelected = info.position;

        switch (item.getItemId()) {
            case R.id.modify_menu:                      // Modificar Centro
                Centro centro = centros.get(itemSelected);
                intent.putExtra("modify_car", true);
                intent.putExtra("id", centro.getId());
                intent.putExtra("nombre", centro.getNombre());
                intent.putExtra("dirección", centro.getDireccion());
                intent.putExtra("Num Registro", centro.getNumRegistro());
                intent.putExtra("teléfono", centro.getTelefono());
                intent.putExtra("email", centro.getEmail());


                startActivity(intent);
                return true;

            /*case R.id.detail_menu:                      // Detalles del coche
                showDetails(info.position);
                return true;

            case R.id.add_menu:                         // Añadir coche
                startActivity(intent);
                return true;*/

//            case R.id.delete_menu:                      // Eliminar coche
//                deleteCentro(info);
//                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

}