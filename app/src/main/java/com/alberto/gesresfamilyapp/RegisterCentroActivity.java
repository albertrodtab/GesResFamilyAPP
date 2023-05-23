package com.alberto.gesresfamilyapp;

import static com.alberto.gesresfamilyapp.db.Constants.DATABASE_NAME;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.room.Room;

import com.alberto.gesresfamilyapp.db.AppDatabase;
import com.alberto.gesresfamilyapp.domain.Centro;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.GesturesUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterCentroActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private boolean isModifyCentro;
    private AppDatabase db;

    private TextInputLayout tilNombre;
    private TextInputEditText etNombre;
    private TextInputLayout tilDireccion;
    private EditText etDireccion;
    private TextInputLayout tilNumRegistro;
    private EditText etNumRegistro;
    private TextInputLayout tilTelefono;
    private EditText etTelefono;
    private TextInputLayout tilEmail;
    private EditText etMail;
    private CheckBox cbWifi;
    private ImageView imageView;
    private MapView mapView;

    private Centro centro;

    private ActivityResultLauncher<Intent> photoPickerLauncher;

    private Point point; //Guardamos el point para gestionar la latitud y longuitud
    private PointAnnotationManager pointAnnotationManager; //Para anotar el point así es común para todos



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_centro);
        mapView = findViewById(R.id.mvCentro);

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


        // Define las coordenadas por defecto
        double defaultLatitude = -8.105759; // Latitud por defecto
        double defaultLongitude = 42.644695; // Longitud por defecto

        setCameraPosition(Point.fromLngLat(defaultLatitude, defaultLongitude)); //Fijamos la camara del mapa en el ultimo centro

        GesturesPlugin gesturesPlugin = GesturesUtils.getGestures(mapView);
        gesturesPlugin.addOnMapClickListener(point -> { //Cuando hacemos click en el mapa devolvemos un point
            removeAllMarkers(); //Método creado para borrar los anteriores antes de seleccionar alguna para no tener problemas con los point
            this.point = point; //Ese point lo guardamos para tener la longuitud y latitude
            addMarker(point);
            return true;
        });

        initializePointManager();// Para que se cree nada más arrancar



        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri selectedPhotoUri = data.getData();
                            String photoUriString = selectedPhotoUri.toString();

                            // Guardar la URI de la foto en el centro
                            centro.setPhotoUri(photoUriString);

                            // Cargar y mostrar la foto en el ImageView
                            loadImage(photoUriString);

                            Snackbar.make(imageView, R.string.fotoSeleccionada, BaseTransientBottomBar.LENGTH_LONG).show();
                        } else {
                            // Foto capturada con la cámara
                            Uri photoUri = Uri.fromFile(createTempImageFile());
                            String photoUriString = photoUri.toString();
                            centro.setPhotoUri(photoUriString);
                            loadImage(photoUriString);
                            Snackbar.make(imageView, R.string.fotoCapturada, BaseTransientBottomBar.LENGTH_LONG).show();
                        }
                    }
                }
        );

        tilNombre = findViewById(R.id.tilNombre);
        tilDireccion = findViewById(R.id.tilDireccion);
        tilNumRegistro = findViewById(R.id.tilNumRegistro);
        tilTelefono = findViewById(R.id.tilTelefono);
        tilEmail = findViewById(R.id.tilEmail);
        etNombre = findViewById(R.id.etNombre);
        etDireccion = findViewById(R.id.etDireccion);
        etNumRegistro = findViewById(R.id.etNumRegistro);
        etTelefono = findViewById(R.id.etTelefono);
        etMail = findViewById(R.id.etEmail);
        cbWifi = findViewById(R.id.cbWifi);
        imageView = findViewById(R.id.ivCentroReg);

        db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries().build();

        Intent intent = getIntent();
        long centroId = intent.getLongExtra("id", -1);
        isModifyCentro = intent.getBooleanExtra("modify_centro", false);

        if (isModifyCentro) {
            if (centroId != -1) {
                centro = db.centroDao().getById(centroId);
                if (centro != null) {
                    fillData(centro);
                    loadImage(centro.getPhotoUri());
                }
            }
        } else {
            centro = new Centro();

        }
    }

    private void setCameraPosition(Point point) {
        CameraOptions cameraPosition = new CameraOptions.Builder()
                .center(point)
                .pitch(0.0)
                .zoom(6.2)
                .bearing(-17.6)
                .build();

        mapView.getMapboxMap().setCamera(cameraPosition);
    }

    private void fillData(Centro centro) {
        //etNombre.setText(centro.getNombre());
        //Para usarlo con Material
        tilNombre.getEditText().setText(centro.getNombre());
        tilDireccion.getEditText().setText(centro.getDireccion());
        tilNumRegistro.getEditText().setText(centro.getNumRegistro());
        tilTelefono.getEditText().setText(centro.getTelefono());
        tilEmail.getEditText().setText(centro.getEmail());
        //etDireccion.setText(centro.getDireccion());
        //etMail.setText(centro.getEmail());
        //etNumRegistro.setText(centro.getNumRegistro());
        //etTelefono.setText(centro.getTelefono());
        //cbWifi.setChecked(centro.getTieneWifi());
    }

    /*//usando la libreria Glide
    private void loadImage(String photoUriString) {
        if (photoUriString != null) {
            Uri photoUri = Uri.parse(photoUriString);
            Glide.with(this)
                    .load(photoUri)
                    .into(imageView);
        } else {
            Glide.with(this)
                    .load(R.drawable.vector_sector_sanidad)
                    .into(imageView);
        }
    }*/
    //Usando la libreria picasso
    private void loadImage(String photoUriString) {
        if (photoUriString != null) {
            Uri photoUri = Uri.parse(photoUriString);
            Picasso.get()
                    .load(photoUri)
                    .into(imageView);
        } else {
            Picasso.get()
                    .load(R.drawable.icons8_city_buildings_100)
                    .into(imageView);
        }
    }

    public void selectPhoto(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Crea un archivo temporal para guardar la foto capturada por la cámara
        File photoFile = createTempImageFile();

        if (photoFile != null) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }

        Intent chooserIntent = Intent.createChooser(galleryIntent, "Seleccionar foto");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

        //startActivityForResult(chooserIntent, REQUEST_SELECT_PHOTO);
        photoPickerLauncher.launch(chooserIntent);
    }

    private File createTempImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        try {
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void registerCentro(View view) {
        //String nombre = etNombre.getText().toString();
        //Para usarlo con Material Design
        String nombre = tilNombre.getEditText().getText().toString();
        String direccion = tilDireccion.getEditText().getText().toString();
        String numRegistro = tilNumRegistro.getEditText().getText().toString();
        String telefono = tilTelefono.getEditText().getText().toString();
        String mail = tilEmail.getEditText().getText().toString();
        //String direccion = etDireccion.getText().toString();
        //String numRegistro = etNumRegistro.getText().toString();
        //String telefono = etTelefono.getText().toString();
        //String mail = etMail.getText().toString();
        boolean tieneWifi = cbWifi.isChecked();

        //If por si acaso el point no está creado, el usuario no ha selecionado nada en el mapa, asi no da error al crear la tarea porque falte latitude y longuitude
        if (point == null) {
            Toast.makeText(this, R.string.IndicaLaPosicionDelCentroEnElMapa, Toast.LENGTH_LONG).show();
//            Snackbar.make(tilNambre, R.string.select_location_message, BaseTransientBottomBar.LENGTH_LONG); //tilNombre porque el Snackbar hay que asociarlo algún componente del layout
            return;
        }



        if (isModifyCentro) {
            centro.setNombre(nombre);
            centro.setDireccion(direccion);
            centro.setNumRegistro(numRegistro);
            centro.setTelefono(telefono);
            centro.setEmail(mail);
            centro.setTieneWifi(tieneWifi);
            centro.setLatitude(point.latitude());
            centro.setLongitude(point.longitude());

            db.centroDao().update(centro);
            Toast.makeText(this, R.string.centroModificado, Toast.LENGTH_LONG).show();
        } else {
            centro.setNombre(nombre);
            centro.setDireccion(direccion);
            centro.setNumRegistro(numRegistro);
            centro.setTelefono(telefono);
            centro.setEmail(mail);
            centro.setTieneWifi(tieneWifi);
            centro.setLatitude(point.latitude());
            centro.setLongitude(point.longitude());

            db.centroDao().insert(centro);
            Toast.makeText(this, R.string.centroRegistado, Toast.LENGTH_LONG).show();
        }

        etNombre.setText("");
        etDireccion.setText("");
        etMail.setText("");
        etNumRegistro.setText("");
        etTelefono.setText("");
        cbWifi.setChecked(false);
        etNombre.requestFocus();
    }

    /**
     * Para inicializar el Pointmanager y asi la podemos dejar inicializada nada más arrancar en onCreate
     */
    private void initializePointManager() {
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        AnnotationConfig annotationConfig = new AnnotationConfig();
        pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, annotationConfig);
    }

    /**
     * Método para añadir un Marker sobre un mapa
     * @param point le pasamos el point con los datos de latitude y longuitude
     * @param "String" le podemos pasar un titulo para que aparezca en el mapa
     */
    private void addMarker(Point point) {
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_centro_foreground)); //le pasamos el dibujo que queremos que pinte como icono, los podemos crea webinar 4 min 54
        pointAnnotationManager.create(pointAnnotationOptions);
    }

    /**
     * Para borrar el marker anterior y no aparezcan todos en el mapa
     */
    private void removeAllMarkers() {
        pointAnnotationManager.deleteAll(); // Se Podría borra uno en concreto pasandole el point exacto
    }


