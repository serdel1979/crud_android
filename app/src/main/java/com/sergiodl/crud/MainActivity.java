package com.sergiodl.crud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergiodl.crud.MODEL.Persona;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public EditText editNombre, editCorreo;
    public ListView listaPersonas;

    private List<Persona> listaPersona = new ArrayList<Persona>();
    public ArrayAdapter<Persona> arrayAdapter;

    public FirebaseDatabase database;
    public DatabaseReference databaseReference;
    public Persona personaSelec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editNombre = (EditText)findViewById(R.id.editTxt1);
        editCorreo = (EditText)findViewById(R.id.editTxt2);
        listaPersonas = (ListView)findViewById(R.id.lv_datosPersona);
        inicializarFireBase();
        listarDatos();

        listaPersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                      personaSelec = (Persona) adapterView.getItemAtPosition(i);
                      editNombre.setText(personaSelec.getNombre());
                      editCorreo.setText(personaSelec.getCorreo());

            }
        });

    }

    private void listarDatos() {
        databaseReference.child("Personas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPersona.clear();
                int i = 0;
                for(DataSnapshot objSnaptshot: snapshot.getChildren()){
                    Persona p = objSnaptshot.getValue(Persona.class);
                    listaPersona.add(p);

                    arrayAdapter = new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1, listaPersona);
                    listaPersonas.setAdapter(arrayAdapter);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarFireBase() {
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
       // database.setPersistenceEnabled(true);
        databaseReference = database.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String nombre = editNombre.getText().toString();
        String correo = editCorreo.getText().toString();
            switch (item.getItemId()) {
                case R.id.icon_add: {
                    if (nombre.equals("")||correo.equals("")){
                        validar();
                    }else{
                          Persona p = new Persona();
                          p.setId(UUID.randomUUID().toString());
                          p.setCorreo(correo);
                          p.setNombre(nombre);
                          databaseReference.child("Personas").child(p.getId()).setValue(p);
                          limpiarEdits();
                          Toast.makeText(this, "Agregado!!!", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case R.id.icon_save: {
                    if (nombre.equals("")||correo.equals("")){
                        validar();
                    }else {
                        //deberia modificar la persona con id
                        Persona p = new Persona();
                        p.setId(personaSelec.getId());
                        p.setNombre(nombre);
                        p.setCorreo(correo);
                        databaseReference.child("Personas").child(p.getId()).setValue(p);
                        limpiarEdits();
                        Toast.makeText(this, "Actualizado!!!", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case R.id.icon_del: {
                    Persona p = new Persona();
                    p.setId(personaSelec.getId());
                    databaseReference.child("Personas").child(p.getId()).removeValue();
                    limpiarEdits();
                    Toast.makeText(this, "Borrado!!!", Toast.LENGTH_LONG).show();
                    break;
                }
            }
        return super.onOptionsItemSelected(item);
    }

    private void limpiarEdits() {
        editNombre.setText("");
        editCorreo.setText("");
    }

    private void validar() {
        editNombre.setError("Requerido");
        editCorreo.setError("Requerido");
    }
}