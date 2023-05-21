package com.alberto.gesresfamilyapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.alberto.gesresfamilyapp.R;
import com.alberto.gesresfamilyapp.RegisterProfesionalActivity;
import com.alberto.gesresfamilyapp.db.AppDatabase;
import com.alberto.gesresfamilyapp.domain.Profesional;
import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.List;

public class ProfesionalAdapter extends RecyclerView.Adapter<ProfesionalAdapter.ProfesionalHolder>{

    private List<Profesional> profesionalList;
    //esto sirve para guardar la posición para luego poder hacer cosas con ellos.
    private Context context;

    private int selectedPosition;

    public ProfesionalAdapter(Context context, List<Profesional> dataList) {
        this.context = context;
        this.profesionalList = dataList;

        //esto indica que no hay ninguno seleccionado
        selectedPosition = -1;
    }

    //Patron Holder (ESTO
    // ESTOY OBLIGADO A HACERLO SIEMPRE)
    //metodo que crea cada estructura de layout donde iran los datos de cada cnetro.
    @Override
    public ProfesionalHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_view_profesional, parent, false);
        return new ProfesionalHolder(view);
    }

    //hace corresponder cada elemento de la lista para decir como pintarlo en cada elemento del layout
    @Override
    public void onBindViewHolder(ProfesionalHolder holder, int position){
        holder.profesionalNombre.setText(profesionalList.get(position).getNombre());
        holder.profesionalApellidos.setText(profesionalList.get(position).getApellidos());
        holder.profesionalDni.setText(profesionalList.get(position).getDni());
        //Date fechaNacimiento = profesionalList.get(position).getFechaNacimiento();
        //String fechaNacimientoString = "";
        //if (fechaNacimiento != null) {
        //    fechaNacimientoString = fechaNacimiento.toString(); // Convertir Date a String
        //}
        //holder.profesionalFechaNac.setText(fechaNacimientoString);
        holder.profesionalCategoria.setText(profesionalList.get(position).getCategoria());

        Profesional profesional = profesionalList.get(position);

        // Cargar y mostrar la foto en el ImageView
        String photoUriString = profesional.getPhotoUri();
        if (photoUriString != null) {
            Uri photoUri = Uri.parse(photoUriString);
            Glide.with(context)
                    .load(photoUri)
                    .into(holder.profesionalImagen);
        } else {
            // Mostrar una imagen de placeholder si no hay foto disponible
            Glide.with(context)
                    .load(R.drawable.icons8_city_buildings_100)
                    .into(holder.profesionalImagen);
        }
    }

    @Override
    public int getItemCount() {
        return profesionalList.size();
    }

    public class ProfesionalHolder extends RecyclerView.ViewHolder{
        public TextView profesionalNombre;
        public TextView profesionalApellidos;
        public TextView profesionalDni;
        public TextView profesionalCategoria;
        //public TextView profesionalFechaNac;
        public ImageView profesionalImagen;

        //public CheckBox taskDone;
        //public Button doTaskButton;
        //public Button seeDetailsButton;
        public Button btDelete;
        public View parentView;
        public Button btMod;

        public ProfesionalHolder(View view) {
            super(view);
            parentView = view;

            profesionalNombre = view.findViewById(R.id.tvNombre);
            profesionalApellidos = view.findViewById(R.id.tvApellidos);
            profesionalDni = view.findViewById(R.id.tvDni);
            //profesionalFechaNac = view.findViewById(R.id.tvProfesionalFechaNac);
            profesionalCategoria = view.findViewById(R.id.tvCategoria);
            profesionalImagen = view.findViewById(R.id.ivProfesional);


            btDelete = view.findViewById(R.id.btDelete);

            btMod = view.findViewById(R.id.btMod);

//            doTaskButton = view.findViewById(R.id.do_task_button);




            //Programar boton ver detalles de la tarea
            //doTaskButton.setOnClickListener(v -> doTask(getAdapterPosition()));

            //Programar boton marcar tarea como hecha.
            //seeDetailsButton.setOnClickListener(v -> seeDetails(getAdapterPosition()));

            //click on button (remove task from de list).
            btDelete.setOnClickListener(v -> deleteProfesional(getAdapterPosition()));

            btMod.setOnClickListener(v-> modifyProfesional(getAdapterPosition()));
        }

/*        private void doTask(int position){
            Car car = carList.get(position);
            car.setDone(true);

            final AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "tasks")
                    .allowMainThreadQueries().build();
            db.taskDao().update(car);

            notifyItemChanged(position);

        }*/

   /*     private void seeDetails(int position){
            Centro centro = centroList.get(position);

            Intent intent = new Intent(context, ViewCentroActivity.class);
            intent.putExtra("name", centro.getNombre());
            context.startActivity(intent);


        }*/

        private void deleteProfesional(int position){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Estas seguro de Borrar el Profesional")
                    .setTitle("Confirmar Borrado")
                    .setPositiveButton("Si", (dialog, id) -> {
                        final AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "Gesresfamily")
                                .allowMainThreadQueries().build();
                        Profesional profesional = profesionalList.get(position);
                        db.profesionalDao().delete(profesional);

                        profesionalList.remove(position);
                        notifyItemRemoved(position);
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void modifyProfesional(int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("¿Deseas modificar el profesional?")
                    .setTitle("Confirmar Modificación")
                    .setPositiveButton("Sí", (dialog, id) -> {
                        Profesional profesional = profesionalList.get(position);

                        Intent intent = new Intent(context, RegisterProfesionalActivity.class);
                        intent.putExtra("modify_profesional", true);
                        intent.putExtra("id", profesional.getId());
                        intent.putExtra("nombre", profesional.getNombre());
                        intent.putExtra("apellidos", profesional.getApellidos());
                        intent.putExtra("dni", profesional.getDni());
                        //intent.putExtra("fechaNac", profesional.getFechaNacimiento());
                        intent.putExtra("categoria", profesional.getCategoria());

                        context.startActivity(intent);
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }
}
