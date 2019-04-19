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

public class Main3Activity extends AppCompatActivity {

    EditText rfc, nombre, departamento, direccion, telefono;
    Button insertar, actualizar, borrar;
    DatabaseReference base;
    ListView lista;
    List<Map> maestros;
    int maestro_actual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        rfc=findViewById(R.id.rfc);
        nombre=findViewById(R.id.nombre2);
        departamento=findViewById(R.id.departamento);
        direccion=findViewById(R.id.direccion);
        telefono=findViewById(R.id.telefono2);
        insertar=findViewById(R.id.insertar2);
        actualizar=findViewById(R.id.actualizar2);
        borrar=findViewById(R.id.borrar2);
        lista=findViewById(R.id.lista2);

        base=FirebaseDatabase.getInstance().getReference();

        base.child("maestro").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()<=0) {
                    Toast.makeText(Main3Activity.this, "No hay datos a mostrar", Toast.LENGTH_LONG).show();
                    return;
                }
                maestros = new ArrayList<>();
                Log.e("DATOS", dataSnapshot.getValue().toString());
                for(final DataSnapshot dato : dataSnapshot.getChildren()){
                    base.child("maestro").child(dato.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Maestro maestro = dataSnapshot.getValue(Maestro.class);
                            if(maestro!=null){
                                Map<String, Object> obj = new HashMap<>();
                                obj.put("rfc", dato.getKey());
                                obj.put("nombre", maestro.getNombre());
                                obj.put("departamento", maestro.getDepartamento());
                                obj.put("telefono", maestro.getTelefono());
                                obj.put("direccion", maestro.getDireccion());
                                maestros.add(obj);
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
                if (maestros.size() < 1) {
                    return;
                }
                maestro_actual=position;
                rfc.setText(maestros.get(position).get("rfc").toString());
                nombre.setText(maestros.get(position).get("nombre").toString());
                departamento.setText(maestros.get(position).get("departamento").toString());
                direccion.setText(maestros.get(position).get("direccion").toString());
                telefono.setText(maestros.get(position).get("telefono").toString());
                rfc.setEnabled(false);
                actualizar.setEnabled(true);
                borrar.setEnabled(true);
                insertar.setEnabled(false);
            }
        });

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validado()) {
                    base.child("maestro").child(rfc.getText().toString()).setValue(obtenerMaestro());
                    Toast.makeText(Main3Activity.this, "Se agregó correctamente", Toast.LENGTH_LONG).show();
                    limpiarCampos();
                }
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mensaje = new AlertDialog.Builder(Main3Activity.this);
                mensaje.setTitle("Advertencia").setMessage("¿Seguro que deseas actualizar el maestro?").setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (validado()) {
                            base.child("maestro").child(maestros.get(maestro_actual).get("rfc").toString()).setValue(obtenerMaestro());
                            Toast.makeText(Main3Activity.this, "Se modificó el maestro", Toast.LENGTH_LONG).show();
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
                AlertDialog.Builder mensaje = new AlertDialog.Builder(Main3Activity.this);
                mensaje.setTitle("Advertencia").setMessage("¿Seguro que deseas borrar el maestro?").setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        base.child("maestro").child(maestros.get(maestro_actual).get("rfc").toString()).removeValue();
                        Toast.makeText(Main3Activity.this, "Se eliminó el maestro", Toast.LENGTH_LONG).show();
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
        String[] vector = new String[maestros.size()];
        for(int i=0;i<vector.length;i++){
            Map<String,Object> obj = new HashMap<>();
            obj = maestros.get(i);
            vector[i]=obj.get("nombre").toString();
        }
        ArrayAdapter<String> arr = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,vector);
        lista.setAdapter(arr);
    }


    private Map<String, Object> obtenerMaestro() {
        Map<String, Object> maestro = new HashMap<>();

        maestro.put("nombre", nombre.getText().toString());
        maestro.put("departamento", departamento.getText().toString());
        maestro.put("direccion", direccion.getText().toString());
        maestro.put("telefono", telefono.getText().toString());

        return maestro;
    }

    private boolean validado () {
        if (nombre.getText().toString().equals("")) {
            Toast.makeText(Main3Activity.this, "Escribe un nombre para el maestro", Toast.LENGTH_LONG).show();
            return false;
        }
        if (direccion.getText().toString().equals("")) {
            Toast.makeText(Main3Activity.this, "Escribe una direccion para el maestro", Toast.LENGTH_LONG).show();
            return false;
        }
        if (departamento.getText().toString().equals("")) {
            Toast.makeText(Main3Activity.this, "Escribe un departamento para el maestro", Toast.LENGTH_LONG).show();
            return false;
        }
        if (telefono.getText().toString().equals("")) {
            Toast.makeText(Main3Activity.this, "Escribe un telefono para el maestro", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void limpiarCampos () {
        rfc.setText("");
        nombre.setText("");
        departamento.setText("");
        direccion.setText("");
        telefono.setText("");
        rfc.setEnabled(true);
        actualizar.setEnabled(false);
        borrar.setEnabled(false);
        insertar.setEnabled(true);

    }
}
