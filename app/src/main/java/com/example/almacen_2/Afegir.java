package com.example.almacen_2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Afegir extends AppCompatActivity implements View.OnClickListener, ChildEventListener, ValueEventListener {

    EditText edNom;
    Spinner edCategoria;
    EditText edCajas;
    EditText edUnidades;
    EditText edCantidad;
    EditText edCodi;
    EditText edPrecio;

    Button btnGuardar;
    Button btnCancelar;

    String categoriaSelect="";

    DatabaseReference dbTienda;
    DatabaseReference dbCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afegir);

        dbTienda = FirebaseDatabase.getInstance().getReference().child("productos");
        dbTienda.addValueEventListener(this);
        dbTienda.addChildEventListener(this);

        dbCategorias = FirebaseDatabase.getInstance().getReference().child("categorias");
        dbCategorias.addChildEventListener(this);
        dbCategorias.addValueEventListener(this);

        edNom = findViewById(R.id.etProducte);
        edCategoria = findViewById(R.id.spinCategory);
        edCajas = findViewById(R.id.etCajas);
        edUnidades = findViewById(R.id.etUnidades);
        edCantidad = findViewById(R.id.etUC);
        edCodi = findViewById(R.id.etBarres);
        edPrecio = findViewById(R.id.etPreu);

        btnGuardar = findViewById(R.id.btnSave);
        btnCancelar = findViewById(R.id.btnCancel);

        btnGuardar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);

        loadCaregoria();
    }


    public void loadCaregoria(){
        final List<Categoria> categorias = new ArrayList<>();
        dbCategorias.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String nombre = ds.child("nom").getValue().toString();
                    categorias.add(new Categoria(nombre));
                }
                ArrayAdapter<Categoria> arrayAdapter = new ArrayAdapter<>(Afegir.this, android.R.layout.simple_dropdown_item_1line, categorias);
                edCategoria.setAdapter(arrayAdapter);
                edCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        categoriaSelect = adapterView.getItemAtPosition(i).toString();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:
                Producto producto = new Producto();
                producto.setNom(edNom.getText().toString());
                producto.setCategoria(categoriaSelect);
                producto.setCodi("");
                producto.setCajas(Integer.parseInt(edCajas.getText().toString()));
                producto.setUnidades(Integer.parseInt(edUnidades.getText().toString()));
                producto.setCantidad(Integer.parseInt(edCantidad.getText().toString()));
                producto.setPrecio(Double.parseDouble(edPrecio.getText().toString()));
                dbTienda.child(edNom.getText().toString()).setValue(producto);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            case R.id.btnCancel:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                finish();
            default:
        }
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}