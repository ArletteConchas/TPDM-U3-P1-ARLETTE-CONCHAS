package com.example.anyme.tpdm_u3_p2_arletteconchas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button alumnos, maestros;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        maestros=findViewById(R.id.btn_maestros);
        alumnos=findViewById(R.id.btn_alumnos);

        alumnos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent siguiente = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(siguiente);
            }
        });

        maestros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent siguiente = new Intent(MainActivity.this, Main3Activity.class);
                startActivity(siguiente);
            }
        });
    }
}
