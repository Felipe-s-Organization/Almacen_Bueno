package com.example.almacen_2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ChildEventListener, ValueEventListener, View.OnClickListener, SearchView.OnQueryTextListener, MenuItem.OnMenuItemClickListener {

    private List<Producto> productos;
    public RecyclerView viewLista;
    DatabaseReference dbProductos;
    String categoria ="";

    ProductoAdapter adapter;

    private static final int CODIGO_INTENT = 2, CODIGO_PERMISOS_CAMARA = 1;
    private boolean permisoCamaraConcedido = false, permisoSolicitadoDesdeBoton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            categoria = getIntent().getExtras().getString("categoria");
        }catch (Exception e){
            categoria = "";
        }

        dbProductos = FirebaseDatabase.getInstance().getReference().child("productos");
        dbProductos.addChildEventListener(this);
        dbProductos.addValueEventListener(this);
        productos = new ArrayList<>();

        adapter = new ProductoAdapter(productos,this,dbProductos);

        viewLista = findViewById(R.id.rvLista);

        viewLista.setHasFixedSize(true);
        viewLista.setLayoutManager(new LinearLayoutManager(this));
        viewLista.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        viewLista.setAdapter(adapter);

        FloatingActionButton añadir = findViewById(R.id.floButton);

        añadir.setOnClickListener(this);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu2) {

        getMenuInflater().inflate(R.menu.menu2, menu2);

        MenuItem search = menu2.findItem(R.id.searchView);
        MenuItem scanner = menu2.findItem(R.id.searchScanner);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Nombre del producto");

        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });*/
        searchView.setOnQueryTextListener(this);
        scanner.setOnMenuItemClickListener(this);
        //searchScanner.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu2);
    }

    private void filter(String newText) {
        List<Producto> listaFiltrada = new ArrayList<>();
        for (Producto item : productos){
            if (item.getNom().toLowerCase().contains(newText.toLowerCase()) || item.getCodi().toLowerCase().equals(newText.toLowerCase())){
                listaFiltrada.add(item);
            }
        }
        adapter.filterList(listaFiltrada);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem opcion_menu){
        int id=opcion_menu.getItemId();
        Intent intent;
        switch (id){
            case R.id.listaCategoria:
                intent = new Intent(this, ListaCategoria.class);
                startActivity(intent);
                break;
            case R.id.todosProd:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
        }

        return super.onOptionsItemSelected(opcion_menu);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.floButton){
            Intent intent2 = new Intent(this, Afegir.class);
            startActivity(intent2);
        }
    }

    private void Escaner (){
        Intent i = new Intent(this, Escanear.class);
        startActivityForResult(i, CODIGO_INTENT);
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

    private void permisoDeCamaraDenegado() {
        Toast.makeText(this, R.string.no_escanear, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingSuperCall")
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CODIGO_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String codigo = data.getStringExtra("codigo");
                    filter(codigo);
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        productos.clear();

        for(DataSnapshot element : snapshot.getChildren()){

            Producto producto = new Producto(
                    element.getKey(),
                    element.child("nom").getValue().toString(),
                    element.child("categoria").getValue().toString(),
                    Integer.parseInt(element.child("cajas").getValue().toString()),
                    Integer.parseInt(element.child("unidades").getValue().toString()),
                    Integer.parseInt(element.child("cantidad").getValue().toString()),
                    Double.parseDouble(element.child("precio").getValue().toString()),
                    element.child("fecha").getValue().toString()
            );
            if (producto.getCategoria().equals(categoria)) {
                productos.add(producto);
                productos = ordenar(productos);
            }else if (categoria == "" || categoria == null){
                productos.add(producto);
                productos = ordenar(productos);
            }


        }

        viewLista.getAdapter().notifyDataSetChanged();

    }

    public List<Producto> ordenar (List<Producto> lista){
        Collections.sort(lista, new Comparator<Producto>() {
            public int compare(Producto obj1, Producto obj2) {
                return obj1.getNom().compareTo(obj2.getNom());
            }
        });
        return lista;
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(this, MainActivity.class);
        startActivity(home);
        moveTaskToBack(true);
        finish();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filter(newText);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.searchScanner){
            verificarYPedirPermisosDeCamara();
            if (!permisoCamaraConcedido){
                Toast.makeText(this, R.string.permisoCamara, Toast.LENGTH_SHORT).show();
                permisoSolicitadoDesdeBoton = true;
            }
            Escaner();
        }
        return true;
    }
}