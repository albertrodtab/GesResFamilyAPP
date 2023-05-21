package com.alberto.gesresfamilyapp;

import static com.alberto.gesresfamilyapp.db.Constants.DATABASE_NAME;

import android.content.Intent;
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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.maps.MapView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterCentroActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_PHOTO = 1;

    private boolean isModifyCentro;
    private AppDatabase db;
    private EditText etNombre;
    private EditText etDireccion;
    private EditText etNumRegistro;
    private EditText etTelefono;
    private EditText etMail;
    private CheckBox cbWifi;
    private ImageView imageView;
    private MapView mapView;

    private Centro centro;

    private ActivityResultLauncher<Intent> photoPickerLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_centro);
        mapView = findViewById(R.id.mvCentro);



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
                }
        );

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


    private void fillData(Centro centro) {
        etNombre.setText(centro.getNombre());
        etDireccion.setText(centro.getDireccion());
        etMail.setText(centro.getEmail());
        etNumRegistro.setText(centro.getNumRegistro());
        etTelefono.setText(centro.getTelefono());
        cbWifi.setChecked(centro.getTieneWifi());
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
        String nombre = etNombre.getText().toString();
        String direccion = etDireccion.getText().toString();
        String numRegistro = etNumRegistro.getText().toString();
        String telefono = etTelefono.getText().toString();
        String mail = etMail.getText().toString();
        boolean tieneWifi = cbWifi.isChecked();



        if (isModifyCentro) {
            centro.setNombre(nombre);
            centro.setDireccion(direccion);
            centro.setNumRegistro(numRegistro);
            centro.setTelefono(telefono);
            centro.setEmail(mail);
            centro.setTieneWifi(tieneWifi);

            db.centroDao().update(centro);
            Toast.makeText(this, "Centro modificado", Toast.LENGTH_LONG).show();
        } else {
            centro.setNombre(nombre);
            centro.setDireccion(direccion);
            centro.setNumRegistro(numRegistro);
            centro.setTelefono(telefono);
            centro.setEmail(mail);
            centro.setTieneWifi(tieneWifi);
            db.centroDao().insert(centro);
            Toast.makeText(this, "Centro registrado", Toast.LENGTH_LONG).show();
        }

        etNombre.setText("");
        etDireccion.setText("");
        etMail.setText("");
        etNumRegistro.setText("");
        etTelefono.setText("");
        cbWifi.setChecked(false);
        etNombre.requestFocus();
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

