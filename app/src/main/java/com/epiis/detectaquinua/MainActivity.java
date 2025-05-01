package com.epiis.detectaquinua;

import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.epiis.detectaquinua.data.db.AppDatabase;
import com.epiis.detectaquinua.data.entity.HistorialConsulta;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //para usar la camara
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_PERMISOS = 100;

    private String currentPhotoPath;
    //para ver el historial
    Button btnHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // En tu HistorialActivity.java (en onResume() o donde quieras verificar)

        List<HistorialConsulta> historialCompleto = AppDatabase.getInstance(this).historialDao().obtenerHistorial();

        android.util.Log.d("ConteoHistorial", "Número de registros en el historial: " + historialCompleto.size());

// Iterar a través de la lista e imprimir cada objeto (o propiedades específicas)
        for (HistorialConsulta consulta : historialCompleto) {
            android.util.Log.d("RegistroHistorial", "ID: " + consulta.id +
                    ", Fecha: " + consulta.fecha +
                    ", Imagen: " + consulta.rutaImagen +
                    ", Predicción: " + consulta.prediccion +
                    ", Precisión: " + consulta.precision);
        }

// Si solo quieres ver la representación String de la lista (puede ser muy largo)
        android.util.Log.d("ListaHistorialCompleta", "Contenido completo del historial: " + historialCompleto.toString());

        //para la camara
        Button btnAbrirCamara = findViewById(R.id.btnAbrirCamara);
        btnAbrirCamara.setOnClickListener(v->{
            verificarPermisos();
        });

        //para abrir la galeria de fotos
        Button btnAbrirGaleria = findViewById(R.id.btnAbrirGaleria);
        btnAbrirGaleria.setOnClickListener(v -> {
            abrirGaleria();
        });

        btnHistorial = findViewById(R.id.btnHistorial);

        btnHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
            startActivity(intent);
        });
    }

    //permisos de camara
    private void verificarPermisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISOS);
        } else {
            dispatchTakePictureIntent(); // ya tiene permiso, entonces abre la cámara
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISOS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent(); // permiso otorgado
            } else {
                Toast.makeText(this, "Se requiere permiso para usar la cámara", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Asegúrate de que haya una cámara disponible
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getPackageName() + ".fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //para recortar la imagen
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && data != null) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                abrirVistaPrevia(resultUri.getPath()); // Solo vista previa de la recortada
            } else {
                Toast.makeText(this, "Error al obtener imagen recortada", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (requestCode == UCrop.RESULT_ERROR && data != null) {
            Throwable cropError = UCrop.getError(data);
            Toast.makeText(this, "Error al recortar la imagen: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        // Imagen desde cámara
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (currentPhotoPath != null) {
                Uri imageUri = Uri.fromFile(new File(currentPhotoPath));
                iniciarRecorte(imageUri);
            } else {
                Toast.makeText(this, "Error: ruta de la imagen no encontrada", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Imagen desde galería
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                iniciarRecorte(imageUri);
            } else {
                Toast.makeText(this, "Error al seleccionar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //para recortar la imagen
    private void iniciarRecorte(Uri sourceUri) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Uri destUri = Uri.fromFile(new File(getCacheDir(), "imagen_"+timeStamp+".jpg"));

        UCrop.of(sourceUri, destUri)
                .withAspectRatio(244, 220)
                .withMaxResultSize(244, 220)
                .start(this);
    }

    private void abrirVistaPrevia(String imagePath) {
        Intent intent = new Intent(this, PreviewImgActivity.class);
        intent.putExtra("imagePath", imagePath);
        startActivity(intent);
    }

    //metodo para abrir la galeria
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }
}