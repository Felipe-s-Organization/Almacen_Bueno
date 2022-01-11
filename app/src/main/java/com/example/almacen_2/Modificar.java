package com.example.almacen_2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Modificar extends AppCompatActivity implements View.OnClickListener, ChildEventListener, ValueEventListener {

    TextView tvProducto;
    EditText edCajas;
    EditText edUnidades;
    EditText edPrecio;

    Button btnSumar;
    Button btnSumar2;
    Button btnRestar;
    Button btnRestar2;
    Button btnVacio;
    Button btnGuardar;
    Button btnCancelar;

    DatabaseReference dbTienda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);

        dbTienda = FirebaseDatabase.getInstance().getReference().child("productos");
        dbTienda.addChildEventListener(this);
        dbTienda.addValueEventListener(this);

        String nom = getIntent().getExtras().getString("nom");

        Integer cajas = getIntent().getExtras().getInt("cajas");
        Integer unidades = getIntent().getExtras().getInt("unidades");
        Double precio = getIntent().getExtras().getDouble("precio");
        tvProducto = findViewById(R.id.tvProducto2);
        tvProducto.setText(nom);
        edCajas = findViewById(R.id.edCajas);
        edCajas.setText(cajas.toString());
        edUnidades = findViewById(R.id.edUnidades);
        edUnidades.setText(unidades.toString());
        edPrecio = findViewById(R.id.edPrecio);
        edPrecio.setText(precio.toString());

        btnSumar = findViewById(R.id.btnSumar);
        btnSumar.setOnClickListener(this);
        btnSumar2 = findViewById(R.id.btnSumar2);
        btnSumar2.setOnClickListener(this);
        btnRestar = findViewById(R.id.btnRestar);
        btnRestar.setOnClickListener(this);
        btnRestar2 = findViewById(R.id.btnRestar2);
        btnRestar2.setOnClickListener(this);
        btnVacio = findViewById(R.id.btnVacio);
        btnVacio.setOnClickListener(this);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(this);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Integer cantidad = getIntent().getExtras().getInt("cantidad");
        System.out.print(cantidad);
        Integer uC;
        Integer suma;
        Integer resta;
        switch (v.getId()){
            case R.id.btnSumar:
                String cajas = edCajas.getText().toString();
                uC = Integer.parseInt(edUnidades.getText().toString()) + cantidad;
                suma = Integer.parseInt(cajas) + 1;
                edCajas.setText(suma.toString());
                edUnidades.setText(uC.toString());
                break;
            case R.id.btnRestar:
                String cajas2 = edCajas.getText().toString();
                uC = Integer.parseInt(edUnidades.getText().toString()) - cantidad;
                resta = Integer.parseInt(cajas2) - 1;
                edCajas.setText(resta.toString());
                edUnidades.setText(uC.toString());
                break;
            case R.id.btnSumar2:
                String unidades = edUnidades.getText().toString();
                suma = Integer.parseInt(unidades) + 1;
                uC = suma / cantidad;
                System.out.println(suma);
                edUnidades.setText(suma.toString());
                edCajas.setText(uC.toString());
                break;
            case R.id.btnRestar2:
                String unidades2 = edUnidades.getText().toString();
                resta = Integer.parseInt(unidades2) - 1;
                uC = resta / cantidad;
                edUnidades.setText(resta.toString());
                edCajas.setText(uC.toString());
                break;
            case R.id.btnVacio:
                edUnidades.setText("0");
                edCajas.setText("0");
                break;
            case R.id.btnGuardar:
                String descripcion = getIntent().getExtras().getString("descripcion");
                String categoria = getIntent().getExtras().getString("categoria");

                Producto producto = new Producto();
                producto.setCategoria(categoria);
                producto.setNom(tvProducto.getText().toString());
                producto.setCajas(Integer.parseInt(edCajas.getText().toString()));
                producto.setUnidades(Integer.parseInt(edUnidades.getText().toString()));
                producto.setCantidad(cantidad);
                producto.setPrecio(Double.parseDouble(edPrecio.getText().toString()));
                dbTienda.child(getIntent().getExtras().getString("id")).removeValue();
                dbTienda.child(getIntent().getExtras().getString("id")).setValue(producto);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            case R.id.btnCancelar:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
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