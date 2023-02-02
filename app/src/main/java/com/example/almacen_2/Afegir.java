package com.example.almacen_2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    Button btnEscaner;

    String categoriaSelect="";
    

    DatabaseReference dbTienda;
    DatabaseReference dbCategorias;

    private static final int CODIGO_INTENT = 2, CODIGO_PERMISOS_CAMARA = 1;
    private boolean permisoCamaraConcedido = false, permisoSolicitadoDesdeBoton = false;


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
        btnEscaner = findViewById(R.id.btnEscanear);

        btnGuardar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
        btnEscaner.setOnClickListener(this);

        loadCaregoria();
    }

    //Carga la lista de categorias en el spinner para poder selecionarla
    public void loadCaregoria(){
        final List<Categoria> categorias = new ArrayList<>();
        dbCategorias.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String nombre = Objects.requireNonNull(ds.child("nom").getValue()).toString();
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        Toast toast;
        switch (v.getId()){
            case R.id.btnEscanear:
                verificarYPedirPermisosDeCamara();
                if (!permisoCamaraConcedido){
                    toast = Toast.makeText(this, R.string.permisoCamara, Toast.LENGTH_SHORT);
                    toast.show();
                    permisoSolicitadoDesdeBoton = true;
                    return;
                }
                Escaner();
                break;
            case R.id.btnSave:
                Producto producto = new Producto();
                crearProducto(producto);
                if (producto.getNom().equals("") || producto.getNom().equals(".")){
                    Toast.makeText(this, R.string.no_nom, Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        String codi;
                        String nom;
                        if (edCodi.getText().toString().equals("")){
                            nom = edNom.getText().toString();
                            codi = nom.replace(".","");
                            codi = codi.split("/")[0];
                            dbTienda.child(codi).setValue(producto);
                        }else {
                            dbTienda.child(edCodi.getText().toString()).setValue(producto);
                        }
                        toast = Toast.makeText(this,R.string.valid_produc, Toast.LENGTH_SHORT);
                        toast.show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }catch (Exception e){
                        toast = Toast.makeText(this,R.string.invalid_produc, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

                break;
            case R.id.btnCancel:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                finish();
                break;
            default:
        }
    }

    private void verificarYPedirPermisosDeCamara() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
            // En caso de que haya dado permisos ponemos la bandera en true
            // y llamar al método
            permisoCamaraConcedido = true;
        } else {
            // Si no, pedimos permisos. Ahora mira onRequestPermissionsResult
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CODIGO_PERMISOS_CAMARA);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODIGO_PERMISOS_CAMARA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Escanear directamten solo si fue pedido desde el botón
                    if (permisoSolicitadoDesdeBoton) {
                        Escaner();
                    }
                    permisoCamaraConcedido = true;
                } else {
                    permisoDeCamaraDenegado();
                }
                break;
        }
    }

    private void permisoDeCamaraDenegado() {
        Toast.makeText(this, R.string.no_escanear, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SimpleDateFormat")
    public void crearProducto (Producto producto){
        producto.setNom(edNom.getText().toString());
        producto.setCategoria(categoriaSelect);
        producto.setCodi(edCodi.getText().toString());
        if (edCajas.getText().toString().equals("")) {
            producto.setCajas(0);
        }else{
            producto.setCajas(Integer.parseInt(edCajas.getText().toString()));
        }
        int unidades;
        int cajas = producto.getCajas();
        int cantidad;
        if (edCajas.getText().toString().equals("")){
            cantidad = 0;
        }else {
            cantidad = Integer.parseInt(edCantidad.getText().toString());
        }
        if (edUnidades.getText().toString().equals("")){
            unidades = cajas * cantidad;
            producto.setUnidades(unidades);
        }else{
            unidades = Integer.parseInt(edUnidades.getText().toString());
            int total = cajas * cantidad + unidades;
            producto.setUnidades(total);
        }
        producto.setCantidad(cantidad);
        if (edPrecio.getText().toString().equals("")){
            producto.setPrecio(0.0);
        }else{
            producto.setPrecio(Double.parseDouble(edPrecio.getText().toString()));
        }
        String date;
        date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
        producto.setFecha(date);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CODIGO_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String codigo = data.getStringExtra("codigo");
                    edCodi.setText(codigo);
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void Escaner (){
        Intent i = new Intent(this, Escanear.class);
        startActivityForResult(i, CODIGO_INTENT);
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