package com.example.almacen_2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class ListaCategoria extends AppCompatActivity implements ChildEventListener, ValueEventListener, View.OnClickListener{

    public EditText nomCategoria;
    public Button añadir;
    public RecyclerView listaCategorias;
    private List<Categoria> categorias;
    DatabaseReference dbCategorias;

    CategoriaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_categoria);

        dbCategorias = FirebaseDatabase.getInstance().getReference().child("categorias");
        dbCategorias.addChildEventListener(this);
        dbCategorias.addValueEventListener(this);
        categorias = new ArrayList<Categoria>();

        adapter = new CategoriaAdapter(categorias, this, dbCategorias);

        listaCategorias = (RecyclerView) findViewById(R.id.rCategoria);

        listaCategorias.setHasFixedSize(true);
        listaCategorias.setLayoutManager(new LinearLayoutManager(this));
        listaCategorias.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        listaCategorias.setAdapter(adapter);

        añadir = (Button) findViewById(R.id.añadir);
        añadir.setOnClickListener(this);

        nomCategoria = (EditText) findViewById(R.id.nomCategoria);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //Creo la accion que hace el boton añadir
            case R.id.añadir:
                Categoria categoria = new Categoria();
                String nombre = nomCategoria.getText().toString();
                //Si accidentalmente le dan al botont comprueba el EditText, si esta vacio crea un toast
                if (nombre.equals("")){
                    Toast toast = Toast.makeText(this,R.string.invalid_categoria,Toast.LENGTH_SHORT);
                    toast.show();
                }else {
                    categoria.setNom(nombre);
                    dbCategorias.child(nombre).setValue(categoria);
                    nomCategoria.setText("");
                }
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
        categorias.clear();

        for(DataSnapshot element : snapshot.getChildren()){
            Categoria categoria = new Categoria(
                    element.getKey()
            );
            categorias.add(categoria);
            //Ordeno el recycleView para buscar mejor
            Collections.sort(categorias, new Comparator<Categoria>() {
                public int compare(Categoria obj1, Categoria obj2) {
                    return obj1.getNom().compareTo(obj2.getNom());
                }
            });
        }
        //Se actualiza el recycleView
        listaCategorias.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}