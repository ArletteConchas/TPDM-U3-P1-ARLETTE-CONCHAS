package com.example.anyme.tpdm_u3_p2_arletteconchas;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    EditText no_control, nombre, semestre, carrera, telefono;
    Button insertar, actualizar, borrar;
    DatabaseReference base;
    ListView lista;
    List<Map> alumnos;
    int alumno_actual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        no_control=findViewById(R.id.no_control);
        nombre=findViewById(R.id.nombre);
        semestre=findViewById(R.id.semestre);
        carrera=findViewById(R.id.carrera);
        telefono=findViewById(R.id.telefono);
        insertar=findViewById(R.id.insertar);
        actualizar=findViewById(R.id.actualizar);
        borrar=findViewById(R.id.borrar);
        lista=findViewById(R.id.lista);

        base=FirebaseDatabase.getInstance().getReference();

        base.child("alumno").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()<=0) {
                    Toast.makeText(Main2Activity.this, "No hay datos a mostrar", Toast.LENGTH_LONG).show();
                    return;
                }
                alumnos = new ArrayList<>();
                Log.e("DATOS", dataSnapshot.getValue().toString());
                for(final DataSnapshot dato : dataSnapshot.getChildren()){
                    base.child("alumno").child(dato.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Alumno alumno = dataSnapshot.getValue(Alumno.class);
                            if(alumno!=null){
                                Map<String, Object> obj = new HashMap<>();
                                obj.put("no_control", dato.getKey());
                                obj.put("carrera", alumno.getCarrera());
                                obj.put("nombre", alumno.getNombre());
                                obj.put("semestre", alumno.getSemestre());
                                obj.put("telefono", alumno.getTelefono());
                                alumnos.add(obj);
                                cargarLista();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

    });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (alumnos.size() < 1) {
                    return;
                }
                alumno_actual=position;
                no_control.setText(alumnos.get(position).get("no_control").toString());
                nombre.setText(alumnos.get(position).get("nombre").toString());
                semestre.setText(alumnos.get(position).get("semestre").toString());
                carrera.setText(alumnos.get(position).get("carrera").toString());
                telefono.setText(alumnos.get(position).get("telefono").toString());
                no_control.setEnabled(false);
                actualizar.setEnabled(true);
                borrar.setEnabled(true);
                insertar.setEnabled(false);
            }
        });

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validado()) {
                    base.child("alumno").child(no_control.getText().toString()).setValue(obtenerAlumno());
                    Toast.makeText(Main2Activity.this, "Se agregó correctamente", Toast.LENGTH_LONG).show();
                    limpiarCampos();
                }
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mensaje = new AlertDialog.Builder(Main2Activity.this);
                mensaje.setTitle("Advertencia").setMessage("¿Seguro que deseas actualizar el alumno?").setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (validado()) {
                            base.child("alumno").child(alumnos.get(alumno_actual).get("no_control").toString()).setValue(obtenerAlumno());
                            Toast.makeText(Main2Activity.this, "Se modificó el alumno", Toast.LENGTH_LONG).show();
                            limpiarCampos();
                            dialog.dismiss();
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mensaje = new AlertDialog.Builder(Main2Activity.this);
                mensaje.setTitle("Advertencia").setMessage("¿Seguro que deseas borrar el alumno?").setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        base.child("alumno").child(alumnos.get(alumno_actual).get("no_control").toString()).removeValue();
                        Toast.makeText(Main2Activity.this, "Se eliminó el alumno", Toast.LENGTH_LONG).show();
                        limpiarCampos();
                        dialog.dismiss();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
}
    void cargarLista(){
        String[] vector = new String[alumnos.size()];
        for(int i=0;i<vector.length;i++){
            Map<String,Object> obj = new HashMap<>();
            obj = alumnos.get(i);
            vector[i]=obj.get("nombre").toString();
        }
        ArrayAdapter<String> arr = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,vector);
        lista.setAdapter(arr);
    }


    private Map<String, Object> obtenerAlumno() {
        Map<String, Object> alumno = new HashMap<>();

        alumno.put("nombre", nombre.getText().toString());
        alumno.put("semestre", semestre.getText().toString());
        alumno.put("carrera", carrera.getText().toString());
        alumno.put("telefono", telefono.getText().toString());

        return alumno;
    }

    private boolean validado () {
        if (nombre.getText().toString().equals("")) {
            Toast.makeText(Main2Activity.this, "Escribe un nombre para el alumno", Toast.LENGTH_LONG).show();
            return false;
        }
        if (semestre.getText().toString().equals("")) {
            Toast.makeText(Main2Activity.this, "Escribe un semestre para el alumno", Toast.LENGTH_LONG).show();
            return false;
        }
        if (carrera.getText().toString().equals("")) {
            Toast.makeText(Main2Activity.this, "Escribe una carrera para el alumno", Toast.LENGTH_LONG).show();
            return false;
        }
        if (telefono.getText().toString().equals("")) {
            Toast.makeText(Main2Activity.this, "Escribe un telefono para el alumno", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void limpiarCampos () {
        no_control.setText("");
        nombre.setText("");
        semestre.setText("");
        carrera.setText("");
        telefono.setText("");
        no_control.setEnabled(true);
        actualizar.setEnabled(false);
        borrar.setEnabled(false);
        insertar.setEnabled(true);

    }

}
