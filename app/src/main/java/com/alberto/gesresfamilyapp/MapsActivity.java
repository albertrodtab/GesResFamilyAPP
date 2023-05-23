package com.alberto.gesresfamilyapp;



import static com.alberto.gesresfamilyapp.db.Constants.DATABASE_NAME;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.alberto.gesresfamilyapp.db.AppDatabase;
import com.alberto.gesresfamilyapp.domain.Centro;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;


import java.util.List;

public class MapsActivity extends AppCompatActivity {

    private MapView mapView;
    private PointAnnotationManager pointAnnotationManager;
    private FloatingActionButton btLotacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapView = findViewById(R.id.mapView); //cargamos el mapa
        initializePointManager(); // inicializamos el pointmanager
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Acción a realizar al hacer clic en el ícono de navegación
                // Por ejemplo, cerrar la actividad o realizar alguna acción específica
                onBackPressed(); // Ejemplo: retroceder a la actividad anterior
            }
        });


        /**
         * buscamos el centro y lo traemos para recuperar su información
         */
        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries().build();
        List<Centro> centros = db.centroDao().getAll(); //Lista de la centros
        addCenterToMap(centros); // se lo pasamos al metodo
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
     * @param point
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

    /**
     * Metodo para sacar una lista de puuentes con un for para crear un point por cada puente con la longitude y latitude mas el nombre del puente
     *
     * @param
     */
    private void addCenterToMap(List<Centro> centros) {
        for (Centro centro : centros) {
            Point point = Point.fromLngLat(centro.getLongitude(), centro.getLatitude());
            addMarker(point, centro.getNombre()); //le pasamos el metodo que crea el marker y ponemos el point y nombre del centro       }

            Centro lastCentro = centros.get(centros.size() - 1); // recogemos el ultimo centro
            setCameraPosition(Point.fromLngLat(lastCentro.getLongitude(), lastCentro.getLatitude())); //Fijamos la camara del mapa en el ultimo centro
        }

        /**
         * Para ubicar al usuario en el mapa
         */
//    private void locationUser() {
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        btLotacion =(FloatingActionButton) findViewById(R.id.btLocation);
//        btLotacion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mapView !=null) {
//                    Location lastLocation = fusedLocationClient.getLastLocation();
//
//                }
//            }
//        });
//    }



    }
}





