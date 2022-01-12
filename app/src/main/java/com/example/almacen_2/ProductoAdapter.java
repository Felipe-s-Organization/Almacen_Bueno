package com.example.almacen_2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {
    private View mView;
    public List<Producto> listaProductos;
    public List<Producto> listaBuscada;
    private Context context;
    private DatabaseReference databaseReference;

    public ProductoAdapter(List<Producto> listaProductos, Context context, DatabaseReference databaseReference) {
        this.listaProductos = listaProductos;
        this.context = context;
        this.databaseReference = databaseReference;
        this.listaBuscada = new ArrayList<>();
        listaBuscada.addAll(listaProductos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tvNom;
        public TextView tvNumCajas;
        public TextView tvCantidad;
        public TextView tvPrecio;

        public ViewHolder (View itemView){
            super(itemView);
            tvNom = itemView.findViewById(R.id.tvNom);
            tvNumCajas = itemView.findViewById(R.id.tvNumCajas);
            tvCantidad = itemView.findViewById(R.id.tvNumUnidades);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mostraPopupMenu(v,position);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_llista, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Producto item = listaProductos.get(position);
        String cajas = String.valueOf(item.getCajas());
        String unidades = String.valueOf(item.getUnidades());
        holder.tvNom.setText(item.getNom());
        holder.tvNumCajas.setText(cajas);
        holder.tvCantidad.setText(unidades);
        holder.tvPrecio.setText((double) item.getPrecio() +"€");
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }


    private void mostraPopupMenu(View v, int position) {
        PopupMenu popupMenu = new PopupMenu(this.context, v);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.menu, popupMenu.getMenu());
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
            Producto producto;
            switch (menuItem.getItemId()) {
                case R.id.modificarProducto:
                    producto = listaProductos.get(pos);
                    Intent intent2 = new Intent(context, Modificar.class);
                    intent2.putExtra("id",  producto.getCodi() );
                    intent2.putExtra("nom",  producto.getNom() );
                    intent2.putExtra("categoria", producto.getCategoria());
                    intent2.putExtra("cajas",  producto.getCajas() );
                    intent2.putExtra("unidades",  producto.getUnidades() );
                    intent2.putExtra("precio",  producto.getPrecio() );
                    intent2.putExtra("cantidad", producto.getCantidad());
                    context.startActivity(intent2);
                    return true;
                case R.id.eliminarProducto:
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Seguro que quieres eliminar el producto?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Producto s = listaProductos.get(pos);
                                    databaseReference= FirebaseDatabase.getInstance().getReference().child("productos");
                                    databaseReference.child(s.getCodi()).removeValue();
                                    Toast toast = Toast.makeText(context,"Producto eliminado",Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }).setNegativeButton("Cancelar", null).show();
                default:
            }
            return false;
        }
    }


}
