package com.example.almacen_2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Modificar extends AppCompatActivity implements View.OnClickListener, ChildEventListener, ValueEventListener {

    EditText tvProducto;
    EditText edCajas;
    EditText edUnidades;
    EditText edPrecio;

    TextView tvFecha;

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

        int cajas = getIntent().getExtras().getInt("cajas");
        int unidades = getIntent().getExtras().getInt("unidades");
        double precio = getIntent().getExtras().getDouble("precio");
        String fecha = getIntent().getExtras().getString("fecha");

        tvProducto = findViewById(R.id.tvProducto2);
        tvProducto.setText(nom);
        edCajas = findViewById(R.id.edCajas);
        edCajas.setText(Integer.toString(cajas));
        edUnidades = findViewById(R.id.edUnidades);
        edUnidades.setText(Integer.toString(unidades));
        edPrecio = findViewById(R.id.edPrecio);
        edPrecio.setText(Double.toString(precio));
        tvFecha = findViewById(R.id.tvFecha);
        tvFecha.setText("Ultima modificacion: "+fecha);

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

    @SuppressLint({"NonConstantResourceId", "SimpleDateFormat"})
    @Override
    public void onClick(View v) {
        int cantidad = getIntent().getExtras().getInt("cantidad");
        String categoria = getIntent().getExtras().getString("categoria");
        int uC;
        Integer suma;
        int resta;
        switch (v.getId()){
            case R.id.btnSumar:
                String cajas = edCajas.getText().toString();
                uC = Integer.parseInt(edUnidades.getText().toString()) + cantidad;
                suma = Integer.parseInt(cajas) + 1;
                edCajas.setText(suma.toString());
                edUnidades.setText(Integer.toString(uC));
                break;
            case R.id.btnRestar:
                String cajas2 = edCajas.getText().toString();
                uC = Integer.parseInt(edUnidades.getText().toString()) - cantidad;
                resta = Integer.parseInt(cajas2) - 1;
                edCajas.setText(Integer.toString(resta));
                edUnidades.setText(Integer.toString(uC));
                break;
            case R.id.btnSumar2:
                String unidades = edUnidades.getText().toString();
                suma = Integer.parseInt(unidades) + 1;
                uC = suma / cantidad;
                System.out.println(suma);
                edUnidades.setText(suma.toString());
                edCajas.setText(Integer.toString(uC));
                break;
            case R.id.btnRestar2:
                String unidades2 = edUnidades.getText().toString();
                resta = Integer.parseInt(unidades2) - 1;
                uC = resta / cantidad;
                edUnidades.setText(Integer.toString(resta));
                edCajas.setText(Integer.toString(uC));
                break;
            case R.id.btnVacio:
                edUnidades.setText("0");
                edCajas.setText("0");
                break;
            case R.id.btnGuardar:
                Producto producto = new Producto();
                producto.setCategoria(categoria);
                producto.setNom(tvProducto.getText().toString());
                producto.setCajas(Integer.parseInt(edCajas.getText().toString()));
                producto.setUnidades(Integer.parseInt(edUnidades.getText().toString()));
                producto.setCantidad(cantidad);
                producto.setPrecio(Double.parseDouble(edPrecio.getText().toString()));
                producto.setFecha(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()));

                dbTienda.child(getIntent().getExtras().getString("id")).setValue(producto);
                onBackPressed();
                finish();
            case R.id.btnCancelar:
                onBackPressed();
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