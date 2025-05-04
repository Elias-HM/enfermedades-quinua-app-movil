package com.epiis.detectaquinua;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.epiis.detectaquinua.data.db.AppDatabase;
import com.epiis.detectaquinua.data.entity.HistorialConsulta;

import java.io.File;

public class DetalleHistorialActivity extends BaseActivity {

    ImageView img;
    TextView lblFecha, lblPrediccion, lblPrecision, lblInterpretacion;
    Button btnEliminar;
    ProgressBar progressBar;

    private HistorialConsulta consulta;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setActivityLayout(R.layout.activity_detalle_historial);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle(getString(R.string.detHistorial_title));

        img = findViewById(R.id.imgDetalle);
        lblFecha = findViewById(R.id.lblFecha);
        lblPrediccion = findViewById(R.id.lblPrediccion);
        lblPrecision = findViewById(R.id.lblPrecision);
        btnEliminar = findViewById(R.id.btnDetalleEliminar);
        lblInterpretacion = findViewById(R.id.lblInterpretacionPrecision);
        progressBar = findViewById(R.id.progressBarPrecision);

        consulta = (HistorialConsulta) getIntent().getSerializableExtra("consulta");

        Intent intent = getIntent();
        int id = intent.getIntExtra("id",-1);
        String ruta = intent.getStringExtra("rutaImagen");
        String fecha = intent.getStringExtra("fecha");
        String prediccion = intent.getStringExtra("prediccion");
        Double precision = Double.parseDouble(intent.getStringExtra("precision"));
        String interpretacion = "";

        if (precision>=90) interpretacion=getString(R.string.result_lbl_interpretacion_0);
        else if (precision>=75) interpretacion=getString(R.string.result_lbl_interpretacion_1);
        else if (precision>=60) interpretacion=getString(R.string.result_lbl_interpretacion_2);
        else  interpretacion=getString(R.string.result_lbl_interpretacion_3);


        lblFecha.setText(getString( R.string.detHistorial_lbl_fecha) + " " + fecha);
        lblPrediccion.setText(getString( R.string.detHistorial_lbl_prediccion) + " " + prediccion);
        lblPrecision.setText(getString( R.string.detHistorial_lbl_precision) + " " + precision + "%");
        progressBar.setProgress(precision.intValue());
        lblInterpretacion.setText(interpretacion);

        img.setImageURI(Uri.fromFile(new File(ruta)));

        btnEliminar.setOnClickListener(v -> confirmarEliminacion());
    }

    private void confirmarEliminacion() {
        new AlertDialog.Builder(this)
                .setTitle("¿Eliminar este registro?")
                .setMessage("¿Estás seguro de que deseas eliminarlo?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    int id = getIntent().getIntExtra("id", -1);
                    if (id != -1) {
                        // Recuperar el objeto real desde Room con su ID
                        HistorialConsulta consulta = AppDatabase.getInstance(this).historialDao().obtenerPorId(id);
                        if (consulta != null) {
                            AppDatabase.getInstance(this).historialDao().eliminar(consulta);
                            Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "No se encontró el registro", Toast.LENGTH_SHORT).show();
                        }
                    }
                    finish(); // volver al historial
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}