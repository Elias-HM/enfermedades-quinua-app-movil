package com.epiis.detectaquinua.ui.adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.epiis.detectaquinua.R;
import com.epiis.detectaquinua.data.entity.HistorialConsulta;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDelete(HistorialConsulta consulta);
    }

    public interface OnItemClickListener {
        void onItemClick(HistorialConsulta consulta);
    }


    private List<HistorialConsulta> historialList;
    private final OnDeleteClickListener onDeleteClickListener;
    private final OnItemClickListener onItemClickListener;

    public HistorialAdapter(List<HistorialConsulta> historialList, OnDeleteClickListener deleteListener, OnItemClickListener clickListener) {
        this.historialList = historialList;
        this.onDeleteClickListener = deleteListener;
        this.onItemClickListener = clickListener;
    }

    public void actualizarLista(List<HistorialConsulta> nuevaLista) {
        this.historialList = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistorialConsulta consulta = historialList.get(position);

        DecimalFormat df = new DecimalFormat("#.###%");
        String precision = df.format(consulta.precision);

        holder.txtFecha.setText(consulta.fecha);
        holder.txtPrediccion.setText(holder.itemView.getContext().getString(R.string.detHistorial_lbl_prediccion) + " " + consulta.prediccion);
        holder.txtPrecision.setText(holder.itemView.getContext().getString(R.string.detHistorial_lbl_precision) + " " + precision);

        holder.imageView.setImageURI(Uri.fromFile(new File(consulta.rutaImagen)));

        //para eliminar
        holder.btnEliminar.setOnClickListener(v -> onDeleteClickListener.onDelete(consulta));
        //para ver los detalles
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(consulta));
    }

    @Override
    public int getItemCount() {
        return historialList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFecha, txtPrediccion, txtPrecision;
        ImageView imageView;
        Button btnEliminar;

        public ViewHolder(View itemView) {
            super(itemView);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtPrediccion = itemView.findViewById(R.id.txtPrediccion);
            txtPrecision = itemView.findViewById(R.id.txtPrecision);
            imageView = itemView.findViewById(R.id.imgHistorial);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
