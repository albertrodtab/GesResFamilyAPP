package com.alberto.gesresfamilyapp;

import static com.alberto.gesresfamilyapp.db.Constants.DATABASE_NAME;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.room.Room;

import com.alberto.gesresfamilyapp.db.AppDatabase;
import com.alberto.gesresfamilyapp.domain.Profesional;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterProfesionalActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_PHOTO = 1;

    private boolean isModifyProfesional;
    private AppDatabase db;
    private EditText etNombre;
    private EditText etApellidos;
    private EditText etDni;
    private EditText etFechaNac;
    private EditText etCategoria;
    private ImageView imageView;

    private Profesional profesional;

    private ActivityResultLauncher<Intent> photoPickerLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_profesional);


        photoPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri selectedPhotoUri = data.getData();
                            String photoUriString = selectedPhotoUri.toString();

                            // Guardar la URI de la foto en el centro
                            profesional.setPhotoUri(photoUriString);

                            // Cargar y mostrar la foto en el ImageView
                            loadImage(photoUriString);

                            Snackbar.make(imageView, "Foto seleccionada", BaseTransientBottomBar.LENGTH_LONG).show();
                        } else {
                            // Foto capturada con la cámara
                            Uri photoUri = Uri.fromFile(createTempImageFile());
                            String photoUriString = photoUri.toString();
                            profesional.setPhotoUri(photoUriString);
                            loadImage(photoUriString);
                            Snackbar.make(imageView, "Foto capturada", BaseTransientBottomBar.LENGTH_LONG).show();
                        }
                    }
                }
        );

        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etDni = findViewById(R.id.etDni);
        etFechaNac = findViewById(R.id.etFechaNac);
        etCategoria = findViewById(R.id.etSexo);
        imageView = findViewById(R.id.ivResidenteReg);

        db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries().build();

        Intent intent = getIntent();
        long profesionalId = intent.getLongExtra("id", -1);
        isModifyProfesional = intent.getBooleanExtra("modify_profesional", false);

        if (isModifyProfesional) {
            if (profesionalId != -1) {
                profesional = db.profesionalDao().getById(profesionalId);
                if (profesional != null) {
                    fillData(profesional);
                    loadImage(profesional.getPhotoUri());
                }
            }
        } else {
            profesional = new Profesional();

        }
    }


    private void fillData(Profesional profesional) {
        etNombre.setText(profesional.getNombre());
        etApellidos.setText(profesional.getApellidos());
        etCategoria.setText(profesional.getCategoria());
        etDni.setText(profesional.getDni());
        if (profesional.getFechaNacimiento() != null) {
            etFechaNac.setText(profesional.getFechaNacimiento().toString());
        } else {
            etFechaNac.setText(""); // or provide a default value or handle the case when fechaNacimiento is null
        }
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

    public void registerProfesional(View view) {
        String nombre = etNombre.getText().toString();
        String apellidos = etApellidos.getText().toString();
        String dni = etDni.getText().toString();
        Editable editableFechaNac = etFechaNac.getText();
        String fechaNacString = editableFechaNac.toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaNac = null;
        try {
            fechaNac = dateFormat.parse(fechaNacString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String categoria = etCategoria.getText().toString();



        if (isModifyProfesional) {
            profesional.setNombre(nombre);
            profesional.setApellidos(apellidos);
            profesional.setDni(dni);
            profesional.setFechaNacimiento(fechaNac);
            profesional.setCategoria(categoria);
            db.profesionalDao().update(profesional);
            Toast.makeText(this, "Profesional modificado", Toast.LENGTH_LONG).show();
        } else {
            profesional.setNombre(nombre);
            profesional.setApellidos(apellidos);
            profesional.setDni(dni);
            profesional.setFechaNacimiento(fechaNac);
            profesional.setCategoria(categoria);

            db.profesionalDao().insert(profesional);
            Toast.makeText(this, "Profesional registrado", Toast.LENGTH_LONG).show();
        }

        etNombre.setText("");
        etApellidos.setText("");
        etCategoria.setText("");
        etDni.setText("");
        etFechaNac.setText("");
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

