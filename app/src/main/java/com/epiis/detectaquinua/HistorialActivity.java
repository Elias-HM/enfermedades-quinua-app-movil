package com.epiis.detectaquinua;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.epiis.detectaquinua.data.db.AppDatabase;
import com.epiis.detectaquinua.data.entity.HistorialConsulta;
import com.epiis.detectaquinua.ui.adapter.HistorialAdapter;

import java.text.DecimalFormat;
import java.util.List;

public class HistorialActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private HistorialAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setActivityLayout(R.layout.activity_historial);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle(getString(R.string.historial_title));

        recyclerView = findViewById(R.id.recyclerHistorial);
        //modo de vista del historial
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        List<HistorialConsulta> historial = AppDatabase.getInstance(this).historialDao().obtenerHistorial();
        adapter = new HistorialAdapter(historial,
                // Eliminar consulta
                consulta -> {
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("Eliminar registro")
                            .setMessage("¿Estás seguro de que deseas eliminar este registro del historial?")
                            .setPositiveButton("Sí", (dialog, which) -> {
                                AppDatabase.getInstance(this).historialDao().eliminar(consulta);
                                adapter.actualizarLista(AppDatabase.getInstance(this).historialDao().obtenerHistorial());
                                Toast.makeText(HistorialActivity.this, "Se elimino el registro", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                },
                // Clic en item (ver detalle)
                consulta -> {
                    DecimalFormat df = new DecimalFormat("#.###%");
                    String precision = df.format(consulta.precision);

                    Intent intent = new Intent(HistorialActivity.this, DetalleHistorialActivity.class);
                    intent.putExtra("id", consulta.id);
                    intent.putExtra("rutaImagen", consulta.rutaImagen);
                    intent.putExtra("fecha", consulta.fecha);
                    intent.putExtra("prediccion", consulta.prediccion);
                    intent.putExtra("precision", precision);
                    startActivity(intent);
                }
        );

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarHistorial(); // Llama a cargarHistorial cada vez que la actividad se vuelve visible
    }

    private void cargarHistorial() {
        List<HistorialConsulta> historial = AppDatabase.getInstance(this).historialDao().obtenerHistorial();
        adapter.actualizarLista(historial);
    }
}