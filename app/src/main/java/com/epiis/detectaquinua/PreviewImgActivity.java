package com.epiis.detectaquinua;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.epiis.detectaquinua.data.db.AppDatabase;
import com.epiis.detectaquinua.data.entity.HistorialConsulta;
import com.epiis.detectaquinua.network.ImageUploader;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PreviewImgActivity extends AppCompatActivity {

    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preview_img);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //inicializar los objetos
        ImageView imagePreview;
        Button btnAnalizar;

        // Obtenemos la ruta desde el intent
        imagePath = getIntent().getStringExtra("imagePath");

        imagePreview = findViewById(R.id.imageViewFoto);
        btnAnalizar = findViewById(R.id.btnAnalizar);
        btnAnalizar.setEnabled(false);
        // Mostrar la imagen
        if (imagePath == null || imagePath.isEmpty()) {
            Toast.makeText(this, "No se encontró la imagen", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imagePreview.setImageBitmap(bitmap);
        btnAnalizar.setEnabled(true);

        // Consumir la API cuando se presiona el botón
        btnAnalizar.setOnClickListener(v -> {
            if (imagePath != null) {
                File imageFile = new File(imagePath);
                ImageUploader.uploadImage(imageFile, new ImageUploader.UploadCallback() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> {
                            try {
                                JSONObject respuesta = new JSONObject(response);

                                JSONObject result = respuesta.getJSONArray("results").getJSONObject(0);

                                String prediccion = result.getString("prediction");
                                float precision = (float) result.getDouble("confidence");

                                guardarEnHistorial(imagePath, prediccion, precision);

                                Intent intent = new Intent(PreviewImgActivity.this, ResultadoActivity.class);
                                intent.putExtra("imagePath", imagePath);
                                intent.putExtra("resultado", response);
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(PreviewImgActivity.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onError(String error) {
                        runOnUiThread(() ->
                                Toast.makeText(PreviewImgActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show()
                        );
                    }
                });
            }
        });
    }
    private void guardarEnHistorial(String ruta, String prediccion, float precision) {
        HistorialConsulta consulta = new HistorialConsulta();
        consulta.fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        consulta.rutaImagen = ruta;
        consulta.prediccion = prediccion;
        consulta.precision = precision;

        AppDatabase.getInstance(this).historialDao().insertar(consulta);
    }

}