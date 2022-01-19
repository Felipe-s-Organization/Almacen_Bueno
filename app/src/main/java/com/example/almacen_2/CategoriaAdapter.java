package com.example.almacen_2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder> {

    private View mView;
    public List<Categoria> listaCategorias;
    public List<Categoria> listaBuscada;
    private Context context;
    private DatabaseReference databaseReference;

    public CategoriaAdapter(List<Categoria> listaCategorias, Context context, DatabaseReference databaseReference) {
        this.listaCategorias = listaCategorias;
        this.context = context;
        this.databaseReference = databaseReference;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView tvNom;

        public ViewHolder (View itemView){
            super(itemView);
            tvNom = itemView.findViewById(R.id.tvNom);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Categoria item = listaCategorias.get(position);
            String nombre = item.getNom();
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("categoria", nombre);
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            mostraPopupMenu(v,position);
            return false;
        }
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_llista_2, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Categoria item = listaCategorias.get(position);
        holder.tvNom.setText(item.getNom());
    }

    @Override
    public int getItemCount() {
        return listaCategorias.size();
    }

    private void mostraPopupMenu(View v, int position) {
        PopupMenu popupMenu = new PopupMenu(this.context, v);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.menu3, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new Menu(position));
        popupMenu.show();
    }

    public class Menu implements PopupMenu.OnMenuItemClickListener {
        Integer pos;

        public Menu(int pos) {
            this.pos = pos;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            // Implementar cada opció del menú
            switch (menuItem.getItemId()) {
                case R.id.eliminarCategoria:
                    Categoria s = listaCategorias.get(pos);
                    databaseReference= FirebaseDatabase.getInstance().getReference().child("categorias");
                    databaseReference.child(s.getNom()).removeValue();
                default:
            }
            return false;
        }
    }
}
