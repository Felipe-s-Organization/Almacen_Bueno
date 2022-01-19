package com.example.almacen_2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity implements ChildEventListener, ValueEventListener, View.OnClickListener {

    private List<Producto> productos;
    public RecyclerView viewLista;
    DatabaseReference dbProductos;
    String categoria ="";

    ProductoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            categoria = getIntent().getExtras().getString("categoria");
        }catch (Exception e){

        }

        dbProductos = FirebaseDatabase.getInstance().getReference().child("productos");
        dbProductos.addChildEventListener(this);
        dbProductos.addValueEventListener(this);
        productos = new ArrayList<Producto>();

        adapter = new ProductoAdapter(productos,this,dbProductos);

        viewLista = (RecyclerView) findViewById(R.id.rvLista);

        viewLista.setHasFixedSize(true);
        viewLista.setLayoutManager(new LinearLayoutManager(this));
        viewLista.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        viewLista.setAdapter(adapter);

        FloatingActionButton añadir = findViewById(R.id.floButton);
        añadir.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu2) {
        // Forma 1: utilitzant el main.xml
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu2, menu2);

        MenuItem search = menu2.findItem(R.id.searchView);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Nombre del producto");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu2);
    }

    private void filter(String newText) {
        List<Producto> listaFiltrada = new ArrayList<>();
        for (Producto item : productos){
            if (item.getNom().toLowerCase().contains(newText.toLowerCase())){
                listaFiltrada.add(item);
            }
        }
        adapter.filterList(listaFiltrada);
    }

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
        productos.clear();

        for(DataSnapshot element : snapshot.getChildren()){
            Producto producto = new Producto(
                    element.getKey(),
                    element.child("nom").getValue().toString(),
                    element.child("categoria").getValue().toString(),
                    Integer.parseInt(element.child("cajas").getValue().toString()),
                    Integer.parseInt(element.child("unidades").getValue().toString()),
                    Integer.parseInt(element.child("cantidad").getValue().toString()),
                    Double.parseDouble(element.child("precio").getValue().toString())
            );
            if (producto.getCategoria().equals(categoria)) {
                productos.add(producto);
                productos = ordenar(productos);
            }else if (categoria.equals("")){
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
}