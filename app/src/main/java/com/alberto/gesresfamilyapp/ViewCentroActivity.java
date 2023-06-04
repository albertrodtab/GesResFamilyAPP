package com.alberto.gesresfamilyapp;

import static com.alberto.gesresfamilyapp.db.Constants.DATABASE_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alberto.gesresfamilyapp.db.AppDatabase;
import com.alberto.gesresfamilyapp.domain.Centro;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewCentroActivity extends AppCompatActivity {

    public List<Centro> centros;
    private MapView mapView;
    private ImageView imageView;
    TextInputLayout telefono = findViewById(R.id.tilTelefono);
    private PointAnnotationManager pointAnnotationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_centro);
        imageView = findViewById(R.id.ivCentro);
        //mapView = findViewById(R.id.mvCentro);
        initializePointManager();

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

    //Usando la libreria picasso
    private void loadImage(String photoUriString) {
        if (photoUriString != null) {
            Uri photoUri = Uri.parse(photoUriString);
            Glide.with(this)
                    .load(photoUri)
                    .into(imageView);
        } else {
            Glide.with(this)
                    .load(R.drawable.icons8_city_buildings_100)
                    .into(imageView);
        }
    }

    private void addCenterToMap(Centro centro) {

        Point point = Point.fromLngLat(centro.getLongitude(), centro.getLatitude());
        addMarker(point, centro.getNombre()); //le pasamos el metodo que crea el marker y ponemos el point y nombre del centro       }
        setCameraPosition(Point.fromLngLat(centro.getLongitude(), centro.getLatitude())); //Fijamos la camara del mapa en el ultimo centro

    }

    /**
     * Inicializamos el pointmanager
     */
    private void initializePointManager() {
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        AnnotationConfig annotationConfig = new AnnotationConfig();
        pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, annotationConfig);

    }

    /**
     * Para poder crear un marker y que lo pinte por cada centro
     *
     * @param point  Pasamos el point
     * @param nombre el nombre del centro
     */
    private void addMarker(Point point, String nombre) {
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                .withPoint(point)
                .withTextField(nombre) //asi aparece el nombre en el mapa
                .withIconImage(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_centro_foreground));
        pointAnnotationManager.create(pointAnnotationOptions);
    }

    /**
     * Fija la camara del mapa donde nosotros queramos, asi el mapa arranca desde ese punto
     *
     *
     */
    private void setCameraPosition(Point point) {
        CameraOptions cameraPosition = new CameraOptions.Builder()
                .center(point)
                .pitch(0.0)
                .zoom(13.5)
                .bearing(-17.6)
                .build();
        mapView.getMapboxMap().setCamera(cameraPosition);

    }
    private void fillData(Centro centro) {

        TextInputLayout name = findViewById(R.id.tilNombre);
        TextInputLayout direccion = findViewById(R.id.tilDireccion);
        TextInputLayout registro = findViewById(R.id.tilNumRegistro);
        TextInputLayout telefono = findViewById(R.id.tilTelefono);
        TextInputLayout mail = findViewById(R.id.tilEmail);
        // Obtener el valor de tieneWifi del objeto centro
        boolean tieneWifi = centro.getTieneWifi();
        // Actualizar el estado del TextView tvWifi
        TextInputLayout tvWifi = findViewById(R.id.tilWifi);
        if (tieneWifi) {
            tvWifi.getEditText().setText("Tiene Wi-Fi");
        } else {
            tvWifi.getEditText().setText("No tiene Wi-Fi");
        }

        // Cargar la imagen
        loadImage(centro.getPhotoUri());

        name.getEditText().setText(centro.getNombre());
        direccion.getEditText().setText(centro.getDireccion());
        registro.getEditText().setText(centro.getNumRegistro());
        telefono.getEditText().setText(centro.getTelefono());
        mail.getEditText().setText(centro.getEmail());
        addCenterToMap(centro);


        // Agrega el OnClickListener al ibTelefon
        telefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = telefono.getEditText().getText().toString();
                dialPhoneNumber(phoneNumber);
            }
        });

    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

            startActivity(intent);

    }
    public void cancel(View view) {
        onBackPressed();
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