//Con atctivityResultLauncher ya no hace falta.
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_PHOTO && resultCode == RESULT_OK) {
            if (data != null) {
                // Foto seleccionada desde la galería
                Uri selectedPhotoUri = data.getData();
                String photoUriString = selectedPhotoUri.toString();
                centro.setPhotoUri(photoUriString);
                loadImage(photoUriString);
                Snackbar.make(imageView, "Foto seleccionada", BaseTransientBottomBar.LENGTH_LONG).show();
            } else {
                // Foto capturada con la cámara
                Uri photoUri = Uri.fromFile(createTempImageFile());
                String photoUriString = photoUri.toString();
                centro.setPhotoUri(photoUriString);
                loadImage(photoUriString);
                Snackbar.make(imageView, "Foto capturada", BaseTransientBottomBar.LENGTH_LONG).show();
            }
        }
    }*/


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
            Uri selectedPhotoUri = data.getData();
            String photoUriString = selectedPhotoUri.toString();

            // Guardar la URI de la foto en el centro
            centro.setPhotoUri(photoUriString);

            // Cargar y mostrar la foto en el ImageView
            loadImage(photoUriString);

            Snackbar.make(imageView, "Foto seleccionada", BaseTransientBottomBar.LENGTH_LONG).show();
        }
    }*/


    public void cancel(View view) {
        onBackPressed();
    }
}

